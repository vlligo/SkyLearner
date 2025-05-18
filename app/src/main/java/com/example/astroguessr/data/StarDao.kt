package com.example.astroguessr.data

import androidx.room.Dao
import androidx.room.Query

@Dao
interface StarDao {
    @Query("SELECT * FROM stars WHERE id = :id")
    suspend fun getStarById(id: Int): Star?

    @Query("SELECT * FROM stars WHERE id != :excludeId ORDER BY RANDOM() LIMIT :count")
    suspend fun getRandomStars(count: Int, excludeId: Int): List<Star>

    @Query("SELECT * FROM stars WHERE con = :constellation ORDER BY mag")
    suspend fun getStarsByConstellation(constellation: String): List<Star>

    @Query("""
        SELECT * FROM stars 
        WHERE 
            ABS(ra - :ra) < :radius AND 
            ABS(dec - :dec) < :radius AND
            id != :excludeId
        ORDER BY mag
        LIMIT :count
    """)
    suspend fun getStarsNear(
        ra: Double,
        dec: Double,
        radius: Double,
        excludeId: Int,
        count: Int
    ): List<Star>
}