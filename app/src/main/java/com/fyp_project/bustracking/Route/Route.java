package com.fyp_project.bustracking.Route;

public class Route {

    public Route() {

    }

    public String getRoute_name() {
        return route_name;
    }

    public void setRoute_name(String route_name) {
        this.route_name = route_name;
    }

    public String getRoute_number() {
        return route_number;
    }

    public void setRoute_number(String route_number) {
        this.route_number = route_number;
    }

    public String getRoute_time() {
        return route_time;
    }

    public void setRoute_time(String route_time) {
        this.route_time = route_time;
    }

    public String getRoute_distance() {
        return route_distance;
    }

    public void setRoute_distance(String route_distance) {
        this.route_distance = route_distance;
    }

    public String route_name;
    public String route_number;
    public String route_time;
    public String route_distance;

    public Route(String route_name, String route_number, String route_time, String route_distance) {
        this.route_name = route_name;
        this.route_number = route_number;
        this.route_time = route_time;
        this.route_distance = route_distance;
    }
}
