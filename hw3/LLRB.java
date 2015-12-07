//
// LLRB --- L(eft)-L(eaning) R(ed)-B(lack) BST
// 
// This class stores a set of integer keys using a left-leaning red-black BST
//
// HOMEWORK in this file is to implement:
//
// 1) public void insert()
// 2) public boolean containsRightRedEdge()
// 3) public boolean containsConsecutiveLeftRedEdges()
// 4) public int countBlackEdgesOnLeftmostPath()
// 5) public boolean sameBlackEdgesCountOnAllPaths(int count)
//
// As BONUS, there are two additional methods to implement
//
// 1) public void merge(LLRB llrb)
// 2) public void fixLLRB()
//
// Spencer Everett, 10/16/2015

package hw3;

public class LLRB {
    private static final boolean RED   = true;
    private static final boolean BLACK = false;
    
    public Node root;
	
	public class Node {
		public int key;
		public boolean color;
		public Node left, right;
		
		public Node(int key, boolean color) {
			this.key = key;
			this.color = color;
		}
	}
	
	// Constructor for LLRB
	public LLRB() {
	}
	
	// Is parent link for node x red? false if x is null
	private boolean isRed(Node x) {
		if (x == null) return false;
		return x.color == RED;
	}
	
	// Inserts a key without fixing the tree
	public void bstInsert(int key) {
		root = bstInsert(root, key);
	}
	
	// Recursive helper method for bstInsert
	private Node bstInsert(Node x, int key) {
		if (x == null) return new Node(key, RED);
		if (key < x.key) x.left  = bstInsert(x.left, key);
		else if (key > x.key) x.right = bstInsert(x.right, key);
		return x;
	}
	
	// Inserts a key fixing the red-black tree property
	public void insert(int key) {
		root = insert(key,root);
		root.color = BLACK;
	}
	
	public Node insert(int key, Node x) {
		if (x==null) return new Node(key,RED);
		if (key<x.key) x.left = insert(key,x.left);
		else if (key>x.key) x.right = insert(key,x.right);
		
		if (isRed(x.right) && !isRed(x.left)) x = rotateLeft(x);
		if (isRed(x.left) && isRed(x.left.left)) x = rotateRight(x);
		if (isRed(x.left) && isRed(x.right)) x = flipColors(x);
		
		return x;		
	}
	
	private Node rotateLeft(Node h) {
		Node x = h.right;
		h.right = x.left;
		x.left = h;
		x.color = h.color;
		h.color = RED;
		return x;
	}
	
	private Node rotateRight(Node h) {
		Node x = h.left;
		h.left = x.right;
		x.right = h;
		x.color = h.color;
		h.color = RED;
		return x;
	}
	
	private Node flipColors(Node h) {
		h.color = RED;
		h.right.color = BLACK;
		h.left.color = BLACK;
		return h;
	}
	
	// Checks whether the tree contains a red right edge
	public boolean containsRightRedEdge() {
		return containsRightRedEdge(root);
	}
	
	private boolean containsRightRedEdge(Node x) {
		if (x==null) return false;
		if (isRed(x.right)) return true;
		if (containsRightRedEdge(x.left) || containsRightRedEdge(x.right)) return true;
		return false;
	}
	
	// Checks whether the tree contains two left red edges in a row
	public boolean containsConsecutiveLeftRedEdges() {
		return containsConsecutiveLeftRedEdges(root);
	}
	
	private boolean containsConsecutiveLeftRedEdges(Node x) {
		if (x==null) return false;
		if (isRed(x.left) && isRed(x.left.left)) return true;
		if (containsConsecutiveLeftRedEdges(x.left) || containsConsecutiveLeftRedEdges(x.right)) return true;
		return false;
	}
	
	// Returns the number of black edges on the leftmost path
	public int countBlackEdgesOnLeftmostPath() {
		return countBlackEdgesOnLeftmostPath(root);
	}
	
	private int countBlackEdgesOnLeftmostPath(Node x) {
		if (x==null) return 0;
		int black = !isRed(x) ? 1 : 0;
		return black + countBlackEdgesOnLeftmostPath(x.left);
	}
	
	// Checks whether the number of black edges on any path from root to a leaf
	// is the same as the count provided
	public boolean sameBlackEdgesCountOnAllPaths(int count) {
		return sameBlackEdgesCountOnAllPaths(count,root);
	}
	
	private boolean sameBlackEdgesCountOnAllPaths(int count, Node x) {
		if (x==null && count==0) return true;
		if (x==null && count!=0) return false;
		if (isRed(x)) return sameBlackEdgesCountOnAllPaths(count,x.left) && sameBlackEdgesCountOnAllPaths(count,x.right);
		return sameBlackEdgesCountOnAllPaths(count-1,x.left) && sameBlackEdgesCountOnAllPaths(count-1,x.right);
	}
	
	// Checks whether the BST is a valid left leaning red-black tree
	public boolean isValidLLRB() {
		int count = countBlackEdgesOnLeftmostPath();
		return (sameBlackEdgesCountOnAllPaths(count) && 
				!containsRightRedEdge() &&
				!containsConsecutiveLeftRedEdges());
	}
	
	// Merges another left leaning red-black tree by inserting its key into this tree
	public void merge(LLRB llrb) {
		merge(llrb.root);
		return;
	}
	
	private void merge(Node x) {
		if (x==null) return;
		merge(x.left);
		insert(x.key);
		merge(x.right);
		return;
	}
    
	// Fixes the red-black tree if there is something to fix
	public void fixLLRB() {
		root = fixLLRB(root);
		if (root!=null) root.color = BLACK;
		return;
	}
	
	private Node fixLLRB(Node x) {
		if (x==null) return x;
		if (x.left!=null) x = fixLLRB(x.left);
		if (x.right!=null) x = fixLLRB(x.right);
		
		if (isRed(x.right) && !isRed(x.left)) x = rotateLeft(x);
		if (isRed(x.left) && x.left.left!=null && isRed(x.left.left)) x = rotateRight(x);
		if (isRed(x.left) && isRed(x.right)) x = flipColors(x);
		
		return x;
	}
}