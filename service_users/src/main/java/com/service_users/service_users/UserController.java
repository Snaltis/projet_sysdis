package com.service_users.service_users;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.websocket.server.PathParam;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@RestController
public class UserController {


    @Autowired
    UserRepository userRepository;

    @PostMapping("/user")
    public userModel getUser(@RequestBody String json){
        Map<String, Object> request = gsonToArray(json).get(0);
        if (request.get("userId") == null) return null;
        return userRepository.findUser((String)request.get("userId"), (String)request.get("password"));
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


    public ArrayList<Map<String, Object>> gsonToArray(String gson)
    {
        System.err.println(gson);
        if(gson.startsWith("["))
            gson = "{\"_table\":" + gson + ",\"_content\":[]}";
        else
            gson = "{\"_table\":[" + gson + "],\"_content\":[]}";

        ArrayList<Map<String, Object>> dataTable = Tables.Deseriliaze(gson).getTable();
        return dataTable;
    }

}
