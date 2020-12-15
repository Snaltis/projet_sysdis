package com.show_product.show_product_api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import java.util.List;

@RestController
public class ShowProductController {
    @Autowired
    productRepository _productRepository;

    @GetMapping("/getProducts")
    public List<productModel> index(){
        return _productRepository.findAll();
    }

    @GetMapping("/getProduct")
    public productModel show(@PathParam("id") String id){
        if (id == null) return null;
        Integer productId = Integer.parseInt(id);
        return _productRepository.findById(productId).get();
    }
}
