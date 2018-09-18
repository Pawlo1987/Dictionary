package com.example.user.dictionary;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

//-----------Вспомогательный класс для открытия и создания БД----------------------
public class DBHelper extends SQLiteOpenHelper {
    private static String DB_PATH;                  // полный путь к базе данных
    private static String DB_NAME = "dictionary_db.db";
    private static final int DB_VERSION = 1;        // версия базы данных
//    static final String TABLE = "employees"; // название таблицы в бд

//    // названия столбцов
//    static final String COLUMN_SNAME = "surname";
//    static final String COLUMN_NAME = "name";
//    static final String COLUMN_AGE = "age";
//    static final String COLUMN_SAL = "salary";

    private Context myContext;

    DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.myContext = context;

        // получить путь к базе данных
        DB_PATH = context.getFilesDir().getPath() + "/" +DB_NAME;
    } // DBHelper

    @Override
    public void onCreate(SQLiteDatabase db) { }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion,  int newVersion) { }

    // создание БД - копированием в папку databases
    void create_db(){
        InputStream myInput = null;
        OutputStream myOutput = null;

        try {
            File file = new File(DB_PATH);
            if (!file.exists()) {
                this.getReadableDatabase();

                //получаем локальную бд как поток
                myInput = myContext.getAssets().open(DB_NAME);
                // Путь к новой бд
                String outFileName = DB_PATH;

                // Открываем пустую бд
                myOutput = new FileOutputStream(outFileName);

                // побайтово копируем данные
                byte[] buffer = new byte[1024];
                int length;
                while ((length = myInput.read(buffer)) > 0) {
                    myOutput.write(buffer, 0, length);
                }

                myOutput.flush();  // завершение записи, сброс буферов в устройство
                myOutput.close();
                myInput.close();
            } // if
        } catch(IOException ex){
            Log.d("DBHelper", ex.getMessage());
        } // try-catch
    } // create_db

    public SQLiteDatabase open()throws SQLException {
        return SQLiteDatabase.openDatabase(DB_PATH, null, SQLiteDatabase.OPEN_READWRITE);
    }
}//DBHelper
