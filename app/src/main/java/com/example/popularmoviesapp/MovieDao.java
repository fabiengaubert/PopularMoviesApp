package com.example.popularmoviesapp;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface MovieDao {

    @Query("SELECT * FROM movie")
    List<Movie> loadAllMovies();

    @Query("SELECT * FROM movie WHERE mId = :id")
    Movie getMovie(int id);

    @Insert
    void insertMovie(Movie movie);

    @Update(onConflict = OnConflictStrategy.IGNORE)
    void updateMovie(Movie movie);

    @Delete
    void deleteMovie(Movie movie);
}
