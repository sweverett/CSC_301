package hw1;

import org.junit.Test;
import static org.junit.Assert.*;
import java.util.*;

public class HW1Test {
	String[] words = {"Apple", "Arm", "Bug", "Cat", "Java", "Yellow"};
	
	String[][] defns = {{"The usually round, red or yellow, edible fruit of a small tree."},
						{"The upper limb from the shoulder to the elbow.",
					     "An administrative or operational branch of an organization.",
					     "Weapons, especially firearms."},
			            {"Any insect or insectlike invertebrate.",
			             "Any microorganism, especially a virus.",
			             "A defect or imperfection, as in a computer program; glitch",
			             "A hidden microphone or other electronic eavesdropping device."},
			            {"A small domesticated carnivore."},
			            {"The main island of Indonesia.",
			             "Coffee",
			             "A high-level, object-oriented computer programming language."},
			            {"A color like that of egg yolk, ripe lemons, etc."}
					   };

	Entry[] entries;
	String[][] pairs;
	int[] indices;
	int nLoop = 5;
	int N = words.length;
	int T = 0;
	
	public HW1Test() {
		int i, j, t;
		
		entries = new Entry[N];
		for (i = 0, T = 0; i < N; i++) {
			entries[i] = new Entry(words[i], defns[i]);
			T += entries[i].defns.length;
		}
		
		pairs = new String[T][2];
		indices = new int[T];
		for (i = 0, t = 0; i < N; i++) {
			for (j = 0; j < entries[i].defns.length; j++, t++) {
				pairs[t][0] = entries[i].word;
				pairs[t][1] = entries[i].defns[j];
				indices[t] = i;
			}
		}
	}

	public List<Integer> randomPermutation(int n) {
		List<Integer> list = new ArrayList<Integer> ();
		for (int i = 0; i < n; i++) {
			list.add(i);
		}
		Collections.shuffle(list);
		return list;
	}
	
	public void assertSameEntry(Entry e, Entry t) {
		if (e == null || t == null) {
			assertEquals(e, t);
		} else {
			assertEquals(e.word, t.word);
			List<String> tDefns = new ArrayList<String> (Arrays.asList(t.defns));
			Collections.sort(tDefns);
			List<String> eDefns = new ArrayList<String> (Arrays.asList(e.defns));
			Collections.sort(eDefns);
			for (int i = 0; i < eDefns.size(); i++) {
				assertEquals(eDefns.get(i), tDefns.get(i));
			}
		}
	}

	public void assertDiffEntry(Entry e, Entry t) {
		if (e == null || t == null) {
			return;
		} else {
			assertNotEquals(e.word, t.word);
		}
	}
	
	public void assertClosestEntry(int[] index, Entry t) {
		Entry[] e = {index[0] == -1 ? null : entries[index[0]],
					 index[1] == -1 ? null : entries[index[1]]};
		if (e[0] == null && e[1] == null) {
			assertEquals(null, t);
		} else if (e[0] == null) {
			assertSameEntry(e[1], t);
		} else if (e[1] == null) {
			assertSameEntry(e[0], t);
		} else {
			try {
			    assertSameEntry(e[0], t);
			} catch(AssertionError ae) {
			    assertSameEntry(e[1], t);
			}
		}
	}
	
