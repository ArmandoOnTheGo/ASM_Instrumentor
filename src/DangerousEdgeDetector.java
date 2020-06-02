import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DangerousEdgeDetector extends ControlFlowGraph {

	Set<Edge> dangerEdges = new HashSet<Edge>();

	public Set<Edge> detect(ControlFlowGraph oldCFG, ControlFlowGraph newCFG) {
		dangerEdges = new HashSet<Edge>();
		resetText();

		for (Map.Entry<ControlFlowGraph.Node, Set<ControlFlowGraph.Node>> entry : oldCFG.edges.entrySet()) {
			var n1 = entry.getKey();
			var s1 = entry.getValue();
			for (var edge : s1)
				dangerEdges.add(new Edge(n1.inst, edge.inst));
		}
		for (Map.Entry<ControlFlowGraph.Node, Set<ControlFlowGraph.Node>> entry : newCFG.edges.entrySet()) {
			var n1 = entry.getKey();
			var s1 = entry.getValue();
			for (var edge : s1)
				dangerEdges.remove(new Edge(n1.inst, edge.inst));
		}

		if (dangerEdges.size() > 0)
			createTextDoc();

		return dangerEdges;
	}

	public void createTextDoc() {
		File file;
		file = new File("danRecord.txt");
		try {
			file.createNewFile();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		for (Edge toWrite : dangerEdges) {
			try {
				BufferedWriter out = new BufferedWriter(new FileWriter(file, true));
				out.write(toWrite.toString());
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void resetText() {
		File file;
		file = new File("danRecord.txt");
		try {
			PrintWriter pw = new PrintWriter(file);
			pw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}