package symbolicExecution;

import jdk.nashorn.internal.runtime.regexp.joni.constants.OPCode;
import org.objectweb.asm.*;

import java.util.ArrayList;
import java.util.Stack;

public class MethodAnalyzer implements MethodVisitor {
    private int maxLOCALS = -1;
    private ArrayList<Variable> localVariableList;
    private Stack<Variable> stack;
    private Node root;
    private Node curNode;
    public MethodAnalyzer(Node root){
        localVariableList = new ArrayList<Variable>();
        stack = new Stack<Variable>();
        this.root = root;
        curNode = new Node(root);
        root.setTrueNode(curNode);
    }

    private int getLabelId(Label label){
        String sLabel = label.toString();
        return Integer.parseInt(sLabel.substring(1, sLabel.length()));
    }

    public Variable getVariableByIndex(int index){
        for(Variable variable:localVariableList){
            if(variable.getIndex() == index){
                return variable;
            }
        }
        return null;
    }
    public AnnotationVisitor visitAnnotationDefault() {
        return null;
    }

    public AnnotationVisitor visitAnnotation(String s, boolean b) {
        System.out.println("visitAnnotation: " + s + " # " + b);
        return null;
    }

    public AnnotationVisitor visitParameterAnnotation(int i, String s, boolean b) {
        System.out.println("visitAnnotationDefault");
        return null;
    }

    public void visitAttribute(Attribute attribute) {
        System.out.println("visitAttribute: " + attribute);
    }

    public void visitCode() {
        System.out.println("visitCode: ");
    }

    public void visitFrame(int i, int i1, Object[] objects, int i2, Object[] objects1) {
        System.out.println("visitFrame: " + i + " # " + i1 + " # " + objects + " # " + objects1);
    }

    public void visitInsn(int i) {
        Variable variable1;
        Variable variable2;
        Variable stackVariable;
        switch(i){
            case Opcodes.ICONST_0:
                variable1 = new Variable();
                variable1.setValue("0");
                stack.push(variable1);
                break;
            case Opcodes.ICONST_1:
                variable1 = new Variable();
                variable1.setValue("1");
                stack.push(variable1);
                break;
            case Opcodes.ICONST_2:
                variable1 = new Variable();
                variable1.setValue("2");
                stack.push(variable1);
                break;
            case Opcodes.ICONST_3:
                variable1 = new Variable();
                variable1.setValue("3");
                stack.push(variable1);
                break;
            case Opcodes.ICONST_4:
                variable1 = new Variable();
                variable1.setValue("4");
                stack.push(variable1);
                break;
            case Opcodes.ICONST_5:
                variable1 = new Variable();
                variable1.setValue("5");
                stack.push(variable1);
                break;
            case Opcodes.ICONST_M1:
                variable1 = new Variable();
                variable1.setValue("-1");
                stack.push(variable1);
                break;
            case Opcodes.IADD:
                variable1 = stack.pop();
                variable2 = stack.pop();
                stackVariable = new Variable();
                stackVariable.setValue(variable2.getValue() + " + " + variable1.getValue() + " ");
                stack.push(stackVariable);
                break;
            case Opcodes.ISUB:
                variable1 = stack.pop();
                variable2 = stack.pop();
                stackVariable = new Variable();
                stackVariable.setValue(variable2.getValue() + " - " + variable1.getValue() + " ");
                stack.push(stackVariable);
                break;
            case Opcodes.IMUL:
                variable1 = stack.pop();
                variable2 = stack.pop();
                stackVariable = new Variable();
                stackVariable.setValue(variable2.getValue() + " * " + variable1.getValue() + " ");
                stack.push(stackVariable);
                break;
            case Opcodes.IDIV:
                variable1 = stack.pop();
                variable2 = stack.pop();
                stackVariable = new Variable();
                stackVariable.setValue(variable2.getValue() + " / " + variable1.getValue() + " ");
                stack.push(stackVariable);
                break;
            case Opcodes.GOTO:
                curNode.setEnd();
            default:
                break;
        }
        System.out.println("visitInsn: " + i);
    }

    public void visitIntInsn(int i, int i1) {
        System.out.println("visitIntInsn: " + i + " # " + i1);
        if(i == Opcodes.SIPUSH || i == Opcodes.BIPUSH){
            Variable stackVariable = new Variable();
            stackVariable.setValue("" + i1);
            stack.push(stackVariable);
        }
        if(i == Opcodes.RETURN){
            curNode.setEnd();
            //return;
        }
    }

