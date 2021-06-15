package com.example.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.Map;

@Dao
public interface RobotModelDAO {

    @Query("SELECT specs FROM RobotModel WHERE id = :id")
    String getSpecs(int id);

    @Query("SELECT id, type FROM RobotModel WHERE type = :type")
    Map<Integer, String> getAllModelsOfType(String type);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(RobotModel... robotModels);

    @Query("DELETE FROM RobotModel WHERE id = :id")
    void deleteByID(int id);
}
