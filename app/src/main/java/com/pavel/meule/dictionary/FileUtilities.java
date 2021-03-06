package com.pavel.meule.dictionary;

import android.content.Context;
import android.database.Cursor;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

//класс-утилита для работы с файлами

public class FileUtilities {
    DBUtilities dbUtilities;
    Context context;
    File sourceFile;
    File destFile;
    final String LOG_TAG = "myLogs";
    final String DIR_SD = "Dictionary";
    final String FILENAME_SD = "Dictionary.csv";

    //конструктор
    public FileUtilities(Context context) {
        this.context = context;
        dbUtilities = new DBUtilities(context);
        dbUtilities.open();
    }//FileUtilities

    //процедура импорта БД из кореня устройства (из папки Dictionary)
    public void importDB(){
        //объект File откуда копируем, из папки assets область приложения
        destFile = new File(context.getFilesDir().getPath() + "/dictionary.db");
        // проверяем доступность SD
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            return;
        }
        // получаем путь к SD
        File sdPath = Environment.getExternalStorageDirectory();
        // добавляем свой каталог к пути
        sdPath = new File(sdPath.getAbsolutePath() + "/Dictionary");
        // создаем каталог если папка отсутствует
        if (!sdPath.exists()) {
            sdPath.mkdirs();
        }
        //объект File куда копируем, в папку общего доступа,
        //область устройства
        sourceFile = new File(sdPath,"dictionary.db");
        try {
            copyFile(sourceFile,destFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }//importDB

    //процедура экспорта БД в корень устройства (в папку Dictionary)
    public void exportDB(){
        //объект File откуда копируем, из папки assets область приложения
        sourceFile = new File(context.getFilesDir().getPath() + "/dictionary.db");
        // проверяем доступность SD
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            return;
        }
        // получаем путь к SD
        File sdPath = Environment.getExternalStorageDirectory();
        // добавляем свой каталог к пути
        sdPath = new File(sdPath.getAbsolutePath() + "/Dictionary");
        // создаем каталог если папка отсутствует
        if (!sdPath.exists()) {
            sdPath.mkdirs();
        }
        //объект File куда копируем, в папку общего доступа,
        //область устройства
        destFile = new File(sdPath,"dictionary.db");
        try {
            copyFile(sourceFile,destFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }//exportDB

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
    public void importToCSVFromDB() {

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
        sdPath.setWritable(true);
        // если папка не существует
        if (sdPath.exists()) sdPath.mkdirs();
        // формируем объект File, который содержит путь к файлу
        File sdFile = new File(sdPath, FILENAME_SD);
        sdFile.setWritable(true);
        try {
            BufferedWriter bw = new BufferedWriter( new OutputStreamWriter(
                    new FileOutputStream(sdFile), StandardCharsets.UTF_16));
            // открываем поток для записи
//            BufferedWriter bw = new BufferedWriter(new FileWriter(sdFile));
            // пишем данные

            //получаем курсор с данными для формирования файла
            String query = dbUtilities.mainQuery;
            Cursor cursor = dbUtilities.getDb().rawQuery(query, null);

            bw.append("word");
            bw.append('\t');

            bw.append("transcription");
            bw.append('\t');

            bw.append("translation");
            bw.append('\t');

            bw.append("meaning");
            bw.append('\t');

            bw.append("gender");
            bw.append('\t');

            bw.append("quantity");
            bw.append('\t');

            bw.append('\n');

            if (cursor.moveToFirst()) {
                    do {
                    ////////////////////получаем перевод данного слова//////////
                    //получаем перевод
                    String queryTr = "SELECT russian.word_ru FROM translations " +
                            "INNER JOIN russian ON russian.id = translations.russian_id " +
                            "WHERE translations.hebrew_id = \"" + cursor.getString(0) + "\"";
                    Cursor cursorTr = dbUtilities.getDb().rawQuery(queryTr, null);
                    List<String> listTranslations = new ArrayList<>();
                    cursorTr.moveToFirst();
                    do{
                        listTranslations.add(cursorTr.getString(0));
                    } while (cursorTr.moveToNext());
                       int l = listTranslations.size();
                       int j = 0;
                    ///////////////////////////////////////////////////////////////
                    bw.append(cursor.getString(1));
                    bw.append('\t');

                    bw.append(cursor.getString(2));
                    bw.append('\t');
                    //перевод
                    bw.append(listTranslations.get(j));
                    bw.append('\t');

                    bw.append(cursor.getString(3));
                    bw.append('\t');

                    bw.append(cursor.getString(4));
                    bw.append('\t');

                    bw.append(cursor.getString(5));
                    bw.append('\t');

                    bw.append('\n');

                    while(l>1){
                        j++;
                        bw.append('\t');
                        bw.append('\t');
                        bw.append(listTranslations.get(j));
                        bw.append('\t');
                        bw.append('\t');
                        bw.append('\t');
                        bw.append('\t');
                        bw.append('\n');
                        l--;
                    }//if(l>1)
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
    }//importToCSVFromDB
}//class FileUtilities
