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
import java.awt.Color;
import java.awt.color.ColorSpace;
import java.net.NetworkInterface;
import java.text.DecimalFormat;
//import java.io.*;

/**
 * A treatment Department (Surgery, X-ray room,  ER, Ultrasound, etc)
 * Each department will need
 * - A name,
 * - A maximum number of patients that can be treated at the same time
 * - A Set of Patients that are currently being treated
 * - A Queue of Patients waiting to be treated.
 *    (ordinary queue, or priority queue, depending on argument to constructor)
 */

public class Department{

    private String name;
    private int maxPatients;

    /*# YOUR CODE HERE */
    private int totalPatients =0;
    private int waitTimes=0;
    private int totalPatients_P1=0;
    private int waitTimes_P1=0;
    
     private int[] ER_totalPatientsByPriority = { 0, 0, 0 }; // P1, P2, P3
    private int[] ER_patientsWaitTimeByPriority = { 0, 0, 0 }; // P1, P2, P3
    private int[] ER_patientsTreatmentTimeByPriority = { 0, 0, 0 }; // P1, P2, P3

    private Set<Patient> treatmentRoom = new HashSet<>();
    private Queue<Patient> waitingRoom = new PriorityQueue<>();

    public Department(String name, int capacity, boolean isUsingPriorityQueue){
        this.maxPatients=capacity;
        this.name=name;
        if(isUsingPriorityQueue){
            this.waitingRoom = new PriorityQueue<>();
        }
        else{
            this.waitingRoom = new ArrayDeque<>();
        }
    }
    //completion
    public void addNewPatient(Patient p){
        waitingRoom.offer(p);
    }

    public Set<Patient> getTreatmentRoom(){
        return this.treatmentRoom;
    }

    public Queue<Patient> getWaitingRoom() {
        return this.waitingRoom;
    }

    public void movePatientsFromWaitingToTreatment() {
        /* if possible */
        if (treatmentRoom.size() < maxPatients) {
            Patient transferPatient = waitingRoom.poll();
            if (transferPatient != null) {
                treatmentRoom.add(transferPatient);
            }
        }
    }

    public ArrayList<Patient> removeDischargedPatients() {
        ArrayList<Patient> tempForFullDischarge = new ArrayList<>();
        ArrayList<Patient> tempForNextTreatment = new ArrayList<>();
        for (Patient p : treatmentRoom) {
            // no more treatments => fully remove
            if (p.completedCurrentTreatment() && p.noMoreTreatmentsCompletion()) {
                tempForFullDischarge.add(p);
            }
            // more treatments => add to list for transfer
            else if (p.completedCurrentTreatment() && !p.noMoreTreatmentsCompletion()) {
                p.incrementTreatmentNumber();
                tempForNextTreatment.add(p);
            }
        }
        // discharge treated patients
        this.removeFromTreatmentRoom(tempForFullDischarge, true);
        this.removeFromTreatmentRoom(tempForNextTreatment, false);
        return tempForNextTreatment;
    }

    private void removeFromTreatmentRoom(List<Patient> patientsList, boolean isFullDischarge) {
        for (Patient p : patientsList) {
            treatmentRoom.remove(p);
            // TODO: conditions for moving to another department or just yeet

            // if fully discharged => append stats
            if (isFullDischarge) {
                this.totalPatients++;
                this.waitTimes += p.getWaitingTime();

                if (p.getPriority() == 1) {
                    this.totalPatients_P1++;
                    this.waitTimes_P1 += p.getWaitingTime();
                }
            }
            if (this.name.equals("ER")) {
                // CHALLENGE stats for ER
                switch (p.getPriority()) {
                    case 1:
                    this.ER_totalPatientsByPriority[0]++;
                    this.ER_patientsWaitTimeByPriority[0] += p.getTotalERwaitingTime();
                    this.ER_patientsTreatmentTimeByPriority[0] += p.getTotalERTreatmentTime();
                    break;
                    case 2:
                    this.ER_totalPatientsByPriority[1]++;
                    this.ER_patientsWaitTimeByPriority[1] += p.getTotalERwaitingTime();
                    this.ER_patientsTreatmentTimeByPriority[1] += p.getTotalERTreatmentTime();
                    break;
                    case 3:
                    this.ER_totalPatientsByPriority[2]++;
                    this.ER_patientsWaitTimeByPriority[2] += p.getTotalERwaitingTime();
                    this.ER_patientsTreatmentTimeByPriority[2] += p.getTotalERTreatmentTime();
                    break;
                    default:
                    break;
                }
            }

        }
    }

    /**
     * Draw the department: the patients being treated and the patients waiting
     * You may need to change the names if your fields had different names
     */
    public void redraw(double y){
        UI.setFontSize(14);
        UI.drawString(name, 0, y-35);
        double x = 10;
        UI.drawRect(x-5, y-30, maxPatients*10, 30);  // box to show max number of patients
        for(Patient p : treatmentRoom){
            p.redraw(x, y);
            x += 10;
        }
        x = 200;
        for(Patient p : waitingRoom){
            p.redraw(x, y);
            x += 10;
        }
    }

