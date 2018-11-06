package com.knx;

import com.knx.External.NonTranslatableException;

public interface WordCounter {
    void addWord(String word) throws NonAlphabeticException, NonTranslatableException;
    int getWordCount(String word) throws NonAlphabeticException;
}

