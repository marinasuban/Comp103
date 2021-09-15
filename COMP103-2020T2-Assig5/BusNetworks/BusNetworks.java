// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a COMP103 assignment.
// You may not distribute it in any other way without permission.

/* Code for COMP103 - 2020T2, Assignment 5
 * Name:
 * Username:
 * ID:
 */

import ecs100.*;
import java.io.*;
import java.util.*;
import java.nio.file.*;

public class BusNetworks {

    /** Map of towns, indexed by their names */
    private Map<String,Town> busNetwork = new HashMap<String,Town>();
    public static final int SIZE = 6;
    /** CORE
     * Loads a network of towns from a file.
     * Constructs a Set of Town objects in the busNetwork field
     * Each town has a name and a set of neighbouring towns
     * First line of file contains the names of all the towns.
     * Remaining lines have pairs of names of towns that are connected.
     */
    public void loadNetwork(String filename) {
        try {
            
            busNetwork.clear();
            UI.clearText();
            List<String> lines = Files.readAllLines(Path.of(filename));
            String firstLine = lines.remove(0);
            UI.println(firstLine);
            /*# YOUR CODE HERE */
            List<String> listOftown= new ArrayList<String>();
            Scanner scan1 = new Scanner(firstLine);
            while(scan1.hasNext()){
            listOftown.add(scan1.next());
            }
            for(String townName:listOftown){
                if (townName==null){return;}
                else{
                    Town town = new Town(townName);
                    busNetwork.put(townName,town);
                }
            }
            for(String connection:lines){
                Scanner scan = new Scanner(connection);
                String town1= scan.next();
                String town2= scan.next();
                Town firstTown=busNetwork.get(town1);
                Town secondTown=busNetwork.get(town2);
                if (firstTown == null || secondTown == null){return;}
                else{
                    firstTown.addNeighbour(secondTown);
                    secondTown.addNeighbour(firstTown);
                }

            }
            UI.println("Loaded " + busNetwork.size() + " towns:");

        } catch (IOException e) {throw new RuntimeException("Loading data.txt failed" + e);}
    }
    
    public void drawNetwork() {
        try {
            double yoffset = 35;
            double xoffset = -166;
            int margin = 50;
            busNetwork.clear();
            UI.clearPanes();
            List<String> lines = Files.readAllLines(Path.of("data-with-lat-long.txt"));
            String firstLine = lines.remove(0);
            Scanner flscan = new Scanner(firstLine);
            int numTowns = flscan.nextInt();
            
            for (int i = 0; i < numTowns; i++ ) {
                String line = lines.remove(0); //processing lines like a queue
                Scanner sc = new Scanner(line);
                String townName = sc.next();
                double lat = sc.nextDouble();
                // something is wrong with data... NZ lattitudes are never positive
                if (lat > 0) lat *= (-1); 
                lat += yoffset; //offset
                
                double longi = sc.nextDouble(); //skewing the offset
                longi += xoffset; //offset
                
                double x = longi * 45;
                double y = (lat * -45) + margin;
                UI.fillOval(x-(SIZE/2),y-(SIZE/2),SIZE,SIZE);
                UI.setFontSize(9);
                UI.drawString(townName,x,y);
                Town town = new Town(townName);
                town.x = x; town.y = y;
                busNetwork.put(townName,town);
                
                
            }
            for(String connection:lines){
                Scanner scan = new Scanner(connection);
                String town1= scan.next();
                String town2= scan.next();
                Town firstTown=busNetwork.get(town1);
                Town secondTown=busNetwork.get(town2);
                if (firstTown == null || secondTown == null){return;}
                else{
                    firstTown.addNeighbour(secondTown);
                    secondTown.addNeighbour(firstTown);
                }  
            }
            
            for (Town t : busNetwork.values()) {
                for (Town neighbour : t.getNeighbours()) {
                    UI.drawLine(t.x, t.y, neighbour.x, neighbour.y);
                }
            }
            
        } catch (Exception e) {
            throw new RuntimeException("Loading data.txt failed" + e);
        }
    }

