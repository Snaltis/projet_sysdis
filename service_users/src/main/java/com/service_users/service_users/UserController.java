package com.service_users.service_users;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.websocket.server.PathParam;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.List;

@RestController
public class UserController {


    @Autowired
    UserRepository userRepository;

    @GetMapping("/allUsers")
    public List<userModel> index(){
        return userRepository.findAll();
    }

    @GetMapping("/user")
    public userModel show(@PathParam("id") String id){
        if (id == null) return null;
        Integer userId = Integer.parseInt(id);
        return userRepository.findById(userId).get();
    }

    @PostMapping("/login")
    public String login(@PathParam("mail") String mail, @PathParam("password") String password, @PathParam("salt") String salt){

        userModel temp = userRepository.findByEmailAddress(mail);
        if(temp == null) return "WRONG_MAIL";

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(salt.getBytes());
            byte[] hashedPassword = md.digest(temp.getMdp().getBytes(StandardCharsets.UTF_8));
            if(new String(Base64.getEncoder().encode(hashedPassword)).equals(password.replace(' ', '+')))
                return Integer.toString(temp.getnum_client());
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return "WRONG_PWD";
    }

}
