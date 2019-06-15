package com.fortin.carpool.Model;

/**
 * Created by SWIFT-3 on 16/03/16.
 */
public class FindHistoryPojo {

    int id;

    String source,source_lat,source_long,destination,dest_lat,dest_long;

    public FindHistoryPojo() {
    }

    public FindHistoryPojo(int id, String source, String source_lat, String source_long, String destination, String dest_lat, String dest_long) {
        this.id = id;
        this.source = source;
        this.source_lat = source_lat;
        this.source_long = source_long;
        this.destination = destination;
        this.dest_lat = dest_lat;
        this.dest_long = dest_long;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
}
