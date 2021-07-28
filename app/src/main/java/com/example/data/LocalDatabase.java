package com.example.data;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

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
                            //TODO remove for final version:
                            tmp.robotModelDAO().insertAll(
                                    new RobotModel(0, "Kettenroboter", "EV3", "joystick:50;1,8"),
                                    new RobotModel(0, "Kettenroboter mit Greifarm", "EV3", "joystick:50;1,8|slider:30;4"),
                                    new RobotModel(0, "Michael Gösele", "EV3", "joystick:50;1,8|slider:30;4|button:20;2;5000"),
                                    new RobotModel(0, "Was geht", "EV3", "joystick:50;1,8|slider:30;4|button:20;2;5000|slider:30;4|button:20;4;5000"),
                                    new RobotModel(0, "Gensearsch", "EV3", "button:20;1;5000|button:20;2;5000|button:20;4;5000|button:20;8;5000"),
                                    new RobotModel(0, "NUR GREIFARM", "EV3", "slider:30;4"),
                                    new RobotModel(0, "Painter", "EV3", "slider:30;4|slider:50;1"),
                                    new RobotModel(0, "Sollte nicht angezeigt werde, weil falscher Typ", "TEST", "joystick:50;1,8|slider:30;4|button:20;2;5000")
                            );
                        });
                    }
                })
                .allowMainThreadQueries()
                .build();
    }

    public abstract ConnectedDeviceDAO connectedDeviceDAO();

    public abstract LocalPreferenceDAO localPreferenceDAO();

    public abstract RobotModelDAO robotModelDAO();
}
