import java.util.Iterator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

/**
* This class extends the IterableMultiKeyRBT class to run submission checks on it.
*/
public class P15SubmissionChecker extends IterableMultiKeyRBT {

	/**
	* Creates an iterator for empty tree and checks if hasNext() returns
	* false.
	*/
	@Test
	public void submissionCheckerEmptyTree() {
		IterableMultiKeyRBT<Integer> tree = new IterableMultiKeyRBT<>();
    	Assertions.assertTrue(tree.iterator().hasNext() == false);
	}

	/**
	* Inserts a single key into an empty tree, then and checks if an
	* iterator for the tree returns only this key. Also checks if both
	* numKeys() and size() return 1 for the tree. 
	*/
	@Test
	public void submissionCheckerSingleKey() {
		IterableMultiKeyRBT<Integer> tree = new IterableMultiKeyRBT<>();
		tree.insertSingleKey(1);
		Iterator<Integer> iter = tree.iterator();
		Assertions.assertEquals(1, iter.next());
		Assertions.assertFalse(iter.hasNext());
		Assertions.assertEquals(1, tree.numKeys());
		Assertions.assertEquals(1, tree.size());
	}

	/**
	* Inserts a small number of keys, including some duplicates into an empty
	* tree and checks if both size() and numKeys() return the correct numbers
	* and an iterator includes all duplicates.
	*/
	@Test
	public void submissionCheckerDuplicateKeys() {
		IterableMultiKeyRBT<Integer> tree = new IterableMultiKeyRBT<>();
		tree.insertSingleKey(50);
		tree.insertSingleKey(50);
		tree.insertSingleKey(100);
		tree.insertSingleKey(100);
		tree.insertSingleKey(150);
		tree.insertSingleKey(150);
		
		Assertions.assertEquals(3, tree.size());
		Assertions.assertEquals(6, tree.numKeys());
		
		int count = 0;
		Iterator<Integer> iter = tree.iterator();
		for (Integer key : tree) {
			int expected = ((count++/2)+1)*50;
			Assertions.assertEquals(expected, iter.next());
		}
	}

	/**
	* Inserts 4 keys with a number of duplicates into an empty tree, then
	* checks if they are returned in the correct order. 
	*
	*/
	@Test
	public void submissionCheckerIteratorOrder() {
		IterableMultiKeyRBT<String> tree = new IterableMultiKeyRBT<>();
		String[] keys = new String [] { "red", "blue", "green", "yellow" };
		int[] sequence = new int [] { 1,2,2,3,1,0,3,2,1,0,2 };
		for (int i : sequence) tree.insertSingleKey(keys[i]);
		
		String last = null;
		for (String key : tree) {
			if (last != null) Assertions.assertTrue(last.compareTo(key) <= 0);
			last = key;
		}
	}

	/*
	* Checks if an iterator with a start point iterates over all keys
	* equal to and larger than the start point.
	*/
	@Test
	public void submissionCheckerIteratorStartPoint() {
		IterableMultiKeyRBT<Integer> tree = new IterableMultiKeyRBT<>();
		tree.insertSingleKey(1);
		tree.insertSingleKey(33);
		tree.insertSingleKey(40);	

		tree.setIterationStartPoint(33);	
		Assertions.assertEquals(33, tree.iterator().next());
		tree.setIterationStartPoint(34);
		Assertions.assertEquals(40, tree.iterator().next());
		tree.setIterationStartPoint(null);	
		Assertions.assertEquals(1, tree.iterator().next());
	}

}
