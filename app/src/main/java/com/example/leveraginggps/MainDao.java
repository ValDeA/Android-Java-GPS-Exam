package com.example.leveraginggps;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import static androidx.room.OnConflictStrategy.REPLACE;

import java.util.List;

@Dao
public interface MainDao {
  @Insert(onConflict = REPLACE)
  void insert(MainData mainData);

  @Delete
  void delete(MainData mainData);

  @Delete
  void reset(List<MainData> mainData);

  @Query("UPDATE table_name set latitude = :sLatitude, longitude = :sLongitude, count = :sCount WHERE ID = :sID")
  void update(int sID, double sLatitude, double sLongitude, int sCount);

  @Query("SELECT * FROM table_name")
  List<MainData> getAll();

  @Query("SELECT * FROM table_name WHERE latitude = :sLatitude AND longitude = :sLongitude")
  List<MainData> find(double sLatitude, double sLongitude);
}
