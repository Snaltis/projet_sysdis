package com.service_tva.get_tva;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.websocket.server.PathParam;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RestController
public class GetTvaController {
    @Autowired
    tvaRepository _tvaRepository;

    @GetMapping("/getTvaByCategory")
    public String getTvaByCategory(@PathParam("id") String id){
        if (id == null) return null;
        Integer categorieId = Integer.parseInt(id);
        return Float.toString(_tvaRepository.findById(categorieId).get().getTVA());
    }

    @GetMapping("/getTvaByPoduct")
    public String getTvaByPoduct(@PathParam("id") String id){
        if (id == null) return null;
        System.err.println(id);
        HttpClient client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2).build();
        HttpRequest request = HttpRequest.newBuilder(URI.create("http://localhost:8082/getProduct?id=" + id))
                .header("Content-Type", "application/json")
                .GET().build();
        CompletableFuture<HttpResponse<String>> response = client.sendAsync(request,HttpResponse.BodyHandlers.ofString());

        String gson ="";
        ArrayList<Map<String, Object>> dataTable;


        try {
            gson = response.get().body();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        gson = "{\"_table\":[" + gson + "],\"_content\":[]}";
        System.err.println(gson);

        dataTable = Tables.Deseriliaze(gson).getTable();

        System.err.println(dataTable.get(0));
        Integer categorieId = Integer.parseInt((String)dataTable.get(0).get("id_categorie"));
        System.out.println(categorieId);

        return Float.toString(_tvaRepository.findById(categorieId).get().getTVA());
    }

}
