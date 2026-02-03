Feature: Product Category Management
  Scenario: Manager retrieves all products under a high-level category
    Given the following category structure:
      | Parent     | Child      |
      | Beverages  | Tea        |
      | Beverages  | Coffee     |
      | Tea        | Green Tea  |
      | Tea        | Black Tea  |
    When I request all sub-categories for "Beverages"
    Then the list should contain "Tea", "Coffee", "Green Tea", and "Black Tea"