package com.dfssi.elasticsearch.sql611.spatial;

/**
 * Created by Eliran on 15/8/2015.
 */
public class RangeDistanceFilterParams extends DistanceFilterParams {
    private String distanceTo;

    public RangeDistanceFilterParams(String distanceFrom,String distanceTo, Point from) {
        super(distanceFrom, from);
        this.distanceTo = distanceTo;
    }

    public String getDistanceTo() {
        return distanceTo;
    }

    public String getDistanceFrom() {
        return this.getDistance();
    }
}
