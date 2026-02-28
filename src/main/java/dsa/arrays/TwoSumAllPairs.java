package dsa.arrays;

import java.util.*;

/*

Two Sum — Return All Indexes Including Duplicates
        Question
Given an array of integers and a target, return all pairs of indexes that add up to the target. Array may have duplicates.
Sample Input 1:
nums = [1, 2, 3, 2, 4, 2], target = 4
Sample Output 1:
        [(0,4), (1,3), (1,5), (3,5)]
        (All pairs: 1+3=4, 2+2=4, 2+2=4, 2+2=4)
Sample Input 2:
nums = [0, 0, 0, 0], target = 0
Sample Output 2:
        [(0,1), (0,2), (0,3), (1,2), (1,3), (2,3)]

Thought Process
Classic Two Sum returns ONE pair → use HashMap, O(n)

This variant returns ALL pairs including duplicates.
HashMap approach breaks here because:
        - Same value can appear at multiple indexes
  - We need ALL combinations, not just first found

Key insight:
Map each value → List of all its indexes
Then for each value X, find (target - X)
and pair every index of X with every index of (target-X)

*/


public class TwoSumAllPairs {
    public List<int[]> twoSumAllPairs(int[] nums, int target) {
        List<int[]> result = new ArrayList<>();
        if (nums == null || nums.length < 2) return result;

        // value → list of all indexes with that value
        Map<Integer, List<Integer>> indexMap = new HashMap<>();
        for (int i = 0; i < nums.length; i++) {
            indexMap.computeIfAbsent(nums[i], k -> new ArrayList<>()).add(i);
        }

        // Track processed pairs to avoid duplicates like (1,3) and (3,1)
        Set<String> seen = new HashSet<>();

        for (int i = 0; i < nums.length; i++) {
            int complement = target - nums[i];

            if (indexMap.containsKey(complement)) {
                for (int j : indexMap.get(complement)) {
                    if (i < j) { // ensure i < j to avoid duplicate pairs
                        String pairKey = i + "," + j;
                        if (!seen.contains(pairKey)) {
                            result.add(new int[]{i, j});
                            seen.add(pairKey);
                        }
                    }
                }
            }
        }

        return result;
    }

    // Helper to print results
    private void printResult(List<int[]> pairs) {
        if (pairs.isEmpty()) {
            System.out.println("[]");
            return;
        }
        System.out.print("[");
        for (int i = 0; i < pairs.size(); i++) {
            System.out.print("(" + pairs.get(i)[0] + ","
                    + pairs.get(i)[1] + ")");
            if (i < pairs.size() - 1) System.out.print(", ");
        }
        System.out.println("]");
    }

    public static void main(String[] args) {
        TwoSumAllPairs sol = new TwoSumAllPairs();

        // Test 1: basic with duplicates
        sol.printResult(sol.twoSumAllPairs(
                new int[]{1, 2, 3, 2, 4, 2}, 4));
        // [(0,2), (1,3), (1,5), (3,5)]

        // Test 2: all zeros
        sol.printResult(sol.twoSumAllPairs(
                new int[]{0, 0, 0, 0}, 0));
        // [(0,1), (0,2), (0,3), (1,2), (1,3), (2,3)]

        // Test 3: no pairs
        sol.printResult(sol.twoSumAllPairs(
                new int[]{1, 2, 3}, 10));
        // []

        // Test 4: negative numbers
        sol.printResult(sol.twoSumAllPairs(
                new int[]{-1, 0, 1, 2, -1, -4}, 0));
        // [(-1+1=0): (0,2), (4,2)]
    }

}

/*

---

        ### Dry Run
```
nums = [1, 2, 3, 2, 4, 2], target = 4

Step 1: Build indexMap
  1 → [0]
          2 → [1, 3, 5]
          3 → [2]
          4 → [4]

Step 2: For each index find complement

i=0, nums[0]=1, complement=3
indexMap has 3 → [2]
j=2, 0<2 ✓ → add (0,2)  ← 1+3=4 ✓

i=1, nums[1]=2, complement=2
indexMap has 2 → [1,3,5]
j=1, 1<1 ✗ skip (same index)
j=3, 1<3 ✓ → add (1,3)  ← 2+2=4 ✓
j=5, 1<5 ✓ → add (1,5)  ← 2+2=4 ✓

i=2, nums[2]=3, complement=1
indexMap has 1 → [0]
j=0, 2<0 ✗ skip (already counted as (0,2))

i=3, nums[3]=2, complement=2
indexMap has 2 → [1,3,5]
j=1, 3<1 ✗ skip
        j=3, 3<3 ✗ skip (same index)
j=5, 3<5 ✓ → add (3,5)  ← 2+2=4 ✓

i=4, nums[4]=4, complement=0
indexMap has no 0 → skip

i=5, nums[5]=2, complement=2
j=1, 5<1 ✗ skip
        j=3, 5<3 ✗ skip
        j=5, 5<5 ✗ skip

Result: [(0,2), (1,3), (1,5), (3,5)] ✓

*/

/*


What the Interviewer Expects
This question tests whether you can adapt the classic Two Sum pattern to handle duplicates and multiple results.
They check:

Do you identify that classic HashMap Two Sum breaks for multiple pairs
The i < j condition to avoid counting same pair twice like (1,3) and (3,1)
Handling same-index pair — nums[i] + nums[i] with i == j is invalid
Can you explain trade-off between HashMap approach and brute force

Common follow-ups:

        "What if you need unique value pairs not index pairs?" (use Set of value pairs)
        "Three Sum — find all triplets that sum to target?" (fix one element, two sum on rest)
        "What if the array is sorted?" (two pointer approach — O(n) time, O(1) space)

*/
