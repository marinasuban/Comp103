// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a COMP103 assignment.
// You may not distribute it in any other way without permission.

/* Code for COMP103 - 2020T2, Assignment 3
 * Name:
 * Username:
 * ID:
 */

import ecs100.*;
import java.util.*;
import java.io.*;

/**
 * Simulation of a Hospital ER
 * 
 * The hospital has a collection of Departments, including the ER department, each of which has
 *  and a treatment room.
 * 
 * When patients arrive at the hospital, they are immediately assessed by the
 *  triage team who determine the priority of the patient and (unrealistically) a sequence of treatments 
 *  that the patient will need.
 *
 * The simulation should move patients through the departments for each of the required treatments,
 * finally discharging patients when they have completed their final treatment.
 *
 *  READ THE ASSIGNMENT PAGE!
 */

public class HospitalERCompl{

    // fields for the statistics
    private Map<String, Department> departments = new HashMap<>(); 
    private boolean isUsingPriorityQueue;
    // Fields for the simulation
    private boolean running = false;
    private int time = 0; // The simulated time - the current "tick"
    private int delay = 300;  // milliseconds of real time for each tick

    // fields controlling the probabilities.
    private int arrivalInterval = 5;   // new patient every 5 ticks, on average
    private double probPri1 = 0.1; // 10% priority 1 patients
    private double probPri2 = 0.2; // 20% priority 2 patients
    private Random random = new Random();  // The random number generator.

    /**
     * Construct a new HospitalERCore object, setting up the GUI, and resetting
     */
    public static void main(String[] arguments){
        HospitalERCompl er = new HospitalERCompl();
        er.setupGUI();
        er.reset(false);   // initialise with an ordinary queue.
    }        

    /**
     * Set up the GUI: buttons to control simulation and sliders for setting parameters
     */
    public void setupGUI(){
        UI.addButton("Reset (Queue)", () -> {this.reset(false); });
        UI.addButton("Reset (Pri Queue)", () -> {this.reset(true);});
        UI.addButton("Start", ()->{if (!running){ run(); }});   //don't start if already running!
        UI.addButton("Pause & Report", ()->{running=false;});
        UI.addButton("Pause & Report (Challenge ER)", () -> {
                this.reportStatisticsChallenge();
            });
        UI.addSlider("Speed", 1, 400, (401-delay),
            (double val)-> {delay = (int)(401-val);});
        UI.addSlider("Av arrival interval", 1, 50, arrivalInterval,
            (double val)-> {arrivalInterval = (int)val;});
        UI.addSlider("Prob of Pri 1", 1, 100, probPri1*100,
            (double val)-> {probPri1 = val/100;});
        UI.addSlider("Prob of Pri 2", 1, 100, probPri2*100,
            (double val)-> {probPri2 = Math.min(val/100,1-probPri1);});
        UI.addButton("Quit", UI::quit);
        UI.setWindowSize(1000,600);
        UI.setDivider(0.5);
    }

    /**
     * Reset the simulation:
     *  stop any running simulation,
     *  reset the waiting and treatment rooms
     *  reset the statistics.
     */
    public void reset(boolean usePriorityQueue){
        this.isUsingPriorityQueue = usePriorityQueue;
        running=false;
        UI.sleep(2*delay);  // to make sure that any running simulation has stopped
        time = 0;           // set the "tick" to zero.

        // reset the waiting room, the treatment room, and the statistics.
        initDepartments(usePriorityQueue);
        UI.clearGraphics();
        UI.clearText();
    }

