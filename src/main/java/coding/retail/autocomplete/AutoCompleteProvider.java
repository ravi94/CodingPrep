package coding.retail.autocomplete;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class AutoCompleteProvider {

    class TrieNode {
        Map<Character, TrieNode> children = new ConcurrentHashMap<>();
        boolean isWord = false;
        int frequency = 0;
        String word; // Store the full word at the leaf for easy retrieval
    }

    private final TrieNode root = new TrieNode();

    // Insert a product with its popularity score
    public void insert(String product, int frequency) {
        TrieNode current = root;
        for (char ch : product.toLowerCase().toCharArray()) {
            current = current.children.computeIfAbsent(ch, k -> new TrieNode());
        }
        current.isWord = true;
        current.frequency += frequency;
        current.word = product;
    }

    // Find top 5 suggestions for a prefix
    public List<String> getSuggestions(String prefix) {
        TrieNode current = root;
        for (char ch : prefix.toLowerCase().toCharArray()) {
            current = current.children.get(ch);
            if (current == null) return Collections.emptyList();
        }

        // Use a PriorityQueue (Min-Heap) to keep top 5
        PriorityQueue<TrieNode> heap = new PriorityQueue<>(
              Comparator.comparingInt(a -> a.frequency)
//                (N1,N2)->N1.frequency-N2.frequency
        );

        findAllNodes(current, heap);

        List<String> results = new ArrayList<>();
        while (!heap.isEmpty()) {
            results.add(0, heap.poll().word); // Add to front to get descending order
        }
        return results;
    }

    private void findAllNodes(TrieNode node, PriorityQueue<TrieNode> heap) {
        if (node.isWord) {
            heap.offer(node);
            if (heap.size() > 5) heap.poll(); // Keep only top 5
        }
        for (TrieNode child : node.children.values()) {
            findAllNodes(child, heap);
        }
    }
}