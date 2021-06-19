package com.manech.app.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.manech.app.data.ManechNode;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface ManechNodeDAO {
    @Insert(onConflict = REPLACE)
    public void insert(ManechNode node);


    @Update
    public void update(ManechNode node);

    @Query("DELETE FROM manechnode where userId = :userId")
    public void deleteAll(long userId);

    @Query("SELECT * from ManechNode WHERE id= :nodeId")
    public ManechNode nodeById(long nodeId);

    @Query("SELECT * from ManechNode WHERE userId= :userId")
    public List<ManechNode> nodeByUser(long userId);

    @Query("SELECT * FROM manechnode WHERE apIP LIKE :ip limit 1")
    public ManechNode nodeByIp(String ip);


    @Query("SELECT * FROM manechnode " +
            " WHERE name LIKE :search OR " +
            " apIP LIKE :search  OR " +
            " description like :search OR " +
            " apName like :search "
    )
    public List<ManechNode> find(String search);

    @Query("delete from manechnode where id=:node")
    void delete(long node);

    @Query("select * from manechnode where id=:nodeId")
    ManechNode getNode(long nodeId);
}
