package com.notesdea.sharelocationclient;

import com.baidu.mapapi.model.LatLng;

/**
 * Created by notesdea on 17-3-6.
 */

public class UserLocation {
    private long sessionId;
    private LatLng latLng;

    public long getSessionId() {
        return sessionId;
    }

    public void setSessionId(long sessionId) {
        this.sessionId = sessionId;
    }

    public void setLatLng(double lat, double lng) {
        latLng = new LatLng(lat, lng);
    }

    public LatLng getLatLng() {
        return latLng;
    }
}
