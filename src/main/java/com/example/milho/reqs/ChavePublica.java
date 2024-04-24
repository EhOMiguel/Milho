package com.example.milho.reqs;

import java.math.BigInteger;

public class ChavePublica {
    private BigInteger e;
    private BigInteger n;

    // Getters e setters
    public BigInteger getE() {
        return e;
    }

    public void setE(BigInteger e) {
        this.e = e;
    }

    public BigInteger getN() {
        return n;
    }

    public void setN(BigInteger n) {
        this.n = n;
    }
}
