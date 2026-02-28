package dsa.dp;

import java.util.Arrays;

public class BuyAndSellStock {

    // Variation 1: Single transaction
    // Time: O(n)  Space: O(1)
    public int maxProfitOnce(int[] prices) {
        int minPrice = Integer.MAX_VALUE;
        int maxProfit = 0;
        for (int price : prices) {
            minPrice = Math.min(minPrice, price);
            maxProfit = Math.max(maxProfit, price - minPrice);
        }
        return maxProfit;
    }

    // Variation 2: Unlimited transactions
    // Time: O(n)  Space: O(1)
    public int maxProfitMultiple(int[] prices) {
        int profit = 0;
        for (int i = 1; i < prices.length; i++) {
            if (prices[i] > prices[i-1])
                profit += prices[i] - prices[i-1];
        }
        return profit;
    }

    // Variation 3: At most 2 transactions
    // Time: O(n)  Space: O(1)
    public int maxProfitTwo(int[] prices) {
        int buy1 = Integer.MIN_VALUE, sell1 = 0;
        int buy2 = Integer.MIN_VALUE, sell2 = 0;
        for (int price : prices) {
            buy1  = Math.max(buy1,  -price);
            sell1 = Math.max(sell1, buy1 + price);
            buy2  = Math.max(buy2,  sell1 - price);
            sell2 = Math.max(sell2, buy2 + price);
        }
        return sell2;
    }

    // Variation 4: At most K transactions
    // Time: O(n*k)  Space: O(k)
    public int maxProfitK(int k, int[] prices) {
        int n = prices.length;
        if (k >= n / 2) return maxProfitMultiple(prices);

        int[] buy  = new int[k + 1];
        int[] sell = new int[k + 1];
        Arrays.fill(buy, Integer.MIN_VALUE);

        for (int price : prices) {
            for (int i = 1; i <= k; i++) {
                buy[i]  = Math.max(buy[i],  sell[i-1] - price);
                sell[i] = Math.max(sell[i], buy[i] + price);
            }
        }
        return sell[k];
    }

    public static void main(String[] args) {
        BuyAndSellStock sol = new BuyAndSellStock();

        int[] p1 = {7, 1, 5, 3, 6, 4};
        System.out.println("Once:      " + sol.maxProfitOnce(p1));      // 5
        System.out.println("Multiple:  " + sol.maxProfitMultiple(p1));  // 7

        int[] p2 = {3, 3, 5, 0, 0, 3, 1, 4};
        System.out.println("Two:       " + sol.maxProfitTwo(p2));       // 6

        int[] p3 = {2, 4, 1, 7};
        System.out.println("K=2:       " + sol.maxProfitK(2, p3));      // 9

        // Edge cases
        System.out.println("One price: " + sol.maxProfitOnce(new int[]{5}));       // 0
        System.out.println("Falling:   " + sol.maxProfitOnce(new int[]{5,4,3,2})); // 0
    }
}

/*



### Complexity Summary

        | Variation | Time | Space |
        |-----------|------|-------|
        | Once      | O(n) | O(1) |
        | Multiple  | O(n) | O(1) |
        | Two transactions | O(n) | O(1) |
        | K transactions | O(n×k) | O(k) |

        ---

        ### What the Interviewer Expects

Adobe loves this question family because it **progressively tests DP thinking**. They start with Variation 1 and keep adding constraints.

        **The progression they expect:**
        ```
V1 → greedy (track min price)
V2 → greedy (grab every upward slope)
V3 → DP with 4 state variables
V4 → generalize V3 to k transactions

*/
/*


# V3 — At Most 2 Transactions

## The Thinking Journey

### Step 1: What state do we need to track?
        ```
At any point in time, we can be in one of 5 states:

State 0: Haven't done anything yet
State 1: Currently holding stock (after 1st buy)
State 2: Sold once (after 1st sell)
State 3: Holding stock again (after 2nd buy)
State 4: Sold twice (after 2nd sell) ← answer is here

Transition:
State 0 → State 1: buy  (pay price)
State 1 → State 2: sell (receive price)
State 2 → State 3: buy  (pay price)
State 3 → State 4: sell (receive price)
```

        ### Step 2: What do we track per state?
        ```
We want MAXIMUM PROFIT at each state.

        buy1  = best profit after 1st buy
        = we PAID price, so profit decreases
        = max(-price) across all days so far

        sell1 = best profit after 1st sell
        = buy1 + current price
        = max(sell1, buy1 + price)

buy2  = best profit after 2nd buy
        = we already have sell1 profit
        = use sell1 profit to offset this buy
        = max(buy2, sell1 - price)

sell2 = best profit after 2nd sell
        = buy2 + current price
        = max(sell2, buy2 + price)
```

        ### Step 3: Trace through example
```
prices = [3, 3, 5, 0, 0, 3, 1, 4]

Think of it as: at each price, update all 4 states

buy1    sell1   buy2    sell2
Initial:      -INF    0       -INF    0

Why -INF for buy states?
        → Haven't bought yet, profit is undefined
        → -INF ensures first real price always wins

        price=3:
buy1  = max(-INF, -3)      = -3   (paid 3, profit=-3)
sell1 = max(0,    -3+3)    =  0   (sold at 3 after buying at 3)
buy2  = max(-INF, 0-3)     = -3   (used sell1=0 profit, paid 3)
sell2 = max(0,    -3+3)    =  0

price=3: (same, no change)

price=5:
buy1  = max(-3, -5)        = -3   (buying at 5 is worse)
sell1 = max(0,  -3+5)      =  2   (bought at 3, sold at 5 → +2)
buy2  = max(-3, 2-5)       = -3   (sell1=2, pay 5, net=-3)
sell2 = max(0,  -3+5)      =  2   (net profit=2)

price=0:
buy1  = max(-3, -0)        =  0   (buying at 0 is best so far!)
sell1 = max(2,   0+0)      =  2   (selling at 0 worse than 2)
buy2  = max(-3,  2-0)      =  2   (sell1=2, buy at 0, net=+2!)
sell2 = max(2,   2+0)      =  2

price=0: (same)

price=3:
buy1  = max(0,  -3)        =  0
sell1 = max(2,   0+3)      =  3   (bought at 0, sold at 3 → +3)
buy2  = max(2,   3-3)      =  2
sell2 = max(2,   2+3)      =  5   (first trade +3, second +3 → 5)

price=1:
buy1  = max(0,  -1)        =  0
sell1 = max(3,   0+1)      =  3
buy2  = max(2,   3-1)      =  2   (sell1=3, buy at 1 → net=2)
sell2 = max(5,   2+1)      =  5

price=4:
buy1  = max(0,  -4)        =  0
sell1 = max(3,   0+4)      =  4   (buy at 0, sell at 4 → +4? wait
        buy1=0 means bought at price 0
sell at 4 → profit = 4) ✓
buy2  = max(2,   4-4)      =  2
sell2 = max(5,   2+4)      =  6  ← ANSWER ✓
        ```

        ### The State Machine Visually
```
buy          sell         buy          sell
  ──────────────────────────────────────────────────▶
State0 ──▶ State1 ──▶ State2 ──▶ State3 ──▶ State4
        profit=0   -price     +price     -price      +price

buy1 = -price         (transition 0→1)
sell1 = buy1+price    (transition 1→2)
buy2 = sell1-price    (transition 2→3)
sell2 = buy2+price    (transition 3→4)
```
*/



