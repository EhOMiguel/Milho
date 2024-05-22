package com.example.milho.calculos;

import org.springframework.web.multipart.MultipartFile;

import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.signatures.SignatureUtil;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class CalculoHash {
    public BigInteger calcular(byte[] arquivo_ass) {
        try {
            PdfReader reader = new PdfReader(new ByteArrayInputStream(arquivo_ass));
            PdfDocument pdfDoc = new PdfDocument(reader);

            SignatureUtil signUtil = new SignatureUtil(pdfDoc);
            String signatureName = signUtil.getSignatureNames().get(0);
            PdfArray byteRange = signUtil.getSignature(signatureName).getByteRange();

            System.out.println("ByteRange: " + byteRange);

            byte[] fileContent = arquivo_ass;
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            // Lendo os intervalos especificados pelo ByteRange
            long start1 = byteRange.getAsNumber(0).longValue();
            long end1 = start1 + byteRange.getAsNumber(1).longValue();
            long start2 = byteRange.getAsNumber(2).longValue();
            long end2 = start2 + byteRange.getAsNumber(3).longValue();

            // Primeira parte
            digest.update(fileContent, (int) start1, (int) (end1 - start1));
            // Segunda parte
            digest.update(fileContent, (int) start2, (int) (end2 - start2));

            byte[] documentHash = digest.digest();
            BigInteger hashDecimal = new BigInteger(1, documentHash);
            System.out.println("HASH ASSINADA: " + hashDecimal);

            reader.close();

            // Converte para string para extrair os primeiros 5 dígitos
            String hashString = hashDecimal.toString();
            String firstFiveDigits = hashString.length() > 5 ? hashString.substring(0, 5) : hashString;

            // Converte os primeiros 5 dígitos de volta para BigInteger
            BigInteger firstFiveDigitsBigInteger = new BigInteger(firstFiveDigits);

            System.out.println("HASH DO ARTHUR 5 digitos" + firstFiveDigitsBigInteger);

            // Retorna apenas os primeiros 5 dígitos em formato BigInteger
            return firstFiveDigitsBigInteger;

        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null; // Retorna null em caso de exceção
        }
    }
}