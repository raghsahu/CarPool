package com.fortin.carpool.Model;

/**
 * Created by SWIFT-3 on 16/03/16.
 */
public class MemberPojo {

    String tripdetailid,tripid,memberid,membernm,membermob,pickup,drop,pickuptime,seat;

    public String getTripdetailid() {
        return tripdetailid;
    }

    public void setTripdetailid(String tripdetailid) {
        this.tripdetailid = tripdetailid;
    }

    public String getTripid() {
        return tripid;
    }

    public void setTripid(String tripid) {
        this.tripid = tripid;
    }

    public String getSeat() {
        return seat;
    }

    public void setSeat(String seat) {
        this.seat = seat;
    }

    public String getMemberid() {
        return memberid;
    }

    public void setMemberid(String memberid) {
        this.memberid = memberid;
    }

    public String getMembernm() {
        return membernm;
    }

    public void setMembernm(String membernm) {
        this.membernm = membernm;
    }

    public String getMembermob() {
        return membermob;
    }

    public void setMembermob(String membermob) {
        this.membermob = membermob;
    }

    public String getPickup() {
        return pickup;
    }

    public void setPickup(String pickup) {
        this.pickup = pickup;
    }

    public String getDrop() {
        return drop;
    }

    public void setDrop(String drop) {
        this.drop = drop;
    }

    public String getPickuptime() {
        return pickuptime;
    }

    public void setPickuptime(String pickuptime) {
        this.pickuptime = pickuptime;
    }
}
