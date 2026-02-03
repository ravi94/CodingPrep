Feature: Trending Products

  Scenario Outline: Identify top item
    Given the trending window size is <K>
    When I add these products: "<products>"
    Then the top product should be "<expected>"

    Examples:
      | K | products          | expected |
      | 2 | Milk,Milk,Bread   | Milk     |
      | 2 | Apple,Pear,Apple  | Apple    |