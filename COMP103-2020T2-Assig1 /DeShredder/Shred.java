// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a COMP103 assignment.
// You may not distribute it in any other way without permission.

/* Code for COMP103 - 2020T2, Assignment 1
 * Name:
 * Username:
 * ID:
 */

import ecs100.*;
import java.awt.Color;
import java.util.*;
import java.io.*;
import java.nio.file.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

/**
 * Shred stores information about a shred.
 * All shreds are 40x40 png images.
 */

public class Shred{

    // Fields to store
    //   the name of the image
    //   the description of the image
    public static final double SIZE = 40;
    private String filename;
    private int id;            // ID of the shred
    private Color[][] colors;

    // Constructor
    /** Construct a new Shred object.
     *  Parameters are the name of the directory and the id of the image
     */
    public Shred(Path dir, int id){
        this.filename = dir.resolve(id+".png").toString();
        this.colors = this.loadImage(this.filename);
        this.id = id;
    }
    
    public Color[][] getColors(){
        return this.colors;
    }

    /**
     * Draw the shred (no border) at the specified coordinates
     */
    public void draw(double left, double top){
        UI.drawImage(filename, left, top, SIZE, SIZE);
    }

    /**
     * Draw the shred with a border at the specified coordinates
     */
    public void drawWithBorder(double left, double top){
        UI.drawImage(filename, left, top, SIZE, SIZE);
        UI.drawRect(left, top, SIZE, SIZE);
    }

    public String toString(){
        return "ID:"+id;
    }
    
    public String getFileName() {
        return this.filename;
    }
    
    public Color[][] loadImage(String imageFileName) {
        if (imageFileName==null || !Files.exists(Path.of(imageFileName))){
            return null;
        }
        try {
            BufferedImage img = ImageIO.read(Files.newInputStream(Path.of(imageFileName)));
            int rows = img.getHeight();
            int cols = img.getWidth();
            Color[][] ans = new Color[rows][cols];
            for (int row = 0; row < rows; row++){
                for (int col = 0; col < cols; col++){                 
                    Color c = new Color(img.getRGB(col, row));
                    ans[row][col] = c;
                }
            }
            return ans;
        } catch(IOException e){UI.println("Reading Image from "+imageFileName+" failed: "+e);}
        return null;
    }

}
