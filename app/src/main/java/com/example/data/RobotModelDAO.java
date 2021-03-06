package com.example.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

/**
 * Data access to the {@code RobotModel} database table.
 */
@Dao
public interface RobotModelDAO {

    @Query("SELECT specs FROM RobotModel WHERE id = :id")
    String getSpecs(int id);

    @Query("SELECT * FROM RobotModel WHERE type = :type")
    List<RobotModel> getAllModelsOfType(String type);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(RobotModel... robotModels);

    @Query("DELETE FROM RobotModel WHERE id = :id")
    void deleteByID(int id);

    @Query("SELECT * FROM RobotModel WHERE id = :id")
    RobotModel getRobotModel(int id);

    @Query("SELECT COUNT(id) FROM RobotModel WHERE type = :type")
    int getNumberOfRobotModels(String type);

    @Query(" UPDATE RobotModel SET picture = :uri WHERE id = :id")
    void setPictureOfRobotModel(int id, String uri);
}
