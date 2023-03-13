public class StrongHeap {
    /**
     * Determines whether the binary tree with the given root node is
     * a "strong binary heap", as described in the assignment task sheet.
     *
     * A strong binary heap is a binary tree which is:
     *  - a complete binary tree, AND
     *  - its values satisfy the strong heap property.
     *
     * time complexity: O(n)
     * space complexity: O(log n)
     *
     * @param root root of a binary tree, cannot be null.
     * @return true if the tree is a strong heap, otherwise false.
     */
    public static boolean isStrongHeap(BinaryTree<Integer> root) {
        // TODO: implement question 2
        // A tree with a single node is a strong binary heap.
        if (root.getLeft() == null && root.getRight() == null) {
            return true;
        }
        int numNodes = countNodes(root);
        return isComplete(root, 0, numNodes) && isStrongHeapProperty(root);
    }

    /**
     * Counts the number of nodes in a binary tree.
     *
     * time complexity: O(n)
     * space complexity: O(log n)
     *
     * @param root root of a binary tree
     * @return total number of nodes in the given binary tree
     */
    private static int countNodes(BinaryTree<Integer> root) {
        if (root == null) {
            return 0;
        }
        return 1 + countNodes(root.getLeft()) + countNodes(root.getRight());
    }

    /**
     * Checks whether the binary tree is complete or not.
     *
     * time complexity: O(n)
     * space complexity: O(log n)
     *
     * @param root root of a binary tree
     * @param index index of root node
     * @param numNodes total number of nodes in the given binary tree
     * @return true if the binary tree is complete, otherwise false.
     */
    private static boolean isComplete(BinaryTree<Integer> root, int index,
                                      int numNodes) {
        if (root == null) {
            return true;
        }
        if (root.getLeft() == null && root.getRight() != null) {
            return false;
        }
        if (index >= numNodes) {
            return false;
        }
        return isComplete(root.getLeft(), index * 2 + 1, numNodes)
                && isComplete(root.getRight(), index * 2 + 2, numNodes);
    }

    /**
     * Checks whether a 3 levels' binary tree conforms to strong heap property.
     * For root nodes x of a 3 levels' binary tree, has x > child(x) + child(child(x))
     *
     * time complexity: O(1)
     * space complexity: O(1)
     *
     * @param root root of a 3 levels' binary tree
     * @param child one child node of root
     * @return true if the binary tree conforms to strong heap property, otherwise false.
     */
    private static boolean isMaxGrandparent(BinaryTree<Integer> root,
                                            BinaryTree<Integer> child) {
        if (child != null) {
            if (child.getLeft() != null) {
                if (root.getValue() <= child.getValue()
                        + child.getLeft().getValue()) {
                    return false;
                }
            }
            if (child.getRight() != null) {
                if (root.getValue() <= child.getValue()
                        + child.getRight().getValue()) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Checks the strong heap property in the binary tree.
     *
     * time complexity: O(n)
     * space complexity: O(log n)
     *
     * @param root root of a binary tree
     * @return true if the binary tree conforms to strong heap property, otherwise false.
     */
    private static boolean isStrongHeapProperty(BinaryTree<Integer> root) {
        boolean isMaxGrandpa;
        // Checks strong max heap property
        isMaxGrandpa = isMaxGrandparent(root, root.getLeft());
        if (!isMaxGrandpa) {
            return isMaxGrandpa;
        }
        isMaxGrandpa = isMaxGrandparent(root, root.getRight());
        if (!isMaxGrandpa) {
            return isMaxGrandpa;
        }
        // base case, single node(leaf node) satisfies property
        if (root.getLeft() == null && root.getRight() == null) {
            return true;
        }
        // Checks max heap property at node which is in second last level
        if (root.getRight() == null) {
            return root.getValue() >= root.getLeft().getValue();
        }
        // Checks max heap property at non-leaf node
        // Recursively check max heap property at left and right subtree
        if (root.getValue() > root.getLeft().getValue()
                && root.getValue() > root.getRight().getValue()) {
            return isStrongHeapProperty(root.getLeft())
                    && isStrongHeapProperty(root.getRight());
        } else {
            return false;
        }
    }
}
