//
// HOMEWORK 1 TEST 2
// 
// This class implements 10 tests for the first homework
//
// It first executes the 10 tests of HW1Test.java
// If the above test fails for insert, delete, size, or getClosestEntry
// then it calls a secondary test
//
// For DictionaryST the only tests are those in HW1Test.java
//
// To to execute the tests in this file, you must modify LonkedList and SortedArray
// as follows:
//
// In LinkedList, modify the visibility of variable first, and class Node to public.
// That is modify the original code:
//
// public class LinkedList implements BasicDict {
//     private Node first;
//
//     // Helper Node class for defining a linked list 
//     private static class Node {
//         ...
//
// to read as below:
//
// public class LinkedList implements BasicDict {
//     public Node first;
//
//     // Helper Node class for defining a linked list 
//     public static class Node {
//         ...
//
//
// In SortedArray, modify the visibility of variable entries to public.
// That is modify the original code:
//
// public class SortedArray implements BasicDict {
//     private final int minSize = 8;
//     private Entry[] entries;
//     ....
//
// to read as below:
//
// public class SortedArray implements BasicDict {
//     private final int minSize = 8;
//     public Entry[] entries;
//     ....
//

package hw1;

import org.junit.Test;
import static org.junit.Assert.*;
import java.util.*;

public class HW1Test2 {
	HW1Test firstTest;
	Entry[] entries;
	int nLoop = 5;
	int N;
	
	public void putEntry(Hashtable<String, String[]> hashDict, Entry e) {
		hashDict.put(e.word, e.defns);
	}

	public <T extends BasicDict> Hashtable<String, String[]> toHashtable(T dict) {
		Hashtable<String, String[]> hashDict = new Hashtable<String, String[]> ();
		if (dict.getClass() == LinkedList.class) {
			LinkedList.Node first = ((LinkedList)dict).first;
			while (first != null) {
				putEntry(hashDict, first.entry);
				first = first.next;
			}
		} else if (dict.getClass() == SortedArray.class) {
			Entry[] entries = ((SortedArray)dict).entries;
			for (int i = 0; i < entries.length && entries[i] != null; i++) {
				putEntry(hashDict, entries[i]);
			}
		}
		return hashDict;
	}

	@SuppressWarnings("unchecked")
	public <T extends BasicDict> T toBasicDict(Hashtable<String, String[]> hashDict, T dict) {
		if (dict.size() == 0) {
			assertEquals(0, toHashtable(dict).size());
		} else {
			if (dict.getClass() == LinkedList.class) {
				dict = (T) (new LinkedList());
			} else if (dict.getClass() == SortedArray.class) {
				dict = (T) (new SortedArray());
			}
		}
		// First trying to create BasicDict by using the insert method
		try {
			Enumeration<String> keys = hashDict.keys();
			while(keys.hasMoreElements()){
				String word = keys.nextElement();
				Entry e = new Entry(word, hashDict.get(word));
				dict.insert(e);
			}
			assertEquals(hashDict, toHashtable(dict));
			return dict;
		} catch(AssertionError ae) {
			// Insert is not working, manually creating BasicDict
			// System.out.println("Insert is not working!");
			if (dict.getClass() == LinkedList.class) {
				dict = (T) (new LinkedList());
				Enumeration<String> keys = hashDict.keys();
				while(keys.hasMoreElements()){
					String word = keys.nextElement();
					Entry e = new Entry(word, hashDict.get(word));
					((LinkedList)dict).first = new LinkedList.Node(e, ((LinkedList)dict).first);
				}
				assertEquals(hashDict, toHashtable(dict));
				return dict;
			} else if (dict.getClass() == SortedArray.class) {
				dict = (T) (new SortedArray());
				((SortedArray)dict).entries = new Entry[hashDict.size()];
				Entry[] entries = ((SortedArray)dict).entries;
				List<String> keys = Collections.list(hashDict.keys());
				Collections.sort(keys);
				for (int i = 0; i < keys.size(); i++) {
					String word = keys.get(i);
					entries[i] = new Entry(word, hashDict.get(word));
				}
				assertEquals(hashDict, toHashtable(dict));
				return dict;
			} else {
				return null;
			}
		}
	}
	
	public HW1Test2() {
		firstTest = new HW1Test();
		entries = firstTest.entries;
		N = entries.length;
	}

	public <T extends BasicDict> void testDictionaryST(T basic) {
		firstTest.testDictionaryST(basic);
	}

