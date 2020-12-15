package com.service_inventory.get_stock;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.websocket.server.PathParam;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RestController
public class GetStockController {

    @Autowired
    StockRepository _stockRepository;

    @Value("${zuul.path}")
    private String zuulPath;

    static int nbParDefaut = 10;

    @GetMapping("/getStockByProduct")
    public String getStockByProduct(@PathParam("id") String id){
        if (id == null) return null;

        String quantite = _stockRepository.findStock(id);

        if(quantite.equals("0"))
            commanderProduit(id, nbParDefaut);

        return quantite;
    }

    @GetMapping("/restock")
    public void restock(@PathParam("id") String id){
        if (id == null) return;
        commanderProduit(id, nbParDefaut);
    }

    public void commanderProduit(String numProduit, int quantite)
    {
        String lowestFournisseurNom = null;
        Double lowestFournisseurPrix, tempFournisseurPrix;
        List<String> fournisseurs;
        fournisseurs = _stockRepository.getFournisseurs();
        List<String> tempFournisseurs = new ArrayList<>(fournisseurs);
        if(fournisseurs == null) return;
        String tempQte;
        for (String fournisseur : tempFournisseurs) {
            tempQte = getRequestToValue(zuulPath + fournisseur + "/getQuantite?id=" + numProduit);

            if(tempQte.contains("\"status\":404") || tempQte.equals("") || Integer.parseInt(tempQte) <= 0){
                fournisseurs.remove(fournisseur);
            }
        }
        if(fournisseurs.isEmpty()) return;

        lowestFournisseurPrix =  Double.parseDouble(getRequestToValue(zuulPath + fournisseurs.get(0) + "/getPrice?id=" + numProduit));
        if(lowestFournisseurPrix != null)
            lowestFournisseurNom = fournisseurs.get(0);
        for (String fournisseur : fournisseurs) {
            tempFournisseurPrix = Double.parseDouble(getRequestToValue(zuulPath + fournisseur + "/getPrice?id=" + numProduit));
            if(tempFournisseurPrix != null && tempFournisseurPrix < lowestFournisseurPrix && Integer.parseInt(getRequestToValue(zuulPath + lowestFournisseurNom + "/getQuantite?id=" + numProduit)) > 0)
            {
                lowestFournisseurPrix = tempFournisseurPrix;
                lowestFournisseurNom = fournisseur;
            }
        }

        if(lowestFournisseurNom == null) return;
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("num_produit", numProduit);
            jsonObject.put("quantite", quantite);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String jsonString = jsonObject.toString();

        int quantiteDispo = Integer.parseInt(getRequestToValue(zuulPath + lowestFournisseurNom + "/getQuantite?id=" + numProduit));
        postRequestToValue(zuulPath + lowestFournisseurNom + "/buy", jsonString);
        if(quantiteDispo < quantite) {
            _stockRepository.updateQuantite(numProduit, quantiteDispo);
            commanderProduit(numProduit, quantite - quantiteDispo);
        }
        else
            _stockRepository.updateQuantite(numProduit, quantite);

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
