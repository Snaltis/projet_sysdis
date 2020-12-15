package com.get_livraisons_api.get_livraisons;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import javax.websocket.server.PathParam;
import java.util.List;

@RestController
public class GetLivraisonsController {
    @Autowired
    livraisonRepository _livraisonRepository;

    @GetMapping("/getShippingRates")
    public List<livraisonModel> index(HttpSession session){
        System.err.println(session.getId());
        return _livraisonRepository.findAll();

    }

    @GetMapping("/getShippingRateByName")
    public float show(@PathParam("name") String name){
        if (name == null || _livraisonRepository.findByName(name) == null) return _livraisonRepository.findByName("normal").getPrix();
        return _livraisonRepository.findByName(name).getPrix();
    }
}
