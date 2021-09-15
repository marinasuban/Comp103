// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a COMP103 assignment.
// You may not distribute it in any other way without permission.

/* Code for COMP103 - 2020T2, Assignment 4
 * Name:
 * Username:
 * ID:
 */

/**
 * Implements a decision tree that asks a user yes/no questions to determine a decision.
 * Eg, asks about properties of an animal to determine the type of animal.
 * 
 * A decision tree is a tree in which all the internal nodes have a question, 
 * The answer to the question determines which way the program will
 *  proceed down the tree.  
 * All the leaf nodes have the decision (the kind of animal in the example tree).
 *
 * The decision tree may be a predermined decision tree, or it can be a "growing"
 * decision tree, where the user can add questions and decisions to the tree whenever
 * the tree gives a wrong answer.
 *
 * In the growing version, when the program guesses wrong, it asks the player
 * for another question that would help it in the future, and adds it (with the
 * correct answers) to the decision tree. 
 *
 */

import ecs100.*;
import java.util.*;
import java.io.*;
import java.nio.file.*;
import java.awt.Color;

public class DecisionTree {

    public DTNode theTree;    // root of the decision tree;
    public DTNode runTree1Answer;
    public DTNode runTree2Answer;
    public int maxDepth;
    /**
     * Setup the GUI and make a sample tree
     */
    public static void main(String[] args){
        DecisionTree dt = new DecisionTree();
        dt.setupGUI();
        dt.loadTree("sample-animal-tree.txt");
    }

    /**
     * Set up the interface
     */
    public void setupGUI(){
        UI.addButton("Load Tree", ()->{loadTree(UIFileChooser.open("File with a Decision Tree"));});
        UI.addButton("Print Tree", this::printTree);
        UI.addButton("Run Tree", this::runTree);
        UI.addButton("Grow Tree", this::growTree);
        UI.addButton("Grow Tree Challenge", this::growTree2);
        UI.addButton("Save Tree", this::saveTree);  // for completion
        UI.addButton("Draw Tree", this::drawTree);  // for challenge
        UI.addButton("Reset", ()->{loadTree("sample-animal-tree.txt");});
        UI.addButton("Quit", UI::quit);
        UI.setDivider(0.5);
    }

    public void growTree2(){
        this.runTree();
        String userDecision = UI.askString("Is the answer right or wrong?");
        if(userDecision.equals("wrong")){
            String Question = UI.askString("What question would distinguish the right answer from the wrong answer? e.g the color of the stripes");
            String PCategory= UI.askString("What are the possible category? - seperate with , e.g black, red, blue, green");
            String PAnswers= UI.askString("What are the answer to the category? - seperate with , e.g whitetiger, redtiger, bluetiger, greentiger");
            List<String> listOfCategory= new ArrayList<String>(Arrays.asList(PCategory.split(",")));
            List<String> listOfAnswer= new ArrayList<String>(Arrays.asList(PAnswers.split(",")));
            if(listOfCategory.size()==listOfAnswer.size()){
            
            runTree2Answer.setText("Are " + Question + " " + listOfCategory.get(0) +"?");
            DTNode temp = new DTNode(listOfCategory.get(1)+"?"); //no ask again
            DTNode newChild = new DTNode(listOfAnswer.get(0)); //yes
            DTNode RTA= runTree2Answer;
            RTA.setChildren(newChild,temp); //set kids

            addChallenge(listOfCategory, listOfAnswer, 1, RTA);
        }
        else{
        UI.println("length of category is not equal to length of answer");
        return;
        }
        }
    }

    public void addChallenge(List<String> listOfCategory, List<String> listOfAnswer, int i, DTNode RTA ){
        if(listOfAnswer.size()-1==i+1){
            DTNode tempfinal = new DTNode(listOfAnswer.get(i+1));
            DTNode newChildfinal = new DTNode(listOfAnswer.get(i));
            DTNode RTA1 =RTA.getNo();
            RTA1.setChildren(newChildfinal,tempfinal);
            return;
        }
        DTNode temp1 = new DTNode(listOfCategory.get(i+1)+"?");
        DTNode newChild1 = new DTNode(listOfAnswer.get(i));
        DTNode RTA1 =RTA.getNo();
        RTA1.setChildren(newChild1,temp1);
        i=i+1;
        addChallenge(listOfCategory, listOfAnswer, i, RTA1);
    }

    public void drawTree(){
        //printTree();
        //UI.clearText();
        //UI.println(maxDepth);
        double x = 30;
        double y = 300;

        //draw rectangle
        UI.setFontSize(12);
        UI.drawRect(x,y-10,100,20);
        UI.drawString(theTree.getText(), x,y);

        if (!theTree.isAnswer()) {
            this.drawSubTree(theTree.getYes(),true,x,y,y/2);
            this.drawSubTree(theTree.getNo(),false,x,y,y/2);
        }
    }

    public void drawSubTree(DTNode n, boolean isYes, double x, double y, double yOffSet) {
        double offset = 1;
        String prefix = "no: ";
        if (isYes) {
            offset = -1;
            prefix = "yes: ";
        } //so that the 'yes' nodes are higher and the 'no' nodes are lower
        double newX = x+200;
        double newY = y + (yOffSet*offset);
        UI.drawRect(newX, newY-10,100,20);
        UI.drawString(prefix + n.getText(), newX,newY);
        UI.drawLine(newX,newY,x+100,y);
        if (!n.isAnswer()) {
            this.drawSubTree(n.getYes(),true,newX,newY,yOffSet/2);
            this.drawSubTree(n.getNo(),false, newX,newY,yOffSet/2);
        }
    }

