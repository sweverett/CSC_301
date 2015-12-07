//
// BASIC DICTIONARY INTERFACE
// 
// This interface defines four methods for any data structure that
// that may be used as base for the Symbol Table class DictionaryST
// 
// The coding part of the homework requires you to implement these
// four methods for
//
// 1) Linked lists (LinkedList.java) utilizing sequential search
// 2) Sorted arrays (SortedArray.java) utilizing binary search
//

package hw1;

public interface BasicDict {
	// This method returns the closest word entry in the dictionary
	// in alphabetical order
	public Entry getClosestEntry(String w);

	// This method removes a whole entry from the dictionary
	public void delete(String word) throws Exception;

	// This method inserts a new entry into the dictionary
	public void insert(Entry e);
	
	// This method returns the total number of definitions. Note that
	// this is different than the total number of words defined. 
	public int size();
}