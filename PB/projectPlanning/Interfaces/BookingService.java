import java.time.LocalDate;
import java.util.List;

public interface BookingService {
    /**
     * Retrieves booking details for a specific date.
     *
     * @param date The date of bookings.
     * @return A list of bookings on the given date.
     */
    List<Booking> getBookingsByDate(LocalDate date);

    /**
     * Retrieves bookings for a specific event.
     *
     * @param eventId The event ID.
     * @return A list of bookings for the given event.
     */
    List<Booking> getBookingsByEvent(int eventId);

    /**
     * Retrieves large group bookings.
     *
     * @param minQuantity The minimum number of tickets for large bookings.
     * @return A list of large group bookings.
     */
    List<Booking> getLargeGroupBookings(int minQuantity);
}
