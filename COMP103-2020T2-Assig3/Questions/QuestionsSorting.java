// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a COMP103 assignment.
// You may not distribute it in any other way without permission.

/* Code for COMP103 - 2020T2, Assignment 3
 * Name:
 * Username:
 * ID:
 */

//DO NOT CHANGE THESE IMPORTS OR ADD ANY FURTHER IMPORTS.
import ecs100.*;   
import java.util.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;
//import java.io.*;

/**
 * QuestionsSorting:
 *   Automarked questions about Sorting
 *   Requires that you modify 
 */

public class QuestionsSorting {

    // Part (a)
    // Modify the Storm class so that the testSortStormList() method works correctly.

    // Part (b)
    /**
     * Sort a list of Offcuts by their area, from largest to smallest.
     * The method should just modify the list and return it; is should NOT print it out.
     * You may NOT modify the Offcut class (note, it is not Comparable)
     */
    public List<Offcut> sortOffcutsByArea(List<Offcut> offcuts){
        if (offcuts==null) {return null;}
        int n = offcuts.size();
        Offcut temp = null;
        for(int i=0; i < n; i++){  
            for(int j=1; j < (n-i); j++){ 
                double offcuts1Area = (offcuts.get(j-1).getLength()) * (offcuts.get(j-1).getWidth());
                double offcuts2Area = (offcuts.get(j).getLength()) * (offcuts.get(j).getWidth());
                if(offcuts1Area < offcuts2Area){  
                    //swap elements  
                    int indexoffcuts1Area=offcuts.indexOf(offcuts.get(j-1));
                    int indexoffcuts2Area=offcuts.indexOf(offcuts.get(j));
                    Offcut temp1 = offcuts.get(j-1);
                    Offcut temp2 = offcuts.get(j);
                    offcuts.set(indexoffcuts1Area,temp2);
                    offcuts.set(indexoffcuts2Area, temp1);

                }  

            }  
        }  
        
        return offcuts;  // do not change or remove this line.
    }

    // Part (c)
    /**
     * Sort a list of Offcuts by how close their area is to a target area,
     * sorting from offcuts closest to the target (whether larger or smaller)
     *  to offcuts furthest from the target (whether larger or smaller)
     * The method should just modify the list and return it; is should NOT print it out.
     * You may NOT modify the Offcut class (note, it is not Comparable)
     */
    public List<Offcut> sortOffcutsBySimilarity(List<Offcut> offcuts, double targetArea){
        if (offcuts==null) {return null;}
        this.sortOffcutsByArea(offcuts);
        int n = offcuts.size();
        Offcut temp = null;
        for(int i=0; i < n; i++){  
            for(int j=1; j < (n-i); j++){ 
                double offcuts1TD =Math.abs( targetArea-((offcuts.get(j-1).getLength()) * (offcuts.get(j-1).getWidth())));
                double offcuts2TD =Math.abs(targetArea-((offcuts.get(j).getLength()) * (offcuts.get(j).getWidth())));
                if(offcuts1TD> offcuts2TD){  
                    //swap elements  
                    int indexoffcuts1Area=offcuts.indexOf(offcuts.get(j-1));
                    int indexoffcuts2Area=offcuts.indexOf(offcuts.get(j));
                    Offcut temp1 = offcuts.get(j-1);
                    Offcut temp2 = offcuts.get(j);
                    offcuts.set(indexoffcuts1Area,temp2);
                    offcuts.set(indexoffcuts2Area, temp1);

                }  

            }  
        }  
        
        return offcuts;  // do not change or remove this line.
    }


    //===========================================================
    // For your testing.

    private List<Storm> stormList;
    private List<Offcut> offcutList;

    public static void main(String[] args){
        new QuestionsSorting().setupGUI();
    }

