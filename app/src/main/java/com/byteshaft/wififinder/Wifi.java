package com.byteshaft.wififinder;

import java.io.Serializable;

/**
 * Created by s9iper1 on 3/13/18.
 */

public class Wifi  implements Serializable {

    private String ssid;
    private int strength;

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public int getStrength() {
        return strength;
    }

    public void setStrength(int strength) {
        this.strength = strength;
    }
}
