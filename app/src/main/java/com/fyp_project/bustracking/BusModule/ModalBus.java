package com.fyp_project.bustracking.BusModule;

public class ModalBus {


    public ModalBus(String bus_number, String distance_covered, String extra_query, String fuel_consumed, String trip_time, String date_time) {
        this.bus_number = bus_number;
        this.distance_covered = distance_covered;
        this.extra_query = extra_query;
        this.fuel_consumed = fuel_consumed;
        this.trip_time = trip_time;
        this.date_time = date_time;
    }

    public String getBus_number() {
        return bus_number;
    }

    public void setBus_number(String bus_number) {
        this.bus_number = bus_number;
    }

    public String getDistance_covered() {
        return distance_covered;
    }

    public void setDistance_covered(String distance_covered) {
        this.distance_covered = distance_covered;
    }

    public String getExtra_query() {
        return extra_query;
    }

    public void setExtra_query(String extra_query) {
        this.extra_query = extra_query;
    }

    public String getFuel_consumed() {
        return fuel_consumed;
    }

    public void setFuel_consumed(String fuel_consumed) {
        this.fuel_consumed = fuel_consumed;
    }

    public String getTrip_time() {
        return trip_time;
    }

    public void setTrip_time(String trip_time) {
        this.trip_time = trip_time;
    }

    public String getDate_time() {
        return date_time;
    }

    public void setDate_time(String date_time) {
        this.date_time = date_time;
    }

    public String bus_number;
    public String distance_covered;
    public String extra_query;
    public String fuel_consumed;
    public String trip_time;
    public String date_time;

    public ModalBus() {

    }

}

