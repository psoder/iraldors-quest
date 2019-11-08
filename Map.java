import java.util.ArrayList;
import java.util.Random;

/**
 * This class is the map in the game
 * @author psoderlu
 * @version 42
 */

public class Map{

    //***********************
    // Variables
    //***********************
    private ArrayList<ArrayList<Place>> map;
    private int width;
    private int height;
    private double dangerChance;
    private double helpChance;
    private double neutralChance;

    private Place startingPlace;
    private int startX;
    private int startY;
    private boolean charted;
    //***********************
    // Constructor
    //***********************
    /***
     * Creates a new map
     * @param w width of the map
     * @param h height of the map
     */
    public Map(int w, int h){
        this.width = w;
        this.height = h;
        this.dangerChance = 0.25;
        this.helpChance = 0.25;
        this.neutralChance = 0.25;
        this.charted = false;

        map = new ArrayList<>();

        if(w < 1 || h < 1){
            throw new IllegalArgumentException("width and height must be 1 <=");
        }

        generateMap(width, height);
    }

    //***********************
    // Main Methods
    //***********************
    /***
     * Generates the map
     * @param w width of the map
     * @param h height of the map
     */
    private void generateMap(int w, int h){
        for (int i = 0; i < height; i++) {
            ArrayList<Place> a = new ArrayList<>();
            for (int j = 0; j < width; j++) {
                a.add(generateLocation());
            }
            map.add(a);
        }

        startingPlace = new Place("start", "start", false, true, false, true);
        startX = width/2;
        startY = height/2;
        map.get(startY).set(startX, startingPlace);
    }

    /**
     * Generates a place 
     * @return the place
     */
    private Place generateLocation(){
        String[] biomes = {"Desert", "Mountains", "Medow", "Caverns", "Forest", "Swamp", "Wasteland", "Taiga", "Valley"};
        String[] gAttr = {"Lush", "Fungal", "Sacred"};
        String[] nAttr = {"Silent", "Empty", "Overgrown"};
        String[] bAttr = {"Blazing", "Poisonus", "Cursed", "Volcanic", "Treacherous", "Shadow"};
        Random r = new Random();

        String biome = biomes[r.nextInt(biomes.length)];
        String attr = "";
        
        if(Math.random() < helpChance){
            attr = gAttr[r.nextInt(gAttr.length)];
            return new Place(biome, attr, false, true);
        } else if(Math.random() < dangerChance){
            attr = bAttr[r.nextInt(bAttr.length)];
            return new Place(biome, attr, true);
        } else if(Math.random() < neutralChance){
            attr = nAttr[r.nextInt(nAttr.length)];
            return new Place(biome, attr, false, false, true);
        } else {
            return new Place(biome);
        }
    }

    //***********************
    // Other methods
    //***********************
    @Override
    public String toString(){
        String s = "";
        for (int i = 0; i <  height; i++) {
            for (int j = 0; j < width; j++) {
                s += getLocation(j, i).getShortName() + "\t";
            }
            s += "\n";
        }
        return "\n" + s  + "\n";
    }

    /**
     * @return the charted
     */
    public boolean isCharted() {
        for (ArrayList<Place> arrayList : map) {
            for (Place place : arrayList) {
                if(!place.isCharted()) {
                    return false;
                }
            }
        }
        return true;
    }
    //***********************
    // Getters & Setters
    //***********************
    /**
     * Returns the location at the specified coordinates
     * @param x the x coordinate
     * @param y the y coordinate
     * @return the location at the coordinates
     */
    public Place getLocation(int x, int y){
        if(x < 0 || x >= width || y < 0 || y >= height){
            throw new IllegalArgumentException("x or y value is illeagal");
        } else {
            return map.get(y).get(x);
        }
    }

    /**
     * @return the height
     */
    public int getHeight() {
        return height;
    }

    /**
     * @return the width
     */
    public int getWidth() {
        return width;
    }

    /**
     * @return the startX
     */
    public int getStartX() {
        return startX;
    }

    /**
     * @return the startY
     */
    public int getStartY() {
        return startY;
    }

    /**
     * @return the startingPlace
     */
    public Place getStartingPlace() {
        return startingPlace;
    }
}