package com.example.user.dictionary;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

//-------------------------Утилита для работы с файлом---------------------------------------
public class FileUtility {
    //имя файла
    private Context context;
    final String LOG_TAG = "myLogs";
    final String DIR_SD = "Dictionary";
    final String FILENAME_SD = "Dictionary.txt";

    public FileUtility(Context context) {
        this.context = context;
    }//FileUtility

    //получить путь к рабочему каталогу папке
    public String getPathDir() {
        String pathDir = null;
        // проверяем доступность SD
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Toast toast = Toast.makeText(context, "SD-карта не доступна: " + Environment.getExternalStorageState(), Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
            toast.show();
            pathDir = null;
        }

        // получаем путь к SD и добавляем свой каталог к пути
        File sdPath = new File("/storage/emulated/0/myDir");

        // если папка существует
        if (sdPath.exists()) pathDir = sdPath.getPath();

        return pathDir;
    }//getContentsFileInDir

    //создать рабочий каталог
    public void makeDir() {

        // проверяем доступность SD
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Toast toast = Toast.makeText(context, "SD-карта не доступна: " + Environment.getExternalStorageState(), Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
            toast.show();
            return;
        }

        // получаем путь к SD и добавляем свой каталог к пути
        File sdPath = new File(context.getExternalFilesDir(DIR_SD).getPath());

        // создаем каталог, если его нету
        if (!sdPath.exists()) sdPath.mkdirs();

    } // addNewFile

    // является ли внешнее хранилище только для чтения
    private static boolean isReadOnly() {
        String storageState = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED_READ_ONLY.equals(storageState);
    }

    // проверяем есть ли доступ к внешнему хранилищу
    private static boolean isAvailable() {
        String storageState = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(storageState);
    }

    //добавляем новый файл в папку
    public void addNewFile() {
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
            // открываем поток для записи
            BufferedWriter bw = new BufferedWriter(new FileWriter(sdFile));
            // пишем данные
            bw.write("Содержимое файла на SD");
            // закрываем поток
            bw.close();
            Log.d(LOG_TAG, "Файл записан на SD: " + sdFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    } // addNewFile

    //чтение нового файла из папку
    public double[] readNewFile(Context context, String fileName) {

        double[] masNumber = null;

        // формируем объект File, который содержит путь к файлу (в т.ч. имя файла)
        File sdFile = null;

        if (!isAvailable() || isReadOnly()) {
            // если доступа нет
            Toast toast = Toast.makeText(context, "SD-карта не доступна: " + Environment.getExternalStorageState(), Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
            toast.show();
        } else {
            // если доступ есть, то создаем файл в ExternalStorage
            sdFile = new File(context.getExternalFilesDir(DIR_SD), fileName);

            //формируем данные для записи и записываем файл
            try (DataInputStream dis
                         = new DataInputStream(new FileInputStream(sdFile))) {
                int n = dis.available() /  8 /* Double.BYTES */; // размер поделить на количество байтов в типе double
                if (n > 20) n = 20; // ошраничение массива по заданию
                masNumber = new double[n];
                for (int i = 0; i < n; i++) {
                    masNumber[i] = dis.readDouble();
                }//for
                Log.d("myLog", "Файл сохранен " + fileName);
            } catch (Exception ex) {
                Log.d("myLog", "Ошибка при записи файла"+ ex.getMessage());
            } // try-catch
        }//if (!isAvailable() || isReadOnly())

        return masNumber;
    } // readNewFile

}//class FileUtility
