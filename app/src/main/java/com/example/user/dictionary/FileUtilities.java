package com.example.user.dictionary;

import android.content.Context;
import android.database.Cursor;
import android.os.Environment;

import java.io.File;
import java.io.FileWriter;

public class FileUtilities {
    DBUtilities dbUtilities;
    Context context;

    //конструктор
    public FileUtilities(DBUtilities dbUtilities, Context context) {
        this.dbUtilities = dbUtilities;
        this.context = context;
    }//FileUtilities

    //Файл в вормате CSV
    //переносим данные из БазыДанных в Файл
    public void exportDBToFile() {

        File folder = new File(Environment.getExternalStorageDirectory()
                + "/Folder");
        boolean var = false;
        if (!folder.exists())
            var = folder.mkdir();
        System.out.println("" + var);
        final String filename = folder.toString() + "/" + "Dictionary.csv";

        // show waiting screen
//        CharSequence contentTitle = getString(R.string.app_name);
//        final ProgressDialog progDailog = ProgressDialog.show(
//                MailConfiguration.this, contentTitle, "even geduld aub...",
//                true);//please wait
//        final Handler handler = new Handler() {
//            @Override
//            public void handleMessage(Message msg) {
//
//
//
//
//            }
//        };

        new Thread() {
            public void run() {
                try {

                    FileWriter fw = new FileWriter(filename);
                    //получаем курсор с данными для формирования файла
                    String query = "SELECT russians.word_ru, hebrew.word_he, transcriptions.word_tr, " +
                            "russians.gender_ru, hebrew.gender_he, meanings.option, russians.quantity FROM russians " +
                            "INNER JOIN hebrew ON hebrew.id = russians.hebrew_id " +
                            "INNER JOIN meanings ON meanings.id = russians.meaning_id " +
                            "INNER JOIN transcriptions ON transcriptions.id = hebrew.transcription_id";
                    Cursor cursor = dbUtilities.getDb().rawQuery(query, null);

                    fw.append("word_ru");
                    fw.append(',');

                    fw.append("word_he");
                    fw.append(',');

                    fw.append("word_tr");
                    fw.append(',');

                    fw.append("gender_ru");
                    fw.append(',');

                    fw.append("gender_he");
                    fw.append(',');

                    fw.append("option");
                    fw.append(',');

                    fw.append("quantity");
                    fw.append(',');

                    fw.append('\n');

                    if (cursor.moveToFirst()) {
                        do {
                            fw.append(cursor.getString(0));
                            fw.append(',');

                            fw.append(cursor.getString(1));
                            fw.append(',');

                            fw.append(cursor.getString(2));
                            fw.append(',');

                            fw.append(cursor.getString(3));
                            fw.append(',');

                            fw.append(cursor.getString(4));
                            fw.append(',');

                            fw.append(cursor.getString(5));
                            fw.append(',');

                            fw.append(cursor.getString(6));
                            fw.append(',');

                            fw.append('\n');

                        } while (cursor.moveToNext());
                    }
                    if (cursor != null && !cursor.isClosed()) {
                        cursor.close();
                    }

                    // fw.flush();
                    fw.close();

                } catch (Exception e) {
                }
//                handler.sendEmptyMessage(0);
//                progDailog.dismiss();
            }
        }.start();
    }//exportDBToFile

    //переносим данные из Файл в БазуДанных
    public void importFileToDB(){

    }
}//class FileUtilities
