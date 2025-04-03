package com.lancaster.database;

import java.util.Date;

public abstract class Bookings {
    public static class PriorityBookings{

    }
    public static class GroupBookings{
        private int bookingID;
        private int people;
        private Date date;
        private String room;
        private String event;


        public GroupBookings(int bookingID, int people,Date date, String room,  String event ) {
            this.bookingID = bookingID;
            this.people = people;
            this.room = room;
            this.event = event;
            this.date = date;
        }

        public int getBookingID() {
            return bookingID;
        }

        public int getPeople() {
            return people;
        }

        public String getRoom() {
            return room;
        }

        public String getEvent() {
            return event;
        }

        public Date getDate() {
            return date;
        }
    }
}
