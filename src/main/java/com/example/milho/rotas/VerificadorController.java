package com.example.milho.rotas;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.milho.calculos.CalculoHash;
import com.example.milho.calculos.Descriptografia;
import com.example.milho.reqs.ChavePublica;
import com.example.milho.reqs.PegaChavePublica;
import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
public class VerificadorController {

    @PostMapping("/verificar")
    public String verificarArquivo(@RequestParam("arquivoAssinado") MultipartFile arquivoAssinado,
                                   @RequestParam("arquivoJson") MultipartFile arquivoJson) throws StreamReadException, DatabindException, IOException {

        if (arquivoAssinado == null || !"application/pdf".equals(arquivoAssinado.getContentType())) {
            return "Arquivo PDF inválido.";
        }
        if (arquivoJson == null || !"application/json".equals(arquivoJson.getContentType())) {
            return "Arquivo JSON inválido.";
        }

        ObjectMapper mapper = new ObjectMapper();
            // Deserializa o JSON para um Map
        Map<String, Object> dados = mapper.readValue(arquivoJson.getInputStream(), new TypeReference<Map<String, Object>>() {});



            // Acesso aos dados deserializados
        String token = (String) dados.get("token");
        BigInteger assinatura = (BigInteger) dados.get("assinatura");

        

        CalculoHash calculoHash = new CalculoHash();
        BigInteger hashArquivo = calculoHash.calcular(arquivoAssinado);

        PegaChavePublica pegaChavePublica = new PegaChavePublica();
        ChavePublica chavePublica = pegaChavePublica.trazerChave(token);

        BigInteger e = chavePublica.getE();
        BigInteger n = chavePublica.getN();

        Descriptografia descriptografia = new Descriptografia();
        BigInteger hashAssinatura = descriptografia.descriptografar(assinatura, e, n);

        if(descriptografia.comparar(hashAssinatura, hashArquivo)){
            return "Integridade verificada :)";
        }


        return "Integridade corrompida :(";
    }
}
