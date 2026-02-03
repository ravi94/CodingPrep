Feature: Product Autocomplete
  Scenario: User gets suggestions ranked by popularity
    Given the following products exist in the catalog:
      | name         | popularity |
      | Apple        | 500        |
      | Apricot      | 100        |
      | Apple Juice  | 300        |
    When I type "Ap" in the search bar
    Then the suggestions should be in this order:
      | Apple       |
      | Apple Juice |
      | Apricot     |