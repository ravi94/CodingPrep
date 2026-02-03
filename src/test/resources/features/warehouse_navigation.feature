Feature: Order Fulfillment Pathfinding
  Scenario: Calculate optimal route for a picker
    Given a warehouse grid of size 3x3
    And there is an obstacle at (1,1)
    When a picker starts at (0,0) and needs to reach (2,2)
    Then the system should return the shortest path of 4 steps