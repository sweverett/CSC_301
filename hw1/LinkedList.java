//
// LINKED LIST DICTIONARY
// 
// This class uses a linked list to organize the entries in a dictionary.
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

public class LinkedList implements BasicDict {
	private Node first;

	// Helper Node class for defining a linked list 
	private static class Node {
		public Entry entry;
		public Node next;
		
		// Default constructor
		public Node(Entry entry, Node next) {
			this.entry = entry;
			this.next = next;
		}

		// Recursively computes a formatted string for partial dictionary
		public String toString () {
			return entry.toString() + "\n" + (next == null ? "" : next.toString ());
		}
	}

	// Empty dictionary constructor
	public LinkedList () {
	}
	
	// This method returns the closest word entry in the dictionary
	// in alphabetical order
	public Entry getClosestEntry(String w) {
		Entry lower = null;
		Entry upper = null;
		for (Node n=this.first; n!=null; n=n.next) {
			if (n.entry.word.equals(w)) {
				return n.entry;
			} else if (n.entry.word.compareTo(w)>0 && upper==null) {
				upper = n.entry;
			} else if (n.entry.word.compareTo(w)>0 && n.entry.word.compareTo(upper.word)<0) {
				upper = n.entry;
			} else if (n.entry.word.compareTo(w)<0 && lower==null) {
				lower = n.entry;
			} else if (n.entry.word.compareTo(w)<0 && n.entry.word.compareTo(lower.word)>0) {
				lower = n.entry;
			}			
		}		
		if (upper==null && lower==null){
			return null;
		} 
		else if (lower==null) {
			return upper;
		} 
		else if (upper==null) {
			return lower;
		} 
		else {
			return upper;
		}
	}

	// This method removes a whole entry from the dictionary
	public void delete(String w) throws Exception {
		Node previous = null;
		for(Node n=this.first; n!=null; n=n.next) {
			if (n.entry.word.equals(w)) {
				if (n==this.first) {
					this.first = n.next;
				} else{
					previous.next = n.next;
				}			
				return;
			} else {
				previous = n;
			}
		}
	}
	
	// This method inserts a new entry into the dictionary
	public void insert(Entry e) {
		first = new Node(e,first);
	}
	
	// This method returns the total number of definitions. Note that
	// this is different than the total number of words defined. 
	public int size() {
		int size = 0;
		for (Node n=this.first; n!=null; n=n.next) {
			size += n.entry.defns.length;
		}
		return size;
	}
	
	// Returns a formatted string for the whole dictionary
	public String toString () {
		return (first == null ? "" : first.toString());
	}
}