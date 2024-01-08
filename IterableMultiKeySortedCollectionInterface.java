import java.util.Iterator;

/**
 * This interfaces extends the SortedCollectionInterface to allow storing multiple values for a single key,
 * and to iterate over the keys and values stored in the data structure.
 */
public interface IterableMultiKeySortedCollectionInterface<T extends Comparable<T>> extends SortedCollectionInterface<KeyListInterface<T>>, Iterable<T> {

    /**
     * Inserts value into tree that can store multiple objects per key by keeping
     * lists of objects in each node of the tree.
     * @param key object to insert
     * @return true if a new node was inserted, false if the key was added into an existing node
     */
    public boolean insertSingleKey(T key);

    /**
     * @return the number of values in the tree.
     */
    public int numKeys();

    /**
     * Returns an iterator that does an in-order iteration over the tree.
     */
    public Iterator<T> iterator();

    /**
     * Sets the starting point for iterations. Future iterations will start at the
     * starting point or the key closest to it in the tree. This setting is remembered
     * until it is reset. Passing in null disables the starting point.
     * @param startPoint the start point to set for iterations
     */
    public void setIterationStartPoint(Comparable<T> startPoint);

}
