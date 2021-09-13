package com.example.data;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "LocalPreference")
public class LocalPreference {

    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "id")
    private final String id;

    @ColumnInfo(name = "value")
    private final String value;

    public LocalPreference(String id, String value) {
        this.id = id;
        this.value = value;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public String getValue() {
        return value;
    }
}
