import java.util.Comparator;
import java.util.HashSet;

/**
 * A comparator for Binary Trees.
 */
public class BinaryTreeComparator2<E extends Comparable<E>> implements Comparator<BinaryTree<E>> {
    /**
     * Compares two binary trees with the given root nodes.
     *
     * Two nodes are compared by their left childs, their values, then their right childs,
     * in that order. A null is less than a non-null, and equal to another null.
     *
     * time complexity: O(1)
     * space complexity: O(1)
     *
     *
     * @param tree1 root of the first binary tree, may be null.
     * @param tree2 root of the second binary tree, may be null.
     * @return -1, 0, +1 if tree1 is less than, equal to, or greater than tree2, respectively.
     */
    @Override
    public int compare(BinaryTree<E> tree1, BinaryTree<E> tree2) {
        // a flag that represents two trees are equal
        boolean[] equal = {true};
        // compares the roots of two binary trees
        if (tree1 == null && tree2 == null) {
            return 0;
        } else if (tree1 != null && tree2 == null) {
            return 1;
        } else if (tree1 == null && tree2 != null) {
            return -1;
        }

        return recursivelyCompare(tree1, tree2, equal);

    }

    /**
     * Recursively compares two binary trees with the given root nodes.
     * Two nodes are compared by their left childs, their values, then their
     * right childs, in that order.
     * A null is less than a non-null, and equal to another null.
     * Once a difference is detected, the whole recursion will be terminated.
     *
     * time complexity: O()
     * space complexity: O()
     *
     * @param tree1 root of the first binary tree, may be null.
     * @param tree2 root of the second binary tree, may be null.
     * @param equal a flag that represents two trees are equal.
     * @return -1, 0, +1 if tree1 is less than, equal to, or greater than tree2,
     *         respectively.
     */
    private int recursivelyCompare(BinaryTree<E> tree1, BinaryTree<E> tree2,
                                   boolean[] equal) {
        // base case, when reaches the level below leaf nodes, no difference has
        // been found between the two nodes, return 0
        if (tree1 == null && tree2 == null) {
            return 0;
        }
        // compares left subtrees of this nodes
        if (tree1.getLeft() == null && tree2.getLeft() != null) {
            equal[0] = false;
            return -1;
        } else if (tree1.getLeft() != null && tree2.getLeft() == null) {
            equal[0] = false;
            return 1;
        } else if (tree1.getLeft() != null && tree2.getLeft() != null) {
            if (tree1.getLeft().getValue().compareTo(
                    tree2.getLeft().getValue()) > 0) {
                equal[0] = false;
                return 1;
            }
            if (tree1.getLeft().getValue().compareTo(
                    tree2.getLeft().getValue()) < 0) {
                equal[0] = false;
                return -1;
            }
        }
        // compares this nodes
        if (tree1.getValue().compareTo(tree2.getValue()) > 0) {
            equal[0] = false;
            return 1;
        } else if (tree1.getValue().compareTo(tree2.getValue()) < 0) {
            equal[0] = false;
            return -1;
        }
        // compares right subtrees of this nodes
        if (tree1.getRight() == null && tree2.getRight() != null) {
            equal[0] = false;
            return -1;
        } else if (tree1.getRight() != null && tree2.getRight() == null) {
            equal[0] = false;
            return 1;
        } else if (tree1.getRight() != null && tree2.getRight() != null) {
            if (tree1.getRight().getValue().compareTo(
                    tree2.getRight().getValue()) > 0) {
                equal[0] = false;
                return 1;
            }
            if (tree1.getRight().getValue().compareTo(
                    tree2.getRight().getValue()) < 0) {
                equal[0] = false;
                return -1;
            }
        }
        // recursively checks for left and right subtrees
        if (!equal[0]) {
            return 1;
        } else {
            return recursivelyCompare(tree1.getLeft(), tree2.getLeft(), equal)
                    * recursivelyCompare(tree1.getRight(), tree2.getRight(), equal);
        }
    }
}
