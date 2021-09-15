// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a COMP103 assignment.
// You may not distribute it in any other way without permission.

/* Code for COMP103 - 2020T2, Assignment 2
 * Name:
 * Username:
 * ID:
 */

//DO NOT CHANGE THESE IMPORTS OR ADD ANY FURTHER IMPORTS.
import ecs100.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Stack;

/** ListsOfLists */
public class ListsOfLists{
    /**
     * Given a list of lists of Things, append all the lists into a single list and return it.
     * The method should not modify the lists.
     */
    public List<Thing> appendAll(List<List<Thing>> lists){
        List<Thing> appendList = new ArrayList<Thing>();
        for (int i=0; i<lists.size(); i++){
            List<Thing> append = lists.get(i);
            for (int k=0; k<append.size(); k++){
                appendList.add(append.get(k));
            }
        }
        return appendList;
    }

    /**
     * Given a list of lists of Things,
     * merge the lists into a single list, containing all the first elements of the lists,
     * followed by all the second elements of the lists, etc.
     * The lists do not need to be the same size
     * The method should not modify the lists.
     */
    public List<Thing> mergeZipped(List<List<Thing>> lists){
        List<Thing> mergeList = new ArrayList<Thing>();
        int max=-1;
        for (int i=0; i<lists.size(); i++){
            if (lists.get(i).size() > max){
                max=lists.get(i).size();
            }    
        }
      
       for (int i = 0;i < max;i++) {        
            for (int k = 0;k < lists.size();k++) {      
                List<Thing> temp = lists.get(k);
                if (temp.size()-1 >= i) {
                    mergeList.add(temp.get(i));
                }
            }
                
        }
        return mergeList;
    }

    /**
     * Given a list of _sorted_ lists of Things (ie, the Things in each list are in order)
     * Merge the lists into a single list, in sorted order.
     * None of the lists contain null.
     * At each step:
     *  Find the next item to add to the answer by looking at the front item of each list to find the smallest.
     *  Move that item from the list to the answer.
     * The lists may not be the same size
     * The method may not modify the lists
     */
    public List<Thing> mergeSorted(List<List<Thing>> lists){
        /*# YOUR CODE HERE */
        List<Thing> mergedList = this.mergeZipped(lists);
        List<Thing> mergeSort = new ArrayList<Thing>();
        if (mergedList.size() > 0) 
        {
            
            while(mergedList.size() > 0) {
                Thing min = mergedList.get(0);
                for (Thing t : mergedList) {
                    if (t.compareTo(min) < 0) {
                        min = t;
                    }
                }
                mergedList.remove(mergedList.indexOf(min));
                mergeSort.add(min);
            }
            
        }
       
        return mergeSort;   // You may remove this line - it is just to make the template compile!

    }

    //--------- TESTING --------------------------------------------

    /**
     * Setup the GUI
     */
    public void setupGUI(){
        UI.addButton("AppendAll", this::testAppendAll);
        UI.addButton("MergeZipped", this::testMergeZipped);
        UI.addButton("MergeSorted", this::testMergeSorted);
        UI.addButton("Quit", UI::quit);
        UI.setDivider(1.0);
    }

    /** Test the appendAll method */
    public void testAppendAll(){
        List<List<Thing>> lists = new ArrayList<List<Thing>>();
        for (int i=0; i<4; i++){
            lists.add(Thing.makeList(UI.askString("Enter letters for list:")));
        }
        UI.println("appendAll -> ");
        UI.println(appendAll(lists));
    }

    /** Test the mergeZipped method */
    public void testMergeZipped(){
        List<List<Thing>> lists = new ArrayList<List<Thing>>();
        for (int i=0; i<4; i++){
            lists.add(Thing.makeList(UI.askString("Enter letters for list:")));
        }
        UI.println("mergeZipped -> ");
        UI.println(mergeZipped(lists));
    }

    /** Test the mergeSorted method */
    public void testMergeSorted(){
        List<List<Thing>> lists = new ArrayList<List<Thing>>();
        for (int i=0; i<4; i++){
            lists.add(Thing.makeSortedList(UI.askString("Enter letters for list:")));
        }
        UI.println("mergeSorted of ");
        for (List list : lists){
            UI.println(list);
        }
        UI.println("  -> ");
        UI.println(mergeSorted(lists));
    }

    public static void main(String[] arguments){
        new ListsOfLists().setupGUI();
    }

    //================================================
    // DO NOT CHANGE ANYTHING BELOW THIS LINE!
    // IF YOU CHANGE THIS TO MAKE YOUR CODE COMPILE,
    // THE AUTOMATED MARKING WILL FAIL AND GIVE YOU ZERO

    /**
     * Does nothing, but compiling with this method ensures that the method
     *  headers have not been changed.
     */
    public void checkCompile(){
        List<List<Thing>> lists = new ArrayList<List<Thing>>();
        List<Thing> list = appendAll(lists);
        list = mergeZipped(lists);
        list = mergeSorted(lists);
    }

}

