// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a COMP103 assignment.
// You may not distribute it in any other way without permission.

/* Code for COMP103 - 2020T2, Assignment 2
 * Name:
 * Username:
 * ID:
 */

import ecs100.*;
import java.util.*;
import java.awt.Color;
import javax.swing.JColorChooser;

/** Pencil   */
public class Pencil{
        double lastX;
        double lastY;
    private List<Double> xCoordinates = new ArrayList<Double>();
    private List<Double> yCoordinates = new ArrayList<Double>();
    private List<Color> lineColor = new ArrayList <Color>();
    private List<Double> lineSize = new ArrayList <Double>();
    List<List<Double>> xMoves = new ArrayList<List<Double>>();
    List<List<Double>> yMoves = new ArrayList<List<Double>>();
    private Color currentColor=Color.black;
    private double size=2;
    
    // tracks stuff that has been undo'ed
    List<List<Double>> xMovesUndoed = new ArrayList<List<Double>>();
    List<List<Double>> yMovesUndoed = new ArrayList<List<Double>>();
    List<Color> lineColorUndoed = new ArrayList <Color>();
    List<Double> lineSizeUndoed = new ArrayList <Double>();
    /**
     * Setup the GUI
     */
    public void setupGUI(){
        UI.setMouseMotionListener(this::doMouse);
        UI.addSlider( "Line Width", 1, 20, 1, this::setSize);
        UI.addButton( "line Color", this::setLineColor);
        UI.addButton("Undo", this::undo);
        UI.addButton("Redo", this::redo);
        UI.addButton("Quit", UI::quit);
        UI.setLineWidth(3);
        UI.setDivider(0.0);
    }
    public void setSize(double size){
    this.size=size;
    }
    public void setLineColor(){
        this.currentColor = JColorChooser.showDialog(null,"Choose colour", this.currentColor); 
    }
    public void undo(){
        UI.clearGraphics();
        if (xMoves.size() > 0) {
            List<Double> xTemp = xMoves.remove(xMoves.size()-1);
            List<Double> yTemp = yMoves.remove(yMoves.size()-1);
            Color cTemp= lineColor.remove(lineColor.size()-1);
            Double sTemp = lineSize.remove(lineSize.size()-1);
            xMovesUndoed.add(xTemp);
            yMovesUndoed.add(yTemp);
            lineColorUndoed.add(cTemp);
            lineSizeUndoed.add(sTemp);
        }
        
        for (int i=0; i<xMoves.size(); i++){
            List<Double> xCoords = xMoves.get(i);
            List<Double> yCoords = yMoves.get(i);
            UI.setColor(this.lineColor.get(i));
            UI.setLineWidth(this.lineSize.get(i));
            for (int k=0;k<xCoords.size()-1;k++) {
                UI.drawLine(xCoords.get(k), yCoords.get(k), xCoords.get(k+1), yCoords.get(k+1));
            }
        }

    }
    
    public void redo(){
        UI.clearGraphics();
        if (xMovesUndoed.size() > 0) {
            List<Double> xTemp = xMovesUndoed.remove(xMovesUndoed.size()-1);
            List<Double> yTemp = yMovesUndoed.remove(yMovesUndoed.size()-1);
            Color cTemp= lineColorUndoed.remove(lineColorUndoed.size()-1);
            Double sTemp = lineSizeUndoed.remove(lineSizeUndoed.size()-1);
            xMoves.add(xTemp);
            yMoves.add(yTemp);
            lineColor.add(cTemp);
            lineSize.add(sTemp);
        } 
        
        for (int i=0; i<xMoves.size(); i++){
            List<Double> xCoords = xMoves.get(i);
            List<Double> yCoords = yMoves.get(i);
            UI.setColor(this.lineColor.get(i));
            UI.setLineWidth(this.lineSize.get(i));
            for (int k=0;k<xCoords.size()-1;k++) {
                UI.drawLine(xCoords.get(k), yCoords.get(k), xCoords.get(k+1), yCoords.get(k+1));
            }
        }
    }

    /**
     * Respond to mouse events
     */
    public void doMouse(String action, double x, double y) {
        if (action.equals("pressed")){
            UI.setColor(this.currentColor);
            UI.setLineWidth(this.size);
            xCoordinates = new ArrayList<>(); //resets the list at the start of new mouse press
            yCoordinates = new ArrayList<>();
            lastX = x;
            lastY = y;
        }
        else if (action.equals("dragged")){
            
            UI.drawLine(lastX, lastY, x, y);
            xCoordinates.add(lastX);
            yCoordinates.add(lastY);
            lastX = x;
            lastY = y;
        }
        else if (action.equals("released")){
            UI.drawLine(lastX, lastY, x, y);
            xCoordinates.add(x);
            yCoordinates.add(y);
            lineColor.add(currentColor);
            lineSize.add(size);
            xMoves.add(xCoordinates);
            yMoves.add(yCoordinates);
            
            lineColorUndoed= new ArrayList<>();
            lineSizeUndoed= new ArrayList<>();
            xMovesUndoed = new ArrayList<>();
            yMovesUndoed = new ArrayList<>();
        }
    }

    public static void main(String[] arguments){
        new Pencil().setupGUI();
    }

}
