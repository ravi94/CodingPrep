package dsa.tree;

import java.util.*;

public class Traversal {

    public List<Integer> preorderTraversal(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        if (root == null) return result;

        Stack<TreeNode> stack = new Stack<>();
        stack.push(root);

        while (!stack.isEmpty()) {
            TreeNode node = stack.pop();
            result.add(node.val);          // Visit root first

            // Push right first so left is processed first (LIFO)
            if (node.right != null) stack.push(node.right);
            if (node.left != null) stack.push(node.left);
        }

        return result;
    }

    public List<List<Integer>> levelOrder(TreeNode root) {
        List<List<Integer>> result = new ArrayList<>();
        if (root == null) return result;

        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);

        while (!queue.isEmpty()) {
            int levelSize = queue.size(); // snapshot of current level count
            List<Integer> currentLevel = new ArrayList<>();

            for (int i = 0; i < levelSize; i++) {
                TreeNode node = queue.poll();
                currentLevel.add(node.val);

                if (node.left != null) queue.offer(node.left);
                if (node.right != null) queue.offer(node.right);
            }

            result.add(currentLevel);
        }

        return result;
    }

    public static void main(String[] args) {
        TreeNode root = new TreeNode(1);
        root.left = new TreeNode(2);
        root.right = new TreeNode(3);
        root.left.left = new TreeNode(4);
        root.left.right = new TreeNode(5);

        Traversal sol = new Traversal();
        System.out.println(sol.preorderTraversal(root)); // [1, 2, 4, 5, 3]
    }
}

/*

in order
### What the Interviewer Expects

Simple but revealing. They want to see if you understand **why right is pushed before left** (stack is LIFO, so left must be on top to process first).

        **They check:**
        - The right-before-left push order — common mistake to get this backwards
- Clean use of Stack
- Can you extend this to **in-order** and **post-order** iteratively (harder)

**Common follow-ups:**
        - "Now do iterative in-order" (needs to go all the way left first — different pattern)
        - "Now do iterative post-order" (trickiest — reverse of modified pre-order trick)
        - "Why would you prefer iterative over recursive?" (stack overflow risk for deep trees)



level order
        ### What the Interviewer Expects

This is a **foundational BFS pattern** — used as a building block for many harder problems. The key insight is capturing `levelSize` before the inner loop.

        **They check:**
        - The `levelSize` snapshot trick — without it you can't separate levels
        - Using `Queue` (not `Stack`) — common mix-up under pressure
- Can you adapt it to common variants

**Common follow-ups:**
        - "Print in reverse level order" (add levels to front of result, or reverse at end)
        - "Right side view of tree" (take last element of each level)
        - "Zigzag level order" (alternate direction each level — use `Deque`)

*/
