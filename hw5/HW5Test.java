package hw5;

import org.junit.Test;
import static org.junit.Assert.*;

import hw4.Queue;
import hw5.Digraph.Edge;

import java.util.LinkedList;
import java.util.Random;

public class HW5Test {
	String border = "*******************************************\n";
	String passed = "* Passed!                                 *\n";
	String failed = "* Failed!                                 *\n";
	String test;
	
	AssertionError ae;
	Exception e;
	
	int p, V;
	int seed, prob;
	Digraph g, last, comp;
	boolean[] marked;
	int[] id;
	int count;
	Stack<Integer> revPost;
	Stack<Integer> ret;
	int v, w;
	int[] topo;
	
	public HW5Test () {
	}
	
	public static int randomPerm(int i, int V, int v) {
		int[] mult = {1, 277, 281, 283};
		return (v * mult[i % 4] + i) % V;
	}
	
	private int r(int v) {
		return randomPerm(p, V, v);
	}

	// generates a random set of edges of the form (v, w), v0 <= v < v1, w0 <= w < w1 
	// where each edge is independently included in the set with prob/128 probability
	public LinkedList<Edge> randomEdges(int seed, int v0, int v1, int prob, boolean topo) {
		LinkedList<Edge> edges = new LinkedList<Edge>();
		Random rand = new Random(seed);
		
		for (int v = v0; v < v1; v++) {
			for (int w = (topo ? v + 1 : v0); w < v1; w++) {
				if (v == w) continue;
				int pick = rand.nextInt() & ((1 << 7) - 1);
				if (prob > pick) {
					edges.add(new Edge(r(v), r(w)));
				}
			}
		}
		
		return edges;
	}
	
	private void testComp() {
		for (V = 1; V <= 32; V++) {
			for (seed = 0; seed < 8; seed++) {
				for (prob = 0; prob <= 128; prob+=16) {
					LinkedList<Edge> edges = this.randomEdges(seed, 0, V, prob, false);
					g = new Digraph(V, edges);
					comp = g.complement();
					assertNotEquals(null, comp);
					for (int v = 0; v < V; v++) {
						for (int w = 0; w < V; w++) {
							if (edges.contains(new Edge(v, w)) || v == w) {
								assertFalse(comp.adj(v).contains(w));
							} else {
								assertTrue(comp.adj(v).contains(w));
							}
						}
					}
					last = g;
				}
			}
		}
	}
	
	private void assertIsTopoDAG(Edge e) {
		assertTrue(g.isTopological(topo));
		if (e != null) {
			for (int i = 0; i < V; i++)
				if (topo[i] == e.v) topo[i] = e.w;
				else if (topo[i] == e.w) topo[i] = e.v;
			assertFalse(g.isTopological(topo));
		}
	}
	
	private void testIsTopo() {
		int i;
		for (V = 1; V <= 32; V++) {
			topo = new int[V];
			for (seed = 0; seed < 8; seed++) {
				for (p = 0; p < 4; p++) {
					for (prob = 1; prob <= 128; prob*=2) {
						LinkedList<Edge> edges = this.randomEdges(seed, 0, V, prob, true);
						g = new Digraph(V, edges);
						for (i = 0; i < V; i++) topo[i] = r(i);
						assertIsTopoDAG(edges.poll());
						last = g;
						
						edges = this.randomEdges(seed, 0, V, prob, false);
						g = new Digraph(V, edges);
						SCC();
						i = 0;
						for (int v : revPost) topo[i++] = v;
						if (count == V) {
							assertIsTopoDAG(edges.poll());
						} else {
							assertFalse(g.isTopological(topo));
						}
						last = g;
					}
				}
			}
		}
	}
	
	private void assertTopological(LinkedList<Edge> edges) {
		assertNotEquals(null, ret);
		marked = new boolean[V];
		for (int v : ret) {
			for (int w = 0; w < V; w++) {
				if (marked[w]) assertFalse(edges.contains(new Edge(v, w)));
			}
			marked[v] = true;
		}
		for (int v = 0; v < V; v++) {
			assertTrue(marked[v]);
		}
	}
	
	private void testTopo() {
		for (V = 1; V <= 32; V++) {
			for (seed = 0; seed < 8; seed++) {
				for (p = 0; p < 4; p++) {
					for (prob = 1; prob <= 128; prob*=2) {
						LinkedList<Edge> edges = this.randomEdges(seed, 0, V, prob, true);
						g = new Digraph(V, edges);
						ret = g.topologicalOrder();
						assertTopological(edges);
						last = g;
						
						edges = this.randomEdges(seed, 0, V, prob, false);
						g = new Digraph(V, edges);
						ret = g.topologicalOrder();
						SCC();
						if (count == V) {
							assertTopological(edges);
						} else {
							assertEquals(null, ret);
						}
						last = g;
					}
				}
			}
		}
	}
	
