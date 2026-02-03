package org.example.bdd.steps;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import coding.retail.autocomplete.AutoCompleteProvider;
import org.junit.jupiter.api.Assertions;

import java.util.List;
import java.util.Map;

public class SearchSteps {
    private AutoCompleteProvider provider = new AutoCompleteProvider();
    private List<String> actualSuggestions;

    @Given("the following products exist in the catalog:")
    public void setupCatalog(DataTable table) {
        List<Map<String, String>> rows = table.asMaps();
        for (Map<String, String> row : rows) {
            provider.insert(row.get("name"), Integer.parseInt(row.get("popularity")));
        }
    }

    @When("I type {string} in the search bar")
    public void typePrefix(String prefix) {
        actualSuggestions = provider.getSuggestions(prefix);
    }

    @Then("the suggestions should be in this order:")
    public void verifyOrder(List<String> expected) {
        Assertions.assertEquals(expected, actualSuggestions);
    }
}
