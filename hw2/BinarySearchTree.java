//
// BINARY SEARCH TREE DICTIONARY
// 
// This class uses a binary search tree to organize the entries in a dictionary.
//
// HOMEWORK in this file is to implement:
//
// 1) public Entry getClosestEntry(String w)
// 2) public void delete(String w) throws Exception
// 3) public void insert(Entry e)
// 4) public int size()

// Written by Spencer Everett
// 9/29/2015

package hw2;

import hw1.*;

public class BinarySearchTree implements BasicDict {
	public Node root;

	// Helper Node class for defining a linked list 
	public static class Node {
		public Entry entry;
		public Node left, right;
			
		// Default constructor
		public Node(Entry entry) {
			this.entry = entry;
			this.left = null;
			this.right = null;
		}

		// Recursively computes a formatted string for partial dictionary (subtree)
		public String toString () {
			return (left == null ? "" : left.toString() + "\n") + entry.toString() +
			       (right == null ? "" : "\n" + right.toString());
		}
	}
	
	// Empty dictionary constructor
	public BinarySearchTree () {
	}

	// This method returns the closest word entry in the dictionary
	// in alphabetical order
	public Entry getClosestEntry(String w) {
		Node upper = null;
		Node lower = null;
		
		Node node = root;
		
		while (node!=null) {
			int comp = node.entry.word.compareTo(w);
			if (comp==0) {
				return node.entry;
			}
			else if (comp>0) {
				upper = node;
				node = node.left;
			}
			else if (comp<0) {
				lower = node;
				node = node.right;
			}
		}
		
		if (upper==null && lower==null) {
			return null;
		}
		else if (upper==null) {
			return lower.entry;
		}
		else if (lower==null) {
			return upper.entry;
		}
		else {
			return upper.entry;
		}
	}
	
	// This method removes a whole entry from the dictionary
	public void delete(String w) throws Exception {
		Node temp = delete(w,root);
		if (root==null) {
			throw new Exception("Nonexistent deletion!");
		}
		root = temp;
	}
	
	public Node delete(String w, Node x) throws Exception {
		if (x==null) {
			return null;
		}
		
		int cmp = x.entry.word.compareTo(w);
		
		if (cmp<0) {
			x.right = delete(w,x.right);
		}
		else if (cmp>0) {
			x.left = delete(w,x.left);
		}
		else {
			if (x.right==null) {
				return x.left;
			}
			else if (x.left==null) {
				return x.right;
			}
			Node t = x;
			x = min(t.right);
			x.right = deleteMin(t.right);
			x.left = t.left;
		}		
		return x;
	}
	
	public void deleteMin() {
		root = deleteMin(root);
	}
	
	public Node deleteMin(Node x) {
		if (x.left==null) {
			return x.right;
		}
		else {
			x.left = deleteMin(x.left);
			return x;
		}
	}
	
	public Node min() {
		return min(root);
	}
	
	public Node min(Node x) {
		if (x.left==null) {
			return x;
		}
		return min(x.left);
	}

	// This method inserts a new entry into the dictionary
	public void insert(Entry e) {
		root = insert(e, root);
	}
	
	public Node insert(Entry e, Node x) {
		if (x==null) {
			x = new Node(e);
			return x;
		}
		int cmp = x.entry.compareWord(e);
		if (cmp>0) {
			x.left = insert(e,x.left);
		}
		else if (cmp<0) {
			x.right = insert(e,x.right);
		}
		else if (cmp==0) {
			x.entry.addDef(e.defns);
		}
		return x;
	}
	
	// This method returns the total number of definitions. Note that
	// this is different than the total number of words defined. 
	public int size() {
		return size(root);
	}
	
	public int size(Node x) {
		if (x==null) {
			return 0;
		}
		else {
			return x.entry.defns.length + size(x.left) + size(x.right);
		}		
	}

	// Returns a formatted string for the whole dictionary
	public String toString () {
		return (root == null ? "" : root.toString());
	}
}