package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackDao {
    @Query("SELECT * FROM generated_tracks ORDER BY timestamp DESC")
    fun getAllTracks(): Flow<List<TrackEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(track: TrackEntity): Long

    @Query("DELETE FROM generated_tracks WHERE id = :id")
    suspend fun deleteTrackById(id: Int)

    @Query("DELETE FROM generated_tracks")
    suspend fun clearHistory()
}
