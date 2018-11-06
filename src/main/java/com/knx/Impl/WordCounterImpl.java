package com.knx.Impl;

import com.knx.External.NonTranslatableException;
import com.knx.External.Translator;
import com.knx.NonAlphabeticException;
import com.knx.WordCounter;
import com.knx.WordValidator;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WordCounterImpl implements WordCounter {
    private final WordValidator wordValidator;
    private final Translator translator;

    // For non-concurrent scenarios a normal HashMap<> would also work
    private final ConcurrentHashMap<String, Set<String>> words = new ConcurrentHashMap<>();

    public WordCounterImpl(final WordValidator wordValidator, final Translator translator) {
        this.wordValidator = wordValidator;
        this.translator = translator;
    }

    @Override
    public void addWord(String word) throws NonAlphabeticException, NonTranslatableException {
        final String normalizedWord = wordValidator.validateAndNormalize(word);

        final String translatedWord = translator.translate(normalizedWord).toLowerCase(Locale.ENGLISH);

        words.compute(translatedWord,
            (k, v) -> (v == null)
                ? Collections.singleton(normalizedWord)
                : //Collections.<String>unmodifiableSet(
                    Stream.concat(v.stream(), Stream.of(normalizedWord)).collect(Collectors.toCollection(HashSet::new)));
                    // There is a potential performance hit here with Set recreation
                    // but assuming a limited and small number of languages available it shouldn't be
                    // a real problem in practise and working with immutable sets should help with correctness and
                    // read side concurrency.
    }

    @Override
    public int getWordCount(String word) throws NonAlphabeticException {
        final String normalizedWord = wordValidator.validateAndNormalize(word);
        return words.getOrDefault(normalizedWord, new HashSet<>()).size();
    }
}
