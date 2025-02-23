import java.util.List;

public interface SeatingService {
    /**
     * Retrieves all seats for a given event.
     *
     * @param eventId The event ID.
     * @return A list of all seats for the event.
     */
    List<Seat> getSeatingOverview(int eventId);

    /**
     * Retrieves available seats for a given event.
     *
     * @param eventId The event ID.
     * @return A list of available seats.
     */
    List<Seat> getAvailableSeats(int eventId);

    /**
     * Allocates seats for a group booking.
     *
     * @param eventId The event ID.
     * @param seatIds The list of seat IDs to be reserved.
     * @return True if allocation is successful, false otherwise.
     */
    boolean allocateSeats(int eventId, List<Integer> seatIds);
}
