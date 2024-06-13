package com.example.thi_ph33065

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.Update
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [SanPhamModel::class], version = 2)
abstract class SanPhamDB : RoomDatabase() {
    abstract fun sanphamDao(): SanPhamDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE SanPhamModel ADD COLUMN image TEXT")
            }
        }
    }
}
@Dao
interface SanPhamDao {
    @Query("SELECT * FROM SanPhamModel")
    fun getAll(): List<SanPhamModel>

//      @Query("SELECT * FROM SanPhamModel WHERE uid IN (:sanphamIds)")
//      fun getSanPhamById(sanphamIds: IntArray): List<SanPhamModel>
    @Query("SELECT * FROM SanPhamModel WHERE uid = :sanPhamId")
    fun getSanPhamById(sanPhamId: Int): SanPhamModel?

    @Insert
    fun insert(sanpham: SanPhamModel)

    @Update
    fun update(sanpham: SanPhamModel)

    @Delete
    fun delete(sanpham: SanPhamModel)
}