    /**  
     * Print out the contents of the decision tree in the text pane.
     * The root node should be at the top, followed by its "yes" subtree, and then
     * its "no" subtree.
     * Each node should be indented by how deep it is in the tree.
     * Needs a recursive "helper method" which is passed a node and an indentation string.
     *  (The indentation string will be a string of space characters)
     */
    public void printTree(){
        this.maxDepth=0;
        UI.clearText();
        DTNode n = theTree;
        printTree1(n, "question", 0);
    }

    public void printTree1(DTNode n, String yesNoToString, int depth){
        String prefix = "";
        if(n!=null){
            for (int i = 0; i < depth;i++) {
                prefix = prefix + "    ";
            }
            if (yesNoToString.equals("yes")) {
                prefix = prefix + "yes:";
            }
            else if (yesNoToString.equals("no")) {
                prefix = prefix + "no:";
            }
            if(depth>maxDepth){
                this.maxDepth=depth;
            }
            UI.println(prefix + n.getText());
            printTree1(n.getYes(), "yes", depth+1);
            printTree1(n.getNo(), "no", depth+1);
        }
    }

    /**
     * Run the tree by starting at the top (of theTree), and working
     * down the tree until it gets to a leaf node (a node with no children)
     * If the node is a leaf it prints the answer in the node
     * If the node is not a leaf node, then it asks the question in the node,
     * and depending on the answer, goes to the "yes" child or the "no" child.
     */
    public void runTree() {
        this.runTree1Answer = null;
        DTNode n = theTree;
        String userDecision = UI.askString("Is it true: " + n.getText() + "(yes/no)");
        if(n != null){
            if(userDecision.equals("yes") ){
                n=n.getYes();
                runTree1(n);
            }
            else if(userDecision.equals("no")){
                n=n.getNo();
                runTree1(n);
            }
        }

    }

    public void runTree1(DTNode n){
        while (n.isAnswer() == false){
            String userDecision = UI.askString("Is it true: " + n.getText() + " (yes/no)");
            if(userDecision.equals("yes") ){
                n=n.getYes();
            }
            else if(userDecision.equals("no")){
                n=n.getNo();
            }
        }
        UI.println("The answer is: " + n.getText());
        this.runTree1Answer = n;
        this.runTree2Answer = n;
    }

    /**
     * Grow the tree by allowing the user to extend the tree.
     * Like runTree, it starts at the top (of theTree), and works its way down the tree
     *  until it finally gets to a leaf node. 
     * If the current node has a question, then it asks the question in the node,
     * and depending on the answer, goes to the "yes" child or the "no" child.
     * If the current node is a leaf it prints the decision, and asks if it is right.
     * If it was wrong, it
     *  - asks the user what the decision should have been,
     *  - asks for a question to distinguish the right decision from the wrong one
     *  - changes the text in the node to be the question
     *  - adds two new children (leaf nodes) to the node with the two decisions.
     */
    public void growTree () {

        this.runTree();
        String userDecision = UI.askString("Is the answer right or wrong?");
        if(userDecision.equals("wrong")){
            String Answer = UI.askString("What should the answer be?");
            String Question = UI.askString("What question would distinguish the right answer from the wrong answer?");

            DTNode temp = new DTNode(runTree1Answer.getText()); //wrong answer
            DTNode newChild = new DTNode(Answer); //right answer

            //runTree1Answer = new DTNode(Question,newChild,temp);            
            runTree1Answer.setText(Question);
            runTree1Answer.setChildren(newChild,temp);
        }

    }

    // You will need to define methods for the Completion and Challenge parts.
    public void saveTree() {
        String fname = UIFileChooser.save("output file");
        if (fname==null) {return;}
        if (!fname.endsWith(".txt") && !fname.endsWith(".TXT")){
            fname = fname+".txt";
        }

        try {
            PrintStream ps = new PrintStream(new File(fname));
            this.saveSubTree(theTree,ps);

        }
        catch (Exception e) {
            UI.println("PrintStream failed");
        }
    }

    public void saveSubTree(DTNode n, PrintStream ps) {

        if (!n.isAnswer()) {
            ps.println("Question: " + n.getText());
            saveSubTree(n.getYes(),ps);
            saveSubTree(n.getNo(),ps);
        }
        else {
            ps.println("Answer: " + n.getText());
        }
    }
    // Written for you
    /** 
     * Loads a decision tree from a file.
     * Each line starts with either "Question:" or "Answer:" and is followed by the text
     * Calls a recursive method to load the tree and return the root node,
     *  and assigns this node to theTree.
     */
    public void loadTree (String filename) { 
        if (!Files.exists(Path.of(filename))){
            UI.println("No such file: "+filename);
            return;
        }
        try{theTree = loadSubTree(new ArrayDeque<String>(Files.readAllLines(Path.of(filename))));}
        catch(IOException e){UI.println("File reading failed: " + e);}
    }

    /**
     * Loads a tree (or subtree) from a Scanner and returns the root.
     * The first line has the text for the root node of the tree (or subtree)
     * It should make the node, and 
     *   if the first line starts with "Question:", it loads two subtrees (yes, and no)
     *    from the scanner and add them as the  children of the node,
     * Finally, it should return the  node.
     */
    public DTNode loadSubTree(Queue<String> lines){
        Scanner line = new Scanner(lines.poll());
        String type = line.next();
        String text = line.nextLine().trim();
        DTNode node = new DTNode(text);
        if (type.equals("Question:")){
            DTNode yesCh = loadSubTree(lines);
            DTNode noCh = loadSubTree(lines);
            node.setChildren(yesCh, noCh);
        }
        return node;

    }

}
