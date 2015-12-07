//
// GraphBase --- Base Graph class using edge list representation for control
// 
// There is no HOMEWORK in this file.
//

package hw4;

import java.util.LinkedList;

// DO NOT MODIFY THIS FILE
public class GraphBase {
	protected final int V;
	protected int E;
	private LinkedList<Edge> edges;
	
	protected boolean[] marked;
	protected int[] edgeTo;
	protected int[] id;
	protected boolean[] color;
	protected int count;
	protected SimpleCycle cycle;
	
	public static class Edge {
		public final int v, w; // Vertices of this undirected edge
		
		// Constructor for an edge (no self-loops allowed)
		public Edge(int v, int w) {
			if (v < 0 || w < 0 || v == w) {
				throw new IllegalArgumentException("Vertices of an edge must be different nonnegative integers, v = " + v + ", w = " + w);
			} else if (v < w) {
				this.v = v;
				this.w = w;
			} else {
				this.w = v;
				this.v = w;
			}
		}
		
		@Override
		// Returns true iff the given object describes this edge
		public boolean equals(Object o) {
			if (!(o instanceof Edge)) return false;
			Edge e = (Edge) o;
			return v == e.v && w == e.w;
		}
		
		// Returns a string representing this edge
		public String toString() {
			return "(" + v + "," + w + ")";
		}
	}
	
	// Constructor for an empty graph with V vertices and 0 edges.
	public GraphBase(int V) {
		if (V < 0) throw new IllegalArgumentException("Number of vertices must be nonnegative");
		this.V = V;
		E = 0;
		edges = new LinkedList<Edge>();
	}
	
	// Constructor for a graph with V vertices and a list of edges.
	public GraphBase(int V, LinkedList<Edge> edges) {
		this(V);
		for (Edge e : edges) addEdge(e);
	}
	
	// Constructor for deep copying a graph
	public GraphBase(GraphBase G) {
		this(G.V);
		for (Edge e : G.edges) addValidEdge(e);
	}

	// Resetting variables for BFS and DFS
	protected void resetVars() {
		marked = new boolean[V];
		edgeTo = new int[V];
		id = new int[V];
		color = new boolean[V];
		count = 0;
		
		for (int v = 0; v < V; v++) {
			edgeTo[v] = -1; // vertices without parents are assigned -1 as edgeTo
			id[v] = -1; // undiscovered vertices are assigned -1 as id
		}
	}
	
	// Returns the number of vertices in this graph.
	public int V() {
		return V;
	}
	
	// Returns the number of edges in this graph.
	public int E() {
		return E;
	}
	
	// Throws an IndexOutOfBoundsException unless 0 <= v < V
	protected void validateVertex(int v) {
		if (v < 0 || v >= V)
			throw new IndexOutOfBoundsException("vertex " + v + " is not between 0 and " + (V-1));
	}
	
	// Adds an edge to this graph unless it is a duplicate or an invalid edge
	public void addEdge(int v, int w) {
		addEdge(new Edge(v, w));
	}
	public void addEdge(Edge e) {
		validateVertex(e.w);
		if (!contains(e)) addValidEdge(e);
	}
	// Adds an edge to this graph that is already confirmed to be valid
	protected void addValidEdge(Edge e) {
		E++;
		edges.add(e);
	}
	
	// Returns true iff this graph contains the given edge
	public boolean contains(int v, int w) {
		return contains(new Edge(v, w));
	}
	public boolean contains(Edge e) {
		validateVertex(e.w);
		return containsValid(e);
	}
	// Returns true iff this graph contains the given edge that is already confirmed to be valid
	protected boolean containsValid(Edge e) {
		return edges.contains(e);
	}
	
	// Returns true iff v is connected to w in this graph
	public boolean connected(int v, int w) {
		return connected(new Edge(v, w));
	}
	public boolean connected(Edge e) {
		validateVertex(e.w);
		return (id[e.v] >= 0 && id[e.w] >= 0 && id[e.v] == id[e.w]);
	}
	
	// Returns true iff this graph contains all of the edges of the given cycle
	public boolean containsCycle(SimpleCycle c) {
		int s = c.size();
		if (s == 0) return true;
		if (s < 3) return false;
		for (int i = 1; i < s; i++) {
			if (!contains(c.cycle[i - 1], c.cycle[i])) return false;
		}
		return contains(c.cycle[0], c.cycle[s - 1]);
	}
	
	// Returns the current searchTree (constructed by the last BFS/DFS)
	public int[] searchTree() {
		return edgeTo;
	}
	
	// Returns a string that describes this graph using
	// 1) the number of vertices and edges
	// 2) the list of edges
	public String toString() {
		StringBuilder s = new StringBuilder();
		s.append(V + " vertices and " + E + " edges : ");
		for (Edge e : edges) {
			s.append(e.toString() + " ");
		}
		return s.toString();
	}
}