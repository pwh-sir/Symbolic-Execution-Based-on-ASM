package symbolicExecution;

public class Node{
    private Node parentNode;
    private Node trueNode;
    private Node falseNode;
    private int curLable;
    private String constraint;
    private boolean end;
    public Node(){
        constraint = new String("");
        end = false;
    }
    public Node(Node parentNode, int curLable){
        this.parentNode = parentNode;
        this.curLable = curLable;
        constraint = new String("");
        end = false;
    }
    public Node(Node parentNode){
        this.parentNode = parentNode;
        constraint = new String("");
        end = false;
    }

    public boolean isEnd() {
        return end;
    }

    public void setEnd() {
        end = true;
    }


    public int getCurLable() {
        return curLable;
    }

    public void setCurLable(int curLable) {
        this.curLable = curLable;
    }

    public Node getParentNode() {
        return parentNode;
    }

    public void setParentNode(Node parentNode) {
        this.parentNode = parentNode;
    }

    public Node getTrueNode() {
        return trueNode;
    }

    public void setTrueNode(Node trueNode) {
        this.trueNode = trueNode;
    }

    public Node getFalseNode() {
        return falseNode;
    }

    public void setFalseNode(Node falseNode) {
        this.falseNode = falseNode;
    }

    public String getConstraint() {
        return constraint;
    }

    public void setConstraint(String constraint) {
        this.constraint = constraint;
    }

    public Node findNodeByLabel(int lableId){
        Node resNode;
        if(this.curLable == lableId){
            return this;
        }
        if(trueNode != null &&(resNode = trueNode.findNodeByLabel(lableId)) != null){
            return resNode;
        }
        if(falseNode != null && (resNode = falseNode.findNodeByLabel(lableId)) != null){
            return resNode;
        }
        return null;
    }
}
