package com.knx.External;

public interface Translator {
    String translate(String word) throws NonTranslatableException;
}
