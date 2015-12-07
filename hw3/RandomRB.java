package hw3;

import java.util.LinkedList;
import java.util.Random;

public class RandomRB {
    private static final boolean RED   = true;
    private static final boolean BLACK = false;
    
    private LLRB llrb;
    public Node root;
    public int depth;
    public int seed;
    public int max;
	
	public class Node {
		public int key;
		public boolean color;
		public Node left, right;
		
		public Node(int key, boolean color) {
			this.key = key;
			this.color = color;
		}
		
		public String toString() {
			String lStr = (left == null ? "" : left.toString());  
			String rStr = (right == null ? "" : right.toString());
			String keyStr = (left == null ? "." : (left.color ? "<" : "(")) + key
					      + (right == null ? "," : (right.color ? ">" : ")"));
			return keyStr + lStr + rStr;
		}
	}
	
	public RandomRB() {
		toLLRB();
	}
	
	public LLRB toLLRB() {
		llrb = new LLRB();
		if (root != null) {
			llrb.root = llrb.new Node(root.key, BLACK);
			toLLRB(root, llrb.root);
		}
		return llrb;
	}
	
	private void toLLRB(Node n, LLRB.Node ln) {
		if (n.left != null) {
			ln.left = llrb.new Node(n.left.key, n.left.color);
			toLLRB(n.left, ln.left);
		}
		if (n.right != null) {
			ln.right = llrb.new Node(n.right.key, n.right.color);
			toLLRB(n.right, ln.right);
		}
	}
	
	public void bstInsert(int key, boolean color) {
		root = bstInsert(root, key, color);
	}
	
	private Node bstInsert(Node x, int key, boolean color) {
		if (x == null) return new Node(key, color);
		if (key < x.key) x.left  = bstInsert(x.left, key, color);
		else if (key > x.key) x.right = bstInsert(x.right, key, color);
		return x;
	}
	
	private int assignKeys(Node n, int c, int inc) {
		if (n == null) return c;
		c = assignKeys(n.left, c, inc);
		c += inc;
		n.key = c;
		return assignKeys(n.right, c, inc);
	}
	
	private boolean bit(int []s, int i) {
		int k = i / 32;
		int j = i % 32;
		return (((s[k] >> j) & 1) == 1);
	}
	
	public LLRB resetRandomTree(int seed) {
		return resetRandTree(seed, 0, 4);
	}
	
	public LLRB resetRandTree(int seed, int begin, int inc) {
		this.seed = seed;
		Random rand = new Random(seed);
		int []s = {rand.nextInt(), rand.nextInt(), rand.nextInt(), rand.nextInt()};
		int depthVar = ((s[3] >> 25) & ((1 << 7) - 1)) % 121;
		depth = 0;
		int sum = 1, prod = 1;
		
		while (sum <= depthVar) {
			sum += prod;
			prod *= 3;
			depth++;
		}
		
		if (depth == 0) {
			root = null;
		} else {
			int i = 0;
			LinkedList<Node> nodes = new LinkedList<Node>();
			root = new Node(depth - 1, BLACK);
			nodes.add(root);
			while (!nodes.isEmpty()) {
				Node n = nodes.removeFirst();
				boolean three = bit(s, i++);
				if (three) {
					n.left = new Node(n.key, RED);
					if (n.key > 0) {
						n.left.left = new Node(n.key - 1, BLACK);
						n.left.right = new Node(n.key - 1, BLACK);
						n.right = new Node(n.key - 1, BLACK);
						nodes.add(n.left.left);
						nodes.add(n.left.right);
						nodes.add(n.right);
					}
				} else if (n.key > 0) {
					n.left = new Node(n.key - 1, BLACK);
					n.right = new Node(n.key - 1, BLACK);
					nodes.add(n.left);
					nodes.add(n.right);
				}
			}
		}
		max = assignKeys(root, begin, inc);
		return toLLRB();
	}
	
	public String toString() {
		if (root == null) return "-";
		return root.toString();
	}
}
