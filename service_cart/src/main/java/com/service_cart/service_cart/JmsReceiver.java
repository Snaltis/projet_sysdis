package com.service_cart.service_cart;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;
import org.springframework.stereotype.Component;
import javax.jms.ConnectionFactory;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Component
public class JmsReceiver {

    @Autowired
    cartItemRepository _cartItemRepository;

    @Value("${zuul.path}")
    private String zuulPath;

    @Bean
    public JmsListenerContainerFactory<?> myFactory(ConnectionFactory connectionFactory,
                                                    DefaultJmsListenerContainerFactoryConfigurer configurer) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        // This provides all boot's default to this factory, including the message converter
        configurer.configure(factory, connectionFactory);
        // You could still override some of Boot's default if necessary.
        return factory;
    }

    @Bean // Serialize message content to json using TextMessage
    public MessageConverter jacksonJmsMessageConverter() {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setTargetType(MessageType.TEXT);
        converter.setTypeIdPropertyName("_type");
        return converter;
    }

    @JmsListener(destination = "toRemoveCart", containerFactory = "myFactory")
    public void receiveMessage(String jsonString) {
        System.out.println("Received by jms <" + jsonString + ">");
        //ServiceCartController.removeItemFromCart(data);


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
