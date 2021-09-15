    
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
    import java.util.Map.Entry;
    import java.io.*;
    import java.nio.file.*;
    
    /**
     * WellingtonTrains
     * A program to answer queries about Wellington train lines and timetables for
     *  the train services on those train lines.
     *
     * See the assignment page for a description of the program and what you have to do.
     */
    
    public class WellingtonTrains{
        //Fields to store the collections of Stations and Lines
        private Map<String, Station> StationsWellingtonTrains = new TreeMap<String, Station>();
        private Map<String, TrainLine> LinesWellingtonTrains = new TreeMap<String, TrainLine>();
        // Fields for the suggested GUI.
        private String stationName;        // station to get info about, or to start journey from
        private String lineName;           // train line to get info about.
        private String destinationName;
        private int startTime = 0;         // time for enquiring about
    
        /**
         * main method:  load the data and set up the user interface
         */
        public static void main(String[] args){
            WellingtonTrains wel = new WellingtonTrains();
            wel.loadData();   // load all the data
            wel.setupGUI();   // set up the interface
        }
    
        /**
         * Load data files
         */
        public void loadData(){
            loadStationData();
            UI.println("Loaded Stations");
            loadTrainLineData();
            UI.println("Loaded Train Lines");
            // The following is only needed for the Completion and Challenge
            loadTrainServicesData();
            UI.println("Loaded Train Services");
        }
    
        /**
         * User interface has buttons for the queries and text fields to enter stations and train line
         * You will need to implement the methods here.
         */
        public void setupGUI(){
            UI.addButton("All Stations",        this::listAllStations);
            UI.addButton("Stations by name",    this::listStationsByName);
            UI.addButton("All Lines",           this::listAllTrainLines);
            UI.addTextField("Station",          (String name) -> {this.stationName=name;});
            UI.addTextField("Train Line",       (String name) -> {this.lineName=name;});
            UI.addTextField("Destination",      (String name) -> {this.destinationName=name;});
            UI.addTextField("Time (24hr)",      (String time) ->
                {
                    try 
                    {
                        this.startTime=Integer.parseInt(time);
                    }
                    catch(Exception e)
                    {
                        UI.println("Enter four digits");
                    }
                }
            );
            UI.addButton("Lines of Station",    () -> {listLinesOfStation(this.stationName);});
            UI.addButton("Stations on Line",    () -> {listStationsOnLine(this.lineName);});
            UI.addButton("Stations connected?", () -> {checkConnected(this.stationName, this.destinationName);});
            UI.addButton("Next Services",       () -> {findNextServices(this.stationName, this.startTime);});
            UI.addButton("Find Trip",           () -> {findTrip(this.stationName, this.destinationName, this.startTime);});
            UI.addButton("Find Trip Challenge",           () -> {findTripChallenge(this.stationName, this.destinationName, this.startTime);});
            UI.addButton("Quit", UI::quit);
            UI.setMouseListener(this::doMouse);
    
            UI.setWindowSize(900, 400);
            UI.setDivider(0.2);
            //testing
            UI.drawImage("data/geographic-map.png", 0, 0);
            UI.drawString("Click to list closest stations", 2, 12);
    
        }
    
    public void findTripChallenge(String stationName, String destinationName, int startTime){
        Station start=  StationsWellingtonTrains.get(stationName);
        Station end=  StationsWellingtonTrains.get(destinationName);
        var trainlines = start.getTrainLines();
        boolean foundTrip = false;
        for (Map.Entry<String, TrainLine> entry : LinesWellingtonTrains.entrySet()) {
            TrainLine tl = entry.getValue();          
                if (tl.getStations().contains(start) && tl.getStations().contains(end) && !foundTrip) {
                    this.findTrip(stationName, destinationName, startTime);
                    foundTrip = true;
                }                
                else if (!foundTrip) {
                    int startTime2ndSegment = this.findTrip(stationName, "Wellington", startTime);
                    this.findTrip("Wellington",destinationName, startTime2ndSegment);
                    foundTrip = true;
                }
                
            }
        
    }   

    public int findTrip(String stationName, String destinationName, int startTime){
        Station start=  StationsWellingtonTrains.get(stationName);
        Station end=  StationsWellingtonTrains.get(destinationName);
        var trainlines = start.getTrainLines();
        for (Map.Entry<String, TrainLine> entry : LinesWellingtonTrains.entrySet()) {
            TrainLine tl = entry.getValue();
            int starttime = -1;
            int endtime = -1;

            boolean timeFound = false;
            if (tl.getStations().contains(start) && tl.getStations().contains(end)){
                int startIndex = tl.getStations().indexOf(start);
                int endIndex =   tl.getStations().indexOf(end);
                if (startIndex < endIndex) {
                    int fareZone=((end.getZone())-(start.getZone())+1); // <fare zones
                    for (TrainService ts : tl.getTrainServices()) {

                        int timeAtStation = ts.getTimes().get(startIndex);
                        if (timeAtStation >= startTime && !timeFound) {

                            starttime = timeAtStation;
                            endtime = ts.getTimes().get(endIndex);
                            timeFound = true;
                            UI.println("Find a trip between two stations after the specified time");
                            UI.println("----------------------------------");
                            UI.println("trip found from "+stationName+ " to  " +destinationName+ ": trainline: " + tl.getName());
                            UI.println("departure time: " + starttime);
                            UI.println("arrival time: " + endtime);
                            UI.println("number of fare zone/s: " + Math.abs(fareZone));
                            return endtime;
                        }
                    }

                    //< line name e.g Wellington_Waikanae
                }
                //for(LinesWellingtonTrains.get(tl.getName()).getTrainServices());
            }
        }
        return -1;

    }

    public void findNextServices(String stationName, int startTime){
        Map<String, Integer> earliestTimesAtTrainlines = new TreeMap<>();
        Station station = StationsWellingtonTrains.get(stationName);
        var trainlines = station.getTrainLines();

        for (TrainLine tl : trainlines) {
            int index = tl.getStations().indexOf(station); 
            int time = -1;
            //find the index of the station on the train line 
            //as the index of times on trainServices match 

            boolean timeFound = false;
            for (TrainService ts : tl.getTrainServices()) {
                int timeAtStation = ts.getTimes().get(index);
                if (timeAtStation >= startTime) {
                    if (!timeFound) {
                        time = timeAtStation;
                        timeFound = true;
                    }
                }
            }

            earliestTimesAtTrainlines.put(tl.getName(),time);

        }

        UI.println("for the nearest times at "+stationName+ " station:");
        UI.println("-----------------------");
        UI.print(earliestTimesAtTrainlines);
    }

    public void doMouse(String action, double x, double y){

        if (action.equals("released")){
            Map< Double, Station> stationDistances = new TreeMap<Double, Station>();
            for(String key : StationsWellingtonTrains.keySet()){
                Station start = StationsWellingtonTrains.get(key);
                double distance = Math.hypot((x-start.getXCoord()),(y-start.getYCoord()));
                stationDistances.put(distance, start);
            }

            int count=0;

            UI.println("10 closest stations to point on the map");
            UI.println("------------------");
            for (Map.Entry< Double, Station> entry : stationDistances.entrySet()) {
                if (count<10){
                    Double key = entry.getKey();
                    Station value = entry.getValue();
                    UI.printf("Distance: %.2f km: ", key);
                    UI.println(value);
                    count++;
                }
            }
        }

    }
    // Methods for loading data and answering queries
    public void loadStationData(){
        try{
            String fname="data/stations.data";
            List<String> allLines = Files.readAllLines(Path.of(fname));
            for(String line : allLines){

                var scanline = new Scanner(line);

                String name = scanline.next();
                int zone= scanline.nextInt();
                double x= scanline.nextDouble();
                double y= scanline.nextDouble();
                this.StationsWellingtonTrains.put(name,(new Station(name,zone,x,y) )); 

            }

        }catch(IOException e){}
    }

    public void loadTrainLineData(){
        try{
            String fname="data/train-lines.data";
            List<String> allLines = Files.readAllLines(Path.of(fname));
            for(String line : allLines){

                var scanline = new Scanner(line);
                String name = scanline.next();
                this.LinesWellingtonTrains.put(name,(new TrainLine(name) ));
            }
        }catch(IOException e){}
        try {
            for (Map.Entry<String, TrainLine> entry : this.LinesWellingtonTrains.entrySet()) {
                String trainLineName = entry.getKey();
                TrainLine trainline = entry.getValue();

                String fname="data/"+trainLineName+"-stations.data";
                List<String> allLines = Files.readAllLines(Path.of(fname));

                for(String line : allLines){

                    var scanline = new Scanner(line);
                    String stationName = scanline.next();
                    Station station = this.StationsWellingtonTrains.get(stationName);
                    station.addTrainLine(trainline);
                    this.StationsWellingtonTrains.put(stationName, station);
                    trainline.addStation(station);

                }
            }
        }
        catch (IOException e) {}
    }

    public void loadTrainServicesData(){
        try {
            for (Map.Entry<String, TrainLine> entry : this.LinesWellingtonTrains.entrySet()) {
                String trainLineName = entry.getKey();
                TrainLine trainline = entry.getValue();

                String fname="data/"+trainLineName+"-services.data";
                List<String> allLines = Files.readAllLines(Path.of(fname));

                for(String line : allLines){
                    TrainService ts= new TrainService(trainline);
                    var scanline = new Scanner(line);
                    while (scanline.hasNextInt()){
                        int serviceTime = scanline.nextInt();
                        ts.addTime(serviceTime);
                    }
                    trainline.addTrainService(ts);

                }
                //UI.println(trainline.getTrainServices());
            }
        }
        catch (IOException e) {}
    }

    public void listAllStations(){
        UI.clearText();
        UI.println("All Stations in region:");
        UI.println("---------------");
        for (Map.Entry<String, Station> entry : StationsWellingtonTrains.entrySet()) {

            Station value = entry.getValue();
            UI.println(value);
        }
    }

    public void listStationsByName(){
        UI.clearText();
        UI.println("All Stations in region sorted by name:");
        UI.println("---------------");
        for (Map.Entry<String, Station> entry : StationsWellingtonTrains.entrySet()) {

            Station value = entry.getValue();
            UI.println( value);
        }
    }

    public void listAllTrainLines(){
        UI.clearText();
        UI.println("All train lines in region:");
        UI.println("---------------");
        for (Map.Entry<String, TrainLine> entry : LinesWellingtonTrains.entrySet()) {
            TrainLine value = entry.getValue();
            UI.println(value);
        }
    }

    public void listLinesOfStation(String stationName){

        UI.clearText();
        UI.println("Train lines for "+stationName+" station:");
        UI.println("---------------");
        for (TrainLine tl : StationsWellingtonTrains.get(stationName).getTrainLines()) {
            UI.println(tl);
        }

    }

    public void listStationsOnLine(String lineName){
        UI.clearText();
        UI.println("Stations for "+lineName+" line:");
        UI.println("---------------");
        for (Station st : LinesWellingtonTrains.get(lineName).getStations()) {
            UI.println(st);
        }
    }

    public void checkConnected(String stationName, String destinationName){
        Station start=  StationsWellingtonTrains.get(stationName);
        Station end=  StationsWellingtonTrains.get(destinationName);
        UI.clearText();
        UI.println("Train lines for "+stationName+" station:");
        UI.println("---------------");
        for (Map.Entry<String, TrainLine> entry : LinesWellingtonTrains.entrySet()) {
            TrainLine tl= entry.getValue();
            if (tl.getStations().contains(start) && tl.getStations().contains(end)){
                if (tl.getStations().indexOf(start)<tl.getStations().indexOf(end)){
                    int fareZone=((end.getZone())-(start.getZone())+1);
                    UI.println("The " + tl.getName() +" goes from "+stationName+" to "+destinationName);
                    UI.println("The trip goes through " + fareZone+ " fare zones.");
                }

            }
        }

    }

}

