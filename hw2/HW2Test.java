package hw2;

import hw1.*;
import org.junit.Test;
import static org.junit.Assert.*;
import java.lang.management.*;
import java.util.List;

public class HW2Test {
	HW1Test hw1test;
	Entry[] entries;
	int nLoop = 5;
	int size;
	String border = "****************************************\n";
	String passed = "* Passed!                              *\n";
	String failed = "* Failed!                              *\n";
	
	public HW2Test() {
		hw1test = new HW1Test();
		randomEntries((int)(Math.random() * 32) + 32);
	}
	
	public void randomEntries(int size) {
		this.size = size;
		entries = new Entry[size];
		for (int i = 0; i < size; i++) {
			String strI = "" + i;
			String[] defns = new String[i % 4 + 1];
			for (int j = 0; j < defns.length; j++) {
				defns[j] = "Definition " + (j + 1) + " of " + strI;
			}
			entries[i] = new Entry("word " + ("00000000" + strI).substring(strI.length()), defns);
		}
	}

	public <T extends BasicDict> void testClosestEntryEfficiency(T basic, boolean random) {
		ThreadMXBean thread = ManagementFactory.getThreadMXBean();
		int i, l;
		randomEntries(1024 * 1024);
		System.out.println("Testing getClosestEntry efficiency for " + size + " entries");
		
		int[] sizes = new int[11];
		long[] times = new long[11];
		long[] avg = new long[11];
		long nanoTime = 0;
		boolean usingNanoTime = false;
		int beginInd = 0;
		int failures = 0;
		for (l = 0; l < 11; l++) {
			sizes[l] = size >> (10 - l);
		}

		for (l = 0, i = 0; l < 11; l++) {
			if (random) {
				int offset = i;
				List<Integer> list = hw1test.randomPermutation(sizes[l] - offset);
				for (; i < sizes[l]; i++) {
					basic.insert(entries[list.get(i - offset) + offset]);
				}
			} else {
				for (; i < sizes[l]; i++) {
					basic.insert(entries[i]);
				}
			}
			times[l] = thread.getCurrentThreadCpuTime();
			nanoTime = System.nanoTime();
			for (int j = 0; j < sizes[l]; j++) {
				hw1test.assertSameEntry(entries[j], basic.getClosestEntry(entries[j].word));
			}
			times[l] = thread.getCurrentThreadCpuTime() - times[l];
			if (times[l] == 0) usingNanoTime = true;
			if (usingNanoTime) {
				times[l] = System.nanoTime() - nanoTime;
			}
			avg[l] = times[l] / sizes[l];
			if (usingNanoTime && l < 3) {
				if (avg[beginInd] > 2 * avg[l]) beginInd = l;
			} else {
				if (avg[l] > avg[beginInd] * 6) failures++;
			}
			if ((!usingNanoTime && failures > 0) || failures > 2) {
				if (usingNanoTime) System.out.println("USING NANO TIME!!!");
				for (; l >= 0; l--) {
					System.out.println("Total/average CPU time elapsed for searching " + sizes[l] + " entries: Total=" + times[l] + ", Avg=" + avg[l]);
				}
				System.out.println("Search takes more than logarithmic time");
				System.out.println("Failed search performance test!");
				fail("Search is inefficient");
			}
		}
		if (usingNanoTime) System.out.println("USING NANO TIME!!!");
		for (l--; l >= 0; l--) {
			System.out.println("time elapsed until size " + sizes[l] + ": " + times[l] + " => average : " + avg[l]);
		}
		System.out.println("Passed search performance test!");
	}
	