    public void reportStatistics() {
        UI.println("\n");
        UI.println("---------------------------------------------------------");
        UI.println("Department: " + this.name);
        UI.println("---------------------------------------------------------");
        if (totalPatients > 0 && waitTimes > 0) {
            UI.println("-------------------");
            UI.println("Total patients treated: " + this.totalPatients);
            UI.println("AVG wait times: " + this.waitTimes / this.totalPatients);
        }
         else {
            UI.println("not enough data");
        }
        if (totalPatients_P1 > 0 && waitTimes_P1 > 0) {
            UI.println("----PRIORITY 1-----");
            UI.println("Total Priority1 patients treated: " + this.totalPatients_P1);
            UI.println("AVG   Priority1 wait times: " + this.waitTimes_P1 / this.totalPatients_P1);
        } else {
            UI.println("not enough data");
        }
    }
    public void reportGraphsChallenge(double p1Value, double p2Value, double p3Value) {
        int fontSize = 14;
        UI.setFontSize(fontSize);

        if (!this.name.equals("ER"))
            return;
        double max = Collections.max(Arrays.asList(p1Value, p2Value, p3Value));
        double average = (p1Value + p2Value + p3Value) / 3;

        double ceiling = 0; //tallest ceiling for the graph
        //set the ceiling of the graph based on biggest value
        if (max <= 50) ceiling = 50;
        else if (max <= 100) ceiling = 100;
        else if (max <= 500) ceiling = 500;
        else if (max <= 2000) ceiling = 2000;
        else ceiling = max; // any bigger and it becomes the ceiling

        UI.drawString("Average wait times of patients in ER by priority", 100, 15);
        // width = 500,
        // length = 300
        UI.drawRect(50, 30, 500, 300);

        UI.drawString(Double.toString(ceiling), 10, 30 + fontSize/2);
        UI.drawString(Double.toString(0), 10, 330 + fontSize/2);

        UI.setLineWidth(3);
        
        // average line
        UI.setColor(Color.blue);
        double averageYCoord = 30 + 300*(1-(average/ceiling));
        UI.drawString(new DecimalFormat("#.0").format(average), 10, averageYCoord);
        UI.drawLine(40, averageYCoord , 560, averageYCoord );
        
        // priority1 line
        UI.setColor(Color.red);
        double p1Ycoord = 30 + 300*(1-(p1Value/ceiling));
        double p1YLength = 300*(p1Value/ceiling);
        UI.drawString(new DecimalFormat("#.0").format(p1Value), 10, p1Ycoord);
        UI.drawString("Priority 1", 80, 350);
        
        UI.fillRect(80, 330-p1YLength , 20, p1YLength );
        
        // priority2 line
        UI.setColor(Color.orange);
        double p2Ycoord = 30 + 300*(1-(p2Value/ceiling));
        double p2YLength = 300*(p2Value/ceiling);
        UI.drawString(new DecimalFormat("#.0").format(p2Value), 10, p2Ycoord);
        UI.drawString("Priority 2", 180, 350);
        
        UI.fillRect(180, 330-p2YLength , 20, p2YLength );
        
        // priority3 line
        UI.setColor(Color.green);
        double p3Ycoord = 30 + 300*(1-(p3Value/ceiling));
        double p3YLength = 300*(p3Value/ceiling);
        UI.drawString(new DecimalFormat("#.0").format(p3Value), 10, p3Ycoord);
        UI.drawString("Priority 3", 280, 350);
        
        UI.fillRect(280, 330-p3YLength , 20, p3YLength );

        //revert to old settings
        UI.setLineWidth(1);
        UI.setColor(Color.black);
    }

    public void reportStatsChallenge() {
        if (this.name.equals("ER")) {
            // should be on the graphics pane
            UI.println("\n");
            UI.println("----------------------CHALLENGE ER-----------------------");

            UI.println("for P1: total patients treated: " + this.ER_totalPatientsByPriority[0]);
            double averageWaitP1 = 0;
            double averageTreatP1 = 0;
            if (this.ER_totalPatientsByPriority[0] >= 1) {
                averageWaitP1 = this.ER_patientsWaitTimeByPriority[0] / this.ER_totalPatientsByPriority[0];
                averageTreatP1 = this.ER_patientsTreatmentTimeByPriority[0] / this.ER_totalPatientsByPriority[0];
            }
            UI.println("average ER waiting times: " + averageWaitP1);
            UI.println("average ER treatment times: " + averageTreatP1);
            UI.println("\n");

            UI.println("for P2: total patients treated: " + this.ER_totalPatientsByPriority[1]);
            double averageWaitP2 = 0;
            double averageTreatP2 = 0;
            if (this.ER_totalPatientsByPriority[1] >= 1) {
                averageWaitP2 = this.ER_patientsWaitTimeByPriority[1] / this.ER_totalPatientsByPriority[1];
                averageTreatP2 = this.ER_patientsTreatmentTimeByPriority[1] / this.ER_totalPatientsByPriority[1];
            }
            UI.println("average ER waiting times: " + averageWaitP2);
            UI.println("average ER treatment times: " + averageTreatP2);
            UI.println("\n");

            UI.println("for P3: total patients treated: " + this.ER_totalPatientsByPriority[2]);
            double averageWaitP3 = 0;
            double averageTreatP3 = 0;
            if (this.ER_totalPatientsByPriority[2] >= 1) {
                averageWaitP3 = this.ER_patientsWaitTimeByPriority[2] / this.ER_totalPatientsByPriority[2];
                averageTreatP3 = this.ER_patientsTreatmentTimeByPriority[2] / this.ER_totalPatientsByPriority[2];
            }
            UI.println("average ER waiting times: " + averageWaitP3);
            UI.println("average ER treatment times: " + averageTreatP3);
            UI.println("\n");

            reportGraphsChallenge(averageWaitP1,averageWaitP2, averageWaitP3);

        }

    }
}
