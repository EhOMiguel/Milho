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

        System.out.println("Tipo de conteúdo recebido 2: " + arquivoJson.getContentType());
        System.out.println("Tipo de conteúdo recebido 1: " + arquivoAssinado.getContentType());

        if (arquivoAssinado == null || !"application/pdf".equals(arquivoAssinado.getContentType())) {
            System.out.println("Tipo de conteúdo recebido 1: " + arquivoAssinado.getContentType());
            return "Arquivo PDF inválido.";
        }
        if (arquivoJson == null || !"application/json".equals(arquivoJson.getContentType())) {
            System.out.println("Tipo de conteúdo recebido 2: " + arquivoJson.getContentType());
            return "Arquivo JSON inválido.";
        }

        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> dados = mapper.readValue(arquivoJson.getInputStream(), new TypeReference<Map<String, Object>>() {});

        String token = (String) dados.get("token");
        // Corrigir a chave de assinatura conforme o JSON fornecido
        String assStr = (String) dados.get("ass");
        

        BigInteger assinatura = new BigInteger(assStr, 16); // Converte a string hexadecimal para BigInteger
        System.out.println("assinatura convertida: "+assinatura);

        CalculoHash calculoHash = new CalculoHash();
        BigInteger hashArquivo = calculoHash.calcular(arquivoAssinado);

        PegaChavePublica pegaChavePublica = new PegaChavePublica();
        ChavePublica chavePublica = pegaChavePublica.trazerChave(token);

        BigInteger e = chavePublica.getE();
        BigInteger n = chavePublica.getN();

        Descriptografia descriptografia = new Descriptografia();
        BigInteger hashAssinatura = descriptografia.descriptografar(assinatura, e, n);

        System.out.println(descriptografia.comparar(hashArquivo, hashAssinatura));
        if (descriptografia.comparar(hashArquivo, hashAssinatura) == true) {
            return "Integridade verificada :)";
        }

        return "Integridade corrompida :(";
    }
}
