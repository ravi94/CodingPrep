package coding.retail;

import java.util.*;

public class BFS_DFS {

    Map<String, List<String>> childrenMap;

    public List<String> dfs(String root) {
        List<String> result = new ArrayList<>();
        Set<String> visited = new HashSet<>();
        Deque<String> stack = new ArrayDeque<>();

        stack.push(root);

        while (!stack.isEmpty()) {
            String current = stack.pop();

            if (visited.contains(current)) continue;
            visited.add(current);

            result.add(current);

            List<String> children = childrenMap.get(current);
            if (children != null) {
                // reverse to mimic recursive order
                for (int i = children.size() - 1; i >= 0; i--) {
                    stack.push(children.get(i));
                }
            }
        }

        return result;
    }

    public List<String> bfs(String root) {
        List<String> result = new ArrayList<>();
        Set<String> visited = new HashSet<>();
        Queue<String> queue = new ArrayDeque<>();

        queue.offer(root);

        while (!queue.isEmpty()) {
            String current = queue.poll();

            if (visited.contains(current)) continue;
            visited.add(current);

            result.add(current);

            List<String> children = childrenMap.get(current);
            if (children != null) {
                for (String child : children) {
                    queue.offer(child);
                }
            }
        }

        return result;
    }




}
