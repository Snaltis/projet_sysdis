package com.service_checkout.service_checkout;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;


@RestController
@RequestMapping("checkout")
public class ServiceCheckoutController {

    @Autowired
    CommandeProduitRepository _commandeProduitRepository;

    @Value("${zuul.path}")
    private String zuulPath;

    @PostMapping
    public String doCheckOut(@RequestBody String json) throws JSONException {
        Map<String, Object> request = jsonToArray(json).get(0);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("userId", request.get("userId"));
        jsonObject.put("userPassword", request.get("userPassword"));
        String jsonString = jsonObject.toString();
        int numCommande = Math.round(Math.round((Double)postRequestToArray(zuulPath + "order-service" + "/getOrder", jsonString).get(0).get("num_commande")));

        double prixTotal = _commandeProduitRepository.findPrixTotalByNumCommande(numCommande);

        int stock;
        for(commandeProduitModel cpm : _commandeProduitRepository.findByNumCommande(numCommande))
        {
            stock = Integer.parseInt(getRequestToValue(zuulPath + "stock-service" + "/getStockByProduct?id=" + cpm.getNum_produit()));
            if(stock < cpm.getQuantite())
                return String.valueOf(cpm.getNum_produit());
        }

        if(prixTotal <= _commandeProduitRepository.findSoldeUser((String)request.get("userId"), (String)request.get("userPassword"))) {
            _commandeProduitRepository.updateCommande(numCommande, prixTotal);
            _commandeProduitRepository.updateSoldeUser((String)request.get("userId"), prixTotal);
            for(commandeProduitModel cpm : _commandeProduitRepository.findByNumCommande(numCommande))
            {
                _commandeProduitRepository.updateQuantiteProduit(cpm.getNum_produit(), cpm.getQuantite());
                System.err.println(getRequestToValue(zuulPath + "stock-service" + "/getStockByProduct?id=" + cpm.getNum_produit()));
            }
            return "ACK";
        }
        return "FAIL";
    }

    public ArrayList<Map<String, Object>> jsonToArray(String gson)
    {
        if(gson.startsWith("["))
            gson = "{\"_table\":" + gson + ",\"_content\":[]}";
        else
            gson = "{\"_table\":[" + gson + "],\"_content\":[]}";

        ArrayList<Map<String, Object>> dataTable = Table.Deseriliaze(gson).getTable();
        return dataTable;
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
        return jsonToArray(postRequestToValue(uri,json));
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

}