    public void visitVarInsn(int i, int i1) {
        Variable localVariable = getVariableByIndex(i1);
        if(localVariable == null){
            localVariable = new Variable();
            localVariable.setIndex(i1);
            localVariable.setValue("I"+i1);
            localVariableList.add(localVariable);
        }
        switch(i){
            case Opcodes.ILOAD:
                stack.push(localVariable);
                break;
            case Opcodes.ISTORE:
                Variable stackValue = stack.pop();
                localVariable.setValue(stackValue.getValue());
                break;
            default:
                break;
        }
        System.out.println("visitVarInsn: " + i + " # " + i1);
    }

    public void visitTypeInsn(int i, String s) {
        System.out.println("visitTypeInsn: " + i + "#" + s);
    }

    public void visitFieldInsn(int i, String s, String s1, String s2) {
        System.out.println("visitFieldInsn: " + i + "#" + s + "#" + s1 + "#" + s2);
    }

    public void visitMethodInsn(int i, String s, String s1, String s2) {
        System.out.println("visitMethodInsn: " + i + "#" + s + "#" + s1 + "#" + s2);
    }

    public void visitJumpInsn(int i, Label label) {
        Variable stackViable1;
        Variable stackViable2;
        String v1Value;
        String v2Value;
        int labelId = getLabelId(label);
        Node falseNode = root.getTrueNode().findNodeByLabel(labelId);
        if(falseNode == null && curNode.isEnd() == false){
            falseNode = new Node(curNode,labelId);
            curNode.setFalseNode(falseNode);
        }
        switch(i){
            case Opcodes.IFNE://!=
                stackViable1 = stack.pop();
                if(curNode.getConstraint().equals("")){
                    if(stackViable1.getValue() != null){
                        curNode.setConstraint(stackViable1.getValue() + " == 0 ");
                    }else{
                        curNode.setConstraint("I"+stackViable1.getIndex() + " == 0 " );
                    }
                }else{
                    if(stackViable1.getValue() != null){
                        curNode.setConstraint(curNode.getConstraint() + "&& " + stackViable1.getValue() + " == 0 ");
                    }else{
                        curNode.setConstraint(curNode.getConstraint() + "&& " + "I"+stackViable1.getIndex() + " == 0 " );
                    }
                }
                break;
            case Opcodes.IFEQ://==
                stackViable1 = stack.pop();
                if(curNode.getConstraint().equals("")){
                    if(stackViable1.getValue() != null){
                        curNode.setConstraint(stackViable1.getValue() + " != 0 ");
                    }else{
                        curNode.setConstraint("I"+stackViable1.getIndex() + " != 0 " );
                    }
                }else{
                    if(stackViable1.getValue() != null){
                        curNode.setConstraint(curNode.getConstraint() + "&& " + stackViable1.getValue() + " != 0 ");
                    }else{
                        curNode.setConstraint(curNode.getConstraint() + "&& " + "I"+stackViable1.getIndex() + " != 0 " );
                    }
                }
                break;
            case Opcodes.IFGE://>=0
                stackViable1 = stack.pop();
                if(curNode.getConstraint().equals("")){
                    if(stackViable1.getValue() != null){
                        curNode.setConstraint(stackViable1.getValue() + " < 0 ");
                    }else{
                        curNode.setConstraint("I"+stackViable1.getIndex() + " < 0 " );
                    }
                }else{
                    if(stackViable1.getValue() != null){
                        curNode.setConstraint(curNode.getConstraint() + "&& " + stackViable1.getValue() + " < 0 ");
                    }else{
                        curNode.setConstraint(curNode.getConstraint() + "&& " + "I"+stackViable1.getIndex() + " < 0 " );
                    }
                }
                break;
            case Opcodes.IFLT://<0
                stackViable1 = stack.pop();
                if(curNode.getConstraint().equals("")){
                    if(stackViable1.getValue() != null){
                        curNode.setConstraint(stackViable1.getValue() + " >= 0 ");
                    }else{
                        curNode.setConstraint("I"+stackViable1.getIndex() + " >= 0 " );
                    }
                }else{
                    if(stackViable1.getValue() != null){
                        curNode.setConstraint(curNode.getConstraint() + "&& " + stackViable1.getValue() + " >= 0 ");
                    }else{
                        curNode.setConstraint(curNode.getConstraint() + "&& " + "I"+stackViable1.getIndex() + " >= 0 " );
                    }
                }
                break;
            case Opcodes.IFLE://<=0
                stackViable1 = stack.pop();
                if(curNode.getConstraint().equals("")){
                    if(stackViable1.getValue() != null){
                        curNode.setConstraint(stackViable1.getValue() + " > 0 ");
                    }else{
                        curNode.setConstraint("I"+stackViable1.getIndex() + " > 0 " );
                    }
                }else{
                    if(stackViable1.getValue() != null){
                        curNode.setConstraint(curNode.getConstraint() + "&& " + stackViable1.getValue() + " > 0 ");
                    }else{
                        curNode.setConstraint(curNode.getConstraint() + "&& " + "I"+stackViable1.getIndex() + " > 0 " );
                    }
                }
                break;
            case Opcodes.IFGT://>0
                stackViable1 = stack.pop();
                if(curNode.getConstraint().equals("")){
                    if(stackViable1.getValue() != null){
                        curNode.setConstraint(stackViable1.getValue() + " <= 0 ");
                    }else{
                        curNode.setConstraint("I"+stackViable1.getIndex() + " <= 0 " );
                    }
                }else{
                    if(stackViable1.getValue() != null){
                        curNode.setConstraint(curNode.getConstraint() + "&& " + stackViable1.getValue() + " <= 0 ");
                    }else{
                        curNode.setConstraint(curNode.getConstraint() + "&& " + "I"+stackViable1.getIndex() + " <= 0 " );
                    }
                }
                break;
            case Opcodes.IF_ICMPEQ:// 2 == 1
                stackViable1 = stack.pop();
                stackViable2 = stack.pop();
                v1Value = stackViable1.getValue() != null ? stackViable1.getValue() : "I"+stackViable1.getIndex();
                v2Value = stackViable2.getValue() != null ? stackViable2.getValue() : "I"+stackViable2.getIndex();
                if(curNode.getConstraint().equals("")){
                    curNode.setConstraint(v2Value + " != " + v1Value + " ");
                }else{
                    curNode.setConstraint(curNode.getConstraint() + "&& " + v2Value + " != " + v1Value + " ");
                }
                break;
            case Opcodes.IF_ICMPGE:// 2 >= 1
                stackViable1 = stack.pop();
                stackViable2 = stack.pop();
                v1Value = stackViable1.getValue() != null ? stackViable1.getValue() : "I"+stackViable1.getIndex();
                v2Value = stackViable2.getValue() != null ? stackViable2.getValue() : "I"+stackViable2.getIndex();
                if(curNode.getConstraint().equals("")){
                    curNode.setConstraint(v2Value + " < " + v1Value + " ");
                }else{
                    curNode.setConstraint(curNode.getConstraint() + "&& " + v2Value + " < " + v1Value + " ");
                }
                break;
            case Opcodes.IF_ICMPGT:// 2 > 1
                stackViable1 = stack.pop();
                stackViable2 = stack.pop();
                v1Value = stackViable1.getValue() != null ? stackViable1.getValue() : "I"+stackViable1.getIndex();
                v2Value = stackViable2.getValue() != null ? stackViable2.getValue() : "I"+stackViable2.getIndex();
                if(curNode.getConstraint().equals("")){
                    curNode.setConstraint(v2Value + " <= " + v1Value + " ");
                }else{
                    curNode.setConstraint(curNode.getConstraint() + "&& " + v2Value + " <= " + v1Value + " ");
                }
                break;
            case Opcodes.IF_ICMPLE:// 2 <= 1
                stackViable1 = stack.pop();
                stackViable2 = stack.pop();
                v1Value = stackViable1.getValue() != null ? stackViable1.getValue() : "I"+stackViable1.getIndex();
                v2Value = stackViable2.getValue() != null ? stackViable2.getValue() : "I"+stackViable2.getIndex();
                if(curNode.getConstraint().equals("")){
                    curNode.setConstraint(v2Value + " > " + v1Value + " ");
                }else{
                    curNode.setConstraint(curNode.getConstraint() + "&& " + v2Value + " > " + v1Value + " ");
                }
                break;
            case Opcodes.IF_ICMPLT:// 2 < 1
                stackViable1 = stack.pop();
                stackViable2 = stack.pop();
                v1Value = stackViable1.getValue() != null ? stackViable1.getValue() : "I"+stackViable1.getIndex();
                v2Value = stackViable2.getValue() != null ? stackViable2.getValue() : "I"+stackViable2.getIndex();
                if(curNode.getConstraint().equals("")){
                    curNode.setConstraint(v2Value + " >= " + v1Value + " ");
                }else{
                    curNode.setConstraint(curNode.getConstraint() + "&& " + v2Value + " >= " + v1Value + " ");
                }
                break;
            case Opcodes.IF_ICMPNE:// 2 != 1
                stackViable1 = stack.pop();
                stackViable2 = stack.pop();
                v1Value = stackViable1.getValue() != null ? stackViable1.getValue() : "I"+stackViable1.getIndex();
                v2Value = stackViable2.getValue() != null ? stackViable2.getValue() : "I"+stackViable2.getIndex();
                if(curNode.getConstraint().equals("")){
                    curNode.setConstraint(v2Value + " == " + v1Value + " ");
                }else{
                    curNode.setConstraint(curNode.getConstraint() + "&& " + v2Value + " == " + v1Value + " ");
                }
                break;
            default:
                break;
        }

        System.out.println("visitJumpInsn: " + i + "#" + label);
    }

