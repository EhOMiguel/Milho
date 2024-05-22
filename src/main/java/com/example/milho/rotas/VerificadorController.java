package com.example.milho.rotas;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
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
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.signatures.SignatureUtil;

@RestController
public class VerificadorController {

    @PostMapping("/verificar")
    public String verificarArquivo(@RequestParam("arquivoAssinado") MultipartFile arquivoAssinado
                                /*   @RequestParam("arquivoJson") MultipartFile arquivoJson*/)
                                   throws StreamReadException, DatabindException, IOException {

        // System.out.println("Tipo de conteúdo recebido 2: " + arquivoJson.getContentType());
        System.out.println("Tipo de conteúdo recebido 1: " + arquivoAssinado.getContentType());

        if (arquivoAssinado == null || !"application/pdf".equals(arquivoAssinado.getContentType())) {
            System.out.println("Tipo de conteúdo recebido 1: " + arquivoAssinado.getContentType());
            return "Arquivo PDF inválido.";
        }
        // if (arquivoJson == null || !"application/json".equals(arquivoJson.getContentType())) {
        //     System.out.println("Tipo de conteúdo recebido 2: " + arquivoJson.getContentType());
        //     return "Arquivo JSON inválido.";
        // }

        // ObjectMapper mapper = new ObjectMapper();
        // Map<String, Object> dados = mapper.readValue(arquivoJson.getInputStream(), new TypeReference<Map<String, Object>>() {});

        // String token = (String) dados.get("token");
        // Corrigir a chave de assinatura conforme o JSON fornecido
        // String assStr = (String) dados.get("ass");
        

        // BigInteger assinatura = new BigInteger(assStr, 16); // Converte a string hexadecimal para BigInteger


        // calculo da ASS
        byte[] arquivoAssinadoByte = arquivoAssinado.getBytes();
        PdfReader reader = new PdfReader(new ByteArrayInputStream(arquivoAssinadoByte));
        PdfDocument pdfDoc = new PdfDocument(reader);
        SignatureUtil signUtil = new SignatureUtil(pdfDoc);
        String signatureName = signUtil.getSignatureNames().get(0);
        PdfString contents = signUtil.getSignature(signatureName).getContents();

        // Converte a assinatura de PdfString para byte[]
        byte[] signatureBytes = contents.getValueBytes();

        byte[] teste = truncateZeros(signatureBytes);

        System.out.println("Assinatura Verificada: " + bytesToHex(teste));

        BigInteger assinatura = new BigInteger(1, teste);

        System.out.println("assinatura convertida: "+assinatura);




        //Calculo do token
        String userTokenString = null;

        try {

            PdfDictionary root = pdfDoc.getCatalog().getPdfObject();
            PdfDictionary acroForm = root.getAsDictionary(PdfName.AcroForm);
            PdfArray fields = acroForm.getAsArray(PdfName.Fields);
            PdfDictionary firstField = fields.getAsDictionary(0);
            PdfObject token = firstField.getAsDictionary(PdfName.V).get(new PdfName("UserToken"));
            userTokenString = ((PdfString) token).toUnicodeString();
            
            if (userTokenString != null) {
                System.out.println("UserToken: " + userTokenString);
            } else {
                System.out.println("UserToken not found.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        



        //calculo da hash do arquivo
        CalculoHash calculoHash = new CalculoHash();
        BigInteger hashArquivo = calculoHash.calcular(arquivoAssinadoByte);

        PegaChavePublica pegaChavePublica = new PegaChavePublica();
        ChavePublica chavePublica = pegaChavePublica.trazerChave(userTokenString);

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


    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    private static byte[] truncateZeros(byte[] array) {
        int length = array.length;
        while (length > 0 && array[length - 1] == 0) {
            length--;
        }
        return Arrays.copyOf(array, length);
    }
}
