package org.example.bdd.steps;

import coding.retail.shiftMerge.ShiftMerger;
import io.cucumber.java.en.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

public class ShiftSteps {
    private List<ShiftMerger.Shift> shifts = new ArrayList<>();
    private Map<String, List<int[]>> result;

    @Given("the following shifts exist:")
    public void givenShifts(List<Map<String, String>> table) {
        table.forEach(row -> shifts.add(new ShiftMerger.Shift(
                row.get("role"), Integer.parseInt(row.get("start"), 10), Integer.parseInt(row.get("end"), 10)
        )));
    }

    @When("I run the shift merger")
    public void whenRun() {
        result = new ShiftMerger().mergeShifts(shifts);
    }

    @Then("the {string} role should have {int} merged shift from {int} to {int}")
    public void thenVerify(String role, int count, int s, int e) {
        assertEquals(count, result.get(role).size());
        assertArrayEquals(new int[]{s, e}, result.get(role).get(0));
    }
}