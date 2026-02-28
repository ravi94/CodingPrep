package dsa.tree;
/*

---

        # 🌳 TREES — Question 6 of 6

        ## Trees + DP: Diameter of Binary Tree

### Question
Find the diameter of a Binary Tree — the length of the longest path between any two nodes (path may or may not pass through root).

        **Sample Input:**
        ```
        1
        / \
        2   3
        / \
        4   5
        ```
        **Sample Output:**
        ```
        3

*/

public class DiameterOfTree {

    private int maxDiameter = 0;

    public int diameterOfBinaryTree(TreeNode root) {
        maxDiameter = 0; // reset for multiple calls
        height(root);
        return maxDiameter;
    }

    // Returns height of subtree, updates maxDiameter as side effect
    private int height(TreeNode node) {
        if (node == null) return 0;

        int leftHeight = height(node.left);
        int rightHeight = height(node.right);

        // Diameter through this node = left height + right height
        maxDiameter = Math.max(maxDiameter, leftHeight + rightHeight);

        return 1 + Math.max(leftHeight, rightHeight);
    }

    public static void main(String[] args) {
        TreeNode root = new TreeNode(1);
        root.left = new TreeNode(2);
        root.right = new TreeNode(3);
        root.left.left = new TreeNode(4);
        root.left.right = new TreeNode(5);

        DiameterOfTree sol = new DiameterOfTree();
        System.out.println(sol.diameterOfBinaryTree(root)); // 3

        // Edge case: single node
        TreeNode single = new TreeNode(1);
        System.out.println(sol.diameterOfBinaryTree(single)); // 0

        // Linear tree (skewed)
        TreeNode skewed = new TreeNode(1);
        skewed.left = new TreeNode(2);
        skewed.left.left = new TreeNode(3);
        System.out.println(sol.diameterOfBinaryTree(skewed)); // 2
    }
}
/*


Complexity

Time: O(n) — each node visited once
Space: O(h) — recursion stack


What the Interviewer Expects
This is the classic DP-on-tree pattern. The naive O(n²) approach (compute height separately for each node) is wrong here. They want the single-pass O(n) solution.
They check:

The insight that diameter at each node = leftHeight + rightHeight
Using a class-level variable (or int[] wrapper) to track global max during recursion — since Java can't return two values cleanly
Understanding that the diameter may NOT pass through the root

Common follow-ups:

        "Maximum path sum" (same pattern, replace height count with sum — LeetCode 124, Hard)
        "What if we also need to return the actual path?" (store nodes while traversing)
        "Binary Tree Maximum Path Sum" — direct harder extension

*/