	public <T extends BasicDict> void testDictionaryST(T basic) {
		int i, j, k;
		List<Integer> list;
		Entry[] testing = new Entry[N];
		
		DictionaryST<T> dict = new DictionaryST<T>(basic);
		assertEquals(0, dict.size());
		
		for (i = 0; i < T; i++) {
			System.out.println("Putting pair " + pairs[i][0] + " : " + pairs[i][1]);
			dict.put(pairs[i][0], pairs[i][1]);
			assertEquals(i + 1, dict.size());
		}
		for (i = 0; i < T; i++) {
			System.out.println("Deleting pair " + pairs[i][0] + " : " + pairs[i][1]);
			try {
				dict.delete(pairs[i][0], pairs[i][1]);
			} catch (Exception e) {
				e.printStackTrace();
				fail("Thrown Exception" + e.getMessage());
			}
			assertEquals(T - i - 1, dict.size());
		}
		for (i = T - 1; i >= 0; i--) {
			System.out.println("Putting in reverse pair " + pairs[i][0] + " : " + pairs[i][1]);
			dict.put(pairs[i][0], pairs[i][1]);
			assertEquals(T - i, dict.size());
		}
		for (i = T - 1; i >= 0; i--) {
			System.out.println("Deleting in reverse pair " + pairs[i][0] + " : " + pairs[i][1]);
			try {
				dict.delete(pairs[i][0], pairs[i][1]);
			} catch (Exception e) {
				e.printStackTrace();
				fail("Thrown Exception" + e.getMessage());
			}
			assertEquals(i, dict.size());
		}
		for (i = 0; i < T; i++) {
			System.out.println("Putting first pair " + pairs[i][0] + " : " + pairs[i][1]);
			dict.put(pairs[i][0], pairs[i][1]);
			assertEquals(i + 1, dict.size());
		}
		for (i = 0; i < T; i++) {
			System.out.println("Putting duplicate pair " + pairs[i][0] + " : " + pairs[i][1]);
			dict.put(pairs[i][0], pairs[i][1]);
			assertEquals(T, dict.size());
		}
		list = randomPermutation(T);
		for (j = 0; j < T; j++) {
			i = list.get(j);
			System.out.println("Deleting first pair " + pairs[i][0] + " : " + pairs[i][1]);
			try {
				dict.delete(pairs[i][0], pairs[i][1]);
			} catch (Exception e) {
				e.printStackTrace();
				fail("Thrown Exception" + e.getMessage());
			}
			assertEquals(T - j - 1, dict.size());
			System.out.println("Deleting duplicate pair " + pairs[i][0] + " : " + pairs[i][1]);
			boolean failed = false;
			try {
				dict.delete(pairs[i][0], pairs[i][1]);
			} catch (Exception e) {
				failed = true;
			}
			assertEquals(failed, true);
		}
		
		for (i = 0; i < N; i++) {
			testing[i] = new Entry(entries[i].word, null);
		}

		for (int loop = 0; loop < nLoop; loop++) {
			list = randomPermutation(T);
			for (j = 0; j < T; j++) {
				i = list.get(j);
				System.out.println("Putting random pair " + pairs[i][0] + " : " + pairs[i][1]);
				dict.put(pairs[i][0], pairs[i][1]);
				String[] dfns = testing[indices[i]].defns;
				String[] temp = new String[dfns == null ? 1 : dfns.length + 1];
				for (k = 0; k < temp.length - 1; k++) {
					temp[k] = dfns[k];
				}
				temp[k] = pairs[i][1];
				testing[indices[i]].defns = temp;
				assertSameEntry(testing[indices[i]], basic.getClosestEntry(pairs[i][0]));
			}
			list = randomPermutation(T);
			for (j = 0; j < T; j++) {
				i = list.get(j);
				System.out.println("Deleting random pair " + pairs[i][0] + " : " + pairs[i][1]);
				try {
					dict.delete(pairs[i][0], pairs[i][1]);
				} catch (Exception e) {
					e.printStackTrace();
					fail("Thrown Exception" + e.getMessage());
				}
				String[] dfns = testing[indices[i]].defns;
				if (dfns.length == 1) {
					testing[indices[i]].defns = null;
					assertDiffEntry(testing[indices[i]], basic.getClosestEntry(pairs[i][0]));
				} else {
					String[] temp = new String[dfns.length - 1];
					for (k = 0; k < temp.length && dfns[k] != pairs[i][1]; k++) {
						temp[k] = dfns[k];
					}
					for (k++; k <= temp.length; k++) {
						temp[k - 1] = dfns[k];
					}
					testing[indices[i]].defns = temp;
					assertSameEntry(testing[indices[i]], basic.getClosestEntry(pairs[i][0]));
				}
			}
		}
	}

