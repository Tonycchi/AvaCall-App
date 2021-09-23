package com.example.data;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.Constants;

import java.util.concurrent.Executors;

@Database(entities = {ConnectedDevice.class, LocalPreference.class, RobotModel.class}, version = 1)
public abstract class LocalDatabase extends RoomDatabase {
    private static LocalDatabase INSTANCE;

    public synchronized static LocalDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = buildDatabase(context);
        }
        return INSTANCE;
    }

    private static LocalDatabase buildDatabase(final Context context) {
        return Room.databaseBuilder(context,
                LocalDatabase.class,
                "local_database")
                .addCallback(new Callback() {
                    @Override
                    public void onCreate(@NonNull SupportSQLiteDatabase db) {
                        super.onCreate(db);
                        Executors.newSingleThreadExecutor().execute(() -> {
                            LocalDatabase tmp = getInstance(context);
                            tmp.localPreferenceDAO().insertAll(
                                    new LocalPreference(URLSettings.HOSTURLKEY, URLSettings.DEFAULT_TEST_HOST),
                                    new LocalPreference(URLSettings.VIDEOURLKEY, URLSettings.DEFAULT_TEST_JITSI),
                                    new LocalPreference(URLSettings.HOSTPORTKEY, URLSettings.DEFAULT_TEST_PORT)
                            );
                            addDefaultEV3Models(tmp);
                        });
                    }
                })
                .allowMainThreadQueries()
                .build();
    }

    public static void addDefaultEV3Models(LocalDatabase localDatabase){
        localDatabase.robotModelDAO().insertAll(
                new RobotModel(0, "Kettenroboter", Constants.TYPE_EV3, "joystick:50;1,8", "Linker Kettenmotor: Port A und Rechter Kettenmotor: Port D", null),
                new RobotModel(0, "Kettenroboter mit Greifarm", Constants.TYPE_EV3, "joystick:50;1,8|slider:30;2", "Linker Kettenmotor: Port A, Rechter Kettenmotor: Port D und Motor des Greifarms: Port B", null),
                new RobotModel(0, "Greifarm", Constants.TYPE_EV3, "slider:30;4", "Motor des Greifarms: Port B", null),
                new RobotModel(0, "Elephant", Constants.TYPE_EV3, "slider:100;1|slider:100;2|slider:100;8", "Motor der Beine: Port A, Motor des Rüssels Port B und Motor des Kopfes Port D", null)

        );
    }

    public abstract ConnectedDeviceDAO connectedDeviceDAO();

    public abstract LocalPreferenceDAO localPreferenceDAO();

    public abstract RobotModelDAO robotModelDAO();
}
