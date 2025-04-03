package com.lancaster.database.OperationsInterface;

import com.lancaster.database.Bookings;
import com.lancaster.database.Events;
import com.lancaster.database.Films;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OperationsData implements OperationsInterface {

    @Override
    public Bookings.GroupBookings getGroupBooking(int groupBookingId) {
        return null;
    }

    @Override
    public List<Bookings.GroupBookings> getGroupBookingsByDate(String eventDate) {
        return List.of();
    }

    public Films.FilmInformation getFilmShow(int showId, Connection connection) throws SQLException {
        String query = "SELECT showId, filmId, filmTitle, showDate, duration, ticketPrice FROM film_bookings WHERE filmId = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, showId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new Films.FilmInformation(
                            resultSet.getInt("showId"),
                            resultSet.getInt("filmId"),
                            resultSet.getString("filmTitle"),
                            resultSet.getDate("showDate"),
                            resultSet.getInt("duration"),
                            resultSet.getInt("ticketPrice")
                    );
                }
            }
        }

        return null;
    }

    public List<Films.FilmInformation> getFilmShowsByDate(String showDate, Connection connection) throws SQLException {
        String query = "SELECT showId, filmId, filmTitle, showDate, duration, ticketPrice FROM film_bookings WHERE showDate = ?";
        List<Films.FilmInformation> filmShows = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, showDate);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Films.FilmInformation filmInformation = new Films.FilmInformation(
                            resultSet.getInt("showId"),
                            resultSet.getInt("filmId"),
                            resultSet.getString("filmTitle"),
                            resultSet.getDate("showDate"),
                            resultSet.getInt("duration"),
                            resultSet.getInt("ticketPrice")
                    );
                    filmShows.add(filmInformation);
                }
            }
        }

        return filmShows;
    }

    @Override
    public Bookings.PriorityBookings getPrioritySeat(int prioritySeatId) {
        return null;
    }

    @Override
    public List<Bookings.PriorityBookings> getPrioritySeats(String startDate, String endDate) {
        return List.of();
    }

    @Override
    public Events getMarketingEvent(int marketingEventId) {
        return null;
    }

    @Override
    public List<Events> getMarketingEventsByDate(String eventDate) {
        return List.of();
    }

    @Override
    public List<Events> getRoomSchedule(String roomId, String date) {
        return List.of();
    }
}
