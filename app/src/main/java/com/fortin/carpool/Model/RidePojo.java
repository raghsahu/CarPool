package com.fortin.carpool.Model;

/**
 * Created by SWIFT-3 on 16/02/16.
 */
public class RidePojo
{
String trip_id,vehicle_id,vehicle,vehicleimg,source,source_lat,source_long,destination,dest_lat,dest_long,dep_time,ret_time,trip_type,seats,passenger_type,userid,userimg,trip_status,route,routelat,routelong,rate;
String smoking,ac,extra;

    public RidePojo() {
    }

    public String getRoutelat() {
        return routelat;
    }

    public String getRoutelong() {
        return routelong;
    }

    public String getTrip_id() {
        return trip_id;
    }

    public void setTrip_id(String trip_id) {
        this.trip_id = trip_id;
    }

    public String getVehicle_id() {
        return vehicle_id;
    }

    public void setVehicle_id(String vehicle_id) {
        this.vehicle_id = vehicle_id;
    }

    public String getVehicle() {
        return vehicle;
    }

    public void setVehicle(String vehicle) {
        this.vehicle = vehicle;
    }

    public String getVehicleimg() {
        return vehicleimg;
    }

    public void setVehicleimg(String vehicleimg) {
        this.vehicleimg = vehicleimg;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getSource_lat() {
        return source_lat;
    }

    public void setSource_lat(String source_lat) {
        this.source_lat = source_lat;
    }

    public String getSource_long() {
        return source_long;
    }

    public void setSource_long(String source_long) {
        this.source_long = source_long;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getDest_lat() {
        return dest_lat;
    }

    public void setDest_lat(String dest_lat) {
        this.dest_lat = dest_lat;
    }

    public String getDest_long() {
        return dest_long;
    }

    public void setDest_long(String dest_long) {
        this.dest_long = dest_long;
    }

    public String getDep_time() {
        return dep_time;
    }

    public void setDep_time(String dep_time) {
        this.dep_time = dep_time;
    }

    public String getRet_time() {
        return ret_time;
    }

    public void setRet_time(String ret_time) {
        this.ret_time = ret_time;
    }

    public String getTrip_type() {
        return trip_type;
    }

    public void setTrip_type(String trip_type) {
        this.trip_type = trip_type;
    }

    public String getSeats() {
        return seats;
    }

    public void setSeats(String seats) {
        this.seats = seats;
    }

    public String getPassenger_type() {
        return passenger_type;
    }

    public void setPassenger_type(String passenger_type) {
        this.passenger_type = passenger_type;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getUserimg() {
        return userimg;
    }

    public void setUserimg(String userimg) {
        this.userimg = userimg;
    }

    public String getTrip_status() {
        return trip_status;
    }

    public void setTrip_status(String trip_status) {
        this.trip_status = trip_status;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getSmoking() {
        return smoking;
    }

    public void setSmoking(String smoking) {
        this.smoking = smoking;
    }

    public String getAc() {
        return ac;
    }

    public void setAc(String ac) {
        this.ac = ac;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }
}
