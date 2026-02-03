package org.example.tdd;

import coding.retail.autocomplete.AutoCompleteProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class AutoCompleteProviderTest {

    @Test
    void testTopSuggestionsByFrequency() {
        AutoCompleteProvider search = new AutoCompleteProvider();
        search.insert("Milk", 100);
        search.insert("Milkshake", 50);
        search.insert("Mild Cheddar", 150);

        List<String> results = search.getSuggestions("Mil");

        // "Mild Cheddar" should be first because freq 150 > 100
        Assertions.assertEquals("Mild Cheddar", results.get(0));
        Assertions.assertEquals(3, results.size());
    }
}
