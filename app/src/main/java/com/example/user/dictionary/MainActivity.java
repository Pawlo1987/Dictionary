package com.example.user.dictionary;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    FileUtilities FileUtilities;
    File sourceFile;
    File destFile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ////////////////////////////////////////////////////////
        //объект File откуда копируем, из папки assets область приложения
        sourceFile = new File(this.getFilesDir().getPath() + "/dictionary.db");
        // проверяем доступность SD
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            return;
        }
        // получаем путь к SD
        File sdPath = Environment.getExternalStorageDirectory();
        // добавляем свой каталог к пути
        sdPath = new File(sdPath.getAbsolutePath() + "/Dictionary");
        // создаем каталог
        sdPath.mkdirs();
        //объект File куда копируем, в папку общего доступа,
        //область устройства
        destFile = new File(sdPath,"dictionary.db");

        ///////////////////////////////////////////////////
        FileUtilities = new FileUtilities(this);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnAddNewWord:
                addNewWord();
                break;

            case R.id.btnViewDictionary:
                viewDictionary();
                break;

            case R.id.btnImportToFileFromDBMA:

                try {
                    FileUtilities.copyFile(sourceFile,destFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //создаем рабочий каталог
//                FileUtilities.importToFileFromDB();
                break;
        }//switch

    }//onClick

    private void addNewWord() {
        Intent intent = new Intent(this, AddNewWordActivity.class);
        startActivity(intent);
    }

    private void viewDictionary() {
        Intent intent = new Intent(this, ViewDictionaryActivity.class);
        startActivity(intent);
    }

} //MainActivity
