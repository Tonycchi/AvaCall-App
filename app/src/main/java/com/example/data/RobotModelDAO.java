package com.example.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface RobotModelDAO {

    @Query("SELECT specs FROM RobotModel WHERE id = :id")
    String getSpecs(int id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(RobotModel... robotModels);

    @Query("DELETE FROM RobotModel WHERE id = :id")
    void deleteByID(int id);
}
