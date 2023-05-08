package com.example.leveraginggps;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "table_name")
public class MainData implements Serializable {
  @PrimaryKey(autoGenerate = true)
  private int id;

  @ColumnInfo(name = "latitude")
  private double latitude;
  @ColumnInfo(name = "longitude")
  private double longitude;
  @ColumnInfo(name = "count")
  private int count;

  public int getId() {
    return id;
  }

  public double getLatitude() {
    return latitude;
  }

  public double getLongitude() {
    return longitude;
  }

  public int getCount() {
    return count;
  }

  public void setId(int id) {
    this.id = id;
  }

  public void setLatitude(double latitude) {
    this.latitude = latitude;
  }

  public void setLongitude(double longitude) {
    this.longitude = longitude;
  }

  public void setCount(int count) {
    this.count = count;
  }
}
