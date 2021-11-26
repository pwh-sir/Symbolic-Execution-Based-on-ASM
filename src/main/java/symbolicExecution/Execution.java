package symbolicExecution;

import javaExample.*;
import org.objectweb.asm.ClassReader;

import java.io.IOException;
import java.io.InputStream;

public class Execution {
    public static void main(String[] args) throws IOException {
        Execution test = new Execution();
        test.test0();
        test.test1();
    }
    void test0() throws IOException {
        InputStream in = Test.class.getResourceAsStream("Test.class");
        ClassReader classReader = new ClassReader(in);
        Node root = new Node();
        ClassAnalyzer classAnalyzer = new ClassAnalyzer("test0",root);
        classReader.accept(classAnalyzer,0);
        Graphviz graphviz = new Graphviz(root,"test0");
        graphviz.drawRoot();
    }
    void test1() throws IOException {
        InputStream in = Test.class.getResourceAsStream("Test.class");
        ClassReader classReader = new ClassReader(in);
        Node root = new Node();
        ClassAnalyzer classAnalyzer = new ClassAnalyzer("test1",root);
        classReader.accept(classAnalyzer,0);
        Graphviz graphviz = new Graphviz(root,"test1");
        graphviz.drawRoot();
    }
}
