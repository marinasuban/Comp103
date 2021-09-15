// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a COMP103 assignment.
// You may not distribute it in any other way without permission.

/* Code for COMP103 - 2020T2, Assignment 3
 * Name:
 * Username:
 * ID:
 */

import ecs100.*;
import java.awt.Color;
import javax.swing.JButton;

/**
 *  Simple 'Minesweeper' program.
 *  There is a grid of squares, some of which contain a mine.
 *  The user can click on a square to either expose it or to
 *  mark/unmark it.
 *  
 *  If the user exposes a square with a mine, they lose.
 *  Otherwise, it is uncovered, and shows a number which represents the
 *  number of mines in the eight squares surrounding that one.
 *  If there are no mines adjacent to it, then all the unexposed squares
 *  immediately adjacent to it are exposed (and and so on)
 *
 *  If the user marks a square, then they cannot expose the square,
 *  (unless they unmark it first)
 *  When all squares with mines are marked, and all the squares without
 *  mines are exposed, the user has won.
 */
public class MineSweeper {

    public static final int ROWS = 15;
    public static final int COLS = 15;

    public static final double LEFT = 10; 
    public static final double TOP = 10;
    public static final double SQUARE_SIZE = 20;

    // Fields
    private boolean marking;

    private Square[][] squares;

    private JButton mrkButton;
    private JButton expButton;
    Color defaultColor;

    /** 
     * Construct a new MineSweeper object
     * and set up the GUI
     */
    public static void main(String[] arguments){
        MineSweeper ms = new MineSweeper();
        ms.setupGUI();
        ms.setMarking(false);
        ms.makeGrid();
    }        

    /** Set up the GUI: buttons and mouse to play the game */
    public void setupGUI(){
        UI.setMouseListener(this::doMouse);
        UI.addButton("New Game", this::makeGrid);
        this.expButton = UI.addButton("Expose", ()->setMarking(false));
        this.mrkButton = UI.addButton("Mark", ()->setMarking(true));
        UI.addButton("AI MARK BOMB", this::AI);

        UI.addButton("Quit", UI::quit);
        UI.setDivider(0.0);
    }

    public void AI(){
       boolean AIMine=false;
        for(int i=1; i<ROWS-1; i++){
            for(int k=1; k<COLS-1; k++){
                if ((squares[i-1][k].isExposed() && squares[i-1][k].getAdjacentMines()>0) && (squares[i+1][k].isExposed() && squares[i+1][k].getAdjacentMines()>0)
                && (squares[i][k-1].isExposed() && squares[i][k-1].getAdjacentMines()>0) && (squares[i][k+1].isExposed() && squares[i][k+1].getAdjacentMines()>0)){
                    AIMine=true;
                } 
                 if ((squares[i-1][k-1].isExposed() && squares[i-1][k-1].getAdjacentMines()>0) && (squares[i+1][k+1].isExposed() && squares[i+1][k+1].getAdjacentMines()>0)
                && (squares[i+1][k-1].isExposed() && squares[i+1][k-1].getAdjacentMines()>0) && (squares[i-1][k+1].isExposed() && squares[i-1][k+1].getAdjacentMines()>0)){
                    AIMine=true;
                } 
                if(AIMine==true){
                this.risk(i,k);
                }
                AIMine=false;
            }
        }
        
    }

    /** Respond to mouse events */
    public void doMouse(String action, double x, double y) {
        if (action.equals("released")){
            int row = (int)((y-TOP)/SQUARE_SIZE);
            int col = (int)((x-LEFT)/SQUARE_SIZE);
            if (row>=0 && row < ROWS && col >= 0 && col < COLS){
                if (marking) { mark(row, col);}
                else         { tryExpose(row, col); }
            }
        }
    }

    /**
     * Remember whether it is "Mark" or "Expose"
     * Change the colour of the "Mark", "Expose" buttons
     */
    public void setMarking(boolean v){
        marking=v;
        if (marking==true) {
            mrkButton.setBackground(Color.red);
            mrkButton.setOpaque(true);
            expButton.setBackground(null);
            expButton.setOpaque(false);
        }
        else {
            expButton.setBackground(Color.red);
            expButton.setOpaque(true);
            mrkButton.setBackground(null);
            mrkButton.setOpaque(false);
        }
    }

    // Other Methods

    /** 
     * The player has clicked on a square to expose it
     * - if it is already exposed or marked, do nothing.
     * - if it's a mine: lose (call drawLose()) 
     * - otherwise expose it (call exposeSquareAt)
     * then check to see if the player has won and call drawWon() if they have.
     * (This method is not recursive)
     */
    public void tryExpose(int row, int col){
        /*# YOUR CODE HERE */
        if(squares[row][col].isExposed()||squares[row][col].isMarked()){
            return;
        }
        else if(squares[row][col].hasMine()){
            this.drawLose();
            return;
        }
        else{
            this.exposeSquareAt(row,col);
        }

        if (hasWon()){
            drawWin();
        }
    }

