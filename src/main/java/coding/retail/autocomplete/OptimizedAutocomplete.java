package coding.retail.autocomplete;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

public class OptimizedAutocomplete {

    // Composite Key to handle duplicate frequencies
    record Score(int frequency, String name) implements Comparable<Score> {
        @Override
        public int compareTo(Score other) {
            int res = Integer.compare(other.frequency, this.frequency); // Descending
            return res != 0 ? res : this.name.compareTo(other.name); // Tie-break
        }
    }

    class TrieNode {
        Map<Character, TrieNode> children = new ConcurrentHashMap<>();
        // Stores Top 5: Key is Score (freq + name), Value is just the name
        ConcurrentSkipListMap<Score, String> top5Map = new ConcurrentSkipListMap<>();

        // Helper to keep only top 5
        void addToTop5(String name, int freq) {
            // Remove old entry of this product if it exists (requires a lookup map for O(1))
            // For simplicity in this snippet, we search and remove:
            top5Map.entrySet().removeIf(entry -> entry.getValue().equals(name));

            top5Map.put(new Score(freq, name), name);

            if (top5Map.size() > 5) {
                top5Map.pollLastEntry(); // Remove the lowest score
            }
        }
    }

    private final TrieNode root = new TrieNode();

    public void insert(String name, int frequency) {
        TrieNode current = root;
        for (char ch : name.toLowerCase().toCharArray()) {
            current.addToTop5(name, frequency);
            current = current.children.computeIfAbsent(ch, k -> new TrieNode());
        }
        current.addToTop5(name, frequency);
    }

    public List<String> getSuggestions(String prefix) {
        TrieNode current = root;
        for (char ch : prefix.toLowerCase().toCharArray()) {
            current = current.children.get(ch);
            if (current == null) return Collections.emptyList();
        }
        return new ArrayList<>(current.top5Map.values());
    }
}