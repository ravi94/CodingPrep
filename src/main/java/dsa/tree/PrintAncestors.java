package dsa.tree;


/*
---

        # 🌳 TREES — Question 2 of 6

        ## Print Ancestors of a Given Node in a Binary Tree

### Question
Given a Binary Tree and a node value, print all ancestors of that node from root to parent.

**Sample Input:**
        ```
        1
        / \
        2   3
        / \
        4   5

Node = 4
        ```
        **Sample Output:**
        ```
        2 1*/

public class PrintAncestors {

    // Returns true if target found in subtree rooted at node
    // Prints ancestors on the way back up
    public boolean printAncestors(TreeNode root, int target) {
        if (root == null) return false;

        if (root.val == target) return true;

        // If target is in left or right subtree, current node is an ancestor
        if (printAncestors(root.left, target) ||
                printAncestors(root.right, target)) {
            System.out.print(root.val + " ");
            return true;
        }

        return false;
    }

    public static void main(String[] args) {
        TreeNode root = new TreeNode(1);
        root.left = new TreeNode(2);
        root.right = new TreeNode(3);
        root.left.left = new TreeNode(4);
        root.left.right = new TreeNode(5);

        PrintAncestors sol = new PrintAncestors();

        System.out.print("Ancestors of 4: ");
        sol.printAncestors(root, 4); // prints: 2 1
        System.out.println();

        System.out.print("Ancestors of 5: ");
        sol.printAncestors(root, 5); // prints: 2 1
        System.out.println();

        System.out.print("Ancestors of 3: ");
        sol.printAncestors(root, 3); // prints: 1
    }
}
/*
```

        ---

        ### Complexity
- **Time:** O(n) — visits each node once in worst case
        - **Space:** O(h) — recursion stack depth equals height of tree

---

        ### What the Interviewer Expects

This is testing **recursive thinking and backtracking**. The elegant solution prints on the way back up the call stack — not on the way down.

**They check:**
        - Do you use recursion cleanly with a boolean return to indicate "found"
        - Do you avoid storing the path explicitly in a list (cleaner to print on unwind)
- Edge cases: node is root (no ancestors), node doesn't exist (print nothing)

        **Common follow-ups:**
        - "Return ancestors as a List instead of printing" (minor change — pass list as param)
        - "Print from root to parent order" (currently prints parent → root, reverse the list)
        - "What if tree has duplicate values?" (stop at first found, or collect all)

        ---*/
