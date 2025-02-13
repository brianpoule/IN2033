```
title: Sharing interfaces
author: Filippo Vicini
date of publication: 11/02/2024
version number: v1.0
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
- **Access Level**: Read-only with specific write permissions
- **Structure**: The calendar in the DB will be a set of rows where each row is an event.
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
- **Access level**: Read and write
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
- **Why?**: To plan sales for group bookings
- **Access level**: read and write
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
- **Access level**: read only
- **Required data**
    - `customer_id: int`
    - `customer_name: string`
    - `email: string`
    - `phone_number: int`
    - `friend_member: bool`
