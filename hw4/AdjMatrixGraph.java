//
// AdjMatrixGraph --- Graph representation using an adjacency matrix
// 
// HOMEWORK in this file is to implement:
//
// 1) public AdjMatrixGraph(AdjListGraph G)
//    Clone the graph given in the adjacency list representation
//    You will need to use the adj method of G
//
// 2) public AdjMatrixGraph inducedSubgraph(boolean inducedBy[])
//    Create an induced subgraph of this graph using the vertices v with inducedBy[v] = true
//    A subgraph H of G is a graph whose vertex set S is a subset of the vertex set of G,
//    and whose edges are a subset of the edge set of G.  H is an induced subgraph of G,
//    if it has exactly the edges that appear in G over S, in which case
//    H is said to be induced by S.  S is the argument of this method.
//
// 3) public SimpleCycle threeCycle()
//    Find a three cycle if there exists one
//
// 4) public SimpleCycle oddCycle()
//    Find an odd cycle if there exists one (bipartite check)
//    WARNING : DO NOT USE Breadth First Search
//
// 5) public SimpleCycle anyCycle()
//    Find any length cycle (length of at least 3) if there exists one
//    WARNING : DO NOT USE Breadth First Search
//
// 6) public void CC()
//    Assign the connected components
//    WARNING : DO NOT USE Breadth First Search
//
// One of the methods will be bonus
//
// Completed by Spencer Everett
// 10/31/2015

package hw4;

import java.util.LinkedList;

public class AdjMatrixGraph extends GraphBase {
	private boolean[][] adj;
	
	// Constructor for an empty graph with V vertices and 0 edges.
	public AdjMatrixGraph(int V) {
		super(V);
		adj = new boolean[V][V];
	}
	
	// Constructor for a graph with V vertices and a list of edges.
	public AdjMatrixGraph(int V, LinkedList<Edge> edges) {
		this(V);
		for (Edge e : edges) addEdge(e);
	}
	
	// Constructor for deep copying a graph
	public AdjMatrixGraph(AdjMatrixGraph G) {
		this(G.V);
		E = G.E;
		for (int v = 0; v < V; v++) {
			for (int w = 0; w < V; w++) {
				adj[v][w] = G.adj[v][w];
			}
		}
	}
	
	// Constructor for copying a graph using an adjacency list representation
	public AdjMatrixGraph(AdjListGraph G) {
		this(G.V);
		adj = new boolean[V][V];
				
		for (int v=0; v<V; v++) {
			LinkedList<Integer> adjRow = G.adj(v);
			for (Integer w : adjRow) {
				adj[v][w] = true;
				adj[w][v] = true;
			}
		}
	}
	
	// Returns the induced subgraph restricted to those vertices v with inducedBy[v] = true
	public AdjListGraph inducedSubgraph(boolean inducedBy[]) {
		if (inducedBy.length != V)
			throw new IllegalArgumentException("inducedBy argument must be the same length as the number of vertices");

		int length = 0;
		LinkedList<Integer> vertices = new LinkedList<Integer>();
		LinkedList<Edge> edges = new LinkedList<Edge>();
		
		for (int v=0; v<V; v++) {
			if (inducedBy[v]) {
				length++;
				vertices.add(v);
				boolean[] row = adjRow(v);
				for (int w=v+1; w<V; w++){
					if (inducedBy[w]) {	
						if (!vertices.contains(w)) vertices.add(w);
						if (row[w]) edges.add(new Edge(vertices.indexOf(v),vertices.indexOf(w)));
					}
				}
			}			
		}		
		AdjListGraph subgraph = new AdjListGraph(length);			
		for (Edge e : edges) subgraph.addEdge(e);		
		return subgraph;
	}
	
	// Returns a simple cycle of length three if one exists, or null otherwise
	public SimpleCycle threeCycle() {
		for (int u=0; u<V; u++) {
			for (int v=u+1; v<V; v++) {
				for (int w=v+1; w<V; w++) {
					if (adj[u][v] && adj[v][w] && adj[w][u]) {
						SimpleCycle cycle = new SimpleCycle(V);
						cycle.addVertex(u);
						cycle.addVertex(v);
						cycle.addVertex(w);
						return cycle;
					}
				}
			}
		}
		return null;
	}
	
	// Returns a simple cycle of odd length if one exists, or null otherwise
	public SimpleCycle oddCycle() {
		for (int v = 0; v<V; v++) {
			resetVars();
            if (!marked[v]) {
                dfsOddCycle(v);
            }
        }
		return cycle;
	}
	
	// Returns a simple cycle of any length >= 3 if one exists, or null otherwise
	public SimpleCycle anyCycle() {
		resetVars();
		for (int v = 0; v<V; v++)
            if (!marked[v])
                dfsAllCycle(-1, v);
		return cycle;
	}
	
	// Assigns connected component id's to all vertices
	public void CC() {
		resetVars(); // defined in GraphBase.java
		for (int v = 0; v < V; v++) {
			if (!marked[v]) {
				dfs(v);
				count++;
			}
		}
	}
	
	// Returns the adjacency information for the given vertex
	public boolean[] adjRow(int v) {
		validateVertex(v);
		return adj[v];
	}
	
	// Returns true iff this graph contains the given edge
	protected boolean containsValid(Edge e) {
		return adj[e.v][e.w];
	}
	
	// Adds an edge to this graph that is already confirmed to be valid
	protected void addValidEdge(Edge e) {
		super.addValidEdge(e);
		adj[e.v][e.w] = true;
		adj[e.w][e.v] = true;
	}
	
	private void dfs(int v) {
		marked[v] = true;
		id[v] = count;
		
		boolean[] adjRow = adjRow(v);
		for (int w=0; w<V; w++) {
			if (adjRow[w]) {
				if (!marked[w]) {
					edgeTo[w] = v;
					color[w] = !color[v];
					dfs(w);
				}			
			}			
		}
	}
	
	private void dfsOddCycle(int v) {
		marked[v] = true;
		id[v] = count;
		
		boolean[] adjRow = adjRow(v);
		for (int w=0; w<V; w++) {
			if (adjRow[w]) {
				if (cycle != null) return;
				if (!marked[w]) {
					edgeTo[w] = v;
					color[w] = !color[v];
					dfsOddCycle(w);
				}
				else if (color[w]==color[v]) {
	                cycle = new SimpleCycle(V);
	                for (int x = v; x != w && x!=-1; x = edgeTo[x]) {
	                    cycle.addVertex(x);
	                }
	                cycle.addVertex(w);
	            }
			}			
		}
	}
	
	private void dfsAllCycle(int u, int v) {
        marked[v] = true;
        boolean[] adjRow = adjRow(v);
        for (int w=0; w<V; w++) {
        	if (adjRow[w]) {
	            if (cycle != null) return;
	
	            if (!marked[w]) {
	                edgeTo[w] = v;
	                dfsAllCycle(v, w);
	            }
	            else if (w != u) {
	                cycle = new SimpleCycle(V);
	                for (int x=v; x!=w; x=edgeTo[x]) {
	                    cycle.addVertex(x);
	                }
	                cycle.addVertex(w);
	            }
        	}
        }
    }
}