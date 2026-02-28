package dsa.arrays;

/*

Longest Consecutive Sequence
        Question
Given an unsorted array of integers, find the length of the longest consecutive sequence.
Must solve in O(n) time.
Sample Input:
        [100, 4, 200, 1, 3, 2]
Sample Output:
        4
        (Sequence: 1 → 2 → 3 → 4)
Sample Input 2:
        [0, 3, 7, 2, 5, 8, 4, 6, 0, 1]
Sample Output 2:
        9
        (Sequence: 0 → 1 → 2 → 3 → 4 → 5 → 6 → 7 → 8)

Approach:

Brute force: Sort + scan → O(n log n) ✗ (too slow)

Better: HashSet approach

Key insight:
  A number X is the START of a sequence
  only if (X-1) does NOT exist in the set.

  If X-1 exists → X is middle of a sequence
                → skip it, don't start counting from here

  This ensures each sequence is counted
  exactly once from its starting point.

*/


import java.util.HashSet;
import java.util.Set;

public class LongestConsecutiveSequence {

    public int longestConsecutive(int[] nums) {
        if (nums == null || nums.length == 0) return 0;

        // Put all numbers in a HashSet for O(1) lookup
        Set<Integer> numSet = new HashSet<>();
        for (int num : nums) {
            numSet.add(num);
        }

        int maxLength = 0;

        for (int num : numSet) {
            // Only start counting if num is sequence start
            // i.e. num-1 does NOT exist in set
            if (!numSet.contains(num - 1)) {

                int currentNum = num;
                int currentLength = 1;

                // Extend sequence as far as possible
                while (numSet.contains(currentNum + 1)) {
                    currentNum++;
                    currentLength++;
                }

                maxLength = Math.max(maxLength, currentLength);
            }
        }

        return maxLength;
    }

    public static void main(String[] args) {
        LongestConsecutiveSequence sol = new LongestConsecutiveSequence();

        System.out.println(sol.longestConsecutive(
                new int[]{100, 4, 200, 1, 3, 2}));     // 4

        System.out.println(sol.longestConsecutive(
                new int[]{0, 3, 7, 2, 5, 8, 4, 6, 0, 1})); // 9

        System.out.println(sol.longestConsecutive(
                new int[]{1}));                          // 1

        System.out.println(sol.longestConsecutive(
                new int[]{}));                           // 0

        System.out.println(sol.longestConsecutive(
                new int[]{1, 1, 1, 1}));                // 1 (duplicates)
    }
}


/*

What the Interviewer Expects
The key insight is the "only start from sequence beginning" optimization. Without it, you'd recount overlapping sequences.
They check:

Do you arrive at HashSet approach or need a hint
Can you explain WHY it's O(n) despite nested loops
Duplicate handling — HashSet naturally deduplicates
Edge cases: empty array, single element, all duplicates, negative numbers

Common follow-ups:

        "What if you need to return the actual sequence?" (track start + length, reconstruct)
        "What if numbers can overflow int?" (use Long instead)
        "Can you do it with O(1) space?" (sort first → O(n log n) time tradeoff)

*/

