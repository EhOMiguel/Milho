package com.example.milho.reqs;

import org.springframework.web.client.RestTemplate;

public class PegaChavePublica {

    public String trazerChave(int token) {
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://localhost:(porta)/chavesPublica";
        
        return restTemplate.getForObject(url + "?token=" + token, String.class);
    }
    
}