package com.example.milho.calculos;

import org.springframework.web.multipart.MultipartFile;
import java.math.BigInteger;

public class Descriptografia {
    public BigInteger descriptografar(String chavesPublicas, MultipartFile hashAssinaturaProv){

        BigInteger hashAssinatura = new BigInteger("123456789");
        BigInteger chavePublicaA = new BigInteger("987654321");
        BigInteger chavePublicaB = new BigInteger("111111111");

        BigInteger hash = hashAssinatura.modPow(chavePublicaA, chavePublicaB);

        return hash;
    }

    public boolean comparar(BigInteger hashArquivo, MultipartFile hashAssinatura){

        if(hashArquivo == hashAssinatura){
            return true;
        }

        return false;
    }
}
