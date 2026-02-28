package dsa.arrays;

/*
Question
Given an array of strings, group all anagrams together. Order of groups doesn't matter.
Sample Input:
        ["eat", "tea", "tan", "ate", "nat", "bat"]
Sample Output:
        [["eat","tea","ate"], ["tan","nat"], ["bat"]]
Sample Input 2:
        ["", ""]
Sample Output 2:
        [["", ""]]

Thought Process
Two strings are anagrams if they contain
the same characters in same frequency.

Approach 1: Sort each string → use as key
  "eat" → sorted → "aet"
        "tea" → sorted → "aet"  ← same key! they're anagrams
        "tan" → sorted → "ant"
        "nat" → sorted → "ant"  ← same key!

Approach 2: Character frequency array as key
  "eat" → [1,0,0,0,1,0...1,0,0] (a=1,e=1,t=1)
        "tea" → [1,0,0,0,1,0...1,0,0]  ← same key!
Better for long strings, avoids sorting cost
  */


import java.util.*;

public class GroupAnagrams {

    // ── Approach 1: Sort as key ──────────────────────────────
    // Simpler code, O(n * k log k) time
    public List<List<String>> groupAnagramsSort(String[] strs) {
        if (strs == null || strs.length == 0) return new ArrayList<>();

        // sorted string → list of original strings
        Map<String, List<String>> map = new HashMap<>();

        for (String str : strs) {
            // Sort characters to get canonical key
            char[] chars = str.toCharArray();
            Arrays.sort(chars);
            String key = new String(chars);

            map.computeIfAbsent(key, k -> new ArrayList<>()).add(str);
        }

        return new ArrayList<>(map.values());
    }

    // ── Approach 2: Frequency array as key ──────────────────
    // Better for long strings, O(n * k) time
    public List<List<String>> groupAnagramsFreq(String[] strs) {
        if (strs == null || strs.length == 0) return new ArrayList<>();

        Map<String, List<String>> map = new HashMap<>();

        for (String str : strs) {
            // Build frequency key: "a2b1c0...z1"
            int[] freq = new int[26];
            for (char c : str.toCharArray()) {
                freq[c - 'a']++;
            }

            // Convert freq array to string key
            // e.g. "eat" → "#1#0#0#0#1#0#0#0#0#0#0#0#0#0#0#0#0#0#0#1#0#0#0#0#0#0"
            StringBuilder keyBuilder = new StringBuilder();
            for (int count : freq) {
                keyBuilder.append('#');   // delimiter to avoid ambiguity
                keyBuilder.append(count);
            }
            String key = keyBuilder.toString();

            map.computeIfAbsent(key, k -> new ArrayList<>()).add(str);
        }

        return new ArrayList<>(map.values());
    }

    public static void main(String[] args) {
        GroupAnagrams sol = new GroupAnagrams();

        // Test 1: Basic
        String[] input1 = {"eat", "tea", "tan", "ate", "nat", "bat"};
        System.out.println("Sort approach:");
        System.out.println(sol.groupAnagramsSort(input1));
        // [[eat, tea, ate], [tan, nat], [bat]]

        System.out.println("Freq approach:");
        System.out.println(sol.groupAnagramsFreq(input1));
        // [[eat, tea, ate], [tan, nat], [bat]]

        // Test 2: Empty strings
        String[] input2 = {"", ""};
        System.out.println(sol.groupAnagramsSort(input2));
        // [["", ""]]

        // Test 3: Single character
        String[] input3 = {"a"};
        System.out.println(sol.groupAnagramsSort(input3));
        // [["a"]]

        // Test 4: All same anagram
        String[] input4 = {"abc", "bca", "cab", "cba"};
        System.out.println(sol.groupAnagramsSort(input4));
        // [[abc, bca, cab, cba]]

        // Test 5: No anagrams
        String[] input5 = {"abc", "def", "ghi"};
        System.out.println(sol.groupAnagramsSort(input5));
        // [[abc], [def], [ghi]]
    }


}

/*

Complexity
ApproachTimeSpaceSort as keyO(n × k log k)O(n × k)Frequency arrayO(n × k)O(n × k)
n = number of strings, k = max length of a string

What the Interviewer Expects
This is testing HashMap keying strategy — the core skill is identifying what makes a good canonical key.
They check:

Do you immediately think of sorting as canonical form
Do you know the frequency approach as an optimization
The # delimiter trick — without it keys can collide
computeIfAbsent — clean Java idiom vs verbose null check

*/



