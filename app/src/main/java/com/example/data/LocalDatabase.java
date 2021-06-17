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
    public abstract ConnectedDeviceDAO connectedDeviceDAO();
    public abstract LocalPreferenceDAO localPreferenceDAO();
    public abstract RobotModelDAO robotModelDAO();

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
                        Executors.newSingleThreadExecutor().execute(() -> getInstance(context).localPreferenceDAO().insertAll(
                                new LocalPreference(URLSettings.HOSTURLKEY, "avatar.mintclub.org"),
                                new LocalPreference(URLSettings.JITSIURLKEY, "meet.jit.si"),
                                new LocalPreference(URLSettings.HOSTPORTKEY, "22222")));
                    }
                })
                .allowMainThreadQueries()
                .build();
    }
}