    public void visitLabel(Label label) {
        int labelId = getLabelId(label);
        Node visitNode = root.getTrueNode().findNodeByLabel(labelId);
        if(visitNode == null){
            if(!curNode.isEnd()){
                if(curNode.getFalseNode() == null && curNode.getTrueNode() == null){
                    curNode.setCurLable(labelId);
                }else if(curNode.getFalseNode() != null && curNode.getTrueNode() == null){
                    Node trueNode = new Node(curNode,labelId);
                    curNode.setTrueNode(trueNode);
                    curNode = trueNode;
                }
            }
        }else{
            curNode = visitNode;
        }
        System.out.println("visitLabel: " + label);
    }

    public void visitLdcInsn(Object o) {
        System.out.println("visitLdcInsn: " + o);
    }

    public void visitIincInsn(int i, int i1) {
        System.out.println("visitIincInsn: " + i + "#" + i1);
    }

    public void visitTableSwitchInsn(int i, int i1, Label label, Label[] labels) {
        System.out.println("visitTableSwitchInsn: " + i + "#" + i1 + "#" + label + "#" + labels);
    }

    public void visitLookupSwitchInsn(Label label, int[] ints, Label[] labels) {
        System.out.println("visitLookupSwitchInsn: " + label + "#" + ints + "#" + labels);
    }