	public <T extends BasicDict> void testClosestEntry(T basic) {
		int i, j, k;
		int [][] closest = new int[entries.length][2];
		List<Integer> list;

		DictionaryST<T> dict = new DictionaryST<T>(basic);
		assertEquals(0, dict.size());
		
		for (i = 0; i < entries.length; i++) {
			closest[i][0] = -1;
			closest[i][1] = -1;
			assertClosestEntry(closest[i], basic.getClosestEntry(entries[i].word));
		}

		for (j = 0; j < entries.length; j++) {
			System.out.println("Inserting entry " + j);
			basic.insert(entries[j]);
			for (k = j; k >= 0 && (closest[k][1] == -1 || closest[k][1] > j); k--) {
				closest[k][1] = j;
			}
			for (k = j; k < entries.length && closest[k][0] < j; k++) {
				closest[k][0] = j;
			}
			for (i = 0; i < entries.length; i++) {
				assertClosestEntry(closest[i], basic.getClosestEntry(entries[i].word));
			}
		}
		for (j = 0; j < entries.length; j++) {
			try {
				System.out.println("Deleting entry " + j);
				basic.delete(entries[j].word);
				int l;
				l = j < entries.length - 1 ? closest[j + 1][1] : -1;
				for (k = j; k >= 0 && closest[k][1] == j; k--) {
					closest[k][1] = l;
				}
				l = j > 0 ? closest[j - 1][0] : -1;
				for (k = j; k < entries.length && closest[k][0] == j; k++) {
					closest[k][0] = l;
				}
				for (i = 0; i < entries.length; i++) {
					assertClosestEntry(closest[i], basic.getClosestEntry(entries[i].word));
				}
			} catch (Exception e) {
				e.printStackTrace();
				fail("Thrown Exception" + e.getMessage());
			}
		}
		
		for (j = entries.length - 1; j >= 0; j--) {
			System.out.println("Inserting in reverse entry " + j);
			basic.insert(entries[j]);
			for (k = j; k >= 0 && (closest[k][1] == -1 || closest[k][1] > j); k--) {
				closest[k][1] = j;
			}
			for (k = j; k < entries.length && closest[k][0] < j; k++) {
				closest[k][0] = j;
			}
			for (i = 0; i < entries.length; i++) {
				assertClosestEntry(closest[i], basic.getClosestEntry(entries[i].word));
			}
		}
		for (j = entries.length - 1; j >= 0; j--) {
			try {
				System.out.println("Deleting in reverse entry " + j);
				basic.delete(entries[j].word);
				int l;
				l = j < entries.length - 1 ? closest[j + 1][1] : -1;
				for (k = j; k >= 0 && closest[k][1] == j; k--) {
					closest[k][1] = l;
				}
				l = j > 0 ? closest[j - 1][0] : -1;
				for (k = j; k < entries.length && closest[k][0] == j; k++) {
					closest[k][0] = l;
				}
				for (i = 0; i < entries.length; i++) {
					assertClosestEntry(closest[i], basic.getClosestEntry(entries[i].word));
				}
			} catch (Exception e) {
				e.printStackTrace();
				fail("Thrown Exception" + e.getMessage());
			}
		}
		
		for (int loop = 0; loop < nLoop; loop++) {
			list = randomPermutation(entries.length);
			for (i = 0; i < entries.length; i++) {
				j = list.get(i);
				System.out.println("Inserting random entry " + j);
				basic.insert(entries[j]);
				for (k = j; k >= 0 && (closest[k][1] == -1 || closest[k][1] > j); k--) {
					closest[k][1] = j;
				}
				for (k = j; k < entries.length && closest[k][0] < j; k++) {
					closest[k][0] = j;
				}
				for (j = 0; j < entries.length; j++) {
					assertClosestEntry(closest[j], basic.getClosestEntry(entries[j].word));
				}
			}
			for (i = 0; i < entries.length; i++) {
				try {
					j = list.get(i);
					System.out.println("Deleting same random entry " + j);
					basic.delete(entries[j].word);
					int l;
					l = j < entries.length - 1 ? closest[j + 1][1] : -1;
					for (k = j; k >= 0 && closest[k][1] == j; k--) {
						closest[k][1] = l;
					}
					l = j > 0 ? closest[j - 1][0] : -1;
					for (k = j; k < entries.length && closest[k][0] == j; k++) {
						closest[k][0] = l;
					}
					for (j = 0; j < entries.length; j++) {
						assertClosestEntry(closest[j], basic.getClosestEntry(entries[j].word));
					}
				} catch (Exception e) {
					e.printStackTrace();
					fail("Thrown Exception" + e.getMessage());
				}
			}
		}
	}

	public <T extends BasicDict> void testDelete(T basic) {
		int i, j;
		List<Integer> list;

		DictionaryST<T> dict = new DictionaryST<T>(basic);
		assertEquals(0, dict.size());
		
		for (int loop = 0; loop < nLoop; loop++) {
			list = randomPermutation(entries.length);
			for (i = 0; i < entries.length; i++) {
				System.out.println("Inserting random entry " + i);
				basic.insert(entries[i]);
			}
			for (i = 0; i < entries.length; i++) {
				try {
					j = list.get(i);
					System.out.println("Deleting same random entry " + j);
					basic.delete(entries[j].word);
					assertDiffEntry(entries[j], basic.getClosestEntry(entries[j].word));
				} catch (Exception e) {
					e.printStackTrace();
					fail("Thrown Exception" + e.getMessage());
				}
			}
			assertEquals(0, dict.size());
		}
	}

