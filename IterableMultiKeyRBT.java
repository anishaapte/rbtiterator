// --== CS400 Fall 2023 File Header Information ==--
// Name: Anisha Apte
// Email: aaapte@wisc.edu
// Group: A39
// TA: Casey Ford
// Lecturer: Gary Dahl
// Notes to Grader: none

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import org.junit.jupiter.api.Test;

/**
 * The class will implement the iterator method for the RBT
 * and will insert values into the RBT based on the key. It 
 * extends the RedBlackTree class and implements the 
 * IterableMultiKeySortedCollectionInterface
 * @author anishaapte
 *
 * @param <T> - generic parameter
 */

public class IterableMultiKeyRBT<T extends Comparable<T>> 
    extends RedBlackTree<KeyListInterface<T>> 
    implements IterableMultiKeySortedCollectionInterface<T> {
    
    Comparable<T> start;
    int numKeys;

    /**
     * Inserts value into tree that can store multiple objects per key by keeping
     * lists of objects in each node of the tree.
     * @param key object to insert
     * @return true if a new node was inserted, false if the key was added into an existing node
     */
    @Override
    public boolean insertSingleKey(T key) {
        KeyListInterface<T> kI = new KeyList<T>(key);
        Node<KeyListInterface<T>> n = this.findNode(kI);
        //if node is not null, then adding the new duplicate key to the node 
        //by using add method of KeyListInterface. Also incrementing numKeys
        if (n != null) {
            n.data.addKey(key);
            numKeys++;
            return false;
        }
        //else, call insert method on the tree to insert the non-duplicate 
        //node inside of the tree. Also incrementing numKeys
        else {
            this.insert(kI);
            numKeys++;
            return true;
        }
    }

    /**
     * @return the number of values in the tree.
     */
    @Override
    public int numKeys() {
        return numKeys;
    }

    /**
     * Returns an iterator that does an in-order iteration over the tree.
     */
    @Override
    public Iterator<T> iterator() {
        //preparing an iterator, a main stack, and a stack of the right subtree of each element in the stack
        //Using an anonymous class
        Iterator<T> anon = new Iterator<T>() {
            Iterator<T> kIter2;
            Stack<Node<KeyListInterface<T>>> s = getStartStack();
            Stack<Node<KeyListInterface<T>>> s2 = null;
            @Override
            public boolean hasNext() { 
                return !s.isEmpty();
            }

            @Override
            public T next() {
                //if the right subtree stack is null, then it will generate a new right subtree
                //stack of the element in the main stack
                //The first element of the right subtree stack will be the peek of the main stack
                if (kIter2 == null) {
                    s2 = new Stack<Node<KeyListInterface<T>>>();
                    getRightSubtree(s.peek().down[1], s2);
                    s2.push(s.peek());
                    kIter2 = s2.peek().data.iterator(); 
                    
                }
                //do not have to do anything here, else if is just a placeholder
                else if (kIter2.hasNext()) {
                    //do nothing
                    
                }
                //keep iterating if the right subtree stack is not empty
                else {
                    if (!s2.isEmpty()) {
                        kIter2 = s2.peek().data.iterator();
                    }
                    
                }
                //if it is the last element on the right-subtree stack, then it will pop it 
                //and make sure to set the iterator to null, so it can move onto the next element
                //in the main stack
                T ret = kIter2.next();
                if (!kIter2.hasNext()) {
                    s2.pop();
                    if (s2.isEmpty()) {
                        s.pop();
                        kIter2 = null;
                    }
                }
                //returning the next element of the iteration
                return ret;
    
            }
            
            /**
             * Helper anonymous class to get the right subtree
             * @param n - node to push into the stack
             * @param s - current stack
             */
            private void getRightSubtree(Node<KeyListInterface<T>> n, Stack<Node<KeyListInterface<T>>> s) {
                if (n == null) {
                    return;
                }
                getRightSubtree(n.down[1],s);
                s.push(n);
                getRightSubtree(n.down[0],s);
          
            }
            
        };
        return anon;
    }

    /**
     * Sets the starting point for iterations. Future iterations will start at the
     * starting point or the key closest to it in the tree. This setting is remembered
     * until it is reset. Passing in null disables the starting point.
     * @param startPoint the start point to set for iterations
     */
    @Override
    public void setIterationStartPoint(Comparable<T> startPoint) {
        this.start = startPoint;
        
    }
    
    /**
     * Clears, and sets numKeys equal to 0
     */
    @Override
    public void clear() {
        super.clear();
        numKeys = 0;
    }
    
    /**
     * Helper method that will return an instance of java.util.Stack after initialization
     * If no iteration start point is set, then stack is initialized with nodes on path from 
     * root node to node with smallest key. If iteration start point is set, stack is 
     * initialized with all nodes with keys equal to or larger than start point along search path. 
     * @return - instance of java.util.Stack containing nodes after initialization
     */
    protected Stack<Node<KeyListInterface<T>>> getStartStack() {
        Stack<Node<KeyListInterface<T>>> stack = new Stack<Node<KeyListInterface<T>>>();
        Node<KeyListInterface<T>> n = this.root;
        //if the starting point is null, then I am returning from root to the left most child,
        //which is the smallest key of the tree
        if (start == null) {
            while (n != null) {
                stack.push(n);
                n = n.down[0];
            }//end while
        }//end if 
        //else the starting point is set, so then have to go through the search path
        //of the starting point until we find the node that is the same as the starting 
        //point or reach null(starting point is not in the tree) and push. 
        else {
            while (n != null) {
                //getting the node's first data element
                T firstKey = n.data.iterator().next();
                //comparing the start point to this first data element 
                int compare = start.compareTo(firstKey);
                //start > firstKey, so get n's right child
                if (compare == 1) {
                    n = n.down[1];
                }
                //start = firstKey, so push the node on the stack
                else if (compare == 0) {
                    stack.push(n);
                    break;
                }
                //start < firstKey, so push the node on the stack and set n as left child 
                else if (compare == -1) {
                    stack.push(n);
                    n = n.down[0];
                }                
            }//end while
        }//end else
        return stack;
    }
    
    /**
     * Testing the numKeys() method and checking that the size is correct, as it 
     * is different from the return value of numKeys()
     */
    @Test
    public void testNumKeysAndSize() {
        //creating a tree
        IterableMultiKeyRBT<Integer> tree = new IterableMultiKeyRBT<Integer>();
        tree.insertSingleKey(50);
        tree.insertSingleKey(50);
        tree.insertSingleKey(25);
        tree.insertSingleKey(30);
        tree.insertSingleKey(30);
        tree.insertSingleKey(20);
        
        //saving the values of the size and numKeys()
        int size = tree.size();
        int numKeys = tree.numKeys();
        
        //checking the size and value of numKeys()
        assertEquals("Expecting size of 4, got " + size, 4, size);
        assertEquals("Expecting numKeys() 6, got " + numKeys, 6, numKeys);
       
    }
    
    /**
     * Test method for the insertSingleKey() method 
     */
    @Test
    public void testInsertSingleKey() {
        //creating a tree and creating a variable to capture the boolean returned by the method
        boolean isInsert = false;
        IterableMultiKeyRBT<Integer> tree = new IterableMultiKeyRBT<Integer>();
        //calling the insertSingleKey method and then immediately seeing if what the method 
        //returned is correct
        isInsert = tree.insertSingleKey(50);
        assertTrue("Should return true, got " + isInsert, isInsert);
        isInsert = tree.insertSingleKey(50);
        assertFalse("Should return false, got " + isInsert, isInsert);
        isInsert = tree.insertSingleKey(25);
        assertTrue("Should return true, got " + isInsert, isInsert);
        isInsert = tree.insertSingleKey(30);
        assertTrue("Should return true, got " + isInsert, isInsert);
        isInsert = tree.insertSingleKey(30);
        assertFalse("Should return false, got " + isInsert, isInsert);
        isInsert = tree.insertSingleKey(20);
        assertTrue("Should return true, got " + isInsert, isInsert);
        
        //Seeing if the data in the node is correct
        int[] expected = new int[] {50,50};
        KeyListInterface<Integer> kI = new KeyList<Integer>(50);
        Node<KeyListInterface<Integer>> n = tree.findNode(kI);
        List<Integer> actualList = new ArrayList<Integer>();
        Iterator<Integer> iter2 = n.data.iterator();
        while(iter2.hasNext()) {
             actualList.add(iter2.next());          
        } 
        int[] actual = new int[actualList.size()];
        for (int i = 0; i < actualList.size(); i++) {
            actual[i] = actualList.get(i);
        }
        assertArrayEquals("Data of node is same as expected[50,50] ", expected , actual);
        
                
    }
    
    /**
     * Testing the Iterator() method without a start point 
     */
    @Test
    public void testIteratorNoStartPoint() {
        //not setting a start point, just testing the iterator 
        //creating a tree
        IterableMultiKeyRBT<Integer> tree = new IterableMultiKeyRBT<Integer>();
        tree.insertSingleKey(50);
        tree.insertSingleKey(50);
        tree.insertSingleKey(25);
        tree.insertSingleKey(25);
        tree.insertSingleKey(30);
        tree.insertSingleKey(30);
        tree.insertSingleKey(20);
        tree.insertSingleKey(60);
        tree.insertSingleKey(60);
        tree.insertSingleKey(90);
        
        Iterator<Integer> t = tree.iterator();
        //checking that the iterator is not null
        assertNotNull("Iterator should not be null", t);
        //creating the actual and setting what the expected should be 
        int[] actual = new int[10];
        int[] expected = new int[] {20,25,25,30,30,50,50,60,60,90};
        int i = 0;
        
        //inserting the values of the tree into the array
        while(t.hasNext()) {
            actual[i] = t.next();
            i++;           
        } 
        //checking that the array is correct
        assertArrayEquals("got the actual array as " + Arrays.toString(actual), expected, actual);
                
    }
    @Test
    public void testLargeTreeWithIterationStartPoint() {
        //not setting a start point, just testing the iterator 
        //creating a tree
        IterableMultiKeyRBT<Integer> tree = new IterableMultiKeyRBT<Integer>();
        boolean b = false;
        b = tree.insertSingleKey(25);
        b = tree.insertSingleKey(25);
        b = tree.insertSingleKey(20);
        b = tree.insertSingleKey(36);
        b = tree.insertSingleKey(10);
        b = tree.insertSingleKey(10);
        b = tree.insertSingleKey(22);
        b = tree.insertSingleKey(30);
        b = tree.insertSingleKey(40);
        b = tree.insertSingleKey(5);
        b = tree.insertSingleKey(5);
        b = tree.insertSingleKey(12);
        b = tree.insertSingleKey(28);
        b = tree.insertSingleKey(38);
        b = tree.insertSingleKey(38);
        b = tree.insertSingleKey(48);
        b = tree.insertSingleKey(1);
        b = tree.insertSingleKey(8);
        b = tree.insertSingleKey(15);
        b = tree.insertSingleKey(15);
        b = tree.insertSingleKey(15);
        b = tree.insertSingleKey(45);
        b = tree.insertSingleKey(50);
        b = tree.insertSingleKey(50);
        b = tree.insertSingleKey(50);

        
        tree.setIterationStartPoint(29);
        
        Iterator<Integer> iter2 = tree.iterator();
        List<Integer> actualList = new ArrayList<Integer>();
        while(iter2.hasNext()) {
             actualList.add(iter2.next());          
        } 
        //getting the actual array
        int[] actual = new int[actualList.size()];
        for (int i = 0; i < actualList.size(); i++) {
            actual[i] = actualList.get(i);
        }
        int[] expected = new int[] {30,36,38,38,40,45,48,50,50,50};
        //checking that array is correct
        assertArrayEquals("Data of node is same as expected[50,50] ", expected , actual);

    }
    

}
