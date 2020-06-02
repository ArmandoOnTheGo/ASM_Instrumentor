import java.io.*;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import java.util.Vector;

import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FrameNode;
import org.objectweb.asm.tree.IincInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.MultiANewArrayInsnNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

class ClassAdapter extends ClassVisitor implements Opcodes {
	ControlFlowGraph cfg = null;
	
	public ClassAdapter(final ClassVisitor cv) {
		super(ASM8, cv);
	}
	
	public ControlFlowGraph getCFG() { return this.cfg; }

	@Override
	public MethodVisitor visitMethod(final int access, final String name, final String desc, final String signature,
			final String[] exceptions) {
		System.out.println("visitMethod: " + name);

		MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);

		if(mv != null) {
			MethodAdapter ma = new MethodAdapter(mv, name);
			if(!name.equals("<init>"))
				cfg = ma.getCFG();
			return ma;
		}
		else
			return null;
	}
}

class MethodAdapter extends MethodNode implements Opcodes {
	ControlFlowGraph cfg;
	String methodName = "";
	String className = "Test";
	int previousHashCode = 0;
	boolean linkLast = false;
	Vector<Integer> jumpNodes;
	File toWrite = null;
	String previousInsn = "";
	public File file;
	
    public MethodAdapter(final MethodVisitor mv, String methodName) {
        super(ASM5);
        this.methodName = methodName;
        cfg = new ControlFlowGraph();
        jumpNodes = new Vector<Integer>();
        resetText();
    }
    
	public void resetText() {
		file = new File("testRecord.txt");
		try {
			file.createNewFile();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(file);
			pw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public void appendText(String instruct) {
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(file, true));
			out.write(instruct + "\n");
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
    
    public ControlFlowGraph getCFG() {
    	return this.cfg;
    }

    public void checkAndAddCFGEdge(int id, String inst) {
    	if(jumpNodes.contains(id)) {
    		int index = jumpNodes.indexOf(id);
        	if(index==jumpNodes.size()-1 && !linkLast) {
        		cfg.addEdge(className, methodName, previousHashCode, previousInsn, id, inst);
            	appendText(previousInsn+" -> "+inst);
        	}

        	jumpNodes.remove(index);
        }
        else if(previousHashCode != 0) {
        	cfg.addEdge(className, methodName, previousHashCode, previousInsn, id, inst);
        	appendText(previousInsn+" -> "+inst);
        }
        else if(previousHashCode == 0) {
        	cfg.setInitialNode(className, methodName, id, inst);
        }
		
    	previousHashCode = id;
    	previousInsn = inst;
    }

	@Override
	public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
		String inst ="CALL" + name;
		checkAndAddCFGEdge(new MethodInsnNode(opcode, owner, name, desc, itf).hashCode(), inst);
	}

	public void visitLabel(Label label) {
		String inst  = "visitLabel:" + label;
		checkAndAddCFGEdge(label.hashCode(), inst);
	}

	public void visitJumpInsn(int opCode, Label label) {
		String inst = "visitJumpInsn: "+opCode+" "+label;
		
		if(previousHashCode != 0) {
        	cfg.addEdge(className, methodName, previousHashCode, previousInsn, label.hashCode(), inst);
        	//appendText(previousInsn+" -> "+ inst);
		}
        else {
        	cfg.setInitialNode(className, methodName, label.hashCode(), inst);
        }
        
        if(opCode != Opcodes.GOTO){
            jumpNodes.add(label.hashCode());
            linkLast = false;
		}
        else {
        	linkLast = true;
        }
        
	}

	public AnnotationVisitor visitAnnotation(String arg0, boolean arg1) {
		return null;
	}

	public AnnotationVisitor visitAnnotationDefault() {
		return null;
	}

	@Override
	public void visitCode() {
	}

	public void visitEnd() {
	}

	public void visitFieldInsn(int arg0, String arg1, String arg2, String arg3) {
		String inst = "visitFieldInsn: " + arg0 + " " + arg1+" "+ arg2+" "+arg3;
		checkAndAddCFGEdge(new FieldInsnNode(arg0, arg1, arg2, arg3).hashCode(), inst);
	}

	public void visitFrame(int arg0, int arg1, Object[] arg2, int arg3, Object[] arg4) {
		String inst = "visitFrame: "+arg0+" "+arg1+" "+arg2+" "+arg3+" "+arg4;
		checkAndAddCFGEdge(new FrameNode(arg0, arg1, arg2, arg3, arg4).hashCode(), inst);
	}

	public void visitIincInsn(int arg0, int arg1) {
		String inst = "visitIincInsn: "+arg0+" "+arg1;
		checkAndAddCFGEdge(new IincInsnNode(arg0, arg1).hashCode(), inst);
	}

	public void visitInsn(int arg0) {
		String inst = "visitFieldInsn: "+arg0;
		checkAndAddCFGEdge(new InsnNode(arg0).hashCode(), inst);
	}

	public void visitIntInsn(int arg0, int arg1) {
		String inst = "visitIntInsn: "+arg0+" "+arg1;
		checkAndAddCFGEdge(new InsnNode(arg0).hashCode(), inst);
	}

	public void visitLdcInsn(Object arg0) {
		String inst ="visitLdcInsn: "+arg0;
		checkAndAddCFGEdge(new LdcInsnNode(arg0).hashCode(), inst);
	}

	public void visitLineNumber(int arg0, Label arg1) {}

	public void visitLocalVariable(String arg0, String arg1, String arg2, Label arg3, Label arg4, int arg5) {}

	@Override
	public void visitMaxs(int arg0, int arg1) { }

	public void visitMultiANewArrayInsn(String arg0, int arg1) {
		String inst = "visitMultiANewArrayInsn: "+arg0+" "+arg1;
		checkAndAddCFGEdge(new MultiANewArrayInsnNode(arg0, arg1).hashCode(), inst);
	}

	public AnnotationVisitor visitParameterAnnotation(int arg0, String arg1, boolean arg2) {
		return null;
	}

	public void visitTypeInsn(int arg0, String arg1) {
		String inst = "visitTypeIns: " + arg0 + " " + arg1;
		checkAndAddCFGEdge(new TypeInsnNode(arg0, arg1).hashCode(), inst);
	}

	public void visitVarInsn(int arg0, int arg1) {
		String inst = "visitVarInsn: "+arg0+" "+arg1;
		checkAndAddCFGEdge(new VarInsnNode(arg0, arg1).hashCode(), inst);
	}
}