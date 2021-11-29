package app;

import building.Building;
import building.BuildingFactory;

import java.util.Arrays;

/**
 * @author Tobias Haider
 * Main entry point for demonstration of the application
 */
public class Main {
    public static final String DC_TOWER_BUILDING_NAME = "DC Tower";
    public static final int DC_TOWER_NUMBER_OF_FLOORS = 55;
    public static final int DC_TOWER_NUMBER_OF_ELEVATORS = 7;
    public static final int DC_TOWER_ELEVATOR_CAPACITY = 6;
    public static final int DC_TOWER_ELEVATOR_SPEED = 10;

    /**
     * Create the DC Tower using the BuildingFactory class.
     * Make some requests to test the program.
     * @param args Input arguments (are ignored in this program)
     */
    public static void main(String[] args) throws InterruptedException {
        // Set this to true to see all the steps from the elevators
        boolean debug = false;
        // Define how many request should be made
        final int numberOfRequests = 30;

        // Create DC Tower
        final Building dcTower = createDCTower(debug);

        String[] directions = {"up", "down", "random"};
        for (String direction : directions) {
            makeRandomRequests(dcTower, numberOfRequests / directions.length, direction);
        }

        // Monitor and shut down the elevators
        monitorElevators(dcTower);
    }

    /**
     * Instantiate a building object using default values for the DC tower
     * @param debug The elevators can provide extra outputs to verify the interactions
     * @return A set up building object fully configured
     */
    private static Building createDCTower(boolean debug) {
        // Define values of DC Tower in factory
        BuildingFactory factory = new BuildingFactory();
        factory.setBuildingName(DC_TOWER_BUILDING_NAME);
        factory.setNumberOfFloors(DC_TOWER_NUMBER_OF_FLOORS);
        factory.setNumberOfElevators(DC_TOWER_NUMBER_OF_ELEVATORS);
        factory.setElevatorCapacity(DC_TOWER_ELEVATOR_CAPACITY);
        factory.setElevatorSpeed(DC_TOWER_ELEVATOR_SPEED);
        factory.setDebug(debug);

        // Build and return Building object
        return factory.create();
    }

    /**
     * Make some requests to demonstrate the functionality of the elevators
     * @param building A building object for which the requests are created
     * @param numberOfRequests The number of request to make
     * @param direction The direction of the passengers (up or down)
     */
    private static void makeRandomRequests(Building building, int numberOfRequests, String direction) {
        // Values needed to make a request
        String passengerName;
        int from, to;

        // Create number of requests
        for (int request = 0; request < numberOfRequests; request++) {
            // Define a name of the request
            passengerName = "<Passenger going " + direction + " " + request + ">";
            // Set some default values for the from and to floor
            from = 0;
            to = 0;
            switch (direction) {
                case "up":
                    // Request from ground floor to random floor
                    to = (int) (Math.random() * building.getNumberOfFloors());
                    break;
                case "down":
                    // Request from random floor to ground floor
                    from = (int) (Math.random() * building.getNumberOfFloors());
                    break;
                default:
                    // Completely random request
                    to = (int) (Math.random() * building.getNumberOfFloors());
                    from = (int) (Math.random() * building.getNumberOfFloors());
            }
            // Add request to the building
            building.addRequest(passengerName, from, to);
        }
    }

    /**
     * Give Monitor the elevators until they are finished and then shut down the program
     * @param building The building to be monitored
     */
    private static void monitorElevators(Building building) throws InterruptedException{
        int occupiedElevators;
        do {
            Thread.sleep(2000);
            occupiedElevators = building.getNumberOfOccupiedElevators();
            System.out.println("Currently " + occupiedElevators + " elevator(s) are still processing requests");
        } while (!(occupiedElevators == 0 && building.getNumberOfWaitingRequests() == 0));

        // Terminate the program gracefully
        System.out.println("Shutting down the elevators.");
        building.stopOperation();
    }
}
