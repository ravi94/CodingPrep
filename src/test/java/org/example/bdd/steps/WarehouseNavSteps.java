package org.example.bdd.steps;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import coding.retail.WarehouseNavigator;
import org.junit.jupiter.api.Assertions;

public class WarehouseNavSteps {

//    Feature: Order Fulfillment Pathfinding
//      Scenario: Calculate optimal route for a picker
        //    Given a warehouse grid of size 3x3
        //    And there is an obstacle at (1,1)
        //    When a picker starts at (0,0) and needs to reach (2,2)
        //    Then the system should return the shortest path of 4 steps
    int[][] grid;
    WarehouseNavigator navigator ;
    int [] start;
    int [] end;

    @Given("a warehouse grid of size {int}x{int}")
    public void givenWarehouse(int rows, int cols) {
        grid = new int[rows][cols];
        navigator= new WarehouseNavigator();
    }

    @And("there is an obstacle at \\({int},{int}\\)")
    public void addObstacle(int r, int c) {
        grid[r][c] = 1; // Mark obstacle
    }

    @When("a picker starts at \\({int},{int}\\) and needs to reach \\({int},{int}\\)")
    public void whenPickerStarts(int startR, int startC, int endR, int endC) {
        start = new int[]{startR, startC};
        end = new int[]{endR, endC};

    }

    @Then("the system should return the shortest path of {int} steps")
    public void thenShortestPath(int expectedSteps) {
           Assertions.assertEquals(navigator.findShortestPath(grid, start,end),expectedSteps);
    }
}