	public <T extends BasicDict> void testInOrderInsertEfficiency(T basic) {
		ThreadMXBean thread = ManagementFactory.getThreadMXBean();
		int i, l;
		randomEntries(1024 * 1024);
		System.out.println("Testing in order insertion efficiency for " + size + " entries");
		
		int[] sizes = new int[11];
		long[] times = new long[11];
		long[] avg = new long[11];
		long nanoTime = 0;
		boolean usingNanoTime = false;
		int beginInd = 0;
		int failures = 0;
		for (l = 0; l < 11; l++) {
			sizes[l] = size >> (10 - l);
		}

		long timeBefore = thread.getCurrentThreadCpuTime();
		nanoTime = System.nanoTime();
		for (l = 0, i = 0; l < 11; l++) {
			times[l] = timeBefore;
			for (; i < sizes[l]; i++) {
				basic.insert(entries[i]);
			}
			times[l] = thread.getCurrentThreadCpuTime() - times[l];
			if (times[l] == 0) usingNanoTime = true;
			if (usingNanoTime) {
				times[l] = System.nanoTime() - nanoTime;
			}
			avg[l] = times[l] / sizes[l];
			if (usingNanoTime && l < 3) {
				if (avg[beginInd] > 2 * avg[l]) beginInd = l;
			} else {
				if (avg[l] > avg[beginInd] * 4) failures++;
			}
			if ((!usingNanoTime && failures > 0) || failures > 2) {
				if (usingNanoTime) System.out.println("USING NANO TIME!!!");
				for (; l >= 0; l--) {
					System.out.println("Total/average CPU time elapsed for inserting " + sizes[l] + " random entries: Total=" + times[l] + ", Avg=" + avg[l]);
				}
				System.out.println("In order insertion takes more than constant time");
				System.out.println("Failed in order insertion performance test!");
				fail("In order insertion is inefficient");
			}
		}
		if (usingNanoTime) System.out.println("USING NANO TIME!!!");
		for (l--; l >= 0; l--) {
			System.out.println("time elapsed until size " + sizes[l] + ": " + times[l] + " => average : " + avg[l]);
		}
		System.out.println("Passed in order insertion performance test!");
	}
	
	public <T extends BasicDict> void testRandomInsertEfficiency(T basic) {
		ThreadMXBean thread = ManagementFactory.getThreadMXBean();
		int i, j, l;
		randomEntries(1024 * 1024);
		System.out.println("Testing random insertion efficiency for " + size + " random entries");
		
		int[] sizes = new int[11];
		long[] times = new long[11];
		long[] avg = new long[11];
		long nanoTime = 0;
		boolean usingNanoTime = false;
		int beginInd = 0;
		int failures = 0;
		for (l = 0; l < 11; l++) {
			sizes[l] = size >> (10 - l);
		}

		List<Integer> list = hw1test.randomPermutation(size);
		long timeBefore = thread.getCurrentThreadCpuTime();
		nanoTime = System.nanoTime();
		for (l = 0, j = 0; l < 11; l++) {
			times[l] = timeBefore;
			for (; j < sizes[l]; j++) {
				i = list.get(j);
				basic.insert(entries[i]);
			}
			times[l] = thread.getCurrentThreadCpuTime() - times[l];
			if (times[0] == 0) usingNanoTime = true;
			if (usingNanoTime) {
				times[l] = System.nanoTime() - nanoTime;
			}
			avg[l] = times[l] / sizes[l];
			if (usingNanoTime && l < 3) {
				if (avg[beginInd] > 2 * avg[l]) beginInd = l;
			} else {
				if (avg[l] > avg[beginInd] * 4) failures++;
			}
			if ((!usingNanoTime && failures > 0) || failures > 2) {
				if (usingNanoTime) System.out.println("USING NANO TIME!!!");
				for (; l >= 0; l--) {
					System.out.println("Total/average CPU time elapsed for inserting " + sizes[l] + " random entries: Total=" + times[l] + ", Avg=" + avg[l]);
				}
				System.out.println("Random insertion takes more than logarithmic time! (which is also more than constant time)");
				System.out.println("Failed random insertion performance test!");
				fail("Random insertion is inefficient");
			}
		}
		if (usingNanoTime) System.out.println("USING NANO TIME!!!");
		for (l--; l >= 0; l--) {
			System.out.println("time elapsed until size " + sizes[l] + ": " + times[l] + " => average : " + avg[l]);
		}
		System.out.println("Passed random insertion performance test!");
	}
	
	private Entry[] bstToEntries(BinarySearchTree.Node root) {
		if (root == null) return new Entry[0];
		Entry[] left = bstToEntries(root.left);
		Entry[] right = bstToEntries(root.right);
		Entry[] total = new Entry[left.length + right.length + 1];
		int i, j;
		for (i = 0; i < left.length; i++) {
			total[i] = left[i];
		}
		total[i] = root.entry;
		for (i++, j = 0; j < right.length; j++, i++) {
			total[i] = right[j];
		}
		return total;
	}
	
	public void assertTreeEntries(Entry[] entries, BinarySearchTree bst) {
		Entry[] bstEntries = bstToEntries(bst.root);
		int i, j;
		for (i = 0, j = 0; i < entries.length; i++) {
			if (entries[i] == null) continue;
			if (j >= bstEntries.length) fail("Deleted extra nodes!");
			hw1test.assertSameEntry(entries[i], bstEntries[j]);
			j++;
		}
	}
	
