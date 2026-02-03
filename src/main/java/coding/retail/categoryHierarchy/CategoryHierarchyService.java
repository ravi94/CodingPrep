package coding.retail.categoryHierarchy;

import java.util.*;

public class CategoryHierarchyService {

    private final Map<String, List<String>> childrenMap = new HashMap<>();
    private final Map<String, String> parentMap = new HashMap<>();

    public void addRelationship(String parent, String child) {
        childrenMap.computeIfAbsent(parent, k -> new ArrayList<>()).add(child);
        parentMap.put(child, parent);
    }

    // Task 1: Find Root (Upward Traversal) - O(H) where H is height
    public String getRootCategory(String category) {
        String current = category;
        Set<String> visited = new HashSet<>(); // Guard against cycles

        while (parentMap.containsKey(current)) {
            if (visited.contains(current)) throw new IllegalStateException("Circular dependency detected!");
            visited.add(current);
            current = parentMap.get(current);
        }
        return current;
    }


    public List<String> getAllSubCategoriesWithStack(String root) {
        List<String> result = new ArrayList<>();
        Set<String> visited = new HashSet<>();
        Deque<String> stack = new ArrayDeque<>();

        stack.push(root);

        while (!stack.isEmpty()) {
            String current = stack.pop();

            if (visited.contains(current)) continue;
            visited.add(current);

            List<String> children = childrenMap.get(current);
            if (children != null) {
                for (String child : children) {
                    result.add(child);
                    stack.push(child);
                }
            }
        }

        return result;
    }


    // Task 2: Get All Sub-categories (Downward Traversal - DFS) - O(N)
    public List<String> getAllSubCategories(String root) {
        List<String> result = new ArrayList<>();
        dfs(root, result, new HashSet<>());
        return result;
    }

    private void dfs(String current, List<String> result, Set<String> visited) {
        if (visited.contains(current)) return;
        visited.add(current);

        List<String> children = childrenMap.get(current);
        if (children != null) {
            for (String child : children) {
                result.add(child);
                dfs(child, result, visited);
            }
        }
    }
}


/*
*
* SDE 3 Deep-Dive: Optimization
1. Scaling the Query: If this tree has millions of nodes, DFS is expensive for every request.
    * Optimization: Use Memoization (Cache the results of getAllSubCategories). When a relationship is added, invalidate the cache for that node and all its ancestors.
2. Path Compression: If you only care about the root, you can use a Union-Find data structure. During getRootCategory, you can point the current node directly to the root, making subsequent lookups $O(\alpha(N))$ (nearly constant time).
3. Database Strategy: In a real SQL database, represent this using a Closure Table. It stores every relationship (A->B, A->C, B->C) in a separate table, allowing you to find all descendants in a single $O(1)$ indexed JOIN query rather than recursive SQL.
*
* */