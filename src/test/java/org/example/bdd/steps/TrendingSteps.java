package org.example.bdd.steps;

import coding.retail.trendingProducts.TrendingProducts;
import io.cucumber.java.en.*;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class TrendingSteps {
    private TrendingProducts trending;

    @Given("the trending window size is {int}")
    public void givenK(int k) {
        trending = new TrendingProducts(k);
    }


    @When("I add these products: {string}")
    public void i_add_these_products(String productList) {
        // Split the "Milk,Milk,Bread" string from the feature file
        String[] products = productList.split(",");
        for (String p : products) {
            trending.addEvent(p.trim());
        }
    }

    @Then("the top product should be {string}")
    public void thenVerifyTop(String expected) {
        List<String> topK = trending.getTopK();
        assertFalse(topK.isEmpty(), "Top K list should not be empty");
        assertEquals(expected, topK.get(0));
    }
}