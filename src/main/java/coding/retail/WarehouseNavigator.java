package coding.retail;

import java.util.*;

public class WarehouseNavigator {

    record Point(int r, int c, int dist) {}

    public int findShortestPath(int[][] grid, int[] start, int[] end) {
        int rows = grid.length;
        int cols = grid[0].length;

        // Direction vectors: Up, Down, Left, Right
        int[][] dirs = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

//        Queue<Point> queue = new LinkedList<>();
        Deque<Point> queue = new ArrayDeque<>();
        queue.add(new Point(start[0], start[1], 0));

        boolean[][] visited = new boolean[rows][cols];
        visited[start[0]][start[1]] = true;

        while (!queue.isEmpty()) {
            Point curr = queue.poll();

            // Check if we reached the target
            if (curr.r == end[0] && curr.c == end[1]) {
                return curr.dist;
            }

            for (int[] d : dirs) {
                int nr = curr.r + d[0];
                int nc = curr.c + d[1];

                if (nr >= 0 && nr < rows && nc >= 0 && nc < cols
                        && grid[nr][nc] == 0 && !visited[nr][nc]) {

                    visited[nr][nc] = true;
                    queue.add(new Point(nr, nc, curr.dist + 1));
                }
            }
        }

        return -1; // Path not found
    }
}