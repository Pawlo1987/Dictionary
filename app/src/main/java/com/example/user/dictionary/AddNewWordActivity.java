package com.example.user.dictionary;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
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
    ActionBar actionBar;                //стрелка НАЗАД

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_word);

        //добавляем actionBar (стрелка сверху слева)
        actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id){
            //обработчик actionBar (стрелка сверху слева)
            case android.R.id.home:
                setResult(RESULT_CANCELED);
                onBackPressed();
                return true;
        }//switch
        return super.onOptionsItemSelected(item);
    }//onOptionsItemSelected

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
            case R.id.btnOkANWAc:
                addNewWordToDB();
                break;

            case R.id.btnCancelANWAc:
                finish();
                break;

        }//switch
    }//onClick

    private void addNewWordToDB() {
        boolean fl = false;
        //id из таблицы transcription
        //для записи в другие таблицы
        int transcId = 0;
        int hebrewId = 0;
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
            Toast.makeText(context, "Found empty lines!", Toast.LENGTH_SHORT).show();
            fl = true;
        }else{
            //проверяем на повторение попарно двух столбцов
            //слово по русски и слово на иврите
            //получаем курсор данных из БД
            String query = "SELECT russians.word_ru, hebrew.word_he FROM russians " +
                    "INNER JOIN hebrew ON hebrew.id = russians.hebrew_id";
            Cursor cursor = dbUtilities.getDb().rawQuery(query, null);
            int n = cursor.getCount();
            for (int i = 0; i < n; i++) {
                cursor.moveToPosition(i);
                if ((ruWord.equals(cursor.getString(0))) && (heWord.equals(cursor.getString(1)))) {
                    Toast.makeText(context, "Found a match! Correct!", Toast.LENGTH_SHORT).show();
                    fl = true;
                    break;
                }
            }//for
        }//if-else

        //если нет пустых строк и повторяющихся слов записываем новое слово
        if(!fl) {
            //проверяем на повторение транскипцию
            //получаем курсор данных из БД
            String query = "SELECT transcriptions.id FROM transcriptions WHERE transcriptions.word_tr = \"" + transc + "\"";
            Cursor cursor = dbUtilities.getDb().rawQuery(query, null);
            if(cursor.getCount() > 0){
                cursor.moveToPosition(0);
                Log.d("ididid", "tr_id"+cursor.getString(0));
                transcId = Integer.parseInt(cursor.getString(0));
            }else{
                //записываем новое слово в таблицу transcriptions
                dbUtilities.insertIntoTranscriptions(transc);
                //получаем id последней записи transcriptions
                query = "SELECT transcriptions.id FROM transcriptions WHERE transcriptions.word_tr = \"" + transc + "\"";
                cursor = dbUtilities.getDb().rawQuery(query, null);
                cursor.moveToPosition(0);
                Log.d("ididid", "tr_id"+cursor.getString(0));
                transcId = Integer.parseInt(cursor.getString(0));
            }//if-else

            //определяем id из таблицы meaning
            //получаем курсор данных из БД
            query = "SELECT meanings.id FROM meanings WHERE meanings.option = \"" + meaning+ "\"";
            cursor = dbUtilities.getDb().rawQuery(query, null);
            cursor.moveToPosition(0);
            meaningId = Integer.parseInt(cursor.getString(0));

            //записываем новое слово в таблицу hebrew
            dbUtilities.insertIntoHebrew(heWord, transcId, heGender, meaningId);

            //получаем id последней записи hebrew
            query = "SELECT hebrew.id FROM hebrew WHERE hebrew.word_he = \"" + heWord + "\"";
            cursor = dbUtilities.getDb().rawQuery(query, null);
            cursor.moveToPosition(0);
            Log.d("ididid", "he_id"+cursor.getString(0));
            hebrewId = Integer.parseInt(cursor.getString(0));

            //записываем новое слово в таблицу russian
            dbUtilities.insertIntoRussians(ruWord, hebrewId, ruGender, quantity, meaningId);

            Toast.makeText(context, "New word additionally!", Toast.LENGTH_SHORT).show();
            finish();
        }//if(!fl)
    }//addNewWordToDB
}//AddNewWordActivity
