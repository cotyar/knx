package com.knx.Impl;

import com.knx.External.NonTranslatableException;
import com.knx.External.Translator;
import com.knx.NonAlphabeticException;
import com.knx.WordCounter;
import com.knx.WordValidator;
import org.junit.*;

import java.util.Locale;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;


public class WordCounterImplTest {

    private WordValidator wordValidator;
    private Translator translator;
    private WordCounter wordCounter;

    @Before
    public void setUp() throws Exception {
        translator = mock(Translator.class);
        wordValidator = s -> {
            if (s.matches("[A-Za-z]+")) return s.toLowerCase();
            else throw new NonAlphabeticException();
        };

        wordCounter = new WordCounterImpl(wordValidator, translator);
    }

    @After
    public void tearDown() {
        translator = null;
        wordValidator = null;
    }

    @Test
    public void testAddEnglishWord() throws Exception {
        final String tw = "Test";
        when(translator.translate(anyString())).thenReturn(tw.toLowerCase(Locale.ENGLISH));

        wordCounter.addWord(tw);
    }

    @Test
    public void testAddAndGetEnglishWord() throws Exception {
        final String tw = "Test";
        when(translator.translate(anyString())).thenReturn(tw.toLowerCase(Locale.ENGLISH));

        wordCounter.addWord(tw);
        final int count = wordCounter.getWordCount(tw);
        assertEquals(1, count);
    }

    @Test
    public void testGetMissingEnglishWord() throws Exception {
        final String tw = "Test";
        when(translator.translate(anyString())).thenReturn(tw.toLowerCase(Locale.ENGLISH));

        final int count = wordCounter.getWordCount(tw);
        assertEquals(0, count);
    }

    @Test
    public void testAddAndGetMissingEnglishWord() throws Exception {
        final String tw = "Test";
        when(translator.translate(anyString())).thenReturn(tw.toLowerCase(Locale.ENGLISH));

        wordCounter.addWord(tw);
        final int count = wordCounter.getWordCount(tw + "s");
        assertEquals(0, count);
    }

    @Test(expected = NonAlphabeticException.class)
    public void testAddNonAlpha() throws Exception {
        final String tw = "Test1";
        when(translator.translate(anyString())).thenReturn(tw.toLowerCase(Locale.ENGLISH));

        wordCounter.addWord(tw);
    }

    @Test(expected = NonTranslatableException.class)
    public void testTranslationExceptionPropagated() throws Exception {
        final String tw = "Test";
        when(translator.translate(anyString())).thenThrow(NonTranslatableException.class);

        wordCounter.addWord(tw);
    }

    @Test
    public void testMultiLang() throws Exception {
        when(translator.translate(eq("flower"))).thenReturn("flower");
        when(translator.translate(eq("flor"))).thenReturn("flower");
        when(translator.translate(eq("blume"))).thenReturn("flower");

        wordCounter.addWord("Flower");
        wordCounter.addWord("Flor");
        wordCounter.addWord("Blume");

        final int count = wordCounter.getWordCount("Flower");
        assertEquals(3, count);
    }

    @Test
    public void testMultiLangDuplicates() throws Exception {
        when(translator.translate(eq("flower"))).thenReturn("flower");
        when(translator.translate(eq("flor"))).thenReturn("flower");
        when(translator.translate(eq("blume"))).thenReturn("flower");

        wordCounter.addWord("flower");
        wordCounter.addWord("flor");
        wordCounter.addWord("blume");
        wordCounter.addWord("flOwer");
        wordCounter.addWord("flOr");
        wordCounter.addWord("blUme");
        wordCounter.addWord("Flor");
        wordCounter.addWord("Blume");

        final int count = wordCounter.getWordCount("Flower");
        assertEquals(3, count);
    }

    @Test
    public void testMultiLangDuplicates10000() throws Exception {
        when(translator.translate(eq("flower"))).thenReturn("flower");
        when(translator.translate(eq("flor"))).thenReturn("flower");
        when(translator.translate(eq("blume"))).thenReturn("flower");

        for(int i = 0; i < 10000; i++) {
            wordCounter.addWord("flower");
            wordCounter.addWord("flor");
            wordCounter.addWord("blume");
            wordCounter.addWord("flOwer");
            wordCounter.addWord("flOr");
            wordCounter.addWord("blUme");
            wordCounter.addWord("Flor");
            wordCounter.addWord("Blume");
        }

        final int count = wordCounter.getWordCount("Flower");
        assertEquals(3, count);
    }}