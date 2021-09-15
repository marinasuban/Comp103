// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a COMP103 assignment.
// You may not distribute it in any other way without permission.

/* Code for COMP103 - 2020T2, Assignment 4
 * Name:
 * Username:
 * ID:
 */

import java.util.*;
import ecs100.UI;

/**
 * Simple simulation of unloading passengers from a plane on the airport 
 * tarmac (not at a gate), and taking the passengers to the terminal by
 * a series of shuttles.
 *
 * There are four shuttles (numbered 0 to 3) going back and forward to the terminal. 
 * Each shuttle has a maximum capacity (ranging from 3 to 8).
 * 
 * For each shuttle, there is a small area on the tarmac 
 * where up to 6 passengers can line up between the barriers.
 * Shuttles will take their passengers off to the terminal whenever
 * they are full, or when there are no passengers left in their queue.
 * 
 * Passengers on the plane are let off in priority order, with
 *  the first-class passengers first,
 *  the business-class passengers next, and then
 *  the economy-class passengers. 
 * Within each class, the priority is governed by their seat number.
 * 
 * For safety reasons, passengers can only be let off the plane when
 * there is space for them in a shuttle waiting area.
 * Until there is space, they have to wait on the plane.
 * Passengers always go to the lowest number queue that has space.

 * The simulation should start by initialising all the queues, and
 *  putting all the passengers onto the plane.
 * Then, each time tick the simulation will:
 *   - Check each shuttle
 *       if it is waiting and full: then start the trip
 *       if it is waiting and not empty, but its queue is empty: then start the trip
 *       if it is waiting and not full and there is a passenger in its queue:
 *              put the passenger on the shuttle
 *       if it is not waiting (ie going to the terminal): advance the trip by one tick.
 *   - Let one passenger off the plane (in priority order), into the
 *     first shuttle queue that has space (if any of them have space)
 * The process repeats until all the passengers have gone to the terminal.
 * 
 * The simulation must create and return a list of every event in the simulation
 * There are three kinds of events to record:
 *    new Event("deplane", p, n)       [passenger p got off the plane to the n'th shuttle queue]
 *    new Event("onShuttle", p, n)     [passenger p got on shuttle n]
 *    new Event("toTerminal", n)       [shuttle n left for the terminal]
 * Your code should add all the Events to the record list.
*/


public class TarmacShuttle {
        
    public static final int SHUTTLE_QUEUE_MAX = 6;  // max number of passengers in a shuttle queue

    
    private boolean running = false;
    private int time = 0; // The simulated time - the current "tick"
    private int numPassengers = 100;
    private int delay = 500; // milliseconds of real time for each tick
        

