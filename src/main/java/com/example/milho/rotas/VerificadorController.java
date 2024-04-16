package com.example.milho.rotas;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import java.math.BigInteger;

import com.example.milho.calculos.Descriptografia;
import com.example.milho.reqs.PegaChavePublica;

@RestController
public class VerificadorController {

    @PostMapping("/verificar")
    public String verificarArquivo(@RequestParam MultipartFile arquivo) {
        if (arquivo.isEmpty()) {
            return "Por favor, envie um arquivo.";
        }
        
        PegaChavePublica pegaChavePublica = new PegaChavePublica();
        String chavePublica = pegaChavePublica.trazerChave(1234);


        Descriptografia descriptografia = new Descriptografia();
        BigInteger hashAssinatura = descriptografia.descriptografar(chavePublica, arquivo);

        if(descriptografia.comparar(hashAssinatura, arquivo)){
            return "Integridade verificada :)";
        }


        return "Integridade corrompida :(";
    }
}
