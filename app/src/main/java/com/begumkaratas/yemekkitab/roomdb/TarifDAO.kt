package com.begumkaratas.yemekkitab.roomdb

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.begumkaratas.yemekkitab.model.Tarif
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable

@Dao
interface TarifDAO {

    @Query("Select * from Tarif")
    fun getAll():Flowable<List<Tarif>>
    @Query("Select * from Tarif where id=:id")

    fun findById(id:Int):Tarif

    @Insert
    fun insert(tarif:Tarif):Completable

    @Delete
    fun delete(tarif: Tarif):Completable
}