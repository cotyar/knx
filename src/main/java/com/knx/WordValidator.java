package com.knx;

// Introducing this interface as a separate word validation (as per the test description alphabetic) entity
// The validator in general should be smart enough to deal with characters tables of other languages (not only English [A-Za-z])
// In a real-world scenario I would probably prefer to have this logic combined with the Translator service, as it naturally has the
// best knowledge if a given word can be successfully translated into English at all.
// But for now following the architecture demanded by the test task description.
// Generally speaking the implementation here may be not fully accurate as e.g. word.toLowerCase() doesn't take Locale
// from the corresponding language (which it doesn't have a clue about here) and also considers different cases as
// the same word ('flower', 'fLOwer' and 'Flower' will be considered the same but situation where they should be
// considered different are at least theoretically imaginable). The best place where this decision should be done
// is the Translator service (for example by returning a tuple of translated and 'standartized' word).
// But implementing this would again require to violate the task prescribed contract for the Translator service.
@FunctionalInterface
public interface WordValidator {
    String validateAndNormalize(String word) throws NonAlphabeticException;
}
