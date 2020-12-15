package com.service_cart.service_cart;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RestController
public class ServiceCartController {
    @Autowired
    cartItemRepository _cartItemRepository;

    @Value("${zuul.path}")
    private String zuulPath;

    @PutMapping(path = "/addItem")
    public int addItemToCart(@RequestBody String jsonString) {
        Map<String, Object> toAddToCart = gsonToArray(jsonString).get(0);
        cartItemModel itemToAdd = new cartItemModel();
        int numProduit = Integer.parseInt((String)toAddToCart.get("numProduit"));
        int numCommande = getNumCommandeFromJsonUser(jsonString);
        Integer numCommandeProduit = _cartItemRepository.findIdByCommandeProduit(numCommande, numProduit);
        Integer quantite;
        if(numCommandeProduit != null) {
            itemToAdd.setNum_commande_produit(numCommandeProduit);
            quantite = _cartItemRepository.findQuantiteById(numCommandeProduit);
            quantite += Integer.parseInt((String) toAddToCart.get("quantite"));
        }
        else
            quantite = Integer.parseInt((String)toAddToCart.get("quantite"));

        System.err.println(zuulPath + "stock-service" + "/getStockByProduct?id=" + numProduit);

        int stock = Integer.parseInt(getRequestToValue(zuulPath + "stock-service" + "/getStockByProduct?id=" + numProduit));
        if(stock < quantite) return stock;
        itemToAdd.setNum_commande(numCommande);
        itemToAdd.setNum_produit(numProduit);
        itemToAdd.setQuantite(quantite);

        _cartItemRepository.save(itemToAdd);

        return -1;
    }

    //Pas de DELETE car il faut les credentials
    @PostMapping(path = "/removeItem")
    public void removeItemFromCart(@RequestBody String jsonString)
    {
        Map<String, Object> toRemoveFromCart = gsonToArray(jsonString).get(0);
        int numProduit = Integer.parseInt((String)toRemoveFromCart.get("numProduit"));
        int numCommande = getNumCommandeFromJsonUser(jsonString);
        Integer numCommandeProduit = _cartItemRepository.findIdByCommandeProduit(numCommande, numProduit);

        if(numCommandeProduit == null) return;
        Integer quantite = _cartItemRepository.findQuantiteById(numCommandeProduit);
        if(quantite > 1 && toRemoveFromCart.get("removeAll").equals("false")) {
            cartItemModel itemToRemove = new cartItemModel();
            itemToRemove.setNum_commande_produit(numCommandeProduit);
            itemToRemove.setNum_commande(numCommande);
            itemToRemove.setNum_produit(numProduit);
            itemToRemove.setQuantite(--quantite);
            _cartItemRepository.save(itemToRemove);
        }
        else
        {
            _cartItemRepository.deleteById(numCommandeProduit);
        }
    }

    //Pas de GET car il faut les credentials
    @PostMapping(path = "/getCart")
    public ArrayList<Map<String, Object>> getCart(@RequestBody String jsonString)
    {
        ArrayList<Map<String, Object>> cart = new ArrayList<>();
        for (cartItemModel cim : _cartItemRepository.findByNumCommande(getNumCommandeFromJsonUser(jsonString))) {
            Map<String, Object> item = getRequestToArray(zuulPath + "product-service" + "/getProduct?id=" + cim.getNum_produit()).get(0);
            item.put("quantite", String.valueOf(Integer.parseInt(String.valueOf(cim.getQuantite()))));
            cart.add(item);
        }

        return cart;
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

    public int getNumCommandeFromJsonUser(String jsonString)
    {
        return Math.round(Math.round((Double)postRequestToArray(zuulPath + "order-service" + "/getOrder", jsonString).get(0).get("num_commande")));
    }
}
