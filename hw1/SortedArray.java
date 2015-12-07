//
// SORTED ARRAY DICTIONARY
// 
// This class uses a sorted array to organize the entries in a dictionary.
//
// HOMEWORK in this file is to implement:
//
// 1) public Entry getClosestEntry(String w)
// 2) public void delete(String w) throws Exception
// 3) public void insert(Entry e)
// 4) public int size()
//
// Modified by Spencer Everett, 9/15/2015

package hw1;

public class SortedArray implements BasicDict {
	private final int minSize = 8;
	private int size;
	private Entry[] entries;
	private int N;
	
	// Empty dictionary constructor
	public SortedArray () {
		entries = new Entry[minSize];
		size = minSize;
		N = 0;
	}

	// This method returns the closest word entry in the dictionary
	// in alphabetical order
	public Entry getClosestEntry(String w) {
		int r = rank(w);
		if (r==-1) {
			return null;
		}
		if (r>=N) {
			r--;
		}
		return entries[r];
	}
	
	// This method removes a whole entry from the dictionary
	public void delete(String w) throws Exception {
		int r = rank(w);		
		if (r==-1) {
			throw new Exception("Nonexistent deletion!");
		}
		else if (r<N && entries[r].word.equals(w)) {
			for (int j=r;j<N;j++) {
				entries[j] = entries[j+1];
			}
			entries[N] = null;
			N--;			
			return;
		}
		else {
			throw new Exception("Nonexistent deletion!");
		}
	}

	// This method inserts a new entry into the dictionary
	public void insert(Entry e) {
		if (N==size) {
			doubleSize();
		}		
		int r = rank(e.word);
		
		if (r==-1) {
			entries[0] = e;
			N++;
		}
		else {
			for (int j=N; j>r; j--) {
				entries[j] = entries[j-1];
			}
			entries[r] = e;
			N++;
		}
	}
	
	// This method returns the total number of definitions. Note that
	// this is different than the total number of words defined. 
	public int size() {
		int size = 0;
		for (int i=0; i<N; i++) {
			size += entries[i].defns.length;
		}
		return size;
	}
	
	private int rank(String w) {
		if (N==0) return -1;
		int low = 0, high = N-1;
		
		while(low<=high) {
			int mid = (low+high)/2;
			int cmp = entries[mid].word.compareTo(w);
			
			if (cmp<0) low = mid+1;
			else if (cmp>0) high = mid-1;
			else return mid;
		}
		return low;
	}
	
	private void doubleSize() {
		size *=2;
		Entry[] newEntries = new Entry[size];
		
		for (int i=0; i<N; i++) {
			newEntries[i] = entries[i];
		}
		entries = newEntries;
		return;
	}

	// Recursively computes a formatted string for partial dictionary
	private String entriesToString(int i) {
		if (i < entries.length && entries[i] != null) {
			return entries[i].toString() + entriesToString(i + 1);
		} else {
			return "";
		}
	}

	// Returns a formatted string for the whole dictionary
	public String toString () {
		return entriesToString(0);
	}
}