package building;

/**
 * @author Tobias Haider
 * his class represents a person in a building using an elevator.
 * It is held by a Floor or Elevator object
 */

class Passenger {

    // The description/name/id of a passenger
    private final String passengerName;
    // The destination of a passenger
    private final Floor destinationFloor;

    /**
     * A Passenger object is defined using the an identifier and the destination of a person.
     * They can then be placed on a floor from which they will be transported to the destination.
     * @param passengerName The description/name/identifier of a person using an elevator
     * @param destinationFloor The destination floor of the person
     */
    Passenger(String passengerName, Floor destinationFloor) {
        this.passengerName = passengerName;
        this.destinationFloor = destinationFloor;
    }

    /**
     * A person only enters a floor from the elevator, if it is the destination. It will return false in any other case.
     * @param floor The floor which the passenger may want to enter from the elevator
     * @return False in any case, except the given floor is the destination of the passenger
     */
    boolean askToEnterFloor(Floor floor) {
        if (floor == destinationFloor) {
            floor.enter(this);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Ask a person leaving a floor (using the Elevator).
     * The passenger agrees to leave any floor, except for the destination floor.
     * @param floor A floor from which the passenger should use the elevator to get to the destination
     * @return True in any case, except when asked to leave its destination.
     */
    boolean askToLeaveFloor(Floor floor) {
        if (floor == destinationFloor) {
            return false;
        } else {
            floor.leave(this);
            return true;
        }
    }

    /**
     * A person can determine,
     * if it wants to take the elevator when given the position of the elevator and its destination.
     * The function will return try,
     * if the destination of the passenger is between the position and destination of the elvator.
     * @param from The current position of the elevator
     * @param to The destination of the elevator
     * @return The passenger can enter and reach its destination when taking this elevator
     */
    boolean canReachDestination(Floor from, Floor to) {
        return from.compareTo(destinationFloor) == -1 && to.compareTo(destinationFloor) >= 0
                ||
                from.compareTo(destinationFloor) == 1 && to.compareTo(destinationFloor) <= 0;
    }

    /**
     * Getter for the name of the passenger name
     * @return The identifer/name/description of a passenger
     */
    public String getPassengerName() {
        return passengerName;
    }

    /**
     * Getter for the destination of the passenger
     * @return The floor the passenger wants to reach
     */
    public Floor getDestinationFloor() {
        return destinationFloor;
    }

}
