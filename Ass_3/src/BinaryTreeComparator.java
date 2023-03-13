import java.util.Comparator;

/**
 * A comparator for Binary Trees.
 */
public class BinaryTreeComparator<E extends Comparable<E>> implements Comparator<BinaryTree<E>> {
    /**
     * Compares two binary trees with the given root nodes.
     *
     * Two nodes are compared by their left childs, their values, then their right childs,
     * in that order. A null is less than a non-null, and equal to another null.
     *
     * time complexity: O(n)
     * space complexity: O(log n)
     *
     * @param tree1 root of the first binary tree, may be null.
     * @param tree2 root of the second binary tree, may be null.
     * @return -1, 0, +1 if tree1 is less than, equal to, or greater than tree2, respectively.
     */
    @Override
    public int compare(BinaryTree<E> tree1, BinaryTree<E> tree2) {
        // a flag that represents two trees are equal
        boolean[] equal = {true};
        int comparison;
        // compares the first two levels of two binary trees
        if (tree1 == null && tree2 == null) {
            return 0;
        } else if (tree1 != null && tree2 == null) {
            return 1;
        } else if (tree1 == null && tree2 != null) {
            return -1;
        }
        // compares left subtrees of two roots
        comparison = compareSubtrees(tree1.getLeft(), tree2.getLeft(), equal);
        if (comparison != 0) {
            return comparison;
        }
        // compares two roots
        if (tree1.getValue().compareTo(tree2.getValue()) > 0) {
            equal[0] = false;
            return 1;
        } else if (tree1.getValue().compareTo(tree2.getValue()) < 0) {
            equal[0] = false;
            return -1;
        }
        // compares right subtrees of two roots
        comparison = compareSubtrees(tree1.getRight(), tree2.getRight(), equal);
        if (comparison != 0) {
            return comparison;
        }

        return deepCompare(tree1, tree2, equal);

    }

    /**
     * Recursively compares two binary trees with the given root nodes.
     * Two nodes are compared by their left childs, then their right childs,
     * in that order.
     * A null is less than a non-null, and equal to another null.
     *
     * Once a difference is detected, the whole recursion will be terminated.
     *
     * time complexity: O(n)
     * space complexity: O(log n)
     *
     * @param tree1 root of the first binary tree, may be null.
     * @param tree2 root of the second binary tree, may be null.
     * @param equal a flag that represents two trees are equal.
     * @return -1, 0, +1 if tree1 is less than, equal to, or greater than tree2, respectively.
     */
    private int deepCompare(BinaryTree<E> tree1,
                            BinaryTree<E> tree2,
                            boolean[] equal) {
        if (!equal[0]) {
            return 1;
        }
        int comparison;
        // base case, when reaches the level below leaf nodes, no difference has
        // been found between the two nodes, return 0
        if (tree1 == null && tree2 == null) {
            return 0;
        }
        // compares left subtrees of tree1 and tree2
        comparison = compareSubtrees(tree1.getLeft(), tree2.getLeft(), equal);
        if (comparison != 0) {
            return comparison;
        }
        // no need to compare tree1 & tree2, since they are their parents' subtrees
        // compares right subtrees of tree1 and tree2
        comparison = compareSubtrees(tree1.getRight(), tree2.getRight(), equal);
        if (comparison != 0) {
            return comparison;
        }
        // recursively checks for left and right subtrees
        return deepCompare(tree1.getLeft(), tree2.getLeft(), equal)
                * deepCompare(tree1.getRight(), tree2.getRight(), equal);
    }

    /**
     * Compares tree1's subtree(child1) with tree2's subtree(child2).
     * If child1 is tree1's left subtree, then child2 should be tree2's
     * left subtree, same rule for right subtrees.
     *
     * time complexity: O(1)
     * space complexity: O(1)
     *
     * @param child1 a subtree of tree1.
     * @param child2 a subtree of tree2.
     * @param equal a flag that represents two trees are equal.
     * @return -1, 0, +1 if tree1's subtree is less than, equal to, or greater
     *          than tree2's, respectively.
     */
    private int compareSubtrees(BinaryTree<E> child1,
                                BinaryTree<E> child2,
                                boolean[] equal) {
        if (child1 != null && child2 == null) {
            equal[0] = false;
            return 1;
        } else if (child1 == null && child2 != null) {
            equal[0] = false;
            return -1;
        } else if (child1 != null && child2 != null) {
            if (child1.getValue().compareTo(child2.getValue()) > 0) {
                equal[0] = false;
                return 1;
            }
            if (child1.getValue().compareTo(child2.getValue()) < 0) {
                equal[0] = false;
                return -1;
            }
        }
        return 0;
    }
}

