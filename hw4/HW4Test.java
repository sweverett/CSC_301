package hw4;

import org.junit.Test;
import static org.junit.Assert.*;

import hw4.GraphBase.Edge;

import java.util.LinkedList;
import java.util.Random;

public class HW4Test {
	String border = "*******************************************\n";
	String passed = "* Passed!                                 *\n";
	String failed = "* Failed!                                 *\n";
	String test;
	
	AssertionError ae;
	Exception e;
	
	int p, V;
	int seed, prob;
	boolean[] inducedBy, lastinducedBy;
	GraphBase g, ind;
	GraphBase last, lastind;
	
	public HW4Test () {
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
	public LinkedList<Edge> randomEdges(int seed, int v0, int v1, int w0, int w1, int prob) {
		LinkedList<Edge> edges = new LinkedList<Edge>();
		Random rand = new Random(seed);
		
		if (w0 < v0) {
			int temp = w0;
			w0 = v0;
			v0 = temp;
			temp = w1;
			w1 = v1;
			v1 = temp;
		}
		
		for (int v = v0; v < v1; v++) {
			for (int w = (w0 > v + 1 ? w0 : (v + 1)); w < w1; w++) {
				int pick = rand.nextInt() & ((1 << 7) - 1);
				if (prob > pick) {
					edges.add(new Edge(r(v), r(w)));
				}
			}
		}
		
		return edges;
	}
	
	private void assertSameGraph(GraphBase g, AdjListGraph h) {
		assertNotEquals(null, h);
		for (int v = 0; v < g.V(); v++) {
			for (int w = v + 1; w < g.V(); w++) {
				assertEquals(g.contains(v, w), h.adj(v).contains(w) && h.adj(w).contains(v));
			}
		}
	}
	
	private void assertSameGraph(GraphBase g, AdjMatrixGraph h) {
		assertNotEquals(null, h);
		for (int v = 0; v < g.V(); v++) {
			for (int w = v + 1; w < g.V(); w++) {
				assertEquals(g.contains(v, w), h.adjRow(v)[w] && h.adjRow(w)[v]);
			}
		}
	}
	
	private void testCC(boolean list) {
		for (V = 1; V <= 32; V++) {
			for (seed = 0; seed < 8; seed++) {
				for (prob = 1; prob <= 128; prob*=2) {
					LinkedList<Edge> edges = this.randomEdges(seed, 0, V, 0, V, prob);
					AdjListGraph g1 = new AdjListGraph(V, edges);
					AdjMatrixGraph g2 = new AdjMatrixGraph(V, edges);
					g = list ? (GraphBase)g1 : (GraphBase)g2;
					int[] m = new int[V];
					Queue<Integer> q = new Queue<Integer>();
					for (int x = 0; x < V; x++) {
						if (m[x] == 0) {
							m[x] = x + 1;
							q.enqueue(x);
							while (!q.isEmpty()) {
								int v = q.dequeue();
								for (int w = 0; w < V; w++) {
									if (m[w] == 0 && g.contains(v, w)) {
										m[w] = m[v];
										q.enqueue(w);
									}
								}
							}
						}
					}
					if (list) g1.CC(); else g2.CC();
					
					for (int v = 0; v < V; v++) {
						for (int w = v + 1; w < V; w++) {
							assertEquals(m[w] == m[v], g.connected(v, w));
						}
					}
					last = g;
				}
			}
		}
	}

	private void testConstruct(boolean list) {
		for (V = 1; V <= 20; V++) {
			for (seed = 0; seed < 8; seed++) {
				for (prob = 1; prob <= 128; prob*=2) {
					LinkedList<Edge> edges = this.randomEdges(seed, 0, V, 0, V, prob);
					AdjListGraph g1;
					AdjMatrixGraph g2;
					if (list) {
						g2 = new AdjMatrixGraph(V, edges);
						g1 = new AdjListGraph(g2);
						g = (GraphBase)g2;
					} else {
						g1 = new AdjListGraph(V, edges);
						g2 = new AdjMatrixGraph(g1);
						g = (GraphBase)g1;
					}
					
					assertSameGraph(g, g1);
					assertSameGraph(g, g2);
					last = g;
				}
			}
		}
	}
	
	private void testInduced(boolean list) {
		for (V = 1; V <= 20; V++) {
			for (int W = 0; W <= V; W++) {
				for (p = 0; p < 4; p++) {
					inducedBy = new boolean[V];
					for (int i = 0; i < W; i++) {
						inducedBy[r(i)] = true;
					}
					int[] map = new int[V];
					for (int i = 0, j = 0; i < V; i++) {
						if (inducedBy[i]) {
							map[i] = j;
							j++;
						}
					}
					for (seed = 0; seed < 8; seed++) {
						for (prob = 1; prob <= 128; prob*=2) {
							LinkedList<Edge> induced = this.randomEdges(seed, 0, W, 0, W, prob);
							ind = new GraphBase(W);
							for (Edge e : induced) {
								ind.addEdge(map[e.v], map[e.w]);
							}
							
							AdjListGraph g1 = new AdjListGraph(V, induced);
							AdjMatrixGraph g2 = new AdjMatrixGraph(V, induced);
							g = list ? (GraphBase)g1 : (GraphBase)g2;
							
							LinkedList<Edge> cross = this.randomEdges(seed, 0, W, W, V, prob);
							LinkedList<Edge> drop = this.randomEdges(seed, W, V, W, V, prob);
							for (Edge e : cross) g.addEdge(e);
							for (Edge e : drop) g.addEdge(e);
							
							if (list) {
								assertSameGraph(ind, g1.inducedSubgraph(inducedBy));
							} else {
								assertSameGraph(ind, g2.inducedSubgraph(inducedBy));
							}
							last = g;
							lastind = ind;
							lastinducedBy = inducedBy;
						}
					}
				}
			}
		}
	}
	
	private void testTriangle(boolean list) {
		for (V = 1; V <= 32; V++) {
			for (seed = 0; seed < 8; seed++) {
				for (prob = 1; prob <= 128; prob*=2) {
					LinkedList<Edge> edges = this.randomEdges(seed, 0, V, 0, V, prob);
					AdjListGraph g1 = new AdjListGraph(V);
					AdjMatrixGraph g2 = new AdjMatrixGraph(V);
					g = list ? (GraphBase)g1 : (GraphBase)g2;
					Edge t = null;
					for (Edge e : edges) {
						boolean ex = false;
						for (int u = 0; u < V; u++) {
							if (u == e.v || u == e.w) continue;
							if (g.contains(e.v, u) && g.contains(e.w, u)) {
								ex = true;
								t = e;
								break;
							}
						}
						if (!ex) g.addEdge(e);
					}
					
					SimpleCycle three = list ? g1.threeCycle() : g2.threeCycle();
					assertEquals(null, three);
					last = new GraphBase(g);
					
					if (t != null) {
						g.addEdge(t);
						three = list ? g1.threeCycle() : g2.threeCycle();
						assertNotEquals(null, three);
						assertTrue(g.containsCycle(three));
					}
					
					last = g;
				}
			}
		}
	}
	
	private void testOdd(boolean list) {
		for (V = 1; V <= 32; V++) {
			for (int W = 1; W < V; W++) {
				for (p = 0; p < 4; p++) {
					for (seed = 0; seed < 8; seed++) {
						for (prob = 1; prob <= 128; prob*=2) {
							LinkedList<Edge> edges = this.randomEdges(seed, 0, W, W, V, prob);
							AdjListGraph g1 = new AdjListGraph(V, edges);
							AdjMatrixGraph g2 = new AdjMatrixGraph(V, edges);
							g = list ? (GraphBase)g1 : (GraphBase)g2;
							
							SimpleCycle odd = list ? g1.oddCycle() : g2.oddCycle();
							assertEquals(null, odd);
							last = new GraphBase(g);
							
							int[] x = new int[2];
							x[0] = x[1] = -1;
							int[][] b = {{0, W}, {W, V}};
							boolean[] marked = new boolean[V];
							Queue<Integer>[] q = new Queue[2];
							q[0] = new Queue<Integer>();
							q[1] = new Queue<Integer>();
							for (Edge e : edges) {
								marked[e.v] = true;
								marked[e.w] = true;
								q[0].enqueue(e.v);
								q[1].enqueue(e.w);
								while (!q[0].isEmpty() && !q[1].isEmpty()) {
									Queue<Integer>[] qN = new Queue[2];
									qN[0] = new Queue<Integer>();
									qN[1] = new Queue<Integer>();
									for (int i = 0; i< 2; i++) {
										while (!q[i].isEmpty()) {
											int v = q[i].dequeue();
											for (int w = b[i][0]; w < b[i][1]; w++) {
												if (!marked[w] && g.contains(w, v)) {
													marked[w] = true;
													x[i] = w;
													qN[1 - i].enqueue(w);
												}
											}
										}
									}
									q[0] = qN[0];
									q[1] = qN[1];
								}
								if (x[0] != -1) {
									g.addEdge(e.w, x[0]);
								} else if (x[1] != -1) {
									g.addEdge(e.v, x[1]);
								} else {
									continue;
								}
								
								odd = list ? g1.oddCycle() : g2.oddCycle();
								assertNotEquals(null, odd);
								assertTrue(g.containsCycle(odd));
								assertEquals(1, odd.size() % 2);
								break;
							}
							last = g;
						}
					}
				}
			}
		}
	}

	private void testAny(boolean list) {
		for (V = 1; V <= 32; V++) {
			for (seed = 0; seed < 8; seed++) {
				for (prob = 1; prob <= 128; prob*=2) {
					LinkedList<Edge> edges = this.randomEdges(seed, 0, V, 0, V, prob);
					AdjListGraph g1 = new AdjListGraph(V, edges);
					AdjMatrixGraph g2 = new AdjMatrixGraph(V, edges);
					g = list ? (GraphBase)g1 : (GraphBase)g2;
					boolean exists = false;
					int[] markdist = new int[V];
					Queue<Integer> q = new Queue<Integer>();
					for (int x = 0; x < V; x++) {
						if (markdist[x] == 0) {
							markdist[x] = 1;
							q.enqueue(x);
							while (!q.isEmpty()) {
								int v = q.dequeue();
								for (int w = 0; w < V; w++) {
									if (markdist[w] == 0 && g.contains(v, w)) {
										markdist[w] = markdist[v] + 1;
										q.enqueue(w);
									} else if (markdist[w] > 0 && markdist[w] >= markdist[v] && v != w && g.contains(v, w)) {
										exists = true;
									}
								}
							}
						}
					}
					
					SimpleCycle any = list ? g1.anyCycle() : g2.anyCycle();
					if (exists) {
						assertNotEquals(null, any);
						assertTrue(g.containsCycle(any));
					} else {
						assertEquals(null, any);
					}
					
					last = g;
				}
			}
		}
	}

	private void testMethod(int method_id) throws Exception {
		try {
			System.out.print(border + test + border);
			switch (method_id) {
			case 0: testCC(true); break;
			case 1: testConstruct(true); break;
			case 2: testInduced(true); break;
			case 3: testTriangle(true); break;
			case 4: testOdd(true); break;
			case 5: testAny(true); break;
			case 6: testCC(false); break;
			case 7: testConstruct(false); break;
			case 8: testInduced(false); break;
			case 9: testTriangle(false); break;
			case 10: testOdd(false); break;
			case 11: testAny(false); break;
			}
		} catch(AssertionError aerr) {
			ae = aerr;
		} catch(Exception err) {
			e = err;
		}
		
		if (ae != null || e != null) {
			System.out.print("\n" + border + test + failed + border);
			System.out.println("failing case V = " + V + " seed = " + seed + " prob = " + prob + "/128 and permutation = " + p);
			System.out.println("the corresponding graph is:");
			System.out.println(g.toString());
			if (ind != null) {
				System.out.print("the inducedBy vertex list is:");
				for (int i = 0; i < inducedBy.length; i++)
					if (inducedBy[i]) System.out.print(" " + i);
				System.out.println("");
				System.out.println("the induced subgraph we are looking for is:");
				System.out.println(ind.toString());
			}
			if (last != null) {
				System.out.println("last graph that passed the test is:");
				System.out.println(last.toString());
				if (lastind != null) {
					System.out.print("last inducedBy vertex list is:");
					for (int i = 0; i < lastinducedBy.length; i++)
						if (lastinducedBy[i]) System.out.print(" " + i);
					System.out.println("");
					System.out.println("last induced subgraph that passed the test is:");
					System.out.println(lastind.toString());
				}
			}
			if (ae != null) throw ae;
			if (e != null) throw e;
		} else {
			System.out.print(border + test + passed + border);
		}
	}
	
	@Test
	public void testAdjMatrixAnyCycle() throws Exception {
		test = "* Testing cycle detection (AdjMatrix)     *\n";
		testMethod(11);
	}
	
	@Test
	public void testAdjMatrixOddCycle() throws Exception {
		test = "* Testing odd cycles/bipartite (AdjMatrix)*\n";
		testMethod(10);
	}
	
	@Test
	public void testAdjMatrixThreeCycle() throws Exception {
		test = "* Testing three cycles (AdjMatrix)        *\n";
		testMethod(9);
	}
	
	@Test
	public void testAdjMatrixInducedSubgraph() throws Exception {
		test = "* Testing induced subgraph (AdjMatrix)    *\n";
		testMethod(8);
	}
	
	@Test
	public void testAdjMatrixfromList() throws Exception {
		test = "* Testing constructor AdjList=>AdjMatrix  *\n";
		testMethod(7);
	}
	
	@Test
	public void testAdjMatrixCC() throws Exception {
		test = "* Testing connected components (AdjMatrix)*\n";
		testMethod(6);
	}
	
	@Test
	public void testAdjListAnyCycle() throws Exception {
		test = "* Testing cycle detection (AdjList)       *\n";
		testMethod(5);
	}
	
	@Test
	public void testAdjListOddCycle() throws Exception {
		test = "* Testing odd cycles/bipartite (AdjList)  *\n";
		testMethod(4);
	}
	
	@Test
	public void testAdjListThreeCycle() throws Exception {
		test = "* Testing three cycles (AdjList)          *\n";
		testMethod(3);
	}
	
	@Test
	public void testAdjListInducedSubgraph() throws Exception {
		test = "* Testing induced subgraph (AdjList)      *\n";
		testMethod(2);
	}
	
	@Test
	public void testAdjListfromMatrix() throws Exception {
		test = "* Testing constructor AdjMatrix=>AdjList  *\n";
		testMethod(1);
	}
	
	@Test
	public void testAdjListCC() throws Exception {
		test = "* Testing connected components (AdjList)  *\n";
		testMethod(0);
	}
}