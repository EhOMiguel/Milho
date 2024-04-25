package com.example.milho.calculos;

import org.springframework.web.multipart.MultipartFile;
import java.math.BigInteger;

public class Descriptografia {
    public BigInteger descriptografar(BigInteger hashAssinatura, BigInteger chavePublicaA, BigInteger chavePublicaB){

        BigInteger hash = hashAssinatura.modPow(chavePublicaA, chavePublicaB);
        System.out.println("Resultado conta: "+hash);

        return hash;
    }

    public boolean comparar(BigInteger hashArquivo, BigInteger hashAssinatura){

        System.out.println("HashArquivo: "+ hashArquivo);
        System.out.println("\nhashAssinatura: "+ hashAssinatura);

        if(hashArquivo.equals(hashAssinatura)){
            return true;
        }

        return false;
    }
}
