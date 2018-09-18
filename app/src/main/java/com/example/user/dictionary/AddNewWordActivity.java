package com.example.user.dictionary;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;

public class AddNewWordActivity extends AppCompatActivity {
    private EditText edRussianANWAc;        //строка с русским словом
    private EditText edHebrewANWAc;         //строка с ивритовским словом
    private EditText edTranscriptionANWAc;  //строка с транскрпцией
    private Spinner spGenderRusANWAc;       //сппинер для рода в русском
    private Spinner spGenderHebANWAc;       //сппинер для рода в иврите
    private Spinner spQuantityANWAc;        //сппинер множест. или единств. число
    private Spinner spMeaningANWAc;         //сппинер значения слова в предложении
    DBUtilities dbUtilities;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_word);

        context = getBaseContext();
        dbUtilities = new DBUtilities(context);
        dbUtilities.open();
        edRussianANWAc = findViewById(R.id.edRussianANWAc);
        edHebrewANWAc = findViewById(R.id.edHebrewANWAc);
        edTranscriptionANWAc = findViewById(R.id.edTranscriptionANWAc);
        spGenderRusANWAc = findViewById(R.id.spGenderRusANWAc);
        spGenderHebANWAc = findViewById(R.id.spGenderHebANWAc);
        spQuantityANWAc = findViewById(R.id.spQuantityANWAc);
        spMeaningANWAc = findViewById(R.id.spMeaningANWAc);

        //строим спиннер для рода в русском
        spGenderRusANWAc.setAdapter(
                buildSpinnerAdapter(Arrays.asList("male", "female")));
        //строим спиннер для рода в иврите
        spGenderHebANWAc.setAdapter(
                buildSpinnerAdapter(Arrays.asList("male", "female")));
        //строим спиннер для множ. и ед. числа
        spQuantityANWAc.setAdapter(
                buildSpinnerAdapter(Arrays.asList("one", "many")));
        //строим спиннер для значения в предложении
        spMeaningANWAc.setAdapter(
                buildSpinnerAdapter(
                        Arrays.asList("adjective", "noun", "verb", "binders")));
    }//onCreate

    //строим адаптер для Spinner
    private ArrayAdapter<String> buildSpinnerAdapter(List<String> spList) {
        ArrayAdapter<String> spAdapter;  //Адаптер для спинера
        //создание адаптера для спинера
        spAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_item,
                spList
        );
        // назначение адапетра для списка
        spAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return spAdapter;
    }//buildCitySpinner

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnConfirmANWAc:
                addNewWordToDB();
                break;

//            case R.id.btnViewDictionary:
//                viewDictionary();
//                break;

        }//switch
    }//onClick

    private void addNewWordToDB() {
        boolean fl = false;
        //id из таблицы transcription
        //для записи в другие таблицы
        int transcId = 0;
        //id из таблицы meaning
        //для записи в другие таблицы
        int meaningId = 0;
        String ruWord = edRussianANWAc.getText().toString();
        String ruGender = spGenderRusANWAc.getSelectedItem().toString();
        String heWord = edHebrewANWAc.getText().toString();
        String heGender = spGenderHebANWAc.getSelectedItem().toString();
        String transc = edTranscriptionANWAc.getText().toString();
        String meaning = spMeaningANWAc.getSelectedItem().toString();
        String quantity = spQuantityANWAc.getSelectedItem().toString();

        //проверяем пустые строки
        if ((ruWord.equals("")) || (heWord.equals("")) || (transc.equals(""))) {
            Toast.makeText(context, "Есть незаполненные строки!", Toast.LENGTH_SHORT).show();
            fl = true;
        }else{
            //проверяем на повторение попарно двух столбцов
            //слово по русски и слово на иврите
            //получаем курсор данных из БД
            String query = "SELECT russian.word, hebrew.word FROM russian " +
                    "INNER JOIN hebrew ON hebrew.id = russian.hebrew_id";
            Cursor cursor = dbUtilities.getDb().rawQuery(query, null);
            int n = cursor.getCount();
            for (int i = 0; i < n; i++) {
                cursor.moveToPosition(i);
                if ((ruWord.equals(cursor.getString(0))) && (heWord.equals(cursor.getString(1)))) {
                    Toast.makeText(context, "Найденно совпадение! Подкорректируйте!", Toast.LENGTH_SHORT).show();
                    fl = true;
                    break;
                }
            }//for
        }//if-else

        //если все нормально записываем новое слово
        if(!fl) {
            //проверяем на повторение транскипцию
            //получаем курсор данных из БД
            String query = "SELECT transcription.id, transcription.word FROM transcription";
            Cursor cursor = dbUtilities.getDb().rawQuery(query, null);
            int n = cursor.getCount();
            for (int i = 0; i < n; i++) {
                cursor.moveToPosition(i);
                if (transc.equals(cursor.getString(1))) {
                    transcId = Integer.parseInt(cursor.getString(0));
                    break;
                }
            }//for
            if(transcId == 0) {
                ContentValues cv = new ContentValues();
                cv.put("word", transc);
                //добваить данные через объект ContentValues(cv), в таблицу
                dbUtilities.insertInto(cv, "transcription");
                transcId = n+1;
            }//if(transcId == 0)

            //определяем id из таблицы meaning
            //получаем курсор данных из БД
            query = "SELECT meaning.id FROM meaning WHERE meaning.option = \"" + meaning+ "\"";
            cursor = dbUtilities.getDb().rawQuery(query, null);
            cursor.moveToPosition(0);
            meaningId = Integer.parseInt(cursor.getString(0));

            //записываем новое слово в таблицу hebrew
            ContentValues cv = new ContentValues();

            cv.put("word", heWord);
            cv.put("transcription_id", transcId);
            cv.put("gender", ruGender);
            cv.put("quantity", quantity);
            cv.put("meaning_id", meaningId);
            //добваить данные через объект ContentValues(cv), в таблицу
            dbUtilities.insertInto(cv, "hebrew");

            query = "SELECT hebrew.id FROM hebrew";
            cursor = dbUtilities.getDb().rawQuery(query, null);
            n = cursor.getCount();

            //записываем новое слово в таблицу russian
            cv = new ContentValues();

            cv.put("word", ruWord);
            cv.put("hebrew_id", n);
            cv.put("gender", heGender);
            cv.put("quantity", quantity);
            cv.put("meaning_id", meaningId);
            //добваить данные через объект ContentValues(cv), в таблицу
            dbUtilities.insertInto(cv, "russian");
            Toast.makeText(context, "Новое слово внесенно", Toast.LENGTH_SHORT).show();
            finish();
        }//if(!fl)
    }//addNewWordToDB
}//AddNewWordActivity
