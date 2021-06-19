package com.manech.app.dao;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.manech.app.data.User;

import static androidx.room.OnConflictStrategy.REPLACE;


@Dao
public interface UserDAO {
    @Insert(onConflict = REPLACE)
   public void insert(User user);

    @Update
    public void update(User user);

    @Delete
    public void delete(User user);

   @Query("SELECT * from User WHERE id= :userId")
   public User userById(long userId);

    @Query("SELECT * FROM User WHERE email= :email and password = :password LIMIT 1 ")
    public User userAuth(String email, String password);

    @Query("SELECT * FROM User WHERE email= :email LIMIT 1 ")
    User userByEmail(String email);

    @Query("update User set userName =:userName,urlPicture =:imageUrl, password =:password " +
            "where id= :id and password =:oldPassword")
    public void update(long id, String userName, String imageUrl, String oldPassword, String password);

    @Query("update User set userName =:userName,urlPicture =:imageUrl where id= :id ")
    public void update(long id, String userName, String imageUrl);

    @Query("delete from User where id=:userId")
    void delete(long userId);

    @Query("delete from manechnode where userId=:userId")
    void deleteNodesOf(long userId);
}
