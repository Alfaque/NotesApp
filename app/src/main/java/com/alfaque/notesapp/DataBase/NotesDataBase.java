package com.alfaque.notesapp.DataBase;

import android.content.Context;

import com.alfaque.notesapp.Model.Note;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Note.class}, version = 1)
public abstract class NotesDataBase extends RoomDatabase {

    public abstract NotesDao notesDao();

    private static NotesDataBase notesDataBase = null;

    public static synchronized NotesDataBase getInstance(Context context) {
        if (notesDataBase == null) {
            notesDataBase = Room.databaseBuilder(
                    context,
                    NotesDataBase.class,
                    "notes_db")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return notesDataBase;
    }

}