    public void visitMultiANewArrayInsn(String s, int i) {
        System.out.println("visitMultiANewArrayInsn: " + s + "#" + i);
    }

    public void visitTryCatchBlock(Label label, Label label1, Label label2, String s) {
        System.out.println("visitTryCatchBlock: " + label + "#" + label1 + "#" + label2 + "#" + s);
    }

    public void visitLocalVariable(String s, String s1, String s2, Label label, Label label1, int i) {
        for(Variable variable: localVariableList){
            if(variable.getIndex() == i){
                variable.setVariableName(s);
                variable.setVariableType(s1);
                break;
            }
        }
        System.out.println("visitLocalVariable: " + s + "#" + s1 + "#" + s2 + "#" + label + "#" + label1 + "#" + i);
    }

    public void visitLineNumber(int i, Label label) {
        System.out.println("visitLineNumber: " + i + "#" + label);
    }

    public void visitMaxs(int i, int i1) {
        if(maxLOCALS == -1){
            maxLOCALS = i1;
        }
        System.out.println("visitMaxs:" + i + "#" + i1);
    }

    public void visitEnd() {
        rebuildRoot(root.getTrueNode());
        System.out.println("visitEnd");
    }

    private void rebuildRoot(Node node){
        if(node.getConstraint().equals("")){
            return;
        }
        String constraint = new String(node.getConstraint());
        String constraintList[] = constraint.split(" ");
        for (int i = 0; i < constraintList.length; ++i){
            if(!constraintList[i].equals("") && constraintList[i].charAt(0) == 'I'){
                constraintList[i] = constraintList[i].substring(1);
                int index = Integer.parseInt(constraintList[i]);
                constraintList[i] = getVariableName(index);
            }
        }
        String res = new String("");
        for(String str:constraintList){
            if(!str.equals("")){
                res = res + " " + str;
            }

        }
        node.setConstraint(res);
        if(node.getFalseNode() != null){
            rebuildRoot(node.getFalseNode());
        }
        if(node.getTrueNode() != null){
            rebuildRoot(node.getTrueNode());
        }
    }
    private String getVariableName(int index){
        for(Variable variable: localVariableList){
            if(variable.getIndex() == index){
                return new String(variable.getVariableName());
            }
        }
        return null;
    }
}
