package com.example.milho;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;



@RestController
public class MensagemController {

    @PostMapping("/verificar")
    public String sendMessage() {
        return "Olá, bem-vindo";
    }
}
