//
// SimpleCycle --- Cycle class representing a simple cycle
//
// A simple cycle is a cycle in which no vertices appear more than once.
// 
// There is no HOMEWORK in this file.
//

package hw4;

//DO NOT MODIFY THIS FILE
public class SimpleCycle {
	public boolean[] contains;
	public int[] cycle;
	private final int V;
	private int len;
	
	// Constructor for a new empty cycle (of length 0)
	public SimpleCycle(int V) {
		if (V < 0) throw new IllegalArgumentException("Number of vertices must be nonnegative");
		this.V = V;
		contains = new boolean[V];
		cycle = new int[V];
	}
	
	// Extends the cycle to include the vertex v
	public void addVertex(int v) {
		if (v < 0 || v >= V)
			throw new IndexOutOfBoundsException("vertex " + v + " is not between 0 and " + (V-1));
		if (contains[v])
			throw new UnsupportedOperationException("vertex " + v + " is already part of the cycle, cannot add same vertex more than once!");
		contains[v] = true;
		cycle[len++] = v;
	}
	
	// Returns the size of the cycle
	public int size() {
		return len;
	}
}