    /**
     * Main loop of the simulation
     */
    public void run(){
        if (running) { return; } // don't start simulation if already running one!
        running = true;
        while (running){         // each time step, check whether the simulation should pause.

            // Hint: if you are stepping through a set, you can't remove
            //   items from the set inside the loop!
            //   If you need to remove items, you can add the items to a
            //   temporary list, and after the loop is done, remove all 
            //   the items on the temporary list from the set.

            // advance time by 1 tick:
            this.time++;

            //each loop needs to complete before the other one
            //so that the steps run chronologically

            //for each department:
            for (Department d: departments.values()) {                  
                //check + discharge patients who finished treatments
                ArrayList<Patient> toTransferArrayList = d.removeDischargedPatients();

                //transfer patients that still have other treatments to new department waiting rooms
                for (Patient tempP : toTransferArrayList) {
                    String newDepartment = tempP.getCurrentTreatment(); //already incremented by previous department
                    //already removed => just need to add to new department
                    departments.get(newDepartment).addNewPatient(tempP);
                }
            }

            //add waiting to treatment if there is space
            for (Department d: departments.values()) {      
                d.movePatientsFromWaitingToTreatment();
            }
            //advance time tick
            for (Department d: departments.values()) {  
                for (Patient p : d.getWaitingRoom()) {
                    p.waitForATick();
                }
                for (Patient p : d.getTreatmentRoom()) {                
                    p.advanceTreatmentByTick();
                }
            }
            // Gets any new patient that has arrived and adds them to the waiting room
            if (time==1 || Math.random()<1.0/arrivalInterval){
                Patient newPatient = new Patient(time, randomPriority());
                UI.println(time+ ": Arrived: "+newPatient);
                Department ER = departments.get("ER");
                ER.addNewPatient(newPatient);
            }
            redraw();
            UI.sleep(delay);
        }
        // paused, so report current statistics
        reportStatistics();
    }

    // Additional methods used by run() (You can define more of your own)

    /**
     * Report summary statistics about all the patients that have been discharged.
     * (Doesn't include information about the patients currently waiting or being treated)
     * The run method should have been recording various statistics during the simulation.
     */
    public void reportStatistics(){
        //delegated to Department
        UI.clearText();
        for (Department d : departments.values()) {
            d.reportStatistics();
        }

    }

    public void reportStatisticsChallenge() {
        this.running = false;
        UI.clearGraphics();
        UI.clearText();
        if (!this.isUsingPriorityQueue) {
            UI.println(
                "warning.... the simulation is not "
                + "using priority queues so the data"
                + " won't make much sense here...");
        }
        Department er = departments.get("ER");
        if (er != null) {
            er.reportStatsChallenge();
        }
    }
    // HELPER METHODS FOR THE SIMULATION AND VISUALISATION
    /**
     * Redraws all the departments
     */
    public void redraw(){
        UI.clearGraphics();
        UI.setFontSize(14);
        UI.drawString("Treating Patients", 5, 15);
        UI.drawString("Waiting Queues", 200, 15);
        UI.drawLine(0,32,400, 32);

        // Draw the treatment room and the waiting room:
        double y = 80;
        int count = 1;
        for (Department d : departments.values()) {
            d.redraw(y*count);
            count++;
        }
        count--;
        UI.drawLine(0,(y*count) + 2,400, (y*count) + 2);

    }

    /** 
     * Returns a random priority 1 - 3
     * Probability of a priority 1 patient should be probPri1
     * Probability of a priority 2 patient should be probPri2
     * Probability of a priority 3 patient should be (1-probPri1-probPri2)
     */
    private int randomPriority(){
        double rnd = random.nextDouble();
        if (rnd < probPri1) {return 1;}
        if (rnd < (probPri1 + probPri2) ) {return 2;}
        return 3;
    }

    private void initDepartments(boolean usePriorityQueue) {
        departments = new HashMap<>();
        departments.put("ER", new Department("ER",5,usePriorityQueue));
        departments.put("Surgery", new Department("Surgery",10,usePriorityQueue));
        departments.put("X-ray", new Department("X-ray",1,usePriorityQueue));
        departments.put("Ultrasound", new Department("Ultrasound",2,usePriorityQueue));
        departments.put("MRI", new Department("MRI",2,usePriorityQueue));
    } 
}
