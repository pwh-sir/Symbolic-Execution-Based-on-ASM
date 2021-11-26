package symbolicExecution;

import org.objectweb.asm.*;

public class ClassAnalyzer implements ClassVisitor {
    private String methodName;
    private MethodAnalyzer methodAnalyser;
    private Node root;
    public ClassAnalyzer(String methodToAnalyse, Node root){
        this.methodName = methodToAnalyse;
        this.root = root;
    }
    public void visit(int i, int i1, String s, String s1, String s2, String[] strings) {
    }

    public void visitSource(String s, String s1) {

    }

    public void visitOuterClass(String s, String s1, String s2) {

    }

    public AnnotationVisitor visitAnnotation(String s, boolean b) {
        return null;
    }

    public void visitAttribute(Attribute attribute) {

    }

    public void visitInnerClass(String s, String s1, String s2, int i) {

    }

    public FieldVisitor visitField(int i, String s, String s1, String s2, Object o) {
        return null;
    }

    public MethodVisitor visitMethod(int i, String s, String s1, String s2, String[] strings) {
        if(s.equals(this.methodName)){
            this.methodAnalyser = new MethodAnalyzer(root);
            return this.methodAnalyser;
        }
        return null;
    }

    public void visitEnd() {

    }
}
