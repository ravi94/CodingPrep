package dsa.tree;


public class DistanceBetweenNodes {

    // Step 1: Find LCA of two nodes
    private TreeNode findLCA(TreeNode root, int a, int b) {
        if (root == null) return null;
        if (root.val == a || root.val == b) return root;

        TreeNode left = findLCA(root.left, a, b);
        TreeNode right = findLCA(root.right, a, b);

        if (left != null && right != null) return root; // root is LCA
        return left != null ? left : right;
    }

    // Step 2: Find distance from a node to target
    private int findDistance(TreeNode root, int target, int dist) {
        if (root == null) return -1;
        if (root.val == target) return dist;

        int left = findDistance(root.left, target, dist + 1);
        if (left != -1) return left;

        return findDistance(root.right, target, dist + 1);
    }

    // Step 3: Distance = dist(LCA, a) + dist(LCA, b)
    public int findDistanceBetweenNodes(TreeNode root, int a, int b) {
        TreeNode lca = findLCA(root, a, b);
        int d1 = findDistance(lca, a, 0);
        int d2 = findDistance(lca, b, 0);
        return d1 + d2;
    }

    public static void main(String[] args) {
        TreeNode root = new TreeNode(1);
        root.left = new TreeNode(2);
        root.right = new TreeNode(3);
        root.left.left = new TreeNode(4);
        root.left.right = new TreeNode(5);
        root.right.right = new TreeNode(6);

        DistanceBetweenNodes sol = new DistanceBetweenNodes();
        System.out.println(sol.findDistanceBetweenNodes(root, 4, 6)); // 4
        System.out.println(sol.findDistanceBetweenNodes(root, 4, 5)); // 2
        System.out.println(sol.findDistanceBetweenNodes(root, 2, 6)); // 3
    }
}

class TreeNode {
    int val;
    TreeNode left, right;
    TreeNode(int val) { this.val = val; }
}
