package coding.retail.categoryHierarchy;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class MemoizedCategoryService {

    private final Map<String, List<String>> childrenMap = new ConcurrentHashMap<>();
    private final Map<String, String> parentMap = new ConcurrentHashMap<>();

    // The Cache: CategoryName -> List of all descendants
    private final Map<String, List<String>> cache = new ConcurrentHashMap<>();

    public void addRelationship(String parent, String child) {
        childrenMap.computeIfAbsent(parent, k -> new ArrayList<>()).add(child);
        parentMap.put(child, parent);

        // CRITICAL: Invalidate the cache for the parent and all its ancestors
        invalidateCache(parent);
    }

    private void invalidateCache(String category) {
        String current = category;
        while (current != null) {
            cache.remove(current);
            current = parentMap.get(current); // Move up the tree
        }
    }

    public List<String> getAllSubCategories(String root) {
        // O(1) if cached, O(N) only on the first hit after a change
        return cache.computeIfAbsent(root, this::performDFS);
    }

    private List<String> performBFS(String root) {
        List<String> result = new ArrayList<>();
        Set<String> visited = new HashSet<>(); // Guard against cycles
        Queue<String> queue = new LinkedList<>();
        queue.add(root);
        visited.add(root);

        while (!queue.isEmpty()) {
            String current = queue.poll();

            List<String> children = childrenMap.get(current);
            if (children != null) {
                for (String child : children) {
                    if (visited.contains(child)) continue;
                    result.add(child);
                    queue.add(child);
                }
            }
        }
        return result;
    }

    private List<String> performDFS(String root) {
        List<String> result = new ArrayList<>();
        // Using Deque as a Stack (LIFO)
        Deque<String> stack = new ArrayDeque<>();
        stack.push(root);

        Set<String> visited = new HashSet<>(); // Guard against cycles

        while (!stack.isEmpty()) {
            String current = stack.pop();

            if (visited.contains(current)) continue;
            visited.add(current);

            List<String> children = childrenMap.get(current);
            if (children != null) {
                for (String child : children) {
                    // If we don't need the root itself in the result:
                    if (!child.equals(root)) {
                        result.add(child);
                    }
                    stack.push(child);
                }
            }
        }
        return result;
    }
}