	public void assertSameTree(BinarySearchTree.Node root, BinarySearchTree.Node root2) {
		if (root == null || root2 == null) {
			assertEquals(root, root2);
		} else {
			hw1test.assertSameEntry(root.entry, root2.entry);
			assertSameTree(root.left, root2.left);
			assertSameTree(root.right, root2.right);
		}
	}
	
	private BinarySearchTree.Node createTree(Entry[] entries) {
		if (entries.length == 0) {
			return null;
		} else {
			BinarySearchTree.Node root = new BinarySearchTree.Node(entries[0]);
			Entry[] left, right;
			int i, l, r;
			for (l = 0, r = 0, i = 1; i < entries.length && entries[i] != null; i++) {
				if (entries[i].word.compareTo(entries[0].word) <= 0) {
					l++;
				} else {
					r++;
				}
			}
			left = new Entry[l];
			right = new Entry[r];
			for (l = 0, r = 0, i = 1; i < entries.length && entries[i] != null; i++) {
				if (entries[i].word.compareTo(entries[0].word) <= 0) {
					left[l] = entries[i];
					l++;
				} else {
					right[r] = entries[i];
					r++;
				}
			}
			root.left = createTree(left);
			root.right = createTree(right);
			return root;
		}
	}
	
	public BinarySearchTree toBST(Entry[] bstEntries) {
		BinarySearchTree bstAuto = new BinarySearchTree();
		for (int i = 0; i < bstEntries.length && bstEntries[i] != null; i++) {
			bstAuto.insert(bstEntries[i]);
		}
		
		BinarySearchTree bstManual = new BinarySearchTree();
		if (bstEntries[0] != null) bstManual.root = createTree(bstEntries);
		
		try {
			assertSameTree(bstManual.root, bstAuto.root);
			return bstAuto;
		} catch(AssertionError ae) {
			// Insert is not working, manually creating BasicDict
			// System.out.println("Insert is not working!");
			return bstManual;
		}
	}
	
	public void assertClosestEntry(int[] index, Entry t) {
		Entry[] e = {index[0] == -1 ? null : entries[index[0]],
					 index[1] == -1 ? null : entries[index[1]]};
		if (e[0] == null && e[1] == null) {
			assertEquals(null, t);
		} else if (e[0] == null) {
			hw1test.assertSameEntry(e[1], t);
		} else if (e[1] == null) {
			hw1test.assertSameEntry(e[0], t);
		} else {
			try {
			    hw1test.assertSameEntry(e[0], t);
			} catch(AssertionError ae) {
			    hw1test.assertSameEntry(e[1], t);
			}
		}
	}

	public void testClosestEntry() {
		BinarySearchTree bst;
		Entry[] bstEntries;
		List<Integer> list;
		int [][] closest = new int[entries.length][2];
		int i, j, k;

		System.out.println("Resetting empty dictionary");
		bstEntries = new Entry[entries.length];
		bst = toBST(bstEntries);
		for (i = 0; i < entries.length; i++) {
			closest[i][0] = -1;
			closest[i][1] = -1;
			assertClosestEntry(closest[i], bst.getClosestEntry(entries[i].word));
		}
		list = hw1test.randomPermutation(entries.length);
		for (i = 0; i < entries.length; i++) {
			j = list.get(i);
			System.out.println("Inserting random entry " + j);
			bstEntries[i] = entries[j];
			bst = toBST(bstEntries);
			for (k = j; k >= 0 && (closest[k][1] == -1 || closest[k][1] > j); k--) {
				closest[k][1] = j;
			}
			for (k = j; k < entries.length && closest[k][0] < j; k++) {
				closest[k][0] = j;
			}
			for (j = 0; j < entries.length; j++) {
				assertClosestEntry(closest[j], bst.getClosestEntry(entries[j].word));
			}
		}
	}
	
	public void testDelete() {
		BinarySearchTree bst;
		Entry[] bstEntries;
		List<Integer> list;
		int i, j;

		System.out.println("Resetting full dictionary");
		bstEntries = new Entry[entries.length];
		list = hw1test.randomPermutation(entries.length);
		for (i = 0; i < entries.length; i++) {
			j = list.get(i);
			bstEntries[i] = entries[j];
		}
		bst = toBST(bstEntries);
		list = hw1test.randomPermutation(entries.length);
		for (i = 0; i < entries.length; i++) {
			j = list.get(i);
			System.out.println("Deleting random entry " + j);
			try {
				bst.delete(entries[j].word);
				entries[j] = null;
			} catch (Exception e) {
				e.printStackTrace();
				fail("Thrown Exception: " + e.getMessage());
			}
			assertTreeEntries(entries, bst);
		}
	}
	
