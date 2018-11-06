package com.knx.Impl;

import com.knx.External.Translator;
import com.knx.NonAlphabeticException;
import com.knx.WordCounter;
import com.knx.WordValidator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

import static junit.framework.TestCase.assertEquals;
import static org.junit.runners.Parameterized.Parameters;
import static org.mockito.Mockito.*;

@RunWith(Parameterized.class)
public class WordCounterImplCombinationsTest {

    static private<V> Map<String, V> toMap(Object[][] kv) {
        return Arrays.asList(kv).stream().collect(Collectors.toMap(a -> (String) a[0], a -> (V) a[1]));
    }

    static private Object toExpected(Object[][] kv) {
        return toMap(kv);
    }

    static private Object toTestData(Object[][] kv) {
        return toMap(kv);
    }

    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
            { toExpected(new Object[][]{{ "flower", 1 }}), toTestData(new Object[][]{{"flower", new String[]{"flower"}}}) },
            { toExpected(new Object[][]{{ "flower", 2 }}), toTestData(new Object[][]{{"flower", new String[]{"flower", "blume"}}}) },
            { toExpected(new Object[][]{{ "flower", 3 }}), toTestData(new Object[][]{{"flower", new String[]{"flower", "flor", "blume"}}}) },

            { toExpected(new Object[][]{{ "flower", 1 }, { "myflower", 1 }}),
                    toTestData(new Object[][]{{"flower", new String[]{"flower"}}, {"myflower", new String[]{"myflower"}}}) },
            { toExpected(new Object[][]{{ "flower", 2 }, { "myflower", 2 }}),
                    toTestData(new Object[][]{{"flower", new String[]{"flower", "blume"}}, {"myflower", new String[]{"myflower", "myblume"}}}) },
            { toExpected(new Object[][]{{ "flower", 3 }, { "myflower", 3 }}),
                    toTestData(new Object[][]{{"flower", new String[]{"flower", "flor", "blume"}}, {"myflower", new String[]{"myflower", "myflor", "myblume"}}}) }
        });
    }

    private final Map<String, Integer> expected;
    private final Map<String, String[]> testData;

    public WordCounterImplCombinationsTest(
            final Map<String, Integer> expected,
            final Map<String, String[]> testData) {
        this.expected = expected;
        this.testData = testData;
    }

    @Test
    public void testCase() throws Exception {
        final Translator translator = mock(Translator.class);
        final WordValidator wordValidator = s -> {
            if (s.matches("[A-Za-z]+")) return s.toLowerCase();
            else throw new NonAlphabeticException();
        };

        // Setting up validator
        for (final Map.Entry<String, String[]> kv : testData.entrySet()) {
            for (final String v : kv.getValue()) {
                when(translator.translate(eq(v))).thenReturn(kv.getKey());
            }
        }

        final WordCounter wordCounter = new WordCounterImpl(wordValidator, translator);

        // Adding words
        for (final Map.Entry<String, String[]> kv : testData.entrySet()) {
            for (final String v : kv.getValue()) {
                wordCounter.addWord(v);
            }
        }

        // Validating expected
        for (final Map.Entry<String, Integer> e : expected.entrySet()) {
            final int count = wordCounter.getWordCount(e.getKey());
            assertEquals(e.getValue().intValue(), count);
        }
    }
}

