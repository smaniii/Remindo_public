package com.meatyalien.remindo;

/**
 * Created by meatyalien on 2/27/18.
 */

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class TaskListApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
        RealmConfiguration realmConfig = new RealmConfiguration.Builder()
                .name("remindo.realm")
                .schemaVersion(0)
                //.migration(new Migration())
                .build();
        //Realm.deleteRealm(realmConfig);
        Realm.setDefaultConfiguration(realmConfig);
    }
}