	public <T extends BasicDict> void testClosestEntry(T dict) {
		Hashtable<String, String[]> hashDict;
		List<Integer> list;
		int [][] closest = new int[entries.length][2];
		int i, j, k;

		for (int loop = 0; loop < nLoop; loop++) {
			System.out.println("Resetting empty dictionary");
			hashDict = new Hashtable<String, String[]> ();
			dict = toBasicDict(hashDict, dict);
			for (i = 0; i < entries.length; i++) {
				closest[i][0] = -1;
				closest[i][1] = -1;
				firstTest.assertClosestEntry(closest[i], dict.getClosestEntry(entries[i].word));
			}
			list = firstTest.randomPermutation(entries.length);
			for (i = 0; i < entries.length; i++) {
				j = list.get(i);
				System.out.println("Inserting random entry " + j);
				putEntry(hashDict, entries[j]);
				dict = toBasicDict(hashDict, dict);
				for (k = j; k >= 0 && (closest[k][1] == -1 || closest[k][1] > j); k--) {
					closest[k][1] = j;
				}
				for (k = j; k < entries.length && closest[k][0] < j; k++) {
					closest[k][0] = j;
				}
				for (j = 0; j < entries.length; j++) {
					firstTest.assertClosestEntry(closest[j], dict.getClosestEntry(entries[j].word));
				}
			}
		}

	}

	public <T extends BasicDict> void testDelete(T dict) {
		Hashtable<String, String[]> hashDict;
		List<Integer> list;
		int i, j;

		for (int loop = 0; loop < nLoop; loop++) {
			System.out.println("Resetting full dictionary");
			hashDict = new Hashtable<String, String[]> ();
			for (i = 0; i < entries.length; i++) {
				putEntry(hashDict, entries[i]);
			}
			dict = toBasicDict(hashDict, dict);
			list = firstTest.randomPermutation(entries.length);
			for (i = 0; i < entries.length; i++) {
				j = list.get(i);
				System.out.println("Deleting random entry " + j);
				hashDict.remove(entries[j].word);
				try {
					dict.delete(entries[j].word);
				} catch (Exception e) {
					e.printStackTrace();
					fail("Thrown Exception" + e.getMessage());
				}
				assertEquals(hashDict, toHashtable(dict));
			}
		}
	}

	public <T extends BasicDict> void testInsert(T dict) {
		Hashtable<String, String[]> hashDict;
		List<Integer> list;
		int i, j;

		for (int loop = 0; loop < nLoop; loop++) {
			System.out.println("Resetting empty dictionary");
			hashDict = new Hashtable<String, String[]> ();
			dict = toBasicDict(hashDict, dict);
			list = firstTest.randomPermutation(entries.length);
			for (i = 0; i < entries.length; i++) {
				j = list.get(i);
				System.out.println("Inserting random entry " + j);
				putEntry(hashDict, entries[j]);
				dict.insert(entries[j]);
				assertEquals(hashDict, toHashtable(dict));
			}
		}
	}

	public <T extends BasicDict> void testSize(T dict) {
		Hashtable<String, String[]> hashDict = new Hashtable<String, String[]> ();
		List<Integer> list;
		int i, j, s = 0;
		
		for (int loop = 0; loop < nLoop; loop++) {
			list = firstTest.randomPermutation(entries.length);
			for (j = 0; j < entries.length; j++) {
				i = list.get(j);
				System.out.println("Checking size after including random entry " + i);
				putEntry(hashDict, entries[i]);
				dict = toBasicDict(hashDict, dict);
				s += entries[i].defns.length;
				assertEquals(s, dict.size());
			}
			
			list = firstTest.randomPermutation(entries.length);
			for (i = 0; i < entries.length; i++) {
				System.out.println("Checking size after excluding entry " + i);
				hashDict.remove(entries[i].word);
				dict = toBasicDict(hashDict, dict);
				s -= entries[i].defns.length;
				assertEquals(s, dict.size());
			}
		}
	}
	
	private void printTest(String test, int step) {
		final String[] msgs = {"* using the first test                      *",
				               "* The first test passes!                    *",
		                       "* The first test fails!                     *\n" +
		                       "* using the second test                     *",
		                       "* The second test passes!                   *",
		                       "* Both tests fail!                          *",
		                       "* The (first) test fails!                   *"};
		System.out.println("*********************************************");
		System.out.println(test);
		if (step > 0) System.out.println(msgs[step]);
		System.out.println("*********************************************");
	}

