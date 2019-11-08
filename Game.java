import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.io.File;

/**
 * This class is the main class of the game
 * @author psoderlu
 * @version 42
 */

public class Game {

    //***********************
    // Variables
    //***********************
    private Map map;
    private View view;
    private Player player;
    private ArrayList<NPC> npcs;
    private ArrayList<String> verbs;
    private ArrayList<String> nouns;
    private boolean finished = false;

    //***********************
    // Constructor
    //***********************
    /**
     * Create the game and initialise its internal map.
     */
    public Game(int w, int h, String playerName, int noNPCs) {
        getCommands();
        map = new Map(w, h);
        player = new Player(playerName, map.getStartX(), map.getStartY(), map.getStartingPlace());
        view = new View(map, player);
        npcs = new ArrayList<>();
        generateNPCs(noNPCs);
        chartAdjacentPlaces(player.getxCoordinate(), player.getyCoordinate());
    }

    //***********************
    // Main Methods
    //***********************
    /**
     *  Main play routine.  Loops until end of play.
     */
    public void play() {
        printWelcome();

        while (! finished) {
            view.printMap();
            System.out.println("Your stats: " + player.getStats());
            System.out.println("Your Location: " + player.getPlace());
            System.out.println("What do you want to do?");
            processInput();
            System.out.println();
            isFinished();
        }
        System.out.println("The End.");
    }

    //***********************
    // Generation Methods
    //***********************
    /**
     * Generates npc in random locations on the map.
     * @param n the number of npcs to generate
     */
    public void generateNPCs(int n) {
        Random r = new Random();
        for (int i = 0; i < n; i++) {
            int x = r.nextInt(map.getWidth());
            int y = r.nextInt(map.getHeight());
            Place l = map.getLocation(x, y);
            NPC npc = new NPC("npc" + i, x, y, l);
            npcs.add(npc);
            l.addNPCs(npc);
        }
    }

    //***********************
    // Input Methods
    //***********************
    /**
     * Processes the users input into commands
     */
    private void processInput(){
        Scanner sc = new Scanner(System.in);
        String input = sc.nextLine();

        switch(input) {
            case "help":
            printHelp();
            return;

            case "quit":
            finished = true;
            return;
        }

        if(!input.contains(" ")) {
            System.out.println("\"" + input + "\" is not a valid input");
            return;
        }
        String verb = input.substring(0, input.indexOf(" "));
        String noun = input.substring(input.indexOf(" ") + 1, input.length());

        switch(verb) {
            case "move":
            case "go":
            case "m":
            moveInput(noun);
            break;

            case "talk":
            case "talkTo":
            talkInput(noun);
            break;

            case "attack":
            attackInput(noun);
            break;

            default:
            System.out.println("\"" + verb + "\" is an unkown verb");
        }
    }

    /**
     * Moves the player in a direction
     * @param noun the direction to move in.
     */
    private void moveInput(String noun) {
        switch(noun) {
            case "w":
            case "a":
            case "s":
            case "d":
            case "north":
            case "south":
            case "east":
            case "west":
                if(canMove(noun, player.getxCoordinate(), player.getyCoordinate())) {
                    player.move(noun);
                    player.setPlace(map.getLocation(player.getxCoordinate(), player.getyCoordinate()));
                    player.getPlace().setCharted(true);
                    chartAdjacentPlaces(player.getxCoordinate(), player.getyCoordinate());
                    if(player.getPlace().isDangerous()) {
                        player.changeHitpoints(-3);
                        System.out.println("This place is dangerous, you took 3 damage");
                    } else if(player.getPlace().isHelpful()) {
                        player.changeHitpoints(1);
                        System.out.println("A strange force rests here, you gain 1 hitpoint");
                    }

                } else {
                    System.out.println("An impassable mountain range stands in your way");
                }
            break;
            default:
                System.out.println("Unkown noun");
        }
    }

    /**
     * Talks to a npc
     * @param noun the name of the npc
     */
    private void talkInput(String noun) {
        npcs.stream().filter(npc -> npc.getName().equals(noun)).
        forEach(npc -> {
            if(npc.getPlace() == player.getPlace()){
                System.out.println("You approach the person which turns around and says: ");
                System.out.println("\"" + npc.getDialogue() + "\"");
            } else {
                System.out.println("You talk to yourself feeling a bit silly");
            }
        });
    }

