package org.example.bdd.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import coding.retail.categoryHierarchy.CategoryHierarchyService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class CategorySteps {
    CategoryHierarchyService service = new CategoryHierarchyService();
    List<String> actualSubcategories;

    @Given("the following category structure:")
    public void setupCategories(List<List<String>> table) {
        for (List<String> row : table) {
            service.addRelationship(row.get(0), row.get(1));
        }
    }

    @When("I request all sub-categories for {string}")
    public void askForSubcategories(String category) {
        // This step can be used to trigger any necessary actions before verification
        actualSubcategories=service.getAllSubCategories(category);
    }


    @Then("the list should contain {string}, {string}, {string}, and {string}")
    public void verifySubcategories(String c1, String c2, String c3, String c4) {

        assertTrue(actualSubcategories.containsAll(List.of(c1, c2, c3, c4)));
    }
}
