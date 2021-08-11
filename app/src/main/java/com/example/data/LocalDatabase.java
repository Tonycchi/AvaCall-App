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
                            if (!Constants.USER_RELEASE) {
                                tmp.robotModelDAO().insertAll(
                                        new RobotModel(0, "Kettenroboter", Constants.TYPE_EV3, "joystick:50;1,8", "Linker Kettenmotor: Port A und Rechter Kettenmotor: Port D"),
                                        new RobotModel(0, "Kettenroboter mit Greifarm", Constants.TYPE_EV3, "joystick:50;1,8|slider:30;4", "Linker Kettenmotor: Port A, Rechter Kettenmotor: Port D und Motor des Greifarms: Port B"),
                                        new RobotModel(0, "Michael Gösele", Constants.TYPE_EV3, "joystick:50;1,8|slider:30;4|button:20;2;5000", null),
                                        new RobotModel(0, "Was geht", Constants.TYPE_EV3, "joystick:50;1,8|slider:30;4|button:20;2;5000|slider:30;4|button:20;4;5000", ""),
                                        new RobotModel(0, "Gensearsch", Constants.TYPE_EV3, "button:20;1;5000|button:20;2;5000|button:20;4;5000|button:20;8;5000", " "),
                                        new RobotModel(0, "Greifarm", Constants.TYPE_EV3, "slider:30;4", "Motor des Greifarms: Port B"),
                                        new RobotModel(0, "Painter", Constants.TYPE_EV3, "slider:30;4|slider:50;1", "Mal schauen"),
                                        new RobotModel(0, "Sollte nicht angezeigt werde, weil falscher Typ", "TEST", "joystick:50;1,8|slider:30;4|button:20;2;5000", null)
                                );
                            }
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
