package building;

/**
 * @author Tobias Haider
 * This Factory allows for a quick instantiation of a Building object.
 * It ensures that the Building is configured correctly before using it.
 */
public class BuildingFactory {
    // Default values
    public static final String DEFAULT_BUILDING_NAME = "Building";
    public static final int DEFAULT_NUMBER_OF_FLOORS = 10;
    public static final int DEFAULT_NUMBER_OF_ELEVATORS = 2;
    public static final int DEFAULT_ELEVATOR_CAPACITY = 6;
    public static final int DEFAULT_ELEVATOR_SPEED = 2;
    public static final boolean DEFAULT_DEBUG = false;

    // Set values will be stored here
    private String buildingName;
    private int numberOfFloors;
    private int numberOfElevators;
    private int elevatorCapacity;
    private int elevatorSpeed;
    private boolean debug;

    /**
     * A BuildingFactory creates shell object storing all values needed to instantiate a Building object.
     * In the constructor all values are set to some safe default values.
     */
    public BuildingFactory() {
        this.buildingName = DEFAULT_BUILDING_NAME;
        this.numberOfFloors = DEFAULT_NUMBER_OF_FLOORS;
        this.numberOfElevators = DEFAULT_NUMBER_OF_ELEVATORS;
        this.elevatorCapacity = DEFAULT_ELEVATOR_CAPACITY;
        this.elevatorSpeed = DEFAULT_ELEVATOR_SPEED;
        this.debug = DEFAULT_DEBUG;
    }

    /**
     * The name of the building can be set to any string
     * @param buildingName The desired name of the building
     */
    public void setBuildingName(String buildingName) {
        this.buildingName = buildingName;
    }

    /**
     * The number of floors can be set to any integer above 0
     * @param numberOfFloors The desired number of floors
     */
    public void setNumberOfFloors(int numberOfFloors) {
        if (numberOfFloors > 0) {
            this.numberOfFloors = numberOfFloors;
        }
    }

    /**
     * The number of elevators can be set to any integer above 0
     * @param numberOfElevators The desired number of elevators
     */
    public void setNumberOfElevators(int numberOfElevators) {
        if (numberOfElevators > 0) {
            this.numberOfElevators = numberOfElevators;
        }
    }

    /**
     * The capacity of the elevators can be set to any integer above 0
     * @param elevatorCapacity The desired capacity per elevator
     */
    public void setElevatorCapacity(int elevatorCapacity) {
        if (elevatorCapacity > 0) {
            this.elevatorCapacity = elevatorCapacity;
        }
    }

    /**
     * The speed can be set to any value between 0 and 1000 floors per second.
     * Anything between 1 and 20 is recommended.
     * @param elevatorSpeed The desired speed of the elevators
     */
    public void setElevatorSpeed(int elevatorSpeed) {
        if (elevatorSpeed > 0 && elevatorSpeed < 1000) {
            this.elevatorSpeed = elevatorSpeed;
        }
    }

    /**
     * The elevators will give more information depending on this values.
     * @param debug Debug will be activated depending on this
     */
    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    /**
     * Instantiate a building object with the set values.
     * @return A correctly configured Building object
     */
    public Building create() {
        Building building = new Building(buildingName, numberOfFloors);
        for (int i = 0; i < numberOfElevators; i++) {
            building.addElevator(elevatorCapacity, elevatorSpeed, debug);
        }
        building.startOperation();
        return building;
    }
}
