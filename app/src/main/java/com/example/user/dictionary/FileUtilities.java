package com.example.user.dictionary;

import android.content.Context;
import android.database.Cursor;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;

//класс-утилита для работы с файлами
public class FileUtilities {
    DBUtilities dbUtilities;
    Context context;
    final String LOG_TAG = "myLogs";
    final String DIR_SD = "Dictionary";
    final String FILENAME_SD = "Dictionary.csv";

    //конструктор
    public FileUtilities(Context context) {
        this.context = context;
        dbUtilities = new DBUtilities(context);
        dbUtilities.open();
    }//FileUtilities


    //копирование файла
    //https://ru.stackoverflow.com/questions/442228/%D0%9A%D0%BE%D0%BF%D0%B8%D1%80%D0%BE%D0%B2%D0%B0%D1%82%D1%8C-%D0%B8-%D0%B2%D1%81%D1%82%D0%B0%D0%B2%D0%B8%D1%82%D1%8C-%D1%84%D0%B0%D0%B9%D0%BB-%D0%B2-android
    public void copyFile(File sourceFile, File destFile) throws IOException {
        if(!destFile.exists()) {
            destFile.createNewFile();
        }

        FileChannel source = null;
        FileChannel destination = null;

        try {
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destFile).getChannel();
            destination.transferFrom(source, 0, source.size());
            Toast.makeText(context, "*.db File Copy!", Toast.LENGTH_SHORT).show();
        }
        finally {
            if(source != null) {
                source.close();
            }
            if(destination != null) {
                destination.close();
            }
        }//try-finally
    }//copyFile

    //Файл в вормате CSV
    //переносим данные из БазыДанных в Файл
    public void importToFileFromDB() {

        // проверяем доступность SD
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            Log.d(LOG_TAG, "SD-карта не доступна: " + Environment.getExternalStorageState());
            return;
        }
        // получаем путь к SD
        File sdPath = Environment.getExternalStorageDirectory();
        // добавляем свой каталог к пути
        sdPath = new File(sdPath.getAbsolutePath() + "/" + DIR_SD);
        // создаем каталог
        sdPath.mkdirs();
        // формируем объект File, который содержит путь к файлу
        File sdFile = new File(sdPath, FILENAME_SD);
        try {
            BufferedWriter bw = new BufferedWriter( new OutputStreamWriter(
                    new FileOutputStream(sdFile), StandardCharsets.UTF_16));
            // открываем поток для записи
//            BufferedWriter bw = new BufferedWriter(new FileWriter(sdFile));
            // пишем данные

            //получаем курсор с данными для формирования файла
            String query = "SELECT russians.word_ru, hebrew.word_he, transcriptions.word_tr, " +
                    "russians.gender_ru, hebrew.gender_he, meanings.option, russians.quantity FROM russians " +
                    "INNER JOIN hebrew ON hebrew.id = russians.hebrew_id " +
                    "INNER JOIN meanings ON meanings.id = russians.meaning_id " +
                    "INNER JOIN transcriptions ON transcriptions.id = hebrew.transcription_id";
            Cursor cursor = dbUtilities.getDb().rawQuery(query, null);

            bw.append("word");
            bw.append('\t');

            bw.append("translation");
            bw.append('\t');

            bw.append("transcription");
            bw.append('\t');

            bw.append("gender_w");
            bw.append('\t');

            bw.append("gender_t");
            bw.append('\t');

            bw.append("option");
            bw.append('\t');

            bw.append("quantity");
            bw.append('\t');

            bw.append('\n');

            if (cursor.moveToFirst()) {
                do {
                    bw.append(cursor.getString(0));
                    bw.append('\t');

                    bw.append(cursor.getString(1));
                    bw.append('\t');

                    bw.append(cursor.getString(2));
                    bw.append('\t');

                    bw.append(cursor.getString(3));
                    bw.append('\t');

                    bw.append(cursor.getString(4));
                    bw.append('\t');

                    bw.append(cursor.getString(5));
                    bw.append('\t');

                    bw.append(cursor.getString(6));
                    bw.append('\t');

                    bw.append('\n');

                } while (cursor.moveToNext());
            }
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            // закрываем поток
            bw.close();
            Log.d(LOG_TAG, "Файл записан на SD: " + sdFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        Toast.makeText(context, "*.CSV File created!", Toast.LENGTH_SHORT).show();
    }//importToFileFromDB

    //переносим данные из Файл в БазуДанных
    public void exporToDBFromFile() {

    }//exporToDBFromFile
}//class FileUtilities
