//
// Digraph --- Digraph implementation using adjacency list representation
// 
// HOMEWORK in this file is to implement:
//
// 1) public Digraph complement()
// Returns a digraph that is the complement of this digraph
// The complement digraph contains an edge if and only if this digraph does not contain it
//
// 2) public boolean isTopological(int[] order)
// Checks whether the given permutation of vertices is a topological order
// This method will return true if and only if there are no edges going backwards 
//
// 3) public Stack<Integer> topologicalOrder()
// Returns a topological order if there exists one, otherwise returns null
// This method will return a topological order if and only if this digraph is a DAG 
//
// 4) public Stack<Integer> shortestPath(int s, int t)
// Returns a shortest path from s to t if there exists one, otherwise returns null
// This method will return a path if and only if t is reachable from s
//
// 5) public Stack<Integer> cycleBetween(int v, int w)
// Returns a cycle that contains both v and w if there exists one, otherwise returns null
// This method will return a cycle if and only if v and w are strongly connected
//
// Spencer Everett, 11/14/2015

package hw5;

import hw4.Queue;
import java.util.LinkedList;

public class Digraph {
	private final int V;                  // number of vertices in this digraph
	private int E;                        // number of edges in this digraph
	private LinkedList<Integer>[] adj;    // adj[v] = adjacency list for vertex v
	private LinkedList<Edge> edges;       // list of edges in this digraph
	private boolean[] marked;             // marked[v] = is there an s->v path?
	private Stack<Integer> reversePostorder;
	private int[] edgeTo;
	private int[] distTo;
	
	public static class Edge {
		public final int v, w; // Vertices of this directed edge v->w
		
