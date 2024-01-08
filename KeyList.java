import java.util.List;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * This class allows us to store lists of keys in the nodes of our tree.
 */
public class KeyList<T extends Comparable<T>> implements KeyListInterface<T> {

    // stores a list of values for the same key
    protected List<T> keyList;

    /**
     * Create a new KeyList and adds the first value with a specific key to it.
     * @param firstKey the first object with firstKey
     */
    public KeyList(T firstKey) {
        if (firstKey == null) throw new NullPointerException("keys cannot be null");
        keyList = new LinkedList<>();
        keyList.add(firstKey);
    }

    /**
     * Adds another object with the same key to the list.
     * @param newKey new object that maps to the same key as all objects in the list
     */
    public void addKey(T newKey) {
        if (keyList.get(0).compareTo(newKey) != 0) {
            throw new IllegalArgumentException("compareTo for keys in same list must return 0");
        }
        keyList.add(newKey);
    }

	/*
	* Checks of the KeyList contains key.
	* @return true if this KeyList contains key, false if not
	*/
	@Override
    public boolean containsKey(T key) {
        return this.keyList.contains(key);
    }

    /**
     * Compares this KeyList to another KeyList based on the keys of the objects that
     * both lists contain.
     * @param o reference to the other list for this comparison
     * @return negative integer if the keys in this list are smaller than the keys in
     *         the other list, 0 if the keys are the same, and a positive integer if
     *         the keys in the other list are larger then the keys in this list.
     */
    @Override
    public int compareTo(KeyListInterface<T> o) {
        return keyList.get(0).compareTo(o.iterator().next());
    }

    /**
     * Returns an iterator over the objects stored in the list.
     * @returns the iterator object
     */
    @Override
    public Iterator<T> iterator() {
        return keyList.iterator();
    }
    public String toString() {
        return "KeyList = " + keyList;
    }

}
