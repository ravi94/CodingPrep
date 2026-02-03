Feature: Shift Consolidation

  Scenario: Merge touching shifts
    Given the following shifts exist:
      | role    | start | end  |
      | Cashier | 800  | 1000 |
      | Cashier | 1000  | 1200 |
    When I run the shift merger
    Then the "Cashier" role should have 1 merged shift from 800 to 1200