package com.snamazon.snamazon;

import org.springframework.beans.factory.annotation.Value;

import java.lang.IllegalStateException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.activemq.broker.BrokerFactory;
import org.apache.activemq.broker.BrokerService;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.SimpleMessageListenerContainer;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import wiremock.net.minidev.json.JSONObject;

import javax.jms.*;
import javax.jms.Queue;
import javax.jms.Session;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.Application;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.text.DecimalFormat;
import javax.jms.ConnectionFactory;

import org.apache.activemq.ActiveMQConnectionFactory;


@RestController
public class SnamazonService {

    @Value("${zuul.path}")
    private String zuulPath;
    private static DecimalFormat df2 = new DecimalFormat("#.##");

    @GetMapping(value={"", "/", "index"})
    public ModelAndView index(HttpSession session){

        System.err.println(zuulPath);

        ModelAndView mav = getLoginButton(session, "index");
        mav.addObject("products", getHtmlProducts());
        return mav;
    }

    @GetMapping(value = "Cart")
    public ModelAndView cart(HttpSession session){

        ModelAndView mav = getLoginButton(session, "Cart");

        String cart = getHtmlCart(session);
        mav.addObject("products", cart);
        return mav;
    }

    @GetMapping(value = "login")
    public ModelAndView login(String error)
    {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("login");
        mav.addObject("error", error);
        return mav;
    }

    @GetMapping(value = "profile")
    public ModelAndView profile(HttpSession session, String error)
    {
        ModelAndView mav  = new ModelAndView();

        if(session.getAttribute("user_id") == null)
        {
            mav.setViewName("index");
            return mav;
        }

        mav = getLoginButton(session, "profile");

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("userId", session.getAttribute("user_id"));
        jsonObject.put("password", session.getAttribute("password"));
        String jsonString = jsonObject.toString();
        Map<String, Object> user = postRequestToArray(zuulPath + "user-service" + "/user", jsonString).get(0);
        mav.addObject("num_client", user.get("num_client"));
        mav.addObject("nom", user.get("nom"));
        mav.addObject("prenom", user.get("prenom"));
        mav.addObject("solde", user.get("montant_dispo"));
        mav.addObject("mail", user.get("mail"));
        mav.addObject("adresse", user.get("adresse"));

        mav.addObject("commandes", getHtmlOrders(session));

        return mav;
    }

    @GetMapping(value = "disconnect")
    public ModelAndView disconnect(HttpSession session)
    {
        session.invalidate();
        ModelAndView mav = getLoginButton(session, "index");
        mav.addObject("products", getHtmlProducts());
        return mav;
    }

