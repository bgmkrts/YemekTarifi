package com.begumkaratas.yemekkitab.roomdb
import androidx.room.Database
import androidx.room.RoomDatabase
import com.begumkaratas.yemekkitab.model.Tarif
import com.begumkaratas.yemekkitab.roomdb.TarifDAO

@Database (entities = [Tarif::class], version = 1)
abstract class TarifDatabase : RoomDatabase() {
    abstract fun tarifDao(): TarifDAO
}

