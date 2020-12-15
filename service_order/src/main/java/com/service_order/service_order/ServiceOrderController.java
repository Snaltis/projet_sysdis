package com.service_order.service_order;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
//@RequestMapping(path="/getOrder")
public class ServiceOrderController {

    @Autowired
    orderRepository _orderRepository;

    //Pas GET car il faut les credentials
    @PostMapping(path="/getOrders")
    public List<orderModel> getOrdersFromUser(@RequestBody String json) {
        System.err.print("\njson : " + json);
        Map<String, Object> request = jsonToArray(json).get(0);

        return _orderRepository.findAllOrdersFromUser((String)request.get("userId"), (String)request.get("userPassword"));
    }

    //Pas GET car il faut les credentials
    @PostMapping(path="/getOrder")
    public orderModel getOrderFromUser(@RequestBody String json)
    {
        System.err.print("\njson : " + json);
        Map<String, Object> request = jsonToArray(json).get(0);

        orderModel commande = _orderRepository.findByUserAndStatut((String)request.get("userId"), (String)request.get("userPassword"), "NON PAYE");

        if(commande == null)
        {
            commande = new orderModel();
            commande.setNum_commande(0);
            commande.setNum_client(Integer.parseInt((String)request.get("userId")));
            if(request.get("forfait") != null)
                commande.setForfait((String)request.get("forfait"));
            commande.setStatut("NON PAYE");
            _orderRepository.save(commande);
            //Pour etre sur que la commande est dans la DB
            return getOrderFromUser(json);
        }

        if(request.get("forfait") != null) {
            commande.setForfait((String) request.get("forfait"));
            _orderRepository.save(commande);
        }
        return commande;
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
}
