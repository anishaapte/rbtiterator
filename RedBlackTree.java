// --== CS400 Fall 2023 File Header Information ==--
// Name: Anisha Apte
// Email: aaapte@wisc.edu
// Group: A39
// TA: Casey Ford
// Lecturer: Gary Dahl
// Notes to Grader: <optional extra notes>
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * This class will insert nodes into the Red-Black-Tree and extends the BinarySearchTree class
 * @author anishaapte
 *
 * @param <T>
 */
public class RedBlackTree<T extends Comparable<T>> extends BinarySearchTree<T> {
    
    protected static class RBTNode<T> extends Node<T> {
        public int blackHeight = 0;
        public RBTNode(T data) { super(data); }
        public RBTNode<T> getUp() { return (RBTNode<T>)this.up; }
        public RBTNode<T> getDownLeft() { return (RBTNode<T>)this.down[0]; }
        public RBTNode<T> getDownRight() { return (RBTNode<T>)this.down[1]; }
        public String toString() {return "RBTNode: " + data;}
    }
    
    /**
     * Method used to take care of all violations of the insert method
     * Violations include the following and is given that the child is a red node :
     * 1) parent and uncle are both red 
     * 2) parent and uncle are different colors, but red violating child is on same side as parent
     * 3) parent and uncle are different colors, but red violating child is on different side as parent
     * @param redNode - red child node 
     */
    protected void enforceRBTreePropertiesAfterInsert(RBTNode<T> redNode) {
        //if newNode or its parents are null then just have to return
        if (redNode == null || redNode.getUp() == null) {
            return;
        }
        redNode.blackHeight = 0;//setting redNode to red
        RBTNode<T> parent = redNode.getUp();
        //if parent is black, just return 
        if (parent.blackHeight == 1) {
            return;
        }        
        
        //getting the uncle, but have to find which side to get
        RBTNode<T> grandParent = parent.getUp();
        RBTNode<T> uncle = null;
        if (parent.isRightChild()) {
            uncle = grandParent.getDownLeft();
        }
        else {
            uncle = grandParent.getDownRight();
        }
        
        //CASE 1 : uncle has to be not null and red to go into this case
        //In the case following happens : parent becomes black, uncle becomes black, 
        //and grandParent becomes red 
        if (uncle != null && uncle.blackHeight == 0) {
            parent.blackHeight = 1;
            uncle.blackHeight = 1;
            grandParent.blackHeight = 0;
            this.enforceRBTreePropertiesAfterInsert(grandParent);
        }
        else {//uncle is either black or null(also black), which is case 2 or 3
           //entering case 2 : redNode violation is on same side as parent
            if ((redNode.isRightChild() && parent.isRightChild()) ||
                    (!redNode.isRightChild() && !parent.isRightChild())) {
                this.rotate(parent, grandParent);
                int temp = parent.blackHeight;
                parent.blackHeight = grandParent.blackHeight;
                grandParent.blackHeight = temp;
               
            }
            else {
                //entering case 3 : redNode violation is on different side of parent
                //have to rotate child and parent
                //then have in case 2, and have to rotate new parent and grandparent
                //have to swap colors of rotated nodes(child and grandParent)
                this.rotate(redNode, parent);
                this.rotate(redNode, grandParent);
                int temp = redNode.blackHeight;
                redNode.blackHeight = grandParent.blackHeight;
                grandParent.blackHeight = temp;
            }
        }
    }
    
    /**
     *Inserts the node into the tree and calls the enforeRBTProperties method
     *after insertion to make sure that color of node, black height of RBT, and nodes themselves
     *are in proper order 
     */
    @Override
    public boolean insert(T data) throws NullPointerException {
        RBTNode<T> newNode = new RBTNode<T>(data);
        boolean isInserted = this.insertHelper(newNode);
        if (isInserted) {
            this.enforceRBTreePropertiesAfterInsert(newNode);
            ((RBTNode<T>)this.root).blackHeight = 1;
        }
        return isInserted;
    }
    
