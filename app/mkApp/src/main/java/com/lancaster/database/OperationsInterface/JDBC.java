package com.lancaster.database.OperationsInterface;

import com.lancaster.database.Bookings;
import com.lancaster.database.Films;
import io.github.cdimascio.dotenv.Dotenv;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;


public class JDBC {
    private final Connection connection;
    private final OperationsData operationsData;
    // Load environment variables from .env file
    private static final Dotenv dotenv = Dotenv.load();
    private static final String URL = "jdbc:mysql://sst-stuproj.city.ac.uk:3306/in2033t27";
    private static final String USER = dotenv.get("ADMIN_USER");
    private static final String PASSWORD = dotenv.get("ADMIN_PASSWORD");


    public JDBC() throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        this.connection = DriverManager.getConnection(URL, USER, PASSWORD);
        this.operationsData = new OperationsData();

    }

    public List<Map<String, Object>> getGroupBookingByDate(String eventDate, Connection connection) throws SQLException {
        List<Bookings.GroupBookings> bookingsInfo = operationsData.getGroupBookingsByDate(eventDate, connection);
        List<Map<String, Object>> bookingsInfoList = new ArrayList<>();

        for (Bookings.GroupBookings booking : bookingsInfo) {
            Map<String, Object> bookingsInfoMap = new HashMap<>();
            bookingsInfoMap.put("groupBookingId", booking.getBookingID());
            bookingsInfoMap.put("people", booking.getPeople());
            bookingsInfoMap.put("date", booking.getDate());
            bookingsInfoMap.put("event", booking.getEvent());
            bookingsInfoMap.put("room", booking.getRoom());
            bookingsInfoList.add(bookingsInfoMap);
        }

        return bookingsInfoList;
    }

    public Map<String, Object> getGroupBooking(int groupBookingId) throws SQLException {
        Bookings.GroupBookings bookingsInfo = operationsData.getGroupBooking(groupBookingId,connection);

        Map<String, Object> bookingsInfoMap = new HashMap<>();
        bookingsInfoMap.put("groupBookingId", bookingsInfo.getBookingID());
        bookingsInfoMap.put("people", bookingsInfo.getPeople());
        bookingsInfoMap.put("date", bookingsInfo.getDate());
        bookingsInfoMap.put("event", bookingsInfo.getEvent());
        bookingsInfoMap.put("room", bookingsInfo.getRoom());

        return bookingsInfoMap;

    }


    public Map<String, Object> getShowById(int id) throws SQLException, ClassNotFoundException {
        Films.FilmInformation filmInformation = operationsData.getFilmShow(id, connection);


        Map<String, Object> filmInformationMap = new HashMap<>();
        filmInformationMap.put("showId", filmInformation.getShowId());
        filmInformationMap.put("filmId", filmInformation.getFilmId());
        filmInformationMap.put("filmTitle", filmInformation.getFilmTitle());
        filmInformationMap.put("showDate", filmInformation.getShowDate());
        filmInformationMap.put("duration", filmInformation.getDuration());
        filmInformationMap.put("ticketPrice", filmInformation.getTicketPrice());

        return filmInformationMap;
    }

    public List<Films.FilmInformation> getShowsByDate(String showdate) throws SQLException, ClassNotFoundException {
        return operationsData.getFilmShowsByDate(showdate,connection);
    }

}