    /**  CORE
     * Print all the towns and their neighbours:
     * Each line starts with the name of the town, followed by
     *  the names of all its immediate neighbours,
     */
    public void printNetwork() {
        UI.println("The current network: \n====================");
        /*# YOUR CODE HERE */
        for(String town:busNetwork.keySet()){
            if(!town.equals(null)){
            UI.println(busNetwork.get(town).getName() + " -> " +busNetwork.get(town).getNeighbours());
        }
        }

    }

    /** COMPLETION
     * Return a set of all the nodes that are connected to the given node.
     * Traverse the network from this node in the standard way, using a
     * visited set, and then return the visited set
     */
    public Set<Town> findAllConnected(Town town) {
        return findAllConnected1(town, new HashSet<Town>());

    }

    public Set<Town> findAllConnected1(Town town,Set<Town> visited){

        visited.add(town);
        for (Town neighbours : town.getNeighbours()){
            if (! visited.contains(neighbours) ){
                findAllConnected1(neighbours, visited);
            }
        }
        return visited;
    }

    /**  COMPLETION
     * Print all the towns that are reachable through the network from
     * the town with the given name.
     * Note, do not include the town itself in the list.
     */
    public void printReachable(String name){
        Town town = busNetwork.get(name);
        if (town==null){
            UI.println(name+" is not a recognised town");
        }
        else {
            UI.println("\nFrom "+town.getName()+" you can get to:");
            /*# YOUR CODE HERE */
            if(findAllConnected(town)==null){return;}
            else{
                for (Town townName: findAllConnected(town)){
                    if(!townName.getName().equals(town)){
                        UI.println(townName.getName());
                    }
                }
            }
        }

    }

    /**  COMPLETION
     * Print all the connected sets of towns in the busNetwork
     * Each line of the output should be the names of the towns in a connected set
     * Works through busNetwork, using findAllConnected on each town that hasn't
     * yet been printed out.
     */
    public void printConnectedGroups() {
        UI.println("Groups of Connected Towns: \n================");
        int groupNum = 1;
        /*# YOUR CODE HERE */
        List<String> listOfTown = new ArrayList<>(busNetwork.keySet());
        List<List> listOfGroup=new ArrayList<>();
        String root=listOfTown.remove(0);
        findAllConnected(root, listOfTown, listOfGroup);
        if(listOfGroup.isEmpty()){return;}
        else{
            for(List group:listOfGroup){
            UI.println("Group " +groupNum+" : "+group);
            groupNum++;
            }
        }
    }

    public void findAllConnected(String root, List<String> listOfTown, List<List> listOfGroup){
        
        if(findAllConnected(busNetwork.get(root))==null){return;}
            else{
                List<String> group=new ArrayList<>();
                group.add(root);
                for (Town townName: findAllConnected(busNetwork.get(root))){
                    if(!townName.getName().equals(root)){
                        group.add(townName.getName());
                        listOfTown.remove(townName.getName());
                    }
                }
                listOfGroup.add(group);
            }
            if(!listOfTown.isEmpty()){
            root=listOfTown.remove(0);
            findAllConnected(root, listOfTown, listOfGroup);
        }
    }

    /**
     * Set up the GUI (buttons and mouse)
     */
    public void setupGUI() {
        UI.addButton("Load", ()->{loadNetwork(UIFileChooser.open());});
        UI.addButton("Print Network", this::printNetwork);
        UI.addTextField("Reachable from", this::printReachable);
        UI.addButton("Draw Network", this::drawNetwork);
        UI.addButton("All Connected Groups", this::printConnectedGroups);
        UI.addButton("Clear", UI::clearText);
        UI.addButton("Quit", UI::quit);
        UI.setWindowSize(1100, 500);
        UI.setDivider(1.0);
        loadNetwork("data-small.txt");
    }

    // Main
    public static void main(String[] arguments) {
        BusNetworks bnw = new BusNetworks();
        bnw.setupGUI();
    }

}
