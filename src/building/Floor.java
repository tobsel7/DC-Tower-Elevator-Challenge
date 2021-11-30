package building;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

/**
 * @author Tobias Haider
 * The Floor class represents a floor of a building. It can hold passengers until they are transported by the elevator.
 */
class Floor implements Comparable<Floor> {

    // The number of the floor
    private final int floorNumber;
    // The queue of waiting passengers on this floor
    private final Queue<Passenger> waitingPassengers;

    /**
     * A Floor object can be instantiated simply using a floor number.
     * A Floor object only has a purpose, if a Building holds a reference to it.
     * @param floorNumber The floor number of the Floor within a Building
     */
    Floor(int floorNumber) {
        this.floorNumber = floorNumber;
        this.waitingPassengers = new ConcurrentLinkedQueue<>();
    }

    /**
     * This function is used by the elevator to ask for passengers.
     * Only a specific number of passengers which want to go in direction to the destination floor can enter.
     * @param spotsLeft The number of passengers, which can be handed over to the elevator
     * @param destinationFloor The next destination of the elevator
     * @return A list of passengers given to the elevator for transportation
     */
    List<Passenger> getPassengers(int spotsLeft, Floor destinationFloor) {
        //
        List<Passenger> leavingPassengers = waitingPassengers.stream()
                // take only passengers who want to go the same way as the elevator
                .filter(passenger -> passenger.canReachDestination(this, destinationFloor))
                // take only the capacity left of the elevator
                .limit(Math.max(0, spotsLeft))
                .collect(Collectors.toList());

        // Before handing the passengers to the elevator, all passengers are asked to leave the floor
        return leavingPassengers.stream()
                .filter(passenger -> passenger.askToLeaveFloor(this))
                .collect(Collectors.toList());
    }

    /**
     * An empty elevator has no destination.
     * Any passengers going in the same direction can be given to the elevator for transportation.
     * @param spotsLeft The capacity of the elevator
     * @return A list of passengers given to the elevator for transportation
     */
    List<Passenger> getPassengers(int spotsLeft) {
        if (waitingPassengers.peek() == null || spotsLeft < 1) {
            // if no passengers are waiting or no spots are free, return an empty list
            return new LinkedList<>();
        }

        // The first waiting person is used to determine the next destination of the elevator
        Passenger first = waitingPassengers.poll();
        if (first.askToLeaveFloor(this)) {
            // Find all waiting persons wanting to go in the same direction as the first person
            List<Passenger> leavingPassengers = getPassengers(spotsLeft - 1, first.getDestinationFloor());
            leavingPassengers.add(0, first);
            return leavingPassengers;
        } else {
            // The first person does not want to leave the floor
            // Exclude it from the waiting list and try again
            // This feature is not used in the current implementations.
            // Passengers in a waiting queue will always leave the floor
            waitingPassengers.remove(first);
            return getPassengers(spotsLeft);
        }
    }

    /**
     * This functions simulates a passenger leaving the elevator on this floor using System.out.println
     * This function is only called by the passenger itself.
     * @param passenger A passenger leaving an elevator on this floor
     */
    void enter(Passenger passenger) {
        System.out.println(passenger.getPassengerName() + " has reached their destination, which is the floor " + floorNumber);
    }

    /**
     * This functions simulates a passenger entering the elevator from this floor using System.out.println
     * Before entering the elevator, the passenger is removed from the waiting queue on this floor
     * This function is only called by the passenger itself.
     * @param passenger A passenger entering an elevator from this floor
     */
    void leave(Passenger passenger) {
        // The passenger is removed from the waiting queue
        waitingPassengers.remove(passenger);
        System.out.println(passenger.getPassengerName() + " is taking the elevator on floor " + floorNumber);
    }

    /**
     * Passengers can be added to the waiting queue on this floor
     * @param passenger The passenger which will be waiting for the elevator on this floor
     * @return Success of adding them to the waiting queue
     */
    boolean addPassenger(Passenger passenger) {
        boolean success = waitingPassengers.add(passenger);
        if (success) {
            System.out.println("The passenger " + passenger.getPassengerName() + " is waiting for an elevator on the floor " + floorNumber + ".");
            System.out.println("They want to reach the floor " + passenger.getDestinationFloor().getFloorNumber());
        }
        return success;
    }

    /**
     * Get the number of passengers waiting for the elevator to pick them up
     * @return The number of passengers in the queue
     */
    public int getNumberOfWaitingPassengers() {
        return waitingPassengers.size();
    }

    /**
     * Check whether passengers are waiting on this floor
     * @return At least 1 passenger is waiting for an elevator
     */
    public boolean hasWaitingPassengers() {
        return !waitingPassengers.isEmpty();
    }

    /**
     * Getter of the floor number
     * @return The floor number of this floor
     */
    public int getFloorNumber() {
        return floorNumber;
    }

    /**
     * Two floors are compared, based on their floor numbers.
     * @param floor The floor this floor should be compare with
     * @return The comparison between this floor number and the other one
     */
    @Override
    public int compareTo(Floor floor) {
        return Integer.compare(floorNumber, floor.floorNumber);
    }
}