	public void testInsert() {
		BinarySearchTree bst;
		Entry[] bstEntries;
		List<Integer> list;
		int i, j;

		for (int loop = 0; loop < nLoop; loop++) {
			System.out.println("Resetting empty dictionary");
			bstEntries = new Entry[entries.length];
			bst = toBST(bstEntries);
			list = hw1test.randomPermutation(entries.length);
			for (i = 0; i < entries.length; i++) {
				j = list.get(i);
				System.out.println("Inserting random entry " + j);
				bstEntries[i] = entries[j];
				bst.insert(entries[j]);
				assertSameTree(toBST(bstEntries).root, bst.root);
			}
		}
	}

	public void testSize() {
		Entry[] bstEntries = new Entry[entries.length];
		List<Integer> list;
		int i, j, s = 0;
		
		list = hw1test.randomPermutation(entries.length);
		for (j = 0; j < entries.length; j++) {
			i = list.get(j);
			System.out.println("Checking size after including random entry " + i);
			bstEntries[j] = entries[i]; 
			s += entries[i].defns.length;
			assertEquals(s, toBST(bstEntries).size());
		}
	}

	@Test
	public void testBSTSearchPerformance() {
		System.out.println("Testing performance of searches in BST");
		System.out.println("First, testing correctness");
		testBSTInsert();
		testBSTClosestEntry();
		System.out.println("Then, need to check random insertion efficiency as well");
		testRandomInsertEfficiency(new BinarySearchTree());
		testClosestEntryEfficiency(new BinarySearchTree(), true);
	}
	
	@Test
	public void testBSTInsertionPerformance() {
		System.out.println("Testing performance of random insertions in BST");
		System.out.println("First, testing correctness");
		testBSTInsert();
		testRandomInsertEfficiency(new BinarySearchTree());
	}
	
	@Test
	public void testSortedArraySearchPerformance() {
		System.out.println("Testing performance of searches in SortedArray");
		System.out.println("First, testing correctness");
		HW1Test2 test = new HW1Test2();
		test.testSortedArrayInsert();
		test.testSortedArrayClosestEntry();
		System.out.println("Then, need to check in order insertion efficiency as well");
		testInOrderInsertEfficiency(new SortedArray());
		testClosestEntryEfficiency(new SortedArray(), false);
	}

	@Test
	public void testSortedArrayInsertionPerformance() {
		System.out.println("Testing performance of in order insertion in SortedArray");
		System.out.println("First, testing correctness");
		HW1Test2 test = new HW1Test2();
		test.testSortedArrayInsert();
		testInOrderInsertEfficiency(new SortedArray());
	}
	
	@Test
	public void testLinkedListInsertionPerformance() {
		System.out.println("Testing performance of random insertions in LinkedList");
		System.out.println("First, testing correctness");
		HW1Test2 test = new HW1Test2();
		test.testLinkedListInsert();
		testRandomInsertEfficiency(new LinkedList());
		test.testLinkedListClosestEntry();
	}

	@Test
	public void testBSTDictionaryST() {
		String test = "* Testing DictionaryST for BST         *\n";
		try {
			System.out.print(border + test + border);
			hw1test.testDictionaryST(new BinarySearchTree());
			System.out.print(border + test + passed + border);
		} catch(AssertionError ae) {
			System.out.print(border + test + failed + border);
			throw(ae);
		}
	}

	@Test
	public void testBSTClosestEntry() {
		String test = "* Testing getClosestEntry for BST      *\n";
		try {
			System.out.print(border + test + border);
			testClosestEntry();
			System.out.print(border + test + passed + border);
		} catch(AssertionError ae) {
			System.out.print(border + test + failed + border);
			throw(ae);
		}
	}

	@Test
	public void testBSTDelete() {
		String test = "* Testing delete for BST               *\n";
		try {
			System.out.print(border + test + border);
			testDelete();
			System.out.print(border + test + passed + border);
		} catch(AssertionError ae) {
			System.out.print(border + test + failed + border);
			throw(ae);
		}
	}

	@Test
	public void testBSTInsert() {
		String test = "* Testing insert for BST               *\n";
		try {
			System.out.print(border + test + border);
			testInsert();
			System.out.print(border + test + passed + border);
		} catch(AssertionError ae) {
			System.out.print(border + test + failed + border);
			throw(ae);
		}
	}

	@Test
	public void testBSTSize() {
		String test = "* Testing size for BST                 *\n";
		try {
			System.out.print(border + test + border);
			testSize();
			System.out.print(border + test + passed + border);
		} catch(AssertionError ae) {
			System.out.print(border + test + failed + border);
			throw(ae);
		}
	}
}