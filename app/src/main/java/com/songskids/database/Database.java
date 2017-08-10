package com.songskids.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.songskids.models.Songs;

import java.io.IOException;
import java.util.ArrayList;

public class Database {

    private static String DB_PATH = "/data/data/com.songskids/databases/";
    private static String DB_NAME = "database.sqlite";
    private static final int DB_VERSION = 1;
    private DatabaseHelper db;

    public Database(Context context) {
        db = new DatabaseHelper(context);
        try {
            db.createDataBase();
        } catch (IOException ioe) {
            throw new Error("Unable to create database");
        }
    }

    private void openDb() {
        try {
            db.openDataBase();
        } catch (SQLException sqle) {
            db.close();
            throw sqle;
        }
    }

    public ArrayList<Songs> getSongs() {
        ArrayList<Songs> listQuestions = new ArrayList<Songs>();
        openDb();
        SQLiteDatabase data = db.getReadableDatabase();
        Cursor cursor;
        cursor = data.rawQuery("SELECT * FROM tb_songs", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Songs secsions = new Songs();
            int _id = cursor.getInt(0);
            String name = cursor.getString(1);
            String youtubeid = cursor.getString(2);

            secsions.setId("" + _id);
            secsions.setNames(name);
            secsions.setYoutubeid(youtubeid);
            listQuestions.add(secsions);
            cursor.moveToNext();
        }
        data.close();
        db.close();
        return listQuestions;
    }

    public long insert(Songs songs) {
        SQLiteDatabase data = db.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put("id", songs.getId());
        values.put("names", songs.getNames());
        values.put("youtubeid", songs.getYoutubeid());
        return data.insert("tb_songs", null, values);
    }

    public long remove(String id) {
        SQLiteDatabase data = db.getReadableDatabase();
        ContentValues values = new ContentValues();
        return data.delete("tb_songs", "id=" + "'" + id
                + "'", null);
    }
}