	public <T extends BasicDict> void testInsert(T basic) {
		List<Integer> list;
		int i, j;

		DictionaryST<T> dict = new DictionaryST<T>(basic);
		assertEquals(0, dict.size());
		
		for (int loop = 0; loop < nLoop; loop++) {
			list = randomPermutation(entries.length);
			for (i = 0; i < entries.length; i++) {
				j = list.get(i);
				System.out.println("Inserting random entry " + j);
				basic.insert(entries[j]);
				assertSameEntry(entries[j], basic.getClosestEntry(entries[j].word));
			}
			for (i = 0; i < entries.length; i++) {
				try {
					System.out.println("Deleting same random entry " + i);
					basic.delete(entries[i].word);
					assertDiffEntry(entries[i], basic.getClosestEntry(entries[i].word));
				} catch (Exception e) {
					e.printStackTrace();
					fail("Thrown Exception" + e.getMessage());
				}
			}
			assertEquals(0, dict.size());
		}
	}

	public <T extends BasicDict> void testSize(T basic) {
		DictionaryST<T> dict = new DictionaryST<T>(basic);
		int i, s = 0;
		assertEquals(s, dict.size());
		for (i = 0; i < entries.length; i++) {
			System.out.println("Inserting entry " + i);
			basic.insert(entries[i]);
			s += entries[i].defns.length;
			assertEquals(s, dict.size());
		}
		System.out.print(dict.toString());
		
		for (i = 0; i < entries.length; i++) {
			try {
				System.out.println("Deleting entry " + i);
				basic.delete(entries[i].word);
			} catch (Exception e) {
				e.printStackTrace();
				fail("Thrown Exception" + e.getMessage());
			}
			s -= entries[i].defns.length;
			assertEquals(s, dict.size());
		}

		assertEquals(s, dict.size());
		for (i = entries.length - 1; i >= 0; i--) {
			System.out.println("Inserting in reverse entry " + i);
			basic.insert(entries[i]);
			s += entries[i].defns.length;
			assertEquals(s, dict.size());
		}
		System.out.print(dict.toString());
		
		for (i = entries.length - 1; i >= 0; i--) {
			try {
				System.out.println("Deleting in reverse entry " + i);
				basic.delete(entries[i].word);
			} catch (Exception e) {
				e.printStackTrace();
				fail("Thrown Exception" + e.getMessage());
			}
			s -= entries[i].defns.length;
			assertEquals(s, dict.size());
		}
	}
	
	@Test
	public void testLinkedListDictionaryST() {
		System.out.println("Testing DictionaryST using LinkedList");
		testDictionaryST(new LinkedList());
	}

	@Test
	public void testLinkedListClosestEntry() {
		System.out.println("Testing getClosestEntry for LinkedList");
		testClosestEntry(new LinkedList());
	}

	@Test
	public void testLinkedListDelete() {
		System.out.println("Testing delete for LinkedList");
		testDelete(new LinkedList());
	}

	@Test
	public void testLinkedListInsert() {
		System.out.println("Testing insert for LinkedList");
		testInsert(new LinkedList());
	}

	@Test
	public void testLinkedListSize() {
		System.out.println("Testing size for LinkedList");
		testSize(new LinkedList());
	}

	@Test
	public void testSortedArrayDictionaryST() {
		System.out.println("Testing DictionaryST using SortedArray");
		testDictionaryST(new SortedArray());
	}

	@Test
	public void testSortedArrayClosestEntry() {
		System.out.println("Testing getClosestEntry for SortedArray");
		testClosestEntry(new SortedArray());
	}

	@Test
	public void testSortedArrayDelete() {
		System.out.println("Testing delete for SortedArray");
		testDelete(new SortedArray());
	}

	@Test
	public void testSortedArrayInsert() {
		System.out.println("Testing insert for SortedArray");
		testInsert(new SortedArray());
	}

	@Test
	public void testSortedArraySize() {
		System.out.println("Testing size for SortedArray");
		testSize(new SortedArray());
	}
	
}