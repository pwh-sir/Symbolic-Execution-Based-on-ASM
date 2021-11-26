package symbolicExecution;

import java.io.*;
import java.util.ArrayList;

public class Graphviz {
    private Node root;
    private String dotName;
    public Graphviz(Node root, String dotName){
        this.root = root;
        this.dotName = dotName;
    }
    public void drawRoot(){
        File file = new File("src/main/resources/" + dotName + ".dot");
        FileWriter fileWriter;
        BufferedWriter writer;
        try{
            if(!file.exists()){
                file.createNewFile();
            }
            fileWriter = new FileWriter(file.getAbsoluteFile());
            writer = new BufferedWriter(fileWriter);
            writer.write("digraph " + dotName + "{\n");
            ArrayList<String> nodeList = new ArrayList<String>();
            ArrayList<edge> edgeList = new ArrayList<edge>();
            getNodeList(nodeList,root.getTrueNode());
            getEdgeList(nodeList,edgeList,root.getTrueNode());
            writer.write("start -> node0;\n");
            for(edge e:edgeList){
                writer.write("node" + e.aIndex + " -> " + "node" + e.bIndex + "[label =\"" + e.type + "\"];\n");
            }
            writer.write("start;\n");
            for(int i = 0; i < nodeList.size(); ++i){
                writer.write("node" + i + "[label =\"" + nodeList.get(i) + "\"];\n");
            }
            writer.write("}");
            writer.close();
        }catch (IOException e){
            e.printStackTrace();
        }

    }
    private void getNodeList(ArrayList<String> nodeList, Node curNode){
        if(curNode == null){
            return;
        }
        String constraint = curNode.getConstraint();
        Node trueNode = curNode.getTrueNode();
        Node falseNode = curNode.getFalseNode();
        String trueEdge;
        String falseEdge;
        if(constraint == null || constraint.equals("")){
            return;
        }
        boolean isAdded = false;
        for(String str:nodeList){
            if(str.equals(constraint)){
                isAdded = true;
                break;
            }
        }
        if(!isAdded){
            nodeList.add(new String(constraint));
        }
        getNodeList(nodeList,curNode.getTrueNode());
        getNodeList(nodeList,curNode.getFalseNode());
    }
    private void getEdgeList(ArrayList<String> nodeList, ArrayList<edge> edgeList, Node curNode){
        if(curNode == null || curNode.getConstraint() == null || curNode.getConstraint().equals("")){
            return;
        }
        int curId;
        int trueId;
        int falseId;
        String curConstraint = curNode.getConstraint();
        curId = getID(nodeList,curConstraint);
        if(curNode.getTrueNode() != null && curNode.getTrueNode().getConstraint() != null && !curNode.getTrueNode().getConstraint().equals("")){
            trueId = getID(nodeList,curNode.getTrueNode().getConstraint());
            edgeList.add(new edge(curId,trueId,"true"));
            getEdgeList(nodeList,edgeList,curNode.getTrueNode());
        }
        if(curNode.getFalseNode() != null && curNode.getFalseNode().getConstraint() != null && !curNode.getFalseNode().getConstraint().equals("")){
            falseId = getID(nodeList,curNode.getFalseNode().getConstraint());
            edgeList.add(new edge(curId,falseId,"false"));
            getEdgeList(nodeList,edgeList,curNode.getFalseNode());
        }
    }
    private int getID(ArrayList<String> nodeList, String constraint){
        if(constraint == null || constraint.equals("")){
            return -1;
        }else{
            for(int i = 0; i < nodeList.size(); ++i){
                if(nodeList.get(i).equals(constraint)){
                    return i;
                }
            }
        }
        return -1;
    }
}

class edge{
    int aIndex;
    int bIndex;
    String type;
    public edge(int aIndex, int bIndex, String type){
        this.aIndex = aIndex;
        this.bIndex = bIndex;
        this.type = type;
    }
}