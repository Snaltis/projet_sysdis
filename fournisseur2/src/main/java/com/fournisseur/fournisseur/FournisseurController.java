package com.fournisseur.fournisseur;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.websocket.server.PathParam;
import java.util.ArrayList;
import java.util.Map;

@RestController
public class FournisseurController {

    @Autowired
    ArticleRepository _articleRepository;

    @GetMapping("/getPrice")
    public String getPriceByProduct(@PathParam("id") String id){
        if (id == null) return null;

        return _articleRepository.findPrix(id);
    }

    @GetMapping("/getQuantite")
    public String getQuantiteByProduct(@PathParam("id") String id){
        if (id == null) return null;

        return _articleRepository.findQuantite(id);
    }

    @PostMapping("/buy")
    public void buyProduct(@RequestBody String json)
    {
        Map<String, Object> data = gsonToArray(json).get(0);
        int quantite = Math.round(Math.round((Double)data.get("quantite")));
        int quantiteDispo = Integer.parseInt(_articleRepository.findQuantite((String)data.get("num_produit")));
        //pour ne pas avoir des nbres negatifs
        if(quantite > quantiteDispo) quantite = quantiteDispo;
        _articleRepository.updateQuantite(Integer.parseInt((String)data.get("num_produit")), quantite);
    }

    public ArrayList<Map<String, Object>> gsonToArray(String gson)
    {
        if(gson.startsWith("["))
            gson = "{\"_table\":" + gson + ",\"_content\":[]}";
        else
            gson = "{\"_table\":[" + gson + "],\"_content\":[]}";

        ArrayList<Map<String, Object>> dataTable = Tables.Deseriliaze(gson).getTable();
        return dataTable;
    }
}