	private void testShortest() {
		for (V = 1; V <= 32; V++) {
			for (seed = 0; seed < 8; seed++) {
				for (prob = 0; prob <= 128; prob+=16) {
					LinkedList<Edge> edges = this.randomEdges(seed, 0, V, prob, false);
					g = new Digraph(V, edges);
					for (v = 0; v < V; v++) {
						int[] d = new int[V];
						Queue<Integer> q = new Queue<Integer>();
						q.enqueue(v);
						while (!q.isEmpty()) {
							int s = q.dequeue();
							for (int t = 0; t < V; t++) {
								if (d[t] == 0 && g.containsEdge(s, t)) {
									d[t] = d[s] + 1;
									q.enqueue(t);
								}
							}
						}
						for (w = 0; w < V; w++) {
							if (v == w) continue;
							ret = g.shortestPath(v,  w);
							if (d[w] > 0) {
								assertNotEquals(null, ret);
								assertEquals(d[w], ret.size() - 1);
								assertTrue(g.containsPath(ret));
								int s = ret.peek();
								assertEquals(v, s);
								for (int t : ret) s = t;
								assertEquals(w, s);
							} else {
								assertEquals(null, ret);
							}
						}
					}
					last = g;
				}
			}
		}
	}

	private void testCycle() {
		for (V = 1; V <= 32; V++) {
			for (seed = 0; seed < 8; seed++) {
				for (prob = 0; prob <= 128; prob+=16) {
					LinkedList<Edge> edges = this.randomEdges(seed, 0, V, prob, false);
					g = new Digraph(V, edges);
					SCC();
					for (v = 0; v < V; v++) {
						for (w = v + 1; w < V; w++) {
							ret = g.cycleBetween(v, w);
							if (id[v] == id[w]) {
								assertNotEquals(null, ret);
								assertTrue(g.containsCycle(ret));
							} else {
								assertEquals(null, ret);
							}
						}
					}
					last = g;
				}
			}
		}
	}
	
	private void SCC() {
		Stack<Integer> sccOrd = new Stack<Integer>();
		int V = g.V();
		Digraph original = g;
		Digraph rev = new Digraph(V);
		
		for (int v = 0; v < V; v++)
			for (int w : g.adj(v))
				rev.addEdge(w, v);
		
		marked = new boolean[V];
		id = new int[V];
		count = 0;
		revPost = sccOrd;
		g = rev;
		for (int v = 0; v < V; v++) {
			if (!marked[v]) DFS(v);
		}
		
		marked = new boolean[V];
		revPost = new Stack<Integer>();
		g = original;
		for (int v : sccOrd) {
			if (!marked[v]) {
				DFS(v);
				count++;
			}
		}
	}
	
	private void DFS(int v) {
		marked[v] = true;
		id[v] = count;
		for (int w : g.adj(v)) {
			if (!marked[w]) DFS(w);
		}
		revPost.push(v);
	}

	private void testMethod(int method_id) throws Exception {
		try {
			System.out.print(border + test + border);
			switch (method_id) {
			case 0: testComp(); break;
			case 1: testIsTopo(); break;
			case 2: testTopo(); break;
			case 3: testShortest(); break;
			case 4: testCycle(); break;
			}
		} catch(AssertionError aerr) {
			ae = aerr;
		} catch(Exception err) {
			e = err;
		}
		
		if (ae != null || e != null) {
			System.out.print("\n" + border + test + failed + border);
			System.out.println("failing case V = " + V + " seed = " + seed + " prob = " + prob + "/128 and permutation = " + p);
			System.out.println("the corresponding digraph is:");
			System.out.println(g.toString());
			
			String retStr = null;
			switch (method_id) {
			case 0: // testComp
				if (comp == null) {
					System.out.println("returned a null digraph!");
				} else {
					System.out.println("returned an erroneous complement digraph:");
					System.out.println(comp.toString());
				}
				break;
			case 1: // testIsTopo
				System.out.print("failing vertex order is: ");
				for (int v : topo) System.out.print(v + " ");
				System.out.println();
				break;
			case 2: // testTopo
				retStr = "topological sort";
				break;
			case 3: // testShortest
				retStr = "path";
				System.out.println("failing vertices are s = " + v + ", t = " + w);
				break;
			case 4: // testCycle
				retStr = "cycle";
				System.out.println("failing vertices are v = " + v + ", w = " + w);
				break;
			}
			
			if (ret == null) {
				if (retStr != null) System.out.println("didn't return a " + retStr + "!");
			} else {
				System.out.print("returned an erroneous " + retStr + ": ");
				for (int v : ret) System.out.print(v + " ");
				System.out.println();
			}
			if (last != null) {
				System.out.println("last digraph that passed the test is:");
				System.out.println(last.toString());
			}
			if (ae != null) throw ae;
			if (e != null) throw e;
		} else {
			System.out.print(border + test + passed + border);
		}
	}
	
	@Test
	public void testCycleBetween() throws Exception {
		test = "* Testing cycle betweeen v and w          *\n";
		testMethod(4);
	}
	
	@Test
	public void testShortestPath() throws Exception {
		test = "* Testing shortest path from s to t       *\n";
		testMethod(3);
	}
	
	@Test
	public void testTopological() throws Exception {
		test = "* Testing topological order               *\n";
		testMethod(2);
	}
	
	@Test
	public void testIsTopological() throws Exception {
		test = "* Testing isTopological                   *\n";
		testMethod(1);
	}
	
	@Test
	public void testComplement() throws Exception {
		test = "* Testing complement                      *\n";
		testMethod(0);
	}
}