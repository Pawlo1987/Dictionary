package com.example.user.dictionary;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
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

public class EditWordActivity extends AppCompatActivity {
    private String idRussian, idHebrew, idTranscription;
    private EditText edRussianEWAc;        //строка с русским словом
    private EditText edHebrewEWAc;         //строка с ивритовским словом
    private EditText edTranscriptionEWAc;  //строка с транскрпцией
    private Spinner spGenderRusEWAc;       //сппинер для рода в русском
    private Spinner spGenderHebEWAc;       //сппинер для рода в иврите
    private Spinner spQuantityEWAc;        //сппинер множест. или единств. число
    private Spinner spMeaningEWAc;         //сппинер значения слова в предложении
    DBUtilities dbUtilities;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_word);
        idRussian = getIntent().getStringExtra("id");
        context = getBaseContext();
        dbUtilities = new DBUtilities(context);
        dbUtilities.open();

        edRussianEWAc = findViewById(R.id.edRussianEWAc);
        edHebrewEWAc = findViewById(R.id.edHebrewEWAc);
        edTranscriptionEWAc = findViewById(R.id.edTranscriptionEWAc);
        spGenderRusEWAc = findViewById(R.id.spGenderRusEWAc);
        spGenderHebEWAc = findViewById(R.id.spGenderHebEWAc);
        spQuantityEWAc = findViewById(R.id.spQuantityEWAc);
        spMeaningEWAc = findViewById(R.id.spMeaningEWAc);

        //строим спиннер для рода в русском
        spGenderRusEWAc.setAdapter(
                buildSpinnerAdapter(Arrays.asList("male", "female")));
        //строим спиннер для рода в иврите
        spGenderHebEWAc.setAdapter(
                buildSpinnerAdapter(Arrays.asList("male", "female")));
        //строим спиннер для множ. и ед. числа
        spQuantityEWAc.setAdapter(
                buildSpinnerAdapter(Arrays.asList("one", "many")));
        //строим спиннер для значения в предложении
        spMeaningEWAc.setAdapter(
                buildSpinnerAdapter(
                        Arrays.asList("adjective", "noun", "verb", "binders")));

        //подготовка данных для редактирования
        String mainQuery = "SELECT russian.id, russian.word, hebrew.word, transcription.word, " +
                "russian.gender, hebrew.gender, meaning.option, russian.quantity FROM russian " +
                "INNER JOIN hebrew ON hebrew.id = russian.hebrew_id " +
                "INNER JOIN meaning ON meaning.id = russian.meaning_id " +
                "INNER JOIN transcription ON transcription.id = hebrew.transcription_id " +
                "WHERE russian.id = " + idRussian;
        Cursor cursor = dbUtilities.getDb().rawQuery(mainQuery, null);
        cursor.moveToPosition(0);

        //запоняем строки ввода имеющейся информацией
        edRussianEWAc.setText(cursor.getString(1));
        edHebrewEWAc.setText(cursor.getString(2));
        edTranscriptionEWAc.setText(cursor.getString(3));
        //выставляем спинеры по полученым данным
        if(!spGenderRusEWAc.getSelectedItem().equals(cursor.getString(4)))
        spGenderRusEWAc.setSelection(1);
        if(!spGenderHebEWAc.getSelectedItem().equals(cursor.getString(5)))
        spGenderHebEWAc.setSelection(1);
        if(!spQuantityEWAc.getSelectedItem().equals(cursor.getString(7)))
        spQuantityEWAc.setSelection(1);
        int n = spMeaningEWAc.getCount();
        for (int i = 0; i < n; i++) {
            spMeaningEWAc.setSelection(i);
            if(spMeaningEWAc.getSelectedItem().
                 equals(cursor.getString(6))) break;
        }//for

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
            case R.id.btnOkEWAc:
                updateWordToDB();
                break;

            case R.id.btnCancelEWAc:
                finish();
                break;

        }//switch
    }//onClick

    private void updateWordToDB() {
        boolean fl = false;
        //id из таблицы transcription
        //для записи в другие таблицы
        int transcId = -1;
        //id из таблицы meaning
        //для записи в другие таблицы
        int meaningId = 0;
        String ruWord = edRussianEWAc.getText().toString();
        String ruGender = spGenderRusEWAc.getSelectedItem().toString();
        String heWord = edHebrewEWAc.getText().toString();
        String heGender = spGenderHebEWAc.getSelectedItem().toString();
        String transc = edTranscriptionEWAc.getText().toString();
        String meaning = spMeaningEWAc.getSelectedItem().toString();
        String quantity = spQuantityEWAc.getSelectedItem().toString();

        //проверяем пустые строки
        if ((ruWord.equals("")) || (heWord.equals("")) || (transc.equals(""))) {
            Toast.makeText(context, "Есть незаполненные строки!", Toast.LENGTH_SHORT).show();
            fl = true;
        }

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
            if(transcId == -1) {
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

            //находим значение hebrew_id
            query = "SELECT russian.hebrew_id FROM russian WHERE russian.id = \"" + idRussian+ "\"";
            cursor = dbUtilities.getDb().rawQuery(query, null);
            cursor.moveToPosition(0);
            idHebrew = cursor.getString(0);

            //обновляем запись слова в таблице hebrew
            ContentValues cv = new ContentValues();
            cv.put("word", heWord);
            cv.put("transcription_id", transcId);
            cv.put("gender", heGender);
            cv.put("quantity", quantity);
            cv.put("meaning_id", meaningId);

            // обновляем по id через объект ContentValues(cv), в таблицу
            int successfulUpdate = dbUtilities.updTable("hebrew", cv, idHebrew);

            cv = new ContentValues();
            cv.put("word", ruWord);
            cv.put("hebrew_id", idHebrew);
            cv.put("gender", ruGender);
            cv.put("quantity", quantity);
            cv.put("meaning_id", meaningId);

            // обновляем по id через объект ContentValues(cv), в таблицу
            successfulUpdate = successfulUpdate + dbUtilities.updTable("russian", cv, idRussian);

            if(successfulUpdate == 2) Toast.makeText(context, "Данные успешно обновленны!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(context, ViewDictionaryActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }//if(!fl)
    }//updateWordToDB
}//EditWordActivity
