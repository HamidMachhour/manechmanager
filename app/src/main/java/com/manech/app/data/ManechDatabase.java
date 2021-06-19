package com.manech.app.data;


import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.manech.app.dao.ManechNodeDAO;
import com.manech.app.dao.UserDAO;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Database(entities = {User.class,ManechNode.class}, version = 5)
public abstract class ManechDatabase extends RoomDatabase {
    private static final String DATABASE_NAME = "databaseName";
    private static ManechDatabase instance;
    public abstract UserDAO userDAO();
    public abstract ManechNodeDAO manechNodeDAO();

    public static synchronized ManechDatabase getInstance(Context context){
        if(instance == null){
            instance=Room.databaseBuilder(context.getApplicationContext(),
                    ManechDatabase.class, DATABASE_NAME)
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallback)
                    .build();
        }
        return instance;
    }

    private static RoomDatabase.Callback roomCallback=new RoomDatabase.Callback(){
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            populateDb();
        }
    };

    private static void populateDb(){
        Executor executor = Executors.newSingleThreadExecutor();
        ManechNodeDAO nodeDAO=instance.manechNodeDAO();
        UserDAO userDAO =instance.userDAO();

        executor.execute(() -> {
            userDAO.insert(new User("hamid", "12345678","hamid.machhour@gmail.com",""));

            nodeDAO. insert(new ManechNode(1,"manech1","no description","admin","12345678",
                    0,0,"Manech","12345678","192.168.4.1","255.255.255.0","192.168.4.1",
                    "","","","",""));
            nodeDAO. insert(new ManechNode(1,"manech2","no description","admin","12345678",
                    0,0,"Manech","12345678","192.168.4.1","255.255.255.0","192.168.4.1",
                    "","","","",""));
            nodeDAO. insert(new ManechNode(1,"manech3","no description","admin","12345678",
                    0,0,"Manech","12345678","192.168.4.1","255.255.255.0","192.168.4.1",
                    "","","","",""));;
        });
    }


}