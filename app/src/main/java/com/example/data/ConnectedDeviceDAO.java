package com.example.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

/**
 * Data access to the {@code ConnectedDevice} database table.
 */
@Dao
public interface ConnectedDeviceDAO {

    @Query("SELECT address FROM ConnectedDevice ORDER BY lastConnected DESC")
    List<String> getSortedAddresses();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(ConnectedDevice... connectedDevices);

    @Delete
    void delete(ConnectedDevice connectedDevice);
}
