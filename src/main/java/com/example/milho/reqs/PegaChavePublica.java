package com.example.milho.reqs;

import org.springframework.web.client.RestTemplate;

public class PegaChavePublica {
    public ChavePublica trazerChave(String token) {
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://localhost:8080/chavesPublica"; // Substitua 8080 pela porta real, se necessário

        return restTemplate.getForObject(url + "?token=" + token, ChavePublica.class);
    }
}