    @PostMapping("/index")
    public ModelAndView login(@RequestParam("user_mail") String user_mail, @RequestParam("password") String password, HttpServletRequest request) {

        HttpClient client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2).build();
        HttpRequest httpRequest = HttpRequest.newBuilder(URI.create(zuulPath + "user-service" + "/login?mail=" + user_mail + "&password=" + hashPasswordHtml(password)))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(user_mail)).build();
        CompletableFuture<HttpResponse<String>> response = client.sendAsync(httpRequest,HttpResponse.BodyHandlers.ofString());

        try {
            if (response.get().body().equals("WRONG_MAIL")) return login("<br><div class=\"alert alert-danger\" role=\"alert\"> <strong>Erreur!</strong> e-mail introuvable...</div></br>");
            if (response.get().body().equals("WRONG_PWD")) return login("<br><div class=\"alert alert-danger\" role=\"alert\"> <strong>Erreur!</strong> Mauvais mot de passe...</div></br>");
            request.getSession().setAttribute("user_id", response.get().body());
            request.getSession().setAttribute("password", password);
            request.getSession().setAttribute("user_mail", user_mail);
            if (request.getSession().getAttribute("panier") == null ) return index(request.getSession());
            for (Map<String, Object> product : (ArrayList<Map<String, Object>>)request.getSession().getAttribute("panier")) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("userId", request.getSession().getAttribute("user_id"));
                jsonObject.put("userPassword", request.getSession().getAttribute("password"));
                jsonObject.put("numProduit", String.valueOf(Math.round(Math.round((Double)product.get("num_produit")))));
                jsonObject.put("quantite", String.valueOf(product.get("quantite")));
                String jsonString = jsonObject.toString();
                putRequest("http://localhost:8085/addItem", jsonString);
            }
            return index(request.getSession());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return index(request.getSession());
    }

    //PUT ne marche pas
    @PostMapping("/cart_add")
    public ModelAndView addToCart(HttpSession session, @RequestParam("id") String id) {
        ModelAndView mav = getLoginButton(session, "Cart");

        Map<String, Object> baseProduct = getRequestToArray(zuulPath + "product-service" + "/getProduct?id=" + id).get(0);

        if(session.getAttribute("user_id") != null) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("userId", session.getAttribute("user_id"));
            jsonObject.put("userPassword", session.getAttribute("password"));
            jsonObject.put("numProduit", id);
            jsonObject.put("quantite", "1");
            String jsonString = jsonObject.toString();
            int stock = Integer.parseInt(putRequestToValue("http://localhost:8085/addItem", jsonString));
            if(stock >= 0) {
                mav.addObject("customError", "Plus que " + stock + " " + baseProduct.get("nom") + " de stock");
                //getRequestToValue(zuulPath + "stock-service" + "/restock?id=" + baseProduct.get("num_produit"));
            }

            getHtmlCart(session);
            String cart = getHtmlCart(session);
            mav.addObject("products", cart);
            return mav;
        }

        ArrayList<Map<String, Object>> panier = new ArrayList<>();
        if(session.getAttribute("panier") != null) {
            panier = (ArrayList<Map<String, Object>>)session.getAttribute("panier");
        }

        boolean articleDejaLa = false, articleDeStock = true;
        //pour utiliser le service stock
        int stock = Integer.parseInt(getRequestToValue(zuulPath + "stock-service" + "/getStockByProduct?id=" + Math.round(Math.round((Double)baseProduct.get("num_produit")))));
        for (Map<String, Object> product : panier) {
            if(product.get("num_produit").equals(baseProduct.get("num_produit"))) {
                if((int)product.get("quantite") == stock || stock == 0)
                {
                    articleDeStock = false;
                    mav.addObject("customError", "Plus que " + stock + " " + baseProduct.get("nom") + " de stock");
                    //getRequestToValue(zuulPath + "stock-service" + "/restock?id=" + baseProduct.get("num_produit"));
                    break;
                }
                product.put("quantite", (int) product.get("quantite") + 1 );
                articleDejaLa = true;
                break;
            }
        }
        if(!articleDejaLa && articleDeStock) {
            baseProduct.put("quantite", 1);
            panier.add(baseProduct);
        }

        session.setAttribute("panier", panier);

        String cart = getHtmlCart(session);
        mav.addObject("products", cart);
        return mav;
    }

    //DELETE ne marche pas
    @PostMapping("/cart_remove")
    public ModelAndView removeFromCart(HttpSession session, @RequestParam("id") String id, @RequestParam("boolRemoveAll") String boolRemoveAll) {
        ModelAndView mav = getLoginButton(session, "Cart");

        if(session.getAttribute("user_id") != null) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("userId", session.getAttribute("user_id"));
            jsonObject.put("userPassword", session.getAttribute("password"));
            jsonObject.put("numProduit", String.valueOf((int)(Double.parseDouble(id))));
            jsonObject.put("removeAll", boolRemoveAll);
            String jsonString = jsonObject.toString();

            //UTILISATION DE JMS
            SnamazonApplication.sendJmsMessage("toRemoveCart", jsonString);

            //postRequest(zuulPath + "cart-service" + "/removeItem", jsonString);

            getHtmlCart(session);
            String cart = getHtmlCart(session);
            mav.addObject("products", cart);
            return mav;
        }

        ArrayList<Map<String, Object>> panier = (ArrayList<Map<String, Object>>)session.getAttribute("panier");

        int i=0;
        if (boolRemoveAll.equals("true")){
            for (Map<String, Object> product : panier) {
                if(String.valueOf(product.get("num_produit")).equals(id))
                {
                    panier.remove(i);
                    break;
                }
                i++;
            }
        }
        else {
            for (Map<String, Object> product : panier) {
                if(String.valueOf(product.get("num_produit")).equals(id))
                {
                    if((int) panier.get(i).get("quantite") - 1 == 0)
                        panier.remove(i);
                    else
                        panier.get(i).put("quantite", (int) panier.get(i).get("quantite") - 1);
                    break;
                }
                i++;
            }
        }

        session.setAttribute("panier", panier);

        String cart = getHtmlCart(session);
        mav.addObject("products", cart);
        return mav;
    }

    @GetMapping("/cart_shippingRate")
    public ModelAndView shippingRateCart(HttpSession session, @RequestParam("livraison") String livraison)
    {
        session.setAttribute("shippingRate", livraison);
        return cart(session);
    }

    @GetMapping("/checkout")
    public ModelAndView doCheckout(HttpSession session)
    {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("checkout");

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("userId", session.getAttribute("user_id"));
        jsonObject.put("userPassword", session.getAttribute("password"));
        String jsonString = jsonObject.toString();
        String htmlMessage;
        String ack = postRequestToValue(zuulPath + "checkout-service" + "/checkout", jsonString);
        if(ack.equals("ACK"))
            htmlMessage = "<br><div class=\"alert alert-success text-center\" role=\"alert\" > <strong>Succes !</strong> Payement réussi</div></br>";
        else if(ack.equals("FAIL"))
            htmlMessage = "<br><div class=\"alert alert-danger text-center\" role=\"alert\" > <strong>Erreur !</strong> Payement échoué (vérifiez que votre solde est suffisant)</div></br>";
        else
        {
            Map<String, Object> baseProduct = getRequestToArray(zuulPath + "product-service" + "/getProduct?id=" + ack).get(0);

            htmlMessage = "<br><div class=\"alert alert-danger text-center\" role=\"alert\" > <strong>Erreur !</strong>" + " Plus que " + baseProduct.get("quantite") + " " + baseProduct.get("nom") + " de stock" + "</div></br>";
        }
        mav.addObject("message", htmlMessage);
        return mav;
    }

    @GetMapping("/cart_error")
    public ModelAndView cartError(HttpSession session, @RequestParam("error") String error)
    {
        ModelAndView mav = cart(session);
        mav.addObject("error", error);
        return mav;
    }

    public String hashPasswordHtml(String passwordToHash)
    {
        try {
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[16];
            random.nextBytes(salt);
            String saltB64 = new String(Base64.getEncoder().encode(salt));
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(saltB64.getBytes());
            byte[] hashedPassword = md.digest(passwordToHash.getBytes(StandardCharsets.UTF_8));
            return new String(Base64.getEncoder().encode(hashedPassword)) + "&salt=" + saltB64;
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "ERROR";
    }

    public ModelAndView getLoginButton(HttpSession session, String mavName)
    {
        ModelAndView mav = new ModelAndView();
        mav.setViewName(mavName);

        boolean invalidated = false;
        try {
            session.isNew();
        } catch(IllegalStateException ex) {
            invalidated = true;
        }

        if(invalidated || session.getAttribute("user_id") == null) {
            mav.addObject("connectButton",
                    "<form class=\"float-right\" method=\"GET\" action=\"login\">\n" +
                            "<button type =\"submit\" class=\"btn btn-primary btn-md\"><i class='fas fa-sign-in-alt'></i> Login </button>\n" +
                            "</form>");
            mav.addObject("userid", "Connecté en tant qu'invité");
        }
        else {
            mav.addObject("connectButton",
                    "<form class=\"float-right\" method=\"GET\" action=\"disconnect\">\n" +
                            "<button type =\"submit\" class=\"btn btn-primary btn-md\"><i class='fas fa-sign-out-alt'></i> Se deconnecter </button>\n" +
                            "</form>");

            mav.addObject("userid", "Connecté en tant que " + session.getAttribute("user_mail"));

            mav.addObject("profileButton", "<form class=\"float-right\" method=\"GET\" action=\"profile\">\n" +
                    "<button type =\"submit\" class=\"btn btn-primary btn-md\"><i class='fas fa-user-alt'></i> Page perso </button>\n" +
                    "</form>");
        }

        return mav;
    }

    public String getHtmlCart(HttpSession session)
    {
        if(session.getAttribute("user_id") != null)
        {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("userId", session.getAttribute("user_id"));
            jsonObject.put("userPassword", session.getAttribute("password"));
            if(session.getAttribute("shippingRate") != null)
                jsonObject.put("forfait", session.getAttribute("shippingRate"));
            String jsonString = jsonObject.toString();
            Map<String, Object> order = postRequestToArray(zuulPath + "order-service" + "/getOrder", jsonString).get(0);
            session.setAttribute("numCommande", order.get("num_commande"));
            session.setAttribute("shippingRate", order.get("forfait"));
            session.setAttribute("panier", postRequestToArray(zuulPath + "cart-service" + "/getCart", jsonString));
        }



        String cart = "";
        double prixTotal = 0, tvaTotale = 0;
        if(session.getAttribute("panier") == null || ((ArrayList<Map<String, Object>>)session.getAttribute("panier")).isEmpty())
            return "<br><h2 class=\"alert alert-info text-center\" role=\"alert\" > <strong>Panier vide</strong></h2></br>";

        cart += "<div class=\"basket\">\n" +
                "        <div class=\"basket-labels\">\n" +
                "            <ul>\n" +
                "                <li class=\"item item-heading\">Item</li>\n" +
                "                <li class=\"price\">Price</li>\n" +
                "                <li class=\"quantity\">Quantity</li>\n" +
                "                <li class=\"subtotal\">Subtotal</li>\n" +
                "            </ul>\n" +
                "        </div>";

        for (Map<String, Object> product : (ArrayList<Map<String, Object>>)session.getAttribute("panier")) {
            //Probleme de cast selon d'ou vient la quantite
            double prix, tva;
            if(product.get("quantite") instanceof String) {
                prix = Integer.parseInt((String) product.get("quantite")) * Double.parseDouble((String) product.get("prix"));
            }
            else {
                prix = (int) product.get("quantite") * Double.parseDouble((String) product.get("prix"));
            }
            tva = prix * Double.parseDouble(getRequestToValue(zuulPath + "tva-service" + "/getTvaByPoduct?id=" +
                    Math.round(Math.round((Double)product.get("num_produit"))))) / 100;
            cart += "<div class=\"basket-product\">\n" +
                    "            <div class=\"item\">\n" +
                    "                <div class=\"product-image\">\n" +
                    "                    <img src=\"" + product.get("image") + "\" alt=\"Placholder Image 2\" class=\"product-frame\">\n" +
                    "                </div>\n" +
                    "                <div class=\"product-details\">\n" +
                    "                    <h1><strong>" + product.get("nom") + "</strong> "  + " </h1>\n" +
                    "                    <p><strong>" + product.get("description") + "</strong></p>\n" +
                    "                    <p>Product Code - " + Math.round((Double)product.get("num_produit")) + "</p>\n" +
                    "                </div>\n" +
                    "            </div>\n" +
                    "            <div class=\"price\">" + product.get("prix") + "</div>\n" +
                    "            <div class=\"quantity\">\n" +
                    "            <form method=\"POST\" action=\"cart_add?id=" + Math.round((Double)product.get("num_produit")) + "\">\n" +
                    "            <p><h4><button class=\"w3-btn w3-block w3-blue\">+</button></h4></p>" +
                    "            </form>" +
                    "                <h3 class=\"text-center\"><br><br>" + product.get("quantite") + "</h3>" +
                    "            <form method=\"POST\" action=\"cart_remove?id=" + product.get("num_produit") + "&boolRemoveAll=false\">\n" +
                    "            <p><h4><button class=\"w3-btn w3-block w3-blue\">-</button></h4></p>" +
                    "            </form>" +
                    "            </div>\n" +
                    "            <div class=\"subtotal\">" + prix + "</div>\n" +
                    "            <div class=\"remove\">\n" +
                    "            <form method=\"POST\" action=\"cart_remove?id=" + product.get("num_produit") + "&boolRemoveAll=true\">\n" +
                    "                <button>Remove</button>\n" +
                    "            </form>" +
                    "            </div>\n" +
                    "        </div>";
            prixTotal += prix;
            tvaTotale += tva;
        }

        double subtotal = prixTotal;
        prixTotal += tvaTotale;
        float prixLivraison = -1;
        boolean livraisonSelected = false, loggedIn = false;
        if(session.getAttribute("shippingRate") != null) {
            prixLivraison = Float.parseFloat(getRequestToValue(zuulPath + "livraison-service" + "/getShippingRateByName?name=" + session.getAttribute("shippingRate")));
            prixTotal += prixLivraison;
            livraisonSelected = true;
        }

        if(session.getAttribute("user_id") != null)
            loggedIn = true;


        cart += "</div>\n" +
                "    <aside>\n" +
                "        <div class=\"summary\">\n" +
                "            <div class=\"summary-total-items\"><span class=\"total-items\"></span> Items in your Bag</div>\n" +
                "            <div class=\"summary-subtotal\">\n" +
                "                <div class=\"subtotal-title\">Subtotal</div>\n" +
                "                <div class=\"subtotal-value final-value\" id=\"basket-subtotal\">" + df2.format(subtotal) + "</div>\n" +
                "                <div class=\"summary-promo hide\">\n" +
                "                    <div class=\"promo-title\">Promotion</div>\n" +
                "                    <div class=\"promo-value final-value\" id=\"basket-promo\"></div>\n" +
                "                </div>\n" +
                "            </div>\n" +
                "            <div class=\"summary-delivery\">\n" +
                "                <select id=selectForfait name=\"delivery-collection\" class=\"summary-delivery-selection\">\n" +
                "       <option value=\"default\">Forfait de livraison</option>\n";
        for (Map<String, Object> forfait : getRequestToArray(zuulPath + "livraison-service" + "/getShippingRates")) {
            cart += "<option value=\"" + forfait.get("nom_forfait") + "\">" + forfait.get("nom_forfait") + " (€" + df2.format(forfait.get("prix")) + ")</option>\n";
        }
                cart += "</select>\n" +
                (livraisonSelected ? "<br>Forfait livraison : €" + df2.format(prixLivraison) : "" ) +
                "            <br> TVA € : " + df2.format(tvaTotale) +
                "            </div>\n" +
                "            <div class=\"summary-total\">\n" +
                "                <div class=\"total-title\">Total</div>\n" +
                "                <div class=\"total-value final-value\" id=\"basket-total\">" + df2.format(prixTotal) + "</div>\n" +
                "            </div>\n" +
                "            <div class=\"summary-checkout\">\n" +
                "            <form method=\"GET\" action=\"" + (livraisonSelected && loggedIn ? "checkout\">" : "cart_error?>\n" +
                "            <input type=\"text\" name=\"error\" />") +
                "                <button" + (livraisonSelected && loggedIn ? "" : " name=\"error\" value=\"" + (loggedIn ? "NO_SHIPPING" : "NOT_LOGGED_IN") + "\"") + " class=\"checkout-cta\">Go to Secure Checkout</button>\n" +
                "            </form>" +
                "            </div>\n" +
                "        </div>\n" +
                "    </aside>" +
                "    <script>" +
                "        function onchange(e) {\n" +
                "            window.location.href = \"http://localhost:8080/cart_shippingRate?livraison=\" + e.currentTarget.value\n" +
                "        }" +
                "       document.getElementById('selectForfait').addEventListener('change', onchange);" +
                "    </script>";


        return cart;
    }

    public ArrayList<Map<String, Object>> getRequestToArray(String uri)
    {
       return gsonToArray(getRequestToValue(uri));
    }

    public String getRequestToValue(String uri)
    {
        HttpClient client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2).build();
        HttpRequest request = HttpRequest.newBuilder(URI.create(uri))
                .header("Content-Type", "application/json")
                .GET().build();
        CompletableFuture<HttpResponse<String>> response = client.sendAsync(request,HttpResponse.BodyHandlers.ofString());

        String gson = "";
        try {
            gson = response.get().body();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return gson;
    }

    public void postRequest(String uri, String json)
    {
        HttpClient client = HttpClient.newBuilder().build();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        client.sendAsync(request,HttpResponse.BodyHandlers.ofString());
    }

    public void putRequest(String uri, String json)
    {
        HttpClient client = HttpClient.newBuilder().build();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .PUT(HttpRequest.BodyPublishers.ofString(json))
                .build();

        client.sendAsync(request,HttpResponse.BodyHandlers.ofString());
    }

    public ArrayList<Map<String, Object>> postRequestToArray(String uri, String json)
    {
        return gsonToArray(postRequestToValue(uri,json));
    }

    public String postRequestToValue(String uri, String json)
    {

        HttpClient client = HttpClient.newBuilder().build();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        CompletableFuture<HttpResponse<String>> response = client.sendAsync(request,HttpResponse.BodyHandlers.ofString());

        String gson = null;
        try {
            gson = response.get().body();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return gson;
    }

    public String putRequestToValue(String uri, String json)
    {

        HttpClient client = HttpClient.newBuilder().build();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .PUT(HttpRequest.BodyPublishers.ofString(json))
                .build();

        CompletableFuture<HttpResponse<String>> response = client.sendAsync(request,HttpResponse.BodyHandlers.ofString());

        String gson = null;
        try {
            gson = response.get().body();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return gson;
    }

    public ArrayList<Map<String, Object>> gsonToArray(String gson)
    {
        System.err.println(gson);
        if(gson.startsWith("["))
            gson = "{\"_table\":" + gson + ",\"_content\":[]}";
        else
            gson = "{\"_table\":[" + gson + "],\"_content\":[]}";

        ArrayList<Map<String, Object>> dataTable = Table.Deseriliaze(gson).getTable();
        return dataTable;
    }

    public String getHtmlProducts()
    {
        String html = "";
        for (Map<String, Object> product : getRequestToArray(zuulPath + "product-service" + "/getProducts")) {
            if(Integer.parseInt((String)product.get("quantite")) == 0) continue;
            html += "   <div class=\"card\">\n" +
                    "        <img src=\"" + product.get("image") + "\" alt=\"" + product.get("nom") + "\" style=\"width:100%\">\n" +
                    "        <h1>" + product.get("nom") + "</h1>\n" +
                    "        <p class=\"price\">€" + product.get("prix") + "</p>\n" +
                    "        <p>" + product.get("description") + "<br>(" + product.get("quantite") + " restant)</p>\n" +
                    "        <p><form action=\"/cart_add?id="+ Math.round((double)product.get("num_produit")) +"\" method=\"POST\">\n" +
                    "            <button class=\"add-button\" type=\"submit\">Ajouter au panier</button>\n" +
                    "       </form></p>\n"  +
                    "    </div>";
        }
        return html;
    }

    public String getHtmlOrders(HttpSession session)
    {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("userId", session.getAttribute("user_id"));
        jsonObject.put("userPassword", session.getAttribute("password"));
        String jsonString = jsonObject.toString();


        ArrayList<Map<String, Object>> orders = postRequestToArray(zuulPath + "order-service" + "/getOrders", jsonString);

        if(orders == null) return "Aucune commande";

        String html = "";
        String montant;
        for (Map<String, Object> order : orders) {
            if(order.get("prix_total") != null) montant = "Montant : " + order.get("prix_total");
            else montant = "Commande en cours";
            html += "<div class=\"column\">\n" +
                    "        <div class=\"card\">\n" +
                    "            <div class=\"container\">\n" +
                    "                <h2>Commande " + order.get("num_commande") + "</h2>\n" +
                    "                <p class=\"title\">Statut : " + order.get("statut") + "</p>\n" +
                    "                <p>Livraison : " + order.get("forfait") + "</p>\n" +
                    "                <p><button disabled=\"\" class=\"button\" >" + montant + "</button></p>\n" +
                    "            </div>\n" +
                    "        </div>\n" +
                    "    </div>";
        }

        return html;
    }


}
