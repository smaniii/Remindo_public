package com.meatyalien.remindo;

import io.realm.DynamicRealm;
import io.realm.DynamicRealmObject;
import io.realm.RealmMigration;
import io.realm.RealmObjectSchema;
import io.realm.RealmSchema;

public class Migration implements RealmMigration {

    @Override
    public void migrate(final DynamicRealm realm, long oldVersion, long newVersion) {

        RealmSchema schema = realm.getSchema();
        /*RealmObjectSchema taskSchema = schema.get("Task");
            RealmObjectSchema taskSchema = schema.get("Task");

            taskSchema.addField("reminder", int.class)
                    .transform(new RealmObjectSchema.Function() {
                        @Override
                        public void apply(DynamicRealmObject obj) {
                            obj.set("reminder", 0);
                        }
                    });
            oldVersion++;
        }*/
    }

}
