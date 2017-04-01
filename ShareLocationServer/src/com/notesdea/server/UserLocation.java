package com.notesdea.server;

/**
 * Created by notesdea on 17-4-1.
 */
public class UserLocation {

    private LatLng latLng;

    private long sessionId;

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public long getSessionId() {
        return sessionId;
    }

    public void setSessionId(long sessionId) {
        this.sessionId = sessionId;
    }

    class LatLng {
        public double latitude;
        public double latitudeE6;
        public double longitude;
        public double longitudeE6;
    }
}
