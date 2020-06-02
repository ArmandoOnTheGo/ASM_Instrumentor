import java.io.FileInputStream;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

public class Main {
	public static void main(final String args[]) throws Exception {
		/*Part1: Instrumenting the .class file*/
		var is = new FileInputStream("bin/C.class");
        ClassReader cr = new ClassReader(is);
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		ClassAdapter ca = new ClassAdapter(cw);
        cr.accept(ca, ClassReader.SKIP_DEBUG);
        ControlFlowGraph oldCFG = ca.getCFG();
        System.out.println("\noldCFG "+oldCFG);
        System.out.println("EdgeCount "+oldCFG.edges.size());
		/*Part1: Here we will compare the coverage of a test being run
		 * on the actual class and display the byte code coverage of
		 * the test against the entire class*/
        new RTS().testing("testRecord.txt", "classRecord.txt");
        
//        /* Part 2: Creating a derivative/version to compare CFG*/
        var is2 = new FileInputStream("bin/Cv2.class");
        ClassReader cr2 =new ClassReader(is2);
        ClassWriter cw2 = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		ClassAdapter ca2 = new ClassAdapter(cw2);
        cr2.accept(ca2, ClassReader.SKIP_DEBUG);
        ControlFlowGraph newCFG = ca2.getCFG();
        System.out.println("\nnewCFG "+newCFG);
        System.out.println("EdgeCount "+newCFG.edges.size());
//        //Testing the coverage of the test against the entire class
//      
//        /* Part 2(contd.) Calculating Dangerous Edges*/
		var dan = new DangerousEdgeDetector();
		dan.detect(oldCFG,newCFG);
		System.out.println("Number of Dangerous Edges: "+dan.dangerEdges.size()
								+"\n"+dan.dangerEdges);
		
		/*Part 3: Running Tests and then comparing them 
		 * against the Dangerous Edges to see if they 
		 * are viable testing options
		 * We'll compare the instruments recorded from out test
		 * and compare it against the set of dangerous sets we have.
		 * If we find that the test covers some amount of Dangerous 
		 * Edges, we'll show the percentage of that coverage
		 */
		new RTS().testing("danRecord.txt", "testRecord.txt");
	}
}