import java.util.*;

public class ControlFlowGraph {
// invariant : set " nodes " contains all nodes in the graph , i . e . , if
// there is an edge "m - > n " , both " m " and " n " are in " nodes "
	Set<Node> nodes; // set of nodes
	Map<Node, Set<Node>> edges; // adjacency set
	Set<Edge> edgeSet;
	Node initialNode = null;

	static class Node {
		String clazz; // containing class
		String method; // containing method
		int id; // unique identifier
		String inst;
		
		public Node(String clazz, String method, int id, String inst) {
			this.clazz = clazz;
			this.method = method;
			this.id = id;
			this.inst = inst;
		}

		public String toString() {
			return clazz + "." + method + ": " + id;
		}

		@Override
		public boolean equals(Object o) {
			// postcondition : returns true iff all attributes match ( up to " equals ")
			if (this == o)
				return true;
			if (o == null)
				return false;
			Node temp = (Node) o;
			if ((!Objects.equals(this.clazz, temp.clazz)) || (!Objects.equals(this.method, temp.method))
					|| (this.id != temp.id))
				return false;
			else
				return true;
		}

		@Override
		public int hashCode() {
			// postcondition : satisfies the hash - code contract
			int hashKey = 31;
			int hash = 1;
			hash = hashKey * hash + ((this.clazz == null) ? 0 : clazz.hashCode());
			hash = hashKey * hash + ((this.method == null) ? 0 : method.hashCode());
			hash = hashKey * hash + this.id;
			return hash;
		}
	}
	
	static class Edge {
		String startInst, endInst;

		public Edge(String startInst, String endInst) {
			this.startInst = startInst;
			this.endInst = endInst;
		}

		public String toString() {
			return "\n"+startInst + " -> " +endInst;
		}

		@Override
		public boolean equals(Object o) {
			// postcondition : returns true iff all attributes match ( up to " equals ")
			if (this == o)
				return true;
			if (o == null)
				return false;
			
			Edge temp = (Edge) o;
			return (Objects.equals(this.startInst, temp.startInst)) && 
				(Objects.equals(this.endInst, temp.endInst));
		}

		@Override
		public int hashCode() {
			// postcondition : satisfies the hash - code contract
			int hashKey = 31;
			int hash = 1;
			hash = hashKey * hash + ((this.startInst == null) ? 0 : this.startInst.hashCode());
			hash = hashKey * hash + ((this.endInst == null) ? 0 : this.endInst.hashCode());
			return hash;
		}
	}
	
    public void setInitialNode(Node node) {
    	Node newNode = node;
    	addNode(newNode);
    	this.initialNode = newNode;
    }
    
    public void setInitialNode(String clazz, String method, int id, String inst) {
    	Node newNode = new Node(clazz, method, id, inst);
    	addNode(newNode);
    	this.initialNode = newNode;
    }

	public String toString() { // for ControlFlowGraph
		return "# nodes = " + nodes.size() + "\n nodes = " + nodes + 
				"\n # edges = " + edges.size() +"\n edges = " + edges;
	}


//	Testing to display contents of nodes and edges	
//	public String toTestString(Set<Node> test, Map<Node, Set<Node>> testMap) { // for ControlFlowGraph
//		return "# nodes = " + test.size() + "\n nodes = " + test + "\n edges = " + testMap;
//	}

    public ControlFlowGraph() {
        nodes = new HashSet < Node > ();
        edges = new HashMap < Node, Set < Node > > ();
        edgeSet = new HashSet< Edge >();
    }

	public void addNode(Node node) {
		// postcondition : adds Node ( clazz, method, id ) to the set of nodes ,
		// and if the node is not previously in the set,
		// updates the adjacency - set representation to map the given node to an empty
		// set
		if (nodes.contains(node))
			return;

		nodes.add(node);
		Set<Node> toAddSet = new HashSet<Node>();
		edges.put(node, toAddSet);
	}

	public void addEdge(String clazz1, String method1, int id1, String inst1, 
							String clazz2, String method2, int id2, String inst2) {
		// postcondition : adds edge Node ( clazz1 , method1 , id1 ) -> Node ( clazz2 ,
		// method2 , id2 )
		// if any of the two nodes is not already in the set of nodes ,
		// the set is updated to include them
        Node node1 = new Node(clazz1, method1, id1, inst1);
        Node node2 = new Node(clazz2, method2, id2, inst2);
        addNode(node1);
        addNode(node2);
        
        Set <Node> node1Edges;
        
        if(edges.keySet().contains(node1)) {
        	node1Edges = edges.get(node1);
        }
        else {
        	node1Edges = new HashSet<Node>();
        }
        if(!node1Edges.contains(node2)) {
	        node1Edges.add(node2);
	    	edges.put(node1, node1Edges);
	    	System.out.println(node1.inst + " -> " + node2.inst);
        }
	}

	public void addEdge(String clazz, String method, int id1, String inst1, int id2, String inst2) {
		// postcondition : adds edge between nodes identified by " id1 " and " id2 " ,
		// which are in the same method " method " in class " clazz "
		addEdge(clazz, method, id1, inst1, clazz, method, id2, inst2);
	}

	public boolean reachable(String c1, String m1, int id1, String inst1, 
								String c2, String m2, int id2, String inst2) {
		// postcondition : returns true iff there is a directed path ( of length >= 0)
		// from Node ( c1 , m1 , id1 ) to Node ( c2 , m2 , id2 ) in the graph
    	Node source = new Node(c1, m1, id1, inst1);
		Node dest = new Node(c2, m2, id2, inst2);
		
		if(edges.get(source).contains(dest)) return true; 	// target node is found
		if(edges.get(source).isEmpty()) return false;  		// leaf, a node without a next (directional)
		
		Set<Node> neighbors = edges.get(source);
		boolean found = false;
		
		// Recursively search adjacency list for connection between nodes
		for(Node neighbor: neighbors) {
			found = (found || reachable(neighbor.clazz, neighbor.method, neighbor.id, neighbor.inst,
											c2, m2, id2, inst2)); 
			if(found) break;
		}
		return found;
	}

	public boolean allNodesReachable(String c, String m, int id, String inst) {
		// postcondition : returns true iff all nodes in " C . m " are reachable
		// from Node (c , m , id )
		/* 1: */ Iterator<Node> it = nodes.iterator();
		/* 2: */ while (it.hasNext()) {
			/* 3: */ Node n = it.next();
			/* 4: */ if (!reachable(c, m, id, inst, c, m, n.id, n.inst)) {
				/* 5: */ return false;
				/* 6: */ }
			/* 7: */ }
		/* 8: */ return true;
	}
}