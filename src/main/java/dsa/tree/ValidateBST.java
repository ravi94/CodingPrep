package dsa.tree;


/*

### Question
Given a Binary Tree, determine if it is a valid Binary Search Tree.
BST rule: left subtree values < node < right subtree values, for every node.

        **Sample Input 1:**
        ```
        5
        / \
        3   7
        / \
        2   4
        → true (valid BST)
        ```
        **Sample Input 2:**
        ```
        5
        / \
        3   7
        / \
        2   6
        → false (6 > 5, violates BST property)
*/


public class ValidateBST {

        // Key insight: pass min and max bounds down the recursion
        public boolean isValidBST(TreeNode root) {
            return validate(root, Long.MIN_VALUE, Long.MAX_VALUE);
        }

        private boolean validate(TreeNode node, long min, long max) {
            if (node == null) return true; // empty tree is valid BST

            if (node.val <= min || node.val >= max) return false;

            return validate(node.left, min, node.val) &&   // left must be < node
                    validate(node.right, node.val, max);    // right must be > node
        }

        public static void main(String[] args) {
            ValidateBST sol = new ValidateBST();

            // Valid BST
            TreeNode root1 = new TreeNode(5);
            root1.left = new TreeNode(3);
            root1.right = new TreeNode(7);
            root1.left.left = new TreeNode(2);
            root1.left.right = new TreeNode(4);
            System.out.println(sol.isValidBST(root1)); // true

            // Invalid BST (6 in wrong position)
            TreeNode root2 = new TreeNode(5);
            root2.left = new TreeNode(3);
            root2.right = new TreeNode(7);
            root2.left.left = new TreeNode(2);
            root2.left.right = new TreeNode(6);
            System.out.println(sol.isValidBST(root2)); // false

            // Tricky: looks valid locally but isn't globally
            TreeNode root3 = new TreeNode(10);
            root3.left = new TreeNode(5);
            root3.right = new TreeNode(15);
            root3.right.left = new TreeNode(6); // 6 < 10, violates BST
            root3.right.right = new TreeNode(20);
            System.out.println(sol.isValidBST(root3)); // false
        }
}
  /*
```

        ---

        ### Complexity
- **Time:** O(n) — visits every node exactly once
- **Space:** O(h) — recursion stack. O(log n) balanced, O(n) skewed

---

        ### What the Interviewer Expects

    This is a **classic trap question**. The naive approach of just checking `left.val < root.val < right.val` at each node **fails** for cases like root3 above. The interviewer specifically wants to see if you fall into this trap.

**They check:**
            - Do you use min/max bounds (the correct approach) vs naive local check (wrong)
            - Why `Long.MIN_VALUE / Long.MAX_VALUE` instead of `Integer` — to handle `Integer.MIN_VALUE` or `Integer.MAX_VALUE` as node values
- Can you also solve it via **in-order traversal** (valid BST gives sorted in-order sequence)

            **Common follow-ups:**
            - "Solve it using in-order traversal approach" (iterate, check each val > previous)
            - "What about duplicate values — are they allowed in a BST?" (depends on definition — clarify)
            - "Convert this BST check to also fix it if invalid" (harder variant)

            ---

    */

