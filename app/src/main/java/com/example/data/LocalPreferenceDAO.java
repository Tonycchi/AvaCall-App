package com.example.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

/**
 * Data access to the {@code LocalPreference} database table.
 */
@Dao
public interface LocalPreferenceDAO {

    @Query("SELECT value FROM LocalPreference WHERE id = :id")
    String get(String id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(LocalPreference... localPreferences);
}
