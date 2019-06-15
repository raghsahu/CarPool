package com.fortin.carpool.Model;

/**
 * Created by SWIFT-3 on 22/02/16.
 */
public class RequestedRidePojo {

    String deptdate,depttime,tripdetail_id,trip_id,userid,username,pickup_point,drop_point,seats,comment;

    public RequestedRidePojo() {
    }

    public String getTrip_id() {
        return trip_id;
    }

    public void setTrip_id(String trip_id) {
        this.trip_id = trip_id;
    }

    public String getDeptdate() {
        return deptdate;
    }

    public void setDeptdate(String deptdate) {
        this.deptdate = deptdate;
    }

    public String getDepttime() {
        return depttime;
    }

    public void setDepttime(String depttime) {
        this.depttime = depttime;
    }

    public String getTripdetail_id() {
        return tripdetail_id;
    }

    public void setTripdetail_id(String tripdetail_id) {
        this.tripdetail_id = tripdetail_id;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPickup_point() {
        return pickup_point;
    }

    public void setPickup_point(String pickup_point) {
        this.pickup_point = pickup_point;
    }

    public String getDrop_point() {
        return drop_point;
    }

    public void setDrop_point(String drop_point) {
        this.drop_point = drop_point;
    }

    public String getSeats() {
        return seats;
    }

    public void setSeats(String seats) {
        this.seats = seats;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