    public void setupGUI(){
        UI.addButton("Sort list of Storms", this::testSortStormList);
        UI.addButton("Sort Offcuts by area", this::testSortOffcutsByArea);
        UI.addButton("Sort Offcuts by similarity", this::testSortOffcutsBySimilarity);

        UI.addButton("Load Storms", () -> {loadStormList(UIFileChooser.open("Storm file"));});
        UI.addButton("Load Offcuts", () -> {loadOffcutList(UIFileChooser.open("Offcut file"));});

        UI.addButton("quit", UI::quit);
        UI.setDivider(1.0);
        loadStormList("storms.txt");
        loadOffcutList("offcuts.txt");
    }

    /**
     * Test sorting Storms
     * To make this work, you must modify the Storm class so that
     *  the Collections.sort method will work on the list of Storms.
     */
    public void testSortStormList(){
        UI.clearText();
        UI.println("--------Storms before sorting--------");
        for(Storm storm : stormList){UI.println(storm);}

        Collections.sort(stormList); 

        UI.println("--------Storms after sorting--------");
        for(Storm storm : stormList){UI.println(storm);}
        UI.println("-----------------------------------");
    }

    /** Test the sortOffcutsByArea method */
    public void testSortOffcutsByArea(){
        List<Offcut> ans = new ArrayList<Offcut>();
        ans.addAll(offcutList);
        UI.println("--------Offcuts before sorting--------");
        for(Offcut offcut : ans){UI.println(offcut);}

        ans = sortOffcutsByArea(ans);

        UI.println("--------Offcuts after sorting--------");
        for(Offcut offcut : ans){UI.println(offcut);}
        UI.println("-----------------------------------");
    }

    /** Test the sortOffcutsBySimilarity method */
    public void testSortOffcutsBySimilarity(){
        double target = UI.askDouble("Target area:");
        List<Offcut> ans = new ArrayList<Offcut>();
        ans.addAll(offcutList);
        UI.println("--------Offcuts before sorting--------");
        for(Offcut offcut : ans){UI.println(offcut);}

        ans = sortOffcutsBySimilarity(ans, target);

        UI.println("--------Offcuts after sorting by similarity to target area--------");
        for(Offcut offcut : ans){UI.println(offcut);}
        UI.println("-----------------------------------");
    }

    /** Reads a list of storms from the given file into the stormList field */
    public void loadStormList(String fname){
        try{
            stormList = new ArrayList<Storm>();
            List<String> lines = Files.readAllLines(Path.of(fname));
            for (String line : lines){
                Scanner sc = new Scanner(line);
                stormList.add(new Storm(sc.next(), sc.nextInt(), sc.nextInt(), sc.next(), sc.next()));
            }
        } catch(IOException e){UI.println("Failed to read storm file "+fname+" " + e);}
        UI.println("-------------");
        UI.println("Stormlist is now:");
        for(Storm storm : stormList) { UI.println(storm); }
        UI.println("-------------");
    }

    /** Reads a list of offcuts from the given file into the offcutList field */
    public void loadOffcutList(String fname){
        try{
            List<String> lines = Files.readAllLines(Path.of(fname));
            offcutList = new ArrayList<Offcut>();
            for (String line : lines){
                Scanner sc = new Scanner(line);
                offcutList.add(new Offcut(sc.nextLong(),sc.next(),
                        sc.nextDouble(),sc.nextDouble()));
            }
        } catch(IOException e){UI.println("Failed to read offcut file "+fname+" " + e);}
        UI.println("-------------");
        UI.println("Offcutlist is now:");
        for(Offcut offcut : offcutList) { UI.println(offcut); }
        UI.println("-------------");
    }

    //================================================
    // DO NOT CHANGE ANYTHING BELOW THIS LINE!
    // IF YOU CHANGE THIS TO MAKE YOUR CODE COMPILE,
    // THE AUTOMATED MARKING WILL FAIL AND GIVE YOU ZERO

    public void checkCompile(){
        sortOffcutsByArea(new ArrayList<Offcut>());
        sortOffcutsBySimilarity(new ArrayList<Offcut>(), 100.0);
    }

}
