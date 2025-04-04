package com.lancaster.database;

import java.util.Date;

public abstract class Films {
    public static class FilmFinancials{

    }
    public static class FilmEngagement{

    }
    public static class FilmScreening{

    }
    public static class FilmInformation {
        private int showId;
        private int filmId;
        private String filmTitle;
        private Date showDate;
        private int duration;
        private Date endDate;
        private int ticketPrice;

        public FilmInformation(int showId, int filmId, String filmTitle, Date showDate,  int duration, Date endDate,int ticketPrice) {
            this.showId = showId;
            this.filmId = filmId;
            this.filmTitle = filmTitle;
            this.showDate = showDate;
            this.duration = duration;
            this.endDate = endDate;
            this.ticketPrice = ticketPrice;
        }

        @Override
        public String toString() {
            return "FilmInformation{" +
                    "showId=" + showId +
                    ", filmId=" + filmId +
                    ", filmTitle='" + filmTitle + '\'' +
                    ", showDate=" + showDate +
                    ", duration=" + duration +
                    ", ticketPrice=" + ticketPrice +
                    '}';
        }

        public int getShowId() {
            return showId;
        }



        public int getFilmId() {
            return filmId;
        }


        public String getFilmTitle() {
            return filmTitle;
        }

        public Date getShowDate() {
            return showDate;
        }



        public int getDuration() {
            return duration;
        }



        public int getTicketPrice() {
            return ticketPrice;
        }
    }
}
