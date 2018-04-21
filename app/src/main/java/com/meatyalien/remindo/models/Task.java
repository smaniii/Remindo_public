package com.meatyalien.remindo.models;


import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by meatyalien on 2/27/18.
 */

public class Task extends RealmObject {
    @Required
    @PrimaryKey
    private String id;
    @Required
    private String name;
    private boolean done;
    private long timestamp;
    private double xgps;
    private double ygps;
    private boolean reminder;
    private boolean type;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public long getTimestamp() { return timestamp; }

    public void setTimestamp (long timestamp) { this.timestamp = timestamp; }

    public void setXgps(double xgps) { this.xgps = xgps;}

    public void setYgps(double ygps) { this.ygps = ygps;}

    public double getXgps() {
        return xgps;
    }

    public double getYgps() {
        return ygps;
    }

    public void setReminder(boolean reminder) { this.reminder = reminder;}

    public boolean getReminder() {return reminder;}

    public boolean getType() {
        return type;
    }

    public void setType(boolean type) {
        this.type = type;
    }
}