		// Constructor for an edge (no loops)
		public Edge(int v, int w) {
			this.v = v;
			this.w = w;
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
	
	// Constructor for an empty digraph with V vertices.
	@SuppressWarnings("unchecked")
	public Digraph(int V) {
		if (V < 0) throw new IllegalArgumentException("Number of vertices in a Digraph must be nonnegative");
		this.V = V;
		this.E = 0;
		edges = new LinkedList<Edge>();
		adj = (LinkedList<Integer>[]) new LinkedList[V];
		for (int v = 0; v < V; v++) {
			adj[v] = new LinkedList<Integer>();
		}
	}

	// Constructor for a digraph with V vertices and a list of edges.
	public Digraph(int V, LinkedList<Edge> edges) {
		this(V);
		for (Edge e : edges) addEdge(e.v, e.w);
	}

	// Constructor for a new digraph that is a deep copy of the specified digraph.
	public Digraph(Digraph G) {
		this(G.V());
		this.E = G.E();
		for (int v = 0; v < G.V(); v++) {
			// reverse so that adjacency list is in same order as original
			Stack<Integer> reverse = new Stack<Integer>();
			for (int w : G.adj[v]) {
				reverse.push(w);
			}
			for (int w : reverse) {
				adj[v].add(w);
			}
		}
	}
	
	// Returns the number of vertices in this digraph.
	public int V() {
		return V;
	}

	// Returns the number of edges in this digraph.
	public int E() {
		return E;
	}

	// throw an IndexOutOfBoundsException unless 0 <= v < V
	private void validateVertex(int v) {
		if (v < 0 || v >= V)
			throw new IndexOutOfBoundsException("vertex " + v + " is not between 0 and " + (V-1));
	}

	// Adds the directed edge v->w to this digraph.
	public void addEdge(int v, int w) {
		addEdge(new Edge(v, w));
	}
	public void addEdge(Edge e) {
		validateVertex(e.v);
		validateVertex(e.w);
		adj[e.v].add(e.w);
		edges.add(e);
		E++;
	}
	
	// Returns the vertices adjacent from vertex <tt>v</tt> in this digraph.
	public LinkedList<Integer> adj(int v) {
		validateVertex(v);
		return adj[v];
	}
	
	// Returns a digraph that is the complement of this digraph
	// The complement digraph contains an edge if and only if this digraph does not contain it
	public Digraph complement() {
		Digraph comp = new Digraph(V);
		for (int v=0; v<V; v++) {
			for (int w=0; w<V; w++) {
				if (v!=w && !containsEdge(v,w)) comp.addEdge(v, w);
			}
		}
		return comp;
	}
	
	// Checks whether the given permutation of vertices is a topological order
	// This method will return true if and only if there are no edges going backwards 
	public boolean isTopological(int[] order) {
		if (order.length != V)
			throw new IllegalArgumentException("order array must be the same length as the number of vertices");
		for (int v : order) {
			for (int w : adj[v]) {
				if (indexOf(order,w) < indexOf(order,v)) return false;
			}
		}
		return true;
	}
	
	// Helper method for isTopological
	private int indexOf(int[] array, int v) {
		for (int i=0; i<array.length; i++) {
			if (array[i] == v) return i;
		}
		return -1;
	}
	
	// Returns a topological order if there exists one, otherwise returns null
	// This method will return a topological order if and only if this digraph is a DAG 
	public Stack<Integer> topologicalOrder() {
		reversePostorder = new Stack<Integer>();
		marked = new boolean[V];
		for (int v=0; v<V; v++) {
			if (!marked[v]) orderDFS(v);
		}
		int[] order = new int[reversePostorder.size()];
		for (int i=0; i<order.length; i++) order[i] = reversePostorder.pop();
		for (int i=order.length-1; i>=0; i--) reversePostorder.push(order[i]);
		if (isTopological(order)) return reversePostorder;
		return null;
	}
	
	private void orderDFS(int v) {
		marked[v] = true;
		for (int w : adj(v)) {
			if (!marked[w])	orderDFS(w);
		}
		reversePostorder.push(v);
		//return null;
	}
	
	// Returns a shortest path from s to t if there exists one, otherwise returns null
	// This method will return a path if and only if t is reachable from s
	public Stack<Integer> shortestPath(int s, int t) {
		return shortestPathBFS(s,t);
	}
	
	// Helper method for shortestPath
	private Stack<Integer> shortestPathBFS(int s, int t) {
		marked = new boolean[V];
		distTo = new int[V];
		edgeTo = new int[V];
		
		marked[s] = true;
		for (int v=0; v<V; v++) distTo[v] = -1;
        distTo[s] = 0;
        
		Queue<Integer> queue = new Queue<Integer>();
		queue.enqueue(s);
		
		while (!queue.isEmpty()) {
			int v = queue.dequeue();
			for (int w : adj[v]) {
				if (!marked[w]) {
					edgeTo[w] = v;
                    distTo[w] = distTo[v] + 1;
					marked[w] = true;
					
					if (w==t) {
						Stack<Integer> path = new Stack<Integer>();
						int vertex = w;
						path.push(vertex);
						while (edgeTo[vertex]!=s) {
							vertex = edgeTo[vertex];
							path.push(vertex);
						}
						path.push(s);
						return path;
					}
					queue.enqueue(w);
				}
			}
		}
		return null;
	}
	
	// Returns a cycle that contains both v and w if there exists one, otherwise returns null
	// This method will return a cycle if and only if v and w are strongly connected
	public Stack<Integer> cycleBetween(int v, int w) {
		Stack<Integer> pathVW = shortestPath(v,w);
		Stack<Integer> pathWV = shortestPath(w,v);
		if (pathVW==null || pathWV==null) return null;
		
		Stack<Integer> reverseVW = reverseStack(pathVW);
		reverseVW.pop();
		while(!reverseVW.isEmpty()) {
			pathWV.push(reverseVW.pop());
		}
		pathWV.pop();
		return pathWV;
	}
	
	// Helper function for cycleBetween
	private Stack<Integer> reverseStack(Stack<Integer> stack) {
		Stack<Integer> reverse = new Stack<Integer>();
		while (!stack.isEmpty()) {
			reverse.push(stack.pop());
		}
		return reverse;
	}

	// Returns true iff this graph contains the given edge
	public boolean containsEdge(int v, int w) {
		validateVertex(v);
		validateVertex(w);
		return adj[v].contains(w);
	}
	
	// Returns true iff this graph contains all of the edges of the given path
	public boolean containsPath(Stack<Integer> p) {
		if (p.size() < 2) return true;
		int v = -1;
		for (int w : p) {
			if (v >= 0 && !containsEdge(v, w)) return false;
			v = w;
		}
		return true;
	}
	
	// Returns true iff this graph contains all of the edges of the given cycle
	public boolean containsCycle(Stack<Integer> c) {
		if (c.size() == 0) return true;
		int b = c.peek();
		int v = -1;
		for (int w : c) {
			if (v >= 0 && !containsEdge(v, w)) return false;
			v = w;
		}
		return containsEdge(v, b);
	}
	
	// Returns a path from s to t if there exists one, otherwise returns null
	// This method will return a path if and only if t is reachable from s
	public Stack<Integer> dfsPath(int s, int t) {
		marked = new boolean[V];
		return DFS(s, t);
	}
	
	private Stack<Integer> DFS(int v, int t) {
		Stack<Integer> path;
		marked[v] = true;
		for (int w : adj(v)) {
			if (!marked[w]) {
				if (w == t) {
					path = new Stack<Integer>();
					path.push(t);
					path.push(v);
					return path;
				}
				path = DFS(w, t);
				if (path != null) {
					path.push(v);
					return path;
				}
			}
		}
		return null;
	}
	
	// Returns a string that describes this digraph using
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