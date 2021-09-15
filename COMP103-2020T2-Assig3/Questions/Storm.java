// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a COMP103 assignment.
// You may not distribute it in any other way without permission.

/* Code for COMP103 - 2020T2, Assignment 3
 * Name:
 * Username:
 * ID:
 */

//DO NOT ADD ANY IMPORTS.

/**
 * The Storm class specifies objects that record information about
 * major hurricanes in the US. A Storm object has fields for
 *   - the name
 *   - the year
 *   - the category (1-5)
 *   - the date (month and day)
 *   - the US State 
 * It also contains a constructor and a toString method.
 * Two Storm objects that have the same name and year
 *  are the same storm, even if they have different
 *  States and dates.
 *  The name is not enough to identify the Storm because names are reused.
 * 
 * (a) Modify the Storm class so that a List of Storm objects could be sorted
 *     alphabetically by name and year (ignoring the other fields) using the statement
 *       Collections.sort(myStormList);
 *
 * (b) Modify the Storm class so that you could use Storms properly in a HashSet or HashMap.
 *  If this is done correctly, then the following code would print
 *    "Found storm1" and "Found storm2" but not "Found storm3"
 *
 *   Storm storm = new Storm("Charley", 2004, 1, "August/14", "North-Carolina");
 *   Set<Storm> mySet = new HashSet<Storm>();
 *   mySet.add(storm);
 *
 *   Storm storm1 = new Storm("Charley", 2004, 1, "August/14", "North-Carolina");
 *   if (mySet.contains(storm1)){ UI.println("Found storm1"); }
 *
 *   Storm storm2 = new Storm("Charley", 2004, 4, "August/13", "Florida");
 *   if (mySet.contains(storm2)){ UI.println("Found storm2"); }
 * 
 *   Storm storm3 = new Storm("Charley", 1986, 1, "August/17", "North-Carolina");
 *   if (mySet.contains(storm3)){ UI.println("Found storm3"); }
 * 
 * Note: this means that although storm, storm1, and storm2 are different Storm objects,
 *  and the category, date, and state of storm2 are different,
 *  they are describing the same storm because the name and year are the same , but
 *  storm3 is a different storm because it was a different year. 
 */

public class Storm implements java.io.Serializable, Comparable<Storm> { 
    //note, do NOT remove the java.io.Serializable
    
    // Fields
    private String name;
    private int year; 
    private int category;
    private String date;
    private String state;

    // Constructor
    public Storm(String n, int y, int c, String d, String s){
        name = n;
        year = y;
        category = c;
        date = d;
        state = s;
    }
    /** Return a string description of the storm suitable for printing out */
    public String toString(){
        return name + " (" + year + ") Cat " + category+ " ("+date+" in " + state + ")";
    }
    
    @Override
    public int compareTo(Storm s) {
        if (this.name.compareTo(s.name) > 0) {return 1;}
        else if (this.name.compareTo(s.name) < 0) {return -1;}
        else{
            if (this.year > s.year) return 1;
            else if (this.year < s.year) return -1;
            else return 0;
        }
    }
    
    @Override
    public boolean equals(Object o) {

        if (o == this) return true;
        if (!(o instanceof Storm)) {
            return false;
        }

        Storm s = (Storm) o;

        return s.name == this.name && s.year == this.year;
    }
    
    @Override
    public int hashCode() {
        return name.hashCode() + year;
    }
}