    /**
     * Case 1 : following are the conditions :
     * 1) newly inserted node and its parent are both red 
     * 2) the parent's sibling is red
     * 
     * In this case, the parent and its sibling will become black, and the grandparent 
     * will become red. And you continue checking up recursively up to the root
     */
    @Test
    public void parentAndUncleBothRed() {
        //creating the nodes and defining whether the node is red or black
        RBTNode<Integer> root = new RBTNode<>(50);       
        root.blackHeight = 1;
        RBTNode<Integer> n1 = new RBTNode<>(20);
        n1.blackHeight = 0;
        RBTNode<Integer> n2 = new RBTNode<>(80);
        n2.blackHeight = 1;
        RBTNode<Integer> n3 = new RBTNode<>(10);
        n3.blackHeight = 1;
        RBTNode<Integer> n4 = new RBTNode<>(30);
        n4.blackHeight = 1;
        //putting in the violation at n5 and n6
        RBTNode<Integer> n5 = new RBTNode<>(70);
        n5.blackHeight = 0;
        RBTNode<Integer> n6 = new RBTNode<>(90);
        n6.blackHeight = 0;
        
        root.down[0] = n1;
        root.down[1] = n2;
        n1.up = root;
        n2.up = root;
        n1.down[0] = n3;
        n1.down[1] = n4;
        n3.up = n1;
        n4.up = n1;
        n2.down[0] = n5;
        n2.down[1] = n6;
        n5.up = n2;
        n6.up = n2;
        
        //creating the tree with the correct size
        RedBlackTree<Integer> tree = new RedBlackTree<>();
        tree.root = root;
        tree.size = 7;
        
        //inserting at the very right side of the tree
        //inserted node is child of parent and uncle that are both red 
        //inserted node will be red 
        //checking that the level order is correct; will remain the same as there is no rotation
        tree.insert(100);
        Assertions.assertEquals("[ 50, 20, 80, 10, 30, 70, 90, 100 ]", tree.toLevelOrderString());
        
        //checking that the parent and uncle are both black
        //checking that the grandparent is red
        //checking that the root is black
        Assertions.assertEquals(1, root.blackHeight);
        Assertions.assertEquals(0, root.getDownRight().blackHeight);
        Assertions.assertEquals(1, root.getDownRight().getDownLeft().blackHeight);
        Assertions.assertEquals(1, root.getDownRight().getDownRight().blackHeight);
        Assertions.assertEquals(0, root.getDownRight().getDownRight().getDownRight().blackHeight);
    }
    
    /**
     * Case 2 : following are the conditions :
     * 1) newly inserted node and its parent are both red 
     * 2) the parent's sibling is black(or null)
     * 3) the newly inserted node and its red parent are on same sides of the child hierarchy
     * 
     * In this case, the parent and grandparent are rotated, and their colors are swapped 
     */
    @Test
    public void violatingParentAndChildOnSameSide() {
        //creating the nodes and defining whether nodes are red or black
        RBTNode<Integer> root = new RBTNode<>(50);
        root.blackHeight = 1;
        RBTNode<Integer> n1 = new RBTNode<>(30);
        n1.blackHeight = 1;
        RBTNode<Integer> n2 = new RBTNode<>(80);
        n2.blackHeight = 1;
        RBTNode<Integer> n3 = new RBTNode<>(90);
        n3.blackHeight = 0;
        
        root.down[0] = n1;
        root.down[1] = n2;
        n1.up = root;
        n2.up = root;
        n2.down[1] = n3;
        n3.up = n2;
        
        RedBlackTree<Integer> tree = new RedBlackTree<>();
        tree.root = root;
        tree.size = 4;
        
        //inserting a red node at the very right side of the tree
        //violation is where parent and uncle and different colors and the red violating node 
        //is on the same side as the red parent
        tree.insert(100);
        //checking the level order as there is a rotation of the parent and grandparent
        //checking if the parent and grandparent colors are swapped
        //checking that the color of the root is black
        Assertions.assertEquals("[ 50, 30, 90, 80, 100 ]", tree.toLevelOrderString());
        Assertions.assertEquals(0, root.getDownRight().getDownLeft().blackHeight);
        Assertions.assertEquals(0, root.getDownRight().getDownRight().blackHeight);
        Assertions.assertEquals(1, root.getDownRight().blackHeight);
        Assertions.assertEquals(1, root.getDownLeft().blackHeight);
        Assertions.assertEquals(1, root.blackHeight);
    }
    