	@Test
	public void testLinkedListDictionaryST() {
		String test = "* Testing DictionaryST using LinkedList     *";
		try {
			printTest(test, 0);
			testDictionaryST(new LinkedList());
			printTest(test, 1);
		} catch(AssertionError ae) {
			printTest(test, 5);
			throw(ae);
		}
	}

	@Test
	public void testLinkedListClosestEntry() {
		String test = "* Testing getClosestEntry for LinkedList    *";
		try {
			printTest(test, 0);
			firstTest.testClosestEntry(new LinkedList());
			printTest(test, 1);
		} catch(AssertionError ae) {
			try {
				printTest(test, 2);
				testClosestEntry(new LinkedList());
				printTest(test, 3);
			} catch(AssertionError ae2) {
				printTest(test, 4);
				throw(ae2);
			}
		}
	}

	@Test
	public void testLinkedListDelete() {
		String test = "* Testing delete for LinkedList             *";
		try {
			printTest(test, 0);
			firstTest.testDelete(new LinkedList());
			printTest(test, 1);
		} catch(AssertionError ae) {
			try {
				printTest(test, 2);
				testDelete(new LinkedList());
				printTest(test, 3);
			} catch(AssertionError ae2) {
				printTest(test, 4);
				throw(ae2);
			}
		}
	}

	@Test
	public void testLinkedListInsert() {
		String test = "* Testing insert for LinkedList             *";
		try {
			printTest(test, 0);
			firstTest.testInsert(new LinkedList());
			printTest(test, 1);
		} catch(AssertionError ae) {
			try {
				printTest(test, 2);
				testInsert(new LinkedList());
				printTest(test, 3);
			} catch(AssertionError ae2) {
				printTest(test, 4);
				throw(ae2);
			}
		}
	}

	@Test
	public void testLinkedListSize() {
		String test = "* Testing size for LinkedList               *";
		try {
			printTest(test, 0);
			firstTest.testSize(new LinkedList());
			printTest(test, 1);
		} catch(AssertionError ae) {
			try {
				printTest(test, 2);
				testSize(new LinkedList());
				printTest(test, 3);
			} catch(AssertionError ae2) {
				printTest(test, 4);
				throw(ae2);
			}
		}
	}

	@Test
	public void testSortedArrayDictionaryST() {
		String test = "* Testing DictionaryST using SortedArray    *";
		try {
			printTest(test, 0);
			testDictionaryST(new SortedArray());
			printTest(test, 1);
		} catch(AssertionError ae) {
			printTest(test, 5);
			throw(ae);
		}
	}

	@Test
	public void testSortedArrayClosestEntry() {
		String test = "* Testing getClosestEntry for SortedArray   *";
		try {
			printTest(test, 0);
			firstTest.testClosestEntry(new SortedArray());
			printTest(test, 1);
		} catch(AssertionError ae) {
			try {
				printTest(test, 2);
				testClosestEntry(new SortedArray());
				printTest(test, 3);
			} catch(AssertionError ae2) {
				printTest(test, 4);
				throw(ae2);
			}
		}
	}

	@Test
	public void testSortedArrayDelete() {
		String test = "* Testing delete for SortedArray            *";
		try {
			printTest(test, 0);
			firstTest.testDelete(new SortedArray());
			printTest(test, 1);
		} catch(AssertionError ae) {
			try {
				printTest(test, 2);
				testDelete(new SortedArray());
				printTest(test, 3);
			} catch(AssertionError ae2) {
				printTest(test, 4);
				throw(ae2);
			}
		}
	}

	@Test
	public void testSortedArrayInsert() {
		String test = "* Testing insert for SortedArray            *";
		try {
			printTest(test, 0);
			firstTest.testInsert(new SortedArray());
			printTest(test, 1);
		} catch(AssertionError ae) {
			try {
				printTest(test, 2);
				testInsert(new SortedArray());
				printTest(test, 3);
			} catch(AssertionError ae2) {
				printTest(test, 4);
				throw(ae2);
			}
		}
	}

	@Test
	public void testSortedArraySize() {
		String test = "* Testing size for SortedArray              *";
		try {
			printTest(test, 0);
			firstTest.testSize(new SortedArray());
			printTest(test, 1);
		} catch(AssertionError ae) {
			try {
				printTest(test, 2);
				testSize(new SortedArray());
				printTest(test, 3);
			} catch(AssertionError ae2) {
				printTest(test, 4);
				throw(ae2);
			}
		}
	}
}