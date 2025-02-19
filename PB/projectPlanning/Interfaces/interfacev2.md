
```
title: Sharing interfaces
author: Filippo Vicini
date of publication: 12/02/2024
version number: v2.0
```

## What is an interface
An Interface in Java programming language is defined as an abstract type used to specify the behavior of a class.
In our case we need interfaces to understand what data to access from other teams.
"You need to share the data not the database"

## What we need to provide
We need to provide means by which other teams can access our data without accessing our database. We can control what other teams can
do with our data.

By week 4 we are expected to provide:
- Specification documents
    - what we need from operations
    - what we need from box office
- Java interfaces
    - How we are going to provide access to requested data



## Marketing Team Data Requirements from Operations
### Introduction
The marketing team needs access different data from the operations team to optimize film scheduling, group bookings, and venue promotions. The following specification outlines the required data and its expected format.

### Venue calendar data
- **Why?**: To find empty data slots to display films and other events and have a common calendar between all teams
- **Structure**: The calendar in the DB will be a set of rows where each row is an event, the marketing team wants to be able to access the venue calendar to visualize what is happening. Ideally given a date range we should be able to visualize what is happening each date and at what time
- **Required Data**:
    - `host_id: int`
    - `event_id: int`
    - `event_name: string`
    - `venue_id: int`
    - `venue_name: string`
    - `date: date`
    - `start_time: dateTime`
    - `end_time: dateTime`
    - `status: string`

### Seating data
- **Why?**:  To allocate seats for group bookings and manage accessibility needs.
- **Structure**: The marketing team needs to access seating data for each of the events to have the possibility to view what is happening and manage and allocate seats accordingly to manage accessibility needs. For each event the marketing team should be able to view a seat overview
- **Required Data**:
    - `venue_id: int`
    - `seat_id: int`
    - `row_number: int`
    - `seat_number: int`
    - `status: string`
    - `restricted_view: bool`
    - `date: string`
    - `start_time: string`
    - `end_time: string`



## Specification for box office
The marketing team requires access to various data points from the box office team to manage group bookings, analyze sales performance, and optimize promotions. The following specification outlines the necessary data and its expected format.

### Bookings data
- **Why?**: To plan sales for group bookings, allocate seats, sell special type of tickets, notifications when large booking occurs
- **Structure**: The bookings data is a table in the DB including all the bookings information where each row is a booking with the quanitity and informations about the booking and the event. The marketing team want to be able to access this data to perform certain actions. We want to retrieve booking information giving a specific date or specific event ID. We also want to be able to access bookings for big groups to coordinate seatings.
- **Required Data**
    - `booking_id: int`
    - `customer_id: int`
    - `event_id: int`
    - `price: int`
    - `quantity: int`
    - `booking_date: string`
    - `discount_type: string`

### Customer data
- **Why?**: Personalized communication for "Friends"
- **Structure**: The marketing team wants to access the DB table containing customers information. It specifically wants to access the customers that are friends of lancaster getting their emails for potential communication
- **Required data**
    - `customer_id: int`
    - `customer_name: string`
    - `email: string`
    - `phone_number: int`
    - `friend_member: bool`

### Marketing Functionalities with box office
- Inform box office about large group bookings
- Send requests to hold seats for large group bookings
- Choose films that are shown so Box office can sell tickets
- Provide a list of reserved seats for lancaster friends