    /**
     * Case 3 : following are the conditions :
     * 1) newly inserted node and its parent are both red 
     * 2) the parent's sibling is black(or null)
     * 3) the newly inserted node and its red parent are on opposite sides of the child hierarchy
     * 
     * In this case, the parent and child are rotated, and the rest is handled as case 2. 
     */
    @Test
    public void violatingParentAndChildOnDiffSide() {
        //creating the nodes and defining whether nodes are red or black
        RBTNode<Integer> root = new RBTNode<>(50);
        root.blackHeight = 1;
        RBTNode<Integer> n1 = new RBTNode<>(30);
        n1.blackHeight = 1;
        RBTNode<Integer> n2 = new RBTNode<>(80);
        n2.blackHeight = 1;
        RBTNode<Integer> n3 = new RBTNode<>(90);
        n3.blackHeight = 0;
        
        root.down[0] = n1;
        root.down[1] = n2;
        n1.up = root;
        n2.up = root;
        n2.down[1] = n3;
        n3.up = n2;
        
        RedBlackTree<Integer> tree = new RedBlackTree<>();
        tree.root = root;
        tree.size = 4;
        //inserting a red node at the very left side of the right side of tree
        //violation is where red parent and uncle and different colors and the red violating node 
        //is on a different side as the red parent
        tree.insert(85);
        //checking the level order as there is a rotation of the parent and child, and new parent and grandparent
        //checking if the child and grandparent colors are swapped
        //checking that the original parent remains the same color
        Assertions.assertEquals("[ 50, 30, 85, 80, 90 ]", tree.toLevelOrderString());
        Assertions.assertEquals(0, root.getDownRight().getDownLeft().blackHeight);
        Assertions.assertEquals(0, root.getDownRight().getDownRight().blackHeight);
        Assertions.assertEquals(1, root.getDownRight().blackHeight);
        Assertions.assertEquals(1, root.getDownLeft().blackHeight);
        Assertions.assertEquals(1, root.blackHeight);
    }
    /**
     * Test a more complex scenario where everything is added in ascending order, 
     * which is the worst-case for a BST. This is where all different cases may of 
     * rotations and swapping may get covered 
     */
    @Test
    public void additionalHelperTest() {
        RedBlackTree<Integer> bst = new RedBlackTree<>();
        bst.insert(10);
        bst.insert(20);
        bst.insert(30);
        bst.insert(40);
        bst.insert(50);
        bst.insert(60);
        bst.insert(70);
        RBTNode<Integer> root = ((RBTNode<Integer>)bst.root);
        Assertions.assertEquals("[ 20, 10, 40, 30, 60, 50, 70 ]", bst.toLevelOrderString());
        Assertions.assertEquals(1, root.blackHeight);
        Assertions.assertEquals(0, root.getDownRight().blackHeight);
        Assertions.assertEquals(1, root.getDownLeft().blackHeight);
        Assertions.assertEquals(1, root.getDownRight().getDownRight().blackHeight);
        Assertions.assertEquals(1, root.getDownRight().getDownLeft().blackHeight);
        Assertions.assertEquals(0, root.getDownRight().getDownRight().getDownRight().blackHeight);
        Assertions.assertEquals(0, root.getDownRight().getDownRight().getDownLeft().blackHeight);
    }
    

}