    /**
     * Run the simulation.
     * The first argument is a set of passengers that start off on the plane.
     */
    public List<Event> run(Set<Passenger> passengers) {
        // The record of all passenger movements
        List<Event> record = new ArrayList<Event>();

        // the four shuttles, with their number and their capacity
        Set<Shuttle> shuttles = Set.of(new Shuttle(0, 3),
                                       new Shuttle(1, 4),
                                       new Shuttle(2, 6),
                                       new Shuttle(3, 8));

        // Initialise the Queues for the plane and the four shuttle queues.
        // and fill the plane with the passengers (from the Set of passengers).
        /*# YOUR CODE HERE */
        Queue<Passenger> plane = new PriorityQueue<>(passengers);
        UI.println(plane);
        List<Queue<Passenger>> shuttleQueues = new ArrayList<>();
        for (Shuttle s : shuttles) {
            shuttleQueues.add(new ArrayDeque<Passenger>());
        }
        // loop
        while(true) {
            // Process each shuttle and its queue. See details above.
            // Record the passenger movements
            /*# YOUR CODE HERE */
            this.time++;
            for (Shuttle s : shuttles) {
                var queue = shuttleQueues.get(s.getNum());
                //if shuttles are moving -> advanceTrip
                if (!s.isWaiting()) s.advanceTrip();
                //else if its full -> startTrip
                //or doesnt  have anymore more passengers
                else if (s.isFull() ||(!s.isEmpty() && queue.isEmpty())) {
                    s.startTrip(); 
                    //log
                    record.add(new Event("toTerminal", s.getNum()));
                }
                
                //is not full but has a passenger on queue
                else if (!s.isFull() && !queue.isEmpty()) {
                    var passenger=queue.poll();
                    s.addPassenger(passenger);
                    //log
                    record.add(new Event("onShuttle", passenger, s.getNum()));
                }
            }

            // Move a passenger from the plane onto the first shuttle queue with
            // space, if there is one.
            // Record the passenger movement
            /*# YOUR CODE HERE */
            if (!plane.isEmpty()) {
                boolean passengerOffPlane = false;
                for(int i = 0; i < shuttleQueues.size();i++){
                    var sQueue = shuttleQueues.get(i);
                    if (sQueue.size()<SHUTTLE_QUEUE_MAX){
                        if (passengerOffPlane == false) {
                            var p = plane.poll();
                            sQueue.offer(p);
                            record.add(new Event("deplane", p,i));
                            passengerOffPlane = true;
                        }
                    }
                }
            }

            redraw(plane, shuttleQueues, shuttles);

            // Break if simulation is finished -
            // all the queues are empty.
            /*# YOUR CODE HERE */
            if (plane.isEmpty()) {
                boolean allQueuesEmpty = true;
                boolean allShuttlesEmpty = true;
                for (Queue<Passenger> q : shuttleQueues) {
                    if (!q.isEmpty()) {
                        allQueuesEmpty = false;
                    }
                }
                
                for (Shuttle s: shuttles) {
                    if (!s.isEmpty()) {
                        allShuttlesEmpty = false;
                    }
                }
                if (allQueuesEmpty && allShuttlesEmpty) break;
            }
            UI.sleep(delay);
        }
        return record;    // Do not remove:  Essential for the marking
    }
        
        
    /**
     * Construct a new object, setting up the GUI, and resetting
     */
    public static void main(String[] arguments) {
        TarmacShuttle q= new TarmacShuttle();
        q.setupGUI();
    }
    
    /**
     * Set up the GUI: buttons to control simulation and sliders for setting parameters
     */
    public void setupGUI() {
        UI.addButton("Test run", this::testRun);
        UI.addSlider("Num Passengers", 5, 500, 100, (double v) -> {numPassengers = (int)v;});
        UI.addSlider("Delay", 1, 1000, 500, (double v) -> {delay = (int)v;});
        UI.addButton("Quit", UI::quit);
        UI.setWindowSize(1000, 600);
        UI.setDivider(0);
    }
        
    public void testRun(){
        if (! running) {
            running = true;
            List<Event> events =  run(Passenger.makePassengers(numPassengers));
            running = false;
            UI.clearText();
            for(Event ev : events){ UI.println(ev); }
        }
    }



        
    /** redraw the state of the simulation */
    public void redraw(Queue<Passenger> plane, List<Queue<Passenger>> shuttleQueues, Set<Shuttle> shuttles) {
        UI.clearGraphics();
        UI.setFontSize(14);

        // The Plane
                
        double y = 50;
        double x = 10;
        UI.drawString("Plane Passengers", x, y-35);
        for(Passenger p: plane) {
            p.draw(x, y);
            x += 10;
        }
        UI.drawLine(0, y+2, 600, y+2);
                
        // Shuttle queues
        y = 100;
        for(int n=0; n<shuttleQueues.size(); n++){
            Queue<Passenger> sq = shuttleQueues.get(n);
            UI.setFontSize(14);
            x = 10+n*100;
            UI.drawString("Queue "+n, x, y-35);
            UI.drawRect(x-5, y-30, SHUTTLE_QUEUE_MAX*10, 30); 
            for (Passenger p : sq) {
                p.draw(x, y);
                x += 10;
            }
        }
        UI.drawLine(0, y + 2, 450, y + 2);

        // Shuttles
        y = 150;
        for(Shuttle shuttle : shuttles){
            shuttle.draw(10+shuttle.getNum()*100, y);
        }
        UI.drawLine(0, y + 2, 450, y + 2);
        y = 350;
        UI.drawLine(0, y + 2, 450, y + 2);
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
        List<Event> events =  run(new HashSet<Passenger>());
    }

}