    /**
     * Allows the player to attack an npc
     * @param noun the npc to attack
     */
    private void attackInput(String noun) {
        npcs.stream().filter(npc -> npc.getName().equals(noun)).
        forEach(npc -> {
            if(npc.getPlace() == player.getPlace()){
                System.out.println("You try to attack the person standing by the river.");
                System.out.println("Before you can draw your sword the person spots you and runs away");
                int x = npc.getxCoordinate();
                int y = npc.getyCoordinate();

                npc.getPlace().removeNPC(npc);
                if(canMove("south", x, y)) {
                    npc.move("south");
                    Place p = map.getLocation(x, y + 1);
                    npc.setPlace(p);
                    p.addNPCs(npc);
                } else {
                    npc.move("north");
                    Place p = map.getLocation(x, y - 1);
                    npc.setPlace(p);
                    p.addNPCs(npc);
                }
            } else {
                System.out.println("You draw your sword and lash out at the imagined threats");
            }
        });
    }

    //***********************
    // Other
    //***********************
    /**
         * Reads the possibe commands from two text files
         */
    private void getCommands(){
        verbs = new ArrayList<>();
        nouns = new ArrayList<>();
        Scanner s1 = null;
        Scanner s2 = null;
        try {
            s1 = new Scanner(new File("nouns.txt"));
            s2 = new Scanner(new File("verbs.txt"));
        } catch (Exception e) {
            System.out.println(e);
        }
        while(s1.hasNextLine()){
            nouns.add(s1.nextLine());
        }
        while(s2.hasNextLine()){
            verbs.add(s2.nextLine());
        }
    }

    private void isFinished() {
        if(player.getHitpoints() <= 0) {
            finished = true;
        } else if(map.isCharted()) {
            finished = true;
        } 
    }

    /**
     * Print out the opening message for the player.
     */
    private void printWelcome() {
        System.out.println();
        System.out.println("Welcome to the World of Zuul!");
        System.out.println("World of Zuul is a new, incredibly boring adventure game.");
        System.out.println("Type 'help' if you need help.");
        System.out.println();
        System.out.println();
    }

    /**
     * Print out some help information.
     * Here we print some stupid, cryptic message and a list of the 
     * command words.
     */
    private void printHelp() {
        System.out.println("Availeble commands:");
        System.out.println("Verbs:");
        for (String string : verbs) {
            System.out.println(string);
        }
        System.out.println("\nNouns:");
        for (String string : nouns) {
            System.out.println(string);
        }
    }

    /**
     * Reveals the adjacent tiles to the player
     * @param x the x-coordinate to reveal around
     * @param y the y-coordinate to reveal around
     */
    private void chartAdjacentPlaces(int x, int y) {
        if(x < map.getWidth() - 1) {
            map.getLocation(player.getxCoordinate() + 1 , player.getyCoordinate()).setCharted(true);
        }

        if(x > 0) {
            map.getLocation(player.getxCoordinate() - 1, player.getyCoordinate()).setCharted(true);
        }

        if(y < map.getHeight() - 1) {
            map.getLocation(player.getxCoordinate(), player.getyCoordinate() + 1).setCharted(true);
        }

        if(y > 0) {
            map.getLocation(player.getxCoordinate(), player.getyCoordinate() - 1).setCharted(true);
        }
    }

    /**
     * Checks if it's possivle to move in a certain direction
     * @param direction the direction to move in
     * @param x the x coordinate to move from
     * @param y the y coordinate to move from
     * @return whether it's possible or not to move.
     */
    private boolean canMove(String direction, int x, int y) {
        boolean b = true;
        switch(direction){
            case "north":
            case "w":
            if(y  <= 0) {
                b = false;
            }
            break;
            case "south":
            case "s":
            if(y + 1 >= map.getHeight()) {
                b = false;
            }
            break;
            case "east":
            case "d":
            if(x + 1 >= map.getWidth()) {
                b = false;
            }
            break;
            case "west":
            case "a":
            if(x <= 0) {
                b = false;
            }
            break;
        }
        return b;
    }
}