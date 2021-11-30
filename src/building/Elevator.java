package building;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Tobias Haider
 * The elevator is the core element of this application and simulates a real elevator in a building.
 * Using threads, an elevator continuously makes steps between floors and transports passengers between floors.
 */
class Elevator extends Thread {
    // The default thread sleep time is half a second (the elevator can move 2 floors per second)
    private static long DEFAULT_WAITING_TIME = 500;
    // The elevator interacts with a building and therefore needs a reference to it
    private final Building building;
    // The maximal capacity of the elevator
    private final int capacity;
    // The list of passengers currently transported by the elevator
    private final List<Passenger> passengers;
    // The lowest floor of the building
    private final Floor minFloor;
    // The highest floor of the building
    private final Floor maxFloor;
    // The speed of the elevator/simulation
    // The thread is put to sleep for this time interval
    private final long waitingTime;

    // The thread is shut down using this variable
    private boolean inOperation;
    // The current position of the elevator
    private Floor currentFloor;
    // The destination of the elevator
    private Floor destinationFloor;
    // The movement state of the elevator
    private ElevatorState state;

    // print steps for debug
    private boolean debug;


    /**
     * An elevator can only be created with an existing building. A capacity and the speed have to be set.
     * @param building The building object in which the elevator is placed
     * @param elevatorNumber A number identifying this elevator
     * @param capacity The number of persons which can be transported by this elevator
     * @param floorsPerSecond The speed of the elevator
     * @param debug The elevator will print information based on this boolean
     * @throws Exception The building is not instantiated correctly, if an error is thrown
     */
    Elevator(Building building, int elevatorNumber, int capacity, double floorsPerSecond, boolean debug) throws Exception {
        super("Elevator " + elevatorNumber);
        this.building = building;
        this.capacity = capacity;
        this.passengers = new ArrayList<>(capacity);
        this.minFloor = building.getFloor(0);
        this.maxFloor = building.getFloor(building.getNumberOfFloors() - 1);
        // Set the floor of the elevator to a random values
        final Floor randomFloor = building.getFloor((int) (Math.random() * building.getNumberOfFloors()));
        this.currentFloor = randomFloor;
        this.destinationFloor = randomFloor;
        this.waitingTime = floorsPerSecond == 0 ? DEFAULT_WAITING_TIME : (long) (1000.0 / floorsPerSecond);
        this.debug = debug;

        // Set the elevator to not running
        this.state = ElevatorState.STILL;
        this.inOperation = false;
    }

    /**
     * The main operation of the elevator/thread happens here.
     * Steps between floors are made every few milliseconds based on the parameter waitingTime
     */
    public void run() {
        // Initialize state
        this.state = ElevatorState.STILL;
        this.inOperation = true;

        // Make a move every few milliseconds until operation is shut down
        // The elevator keeps operating until all passengers have left
        while (inOperation || !passengers.isEmpty()) {
            try {
                move();
                sleep(waitingTime);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Move between floors and transport passengers.
     * This function implements a state machine
     * @throws Exception The elevator is caught in an inconsistent state
     */
    private void move() throws Exception {
        // Check whether the next destination has been reached
        boolean destinationReached = currentFloor == destinationFloor;

        // Debug print
        if (debug) {
            System.out.println(this.getName() + ": Floor-"
                    + currentFloor.getFloorNumber()
                    + ", Destination-" + destinationFloor.getFloorNumber()
                    + ", Passengers-" + passengers.size()
                    + ", State-" + state);
        }
        // Let passengers leave and enter
        updatePassengers(currentFloor == destinationFloor);
        // State machine
        switch (state) {
            case STILL:
                // The elevator is not moving
                if (currentFloor.compareTo(destinationFloor) == -1 && currentFloor.compareTo(maxFloor) == -1) {
                    // The next destination is up, start moving upwards
                    state = ElevatorState.UP;
                    break;
                }
                if (currentFloor.compareTo(destinationFloor) == 1 && currentFloor.compareTo(minFloor) == 1) {
                    // The next destination is down, start moving downwards
                    state = ElevatorState.DOWN;
                    break;
                }
                if (passengers.isEmpty()) {
                    // Get a new destination from the building by finding waiting passengers
                    Optional<Floor> floorWithWaitingPassengers = building.findFloorWithWaitingPassengers();
                    if (floorWithWaitingPassengers.isPresent()) {
                        destinationFloor = floorWithWaitingPassengers.get();
                    }
                } else {
                    destinationFloor = passengers.get(0).getDestinationFloor();
                }

                break;
            case UP:
                // The elevator is moving up
                if (destinationReached || currentFloor == maxFloor) {
                    // There is nowhere to move anymore, wait here
                    state = ElevatorState.STILL;
                } else {
                    // Make step upwards
                    currentFloor = building.getFloor(currentFloor.getFloorNumber() + 1);
                }
                break;
            case DOWN:
                if (destinationReached || currentFloor == minFloor) {
                    // There is nowhere to move anymore, wait here
                    state = ElevatorState.STILL;
                } else {
                    // Make step downwards
                    currentFloor = building.getFloor(currentFloor.getFloorNumber() - 1);
                }
                break;
            default:
                throw new Exception("The state " + state + " can not be dealt with.");
        }
    }

    /**
     * Let passengers leave and enter the elevator
     * @param destinationReached A boolean defining whether the next destination of the elevator has been reached
     */
    private void updatePassengers(boolean destinationReached) {
        // Give all passengers the option to leave
        List<Passenger> passengersToRemove = passengers.stream().filter(passenger -> passenger != null)
                .filter(passenger -> passenger.askToEnterFloor(currentFloor))
                .collect(Collectors.toList());
        passengersToRemove.forEach(passenger -> passengers.remove(passenger));

        // Calculate the number of passengers able to join
        int spotsLeft = capacity - passengers.size();
        List<Passenger> passengersToAdd;

        if (destinationReached) {
            // Determine the next destination of the elevator
            passengersToAdd = currentFloor.getPassengers(spotsLeft);
            if (!passengersToAdd.isEmpty()) {
                // Take the destination of the first passenger
                destinationFloor = passengersToAdd.get(0).getDestinationFloor();
            }
        } else {
            // Get passengers which want to go in the same direction as the elevator
            passengersToAdd = currentFloor.getPassengers(spotsLeft, destinationFloor);
        }
        // Add the passengers to the elevator
        passengers.addAll(passengersToAdd);
    }

    /**
     * Check whether the elevator is transporting passengers or on the way to pick up passengers
     * @return The elevator is currently not processing transport requests
     */
    public boolean isOccupied() {
        return !(passengers.isEmpty() && state == ElevatorState.STILL);
    }
    /**
     * Slowly stop the elevator. When all passengers have left, the thread will be shut down
     */
    public void gracefullyStop() {
        inOperation = false;
    }

    /**
     * Internal enum storing the movement state of the elevator
     */
    private enum ElevatorState {
        UP, DOWN, STILL;
    }
}
