package building;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 * @author Tobias Haider
 * This calls represents a building with simplified to floors and elevators.
 * Floors and elevators only make sense, when composed together inside a building
 */
public class Building {

    // The name of the building
    private final String buildingName;
    // This array holds all floors of this building
    private final Floor[] floors;
    // The elevators of the building
    private final List<Elevator> elevators;

    /**
     * A Building can be constructed using its name and the number of floors.
     * Note, that it has no elevators by default. They have to be added using the addElevator function.
     * @param buildingName The name of the building
     * @param numberOfFloors The number of floors of the building
     */
    public Building(String buildingName, int numberOfFloors) {
        this.buildingName = buildingName;
        // Create floor objects for each floor and store them in the floors array
         Floor[] floors = new Floor[numberOfFloors];
        for (int floorNumber = 0; floorNumber < numberOfFloors; floorNumber++) {
            floors[floorNumber] = new Floor(floorNumber);
        }
        this.floors = floors;
        // Create empty list of elevators
        this.elevators = new LinkedList<>();
    }

    /**
     * Add an elevator to the building. An elevator is mainly defined by its capacity and the speed
     * @param capacity Number of passengers able to take the elevator simultaneously
     * @param speed The speed of the elevator in floors per second
     */
    public void addElevator(int capacity, int speed, boolean debug) {
        // Create new elevator object
        try {
            Elevator elevator = new Elevator(
                    this,
                    elevators.size() + 1,
                    capacity,
                    speed,
                    debug
            );
            // Add the elevator to the list of elevators in the building
            elevators.add(elevator);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Get a floor based on the floor number
     * @param floorNumber The floor number for which the floor object should be returned
     * @return The floor object corresponding to the floor number
     * @throws Exception The floor does not exist
     */
    public Floor getFloor(int floorNumber) throws Exception {
        // Can only return floors with the numbers [0, floors.length)
        if (floorExists(floorNumber)) {
            return floors[floorNumber];
        } else {
            throw new Exception("A floor with the number " + floorNumber + " does not exist.");
        }
    }

    /**
     * Find any floor which has waiting passengers
     * @return A floor where passengers are waiting
     */
    Optional<Floor> findFloorWithWaitingPassengers() {
        return Arrays.stream(floors).filter(Floor::hasWaitingPassengers).findAny();
    }

    /**
     * Create a request for a transportation of a passenger from one floor to another.
     * @param passengerName The name of the newly created passenger
     * @param fromFloor The position from which the passenger should be transported
     * @param toFloor The destination to which the passenger should be transported
     * @return The request could be created successfully and will be processed
     */
    public boolean addRequest(String passengerName, int fromFloor, int toFloor) {
        if (floorExists(fromFloor) && floorExists(toFloor)) {
            Passenger passenger = new Passenger(passengerName, floors[toFloor]);
            return floors[fromFloor].addPassenger(passenger);
        }
        return false;
    }

    /**
     * Check, whether a floor with a certain number exists in this building
     * @param floorNumber The floor number which should be found in the building
     * @return The building has a floor with this number
     */
    private boolean floorExists(int floorNumber) {
        // Only floors with the numbers [0, floors.length) exist
        return floorNumber >= 0 && floorNumber < floors.length;
    }

    /**
     * Start the operation of all elevators
     */
    public void startOperation() {
        elevators.stream().forEach(Elevator::start);
    }

    /**
     * Gracefully seize the operation of the all the elevators
     */
    public void stopOperation() {
        elevators.stream().forEach(Elevator::gracefullyStop);
    }

    /**
     * Getter for the building name
     * @return The name of the building
     */
    public String getBuildingName() {
        return buildingName;
    }

    /**
     * Getter for the number of floors
     * @return The number of floors from this building
     */
    public int getNumberOfFloors() {
        return floors.length;
    }

    /**
     * Count the number of occupied elevators
     * @return The number of elevators processing requests
     */
    public int getNumberOfOccupiedElevators() {
        return (int) elevators.stream()
                .filter(elevator -> elevator.isOccupied())
                .count();
    }

    /**
     * Calculate the total number of waiting requests by summing up all waiting requests
     * @return The number of unhandled requests
     */
    public int getNumberOfWaitingRequests() {
        return (int) Arrays.stream(floors)
                .mapToInt(floor -> floor.getNumberOfWaitingPassengers())
                .sum();
    }
}
