package com.zybooks.lockedin.storage

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.zybooks.lockedin.model.SessionEntry

@Dao
interface SessionDao {
    @Query("SELECT * FROM sessions")
    fun getAllSessions(): LiveData<List<SessionEntry>>

    @Insert
    fun insertSession(session: SessionEntry)
}
