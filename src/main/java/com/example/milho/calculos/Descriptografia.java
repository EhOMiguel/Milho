package com.example.milho.calculos;

import org.springframework.web.multipart.MultipartFile;
import java.math.BigInteger;

public class Descriptografia {
    public BigInteger descriptografar(BigInteger hashAssinatura, BigInteger chavePublicaA, BigInteger chavePublicaB){

        BigInteger hash = hashAssinatura.modPow(chavePublicaA, chavePublicaB);

        return hash;
    }

    public boolean comparar(BigInteger hashArquivo, BigInteger hashAssinatura){

        if(hashArquivo == hashAssinatura){
            return true;
        }

        return false;
    }
}