    /** 
     *  Expose a square, and spread to its neighbours if safe to do so.
     *  It is guaranteed that this square is safe to expose (ie, does not have a mine).
     *  If it is already exposed, we are done.
     *  Otherwise expose it, and redraw it.
     *  If the number of adjacent mines of this square is 0, then
     *     expose all its neighbours (which are safe to expose)
     *     (and if they have no adjacent mine, expose their neighbours, and ....)
     */
    public void exposeSquareAt(int row, int col){
        marking=false;
        if(row<0||row>=ROWS||col<0||col>=COLS){return;}//clicked within grid
        if(squares[row][col].isExposed()){return;}//square exposed do nothing
        if(!squares[row][col].hasMine()){    //make sure the cell has no mine
            squares[row][col].setExposed(); //make the cell be exposed
            squares[row][col].draw(LEFT+col*SQUARE_SIZE,TOP+row*SQUARE_SIZE,SQUARE_SIZE);
            if(squares[row][col].getAdjacentMines()==0){
                exposeSquareAt(row-1,col);
                exposeSquareAt(row-1,col+1);
                exposeSquareAt(row-1,col-1);
                exposeSquareAt(row+1,col); 
                exposeSquareAt(row+1,col-1);
                exposeSquareAt(row+1,col+1);
                exposeSquareAt(row,col-1);
                exposeSquareAt(row,col+1);
            }
        }
    }

    /**
     * Mark/unmark the square.
     * If the square is exposed, don't do anything,
     * If it is marked, unmark it.
     * otherwise mark it and redraw.
     * (Marking cannot make the player win or lose)
     */
    public void mark(int row, int col){
        marking = true;
        //square exposed do nothing
        if(squares[row][col].isExposed()){
            return;
        }
        //square If it is marked, unmark it
        //otherwise mark it and redraw.
        else {
            squares[row][col].toggleMark();
            squares[row][col].draw(LEFT+col*SQUARE_SIZE,TOP+row*SQUARE_SIZE,SQUARE_SIZE);
        }

    }
    public void risk(int row, int col){
        
        //square exposed do nothing
        if(squares[row][col].isExposed()){
            return;
        }
        //square If it is marked, unmark it
        //otherwise mark it and redraw.
        else {
            squares[row][col].isRisk();
            squares[row][col].draw(LEFT+col*SQUARE_SIZE,TOP+row*SQUARE_SIZE,SQUARE_SIZE);
        }

    }

    /** 
     * Returns true if the player has won:
     * If all the squares without a mine have been exposed, then the player has won.
     */
    public boolean hasWon(){
        for(int i=0; i<ROWS; i++){
            for(int k=0; k<COLS; k++){
                if(!squares[i][k].isExposed() && !squares[i][k].hasMine()){
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Construct a grid with random mines.
     */
    public void makeGrid(){
        UI.clearGraphics();
        this.squares = new Square[ROWS][COLS];
        for (int row=0; row < ROWS; row++){
            double y = TOP+row*SQUARE_SIZE;
            for (int col=0; col<COLS; col++){
                double x =LEFT+col*SQUARE_SIZE;
                boolean isMine = Math.random()<0.1;     // approx 1 in 10 squares is a mine 
                this.squares[row][col] = new Square(isMine);
                this.squares[row][col].draw(x, y, SQUARE_SIZE);
            }
        }
        // now compute the number of adjacent mines for each square
        for (int row=0; row<ROWS; row++){
            for (int col=0; col<COLS; col++){
                int count = 0;
                //look at each square in the neighbourhood.
                for (int r=Math.max(row-1,0); r<Math.min(row+2, ROWS); r++){
                    for (int c=Math.max(col-1,0); c<Math.min(col+2, COLS); c++){
                        if (squares[r][c].hasMine())
                            count++;
                    }
                }
                if (this.squares[row][col].hasMine())
                    count--;  // we weren't suppose to count this square, just the adjacent ones.

                this.squares[row][col].setAdjacentMines(count);
            }
        }
    }

    /** Draw a message telling the player they have won */
    public void drawWin(){
        UI.setFontSize(28);
        UI.drawString("You Win!", LEFT + COLS*SQUARE_SIZE + 20, TOP + ROWS*SQUARE_SIZE/2);
        UI.setFontSize(12);
    }

    /**
     * Draw a message telling the player they have lost
     * and expose all the squares and redraw them
     */
    public void drawLose(){
        for (int row=0; row<ROWS; row++){
            for (int col=0; col<COLS; col++){
                squares[row][col].setExposed();
                squares[row][col].draw(LEFT+col*SQUARE_SIZE, TOP+row*SQUARE_SIZE, SQUARE_SIZE);
            }
        }
        UI.setFontSize(28);
        UI.drawString("You Lose!", LEFT + COLS*SQUARE_SIZE+20, TOP + ROWS*SQUARE_SIZE/2);
        UI.setFontSize(12);
    }

}
