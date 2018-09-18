package com.example.user.dictionary;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

//активность параметров каждого авторезированого профиля
public class ProfileParametersActivity extends AppCompatActivity {
    Context context;
    int countCursor;
    DBUtilities dbUtilities;
    FileUtilities FileUtilities;
    TextView tvWordsCountForRandomPPAc;
    TextView tvLoopsPPAc;
    TextView tvNextWordLearnPPAc;
    Button btnPlWCPPAc;
    Button btnMiWCPPAc;
    Button btnPlLoPPAc;
    Button btnMiLoPPAc;
    CheckBox cbChooseRussianMethodPPAc;
    CheckBox cbChooseHebrewMethodPPAc;
    CheckBox cbChooseCoupleMethodPPAc;
    CheckBox cbWriteRussianMethodPPAc;
    CheckBox cbWriteHebrewMethodPPAc;
    List<String> listIdLearnWords; // коллекция id слов для изучения
    List<Integer> listMethods; //коллекция выбранных методов
    int idProfile;
    int idWordLastLearn;    //id последнего изучаемого слова оно же первое
    int wordsCount;         //количество слов в цикле
    int countLoopsReserved; //количество циклов для изучения порции слов
    ActionBar actionBar;                //стрелка НАЗАД

    private Cursor cursor;

    // при запросе с INNER JOIN обязательно указываем в запросе:
    // имя таблицы и имя столбца
    // SELECT таблица.столбец FROM таблица
    //основной запрос
    String mainQuery = "SELECT hebrew.id, hebrew.word_he, transcriptions.word_tr, " +
            "meanings.option, gender.option, quantity.option FROM hebrew " +
            "INNER JOIN transcriptions ON transcriptions.id = hebrew.transcription_id " +
            "INNER JOIN meanings ON meanings.id = hebrew.meaning_id " +
            "INNER JOIN gender ON gender.id = hebrew.gender_id " +
            "INNER JOIN quantity ON quantity.id = hebrew.quantity_id ORDER BY hebrew.word_he";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_parameters);

        //добавляем actionBar (стрелка сверху слева)
        actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        tvWordsCountForRandomPPAc = findViewById(R.id.tvWordsCountForRandomPPAc);
        tvLoopsPPAc = findViewById(R.id.tvLoopsPPAc);
        tvNextWordLearnPPAc = findViewById(R.id.tvNextWordLearnPPAc);
        btnPlWCPPAc = findViewById(R.id.btnPlWCPPAc);
        btnMiWCPPAc = findViewById(R.id.btnMiWCPPAc);
        btnPlLoPPAc = findViewById(R.id.btnPlLoPPAc);
        btnMiLoPPAc = findViewById(R.id.btnMiLoPPAc);
        cbChooseRussianMethodPPAc = findViewById(R.id.cbChooseRussianMethodPPAc);
        cbChooseHebrewMethodPPAc = findViewById(R.id.cbChooseHebrewMethodPPAc);
        cbChooseCoupleMethodPPAc = findViewById(R.id.cbChooseCoupleMethodPPAc);
        cbWriteRussianMethodPPAc = findViewById(R.id.cbWriteRussianMethodPPAc);
        cbWriteHebrewMethodPPAc = findViewById(R.id.cbWriteHebrewMethodPPAc);
        cbChooseRussianMethodPPAc.setChecked(true);
        cbChooseHebrewMethodPPAc.setChecked(true);
        cbChooseCoupleMethodPPAc.setChecked(true);
        cbWriteRussianMethodPPAc.setChecked(true);
        cbWriteHebrewMethodPPAc.setChecked(true);
        listMethods = new ArrayList<>();
        FileUtilities = new FileUtilities(this);

        context = getBaseContext();
        dbUtilities = new DBUtilities(context);
        dbUtilities.open();
        idProfile = getIntent().getIntExtra("idProfile", 0);
        listIdLearnWords = new ArrayList<>();
        cursor = dbUtilities.getDb().rawQuery(mainQuery, null);
        countCursor = cursor.getCount();

        //получаем параметры авторизированого пользователя
        String query = "SELECT profile.id_word_last_learn, profile.word_count_in_loop, " +
                "profile.count_loops_reserved FROM profile " +
                "WHERE profile.id = \"" + idProfile + "\"";
        Cursor cursor = dbUtilities.getDb().rawQuery(query, null);
        cursor.moveToPosition(0);
        //id последнего изучаемого слова оно же первое
        idWordLastLearn = cursor.getInt(0);
        //количество слов в цикле
        wordsCount = cursor.getInt(1);
        tvWordsCountForRandomPPAc.setText(String.valueOf(wordsCount));
        //количество циклов для изучения порции слов
        countLoopsReserved = cursor.getInt(2);
        tvLoopsPPAc.setText(String.valueOf(countLoopsReserved));
        //получаем слово с которого начинается изучение
        query = "SELECT hebrew.word_he FROM hebrew " +
                "WHERE hebrew.id = \"" + String.valueOf(idWordLastLearn) + "\"";
        cursor = dbUtilities.getDb().rawQuery(query, null);
        cursor.moveToPosition(0);
        //id последнего изучаемого слова оно же первое
        tvNextWordLearnPPAc.setText(cursor.getString(0));
    }//onCreate

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnStartLearnPPAc:
                startLearn();
                break;

            case R.id.btnSelectNextLearnWordPPAc:
                selectNextWord();
                break;

            case R.id.btnPlWCPPAc:
                if (wordsCount < countCursor) {
                    wordsCount++;
                    tvWordsCountForRandomPPAc.setText(String.valueOf(wordsCount));
                }
                break;

            case R.id.btnMiWCPPAc:
                if (wordsCount > 8) {
                    wordsCount--;
                    tvWordsCountForRandomPPAc.setText(String.valueOf(wordsCount));
                }
                break;

            case R.id.btnPlLoPPAc:
                if (countLoopsReserved < 10) {
                    countLoopsReserved++;
                    tvLoopsPPAc.setText(String.valueOf(countLoopsReserved));
                }
                break;

            case R.id.btnMiLoPPAc:
                if (countLoopsReserved > 1) {
                    countLoopsReserved--;
                    tvLoopsPPAc.setText(String.valueOf(countLoopsReserved));
                }
                break;
        }//switch
    }//onClick

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {

            //обработчик actionBar (стрелка сверху слева)
            case android.R.id.home:
                setResult(RESULT_CANCELED);
                onBackPressed();
                return true;
        }//switch
        return super.onOptionsItemSelected(item);
    }//onOptionsItemSelected

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_learn_word, menu);
        return true;
    }//onCreateOptionsMenu

    private void selectNextWord() {
        listMethods.clear();
        if (cbChooseRussianMethodPPAc.isChecked()) listMethods.add(1);
        if (cbChooseHebrewMethodPPAc.isChecked()) listMethods.add(2);
        if (cbChooseCoupleMethodPPAc.isChecked()) listMethods.add(3);
        if (cbWriteRussianMethodPPAc.isChecked()) listMethods.add(4);
        if (cbWriteHebrewMethodPPAc.isChecked()) listMethods.add(5);
        //проверка на случай невыбранного метода
        if (listMethods.size() > 0) {
            Intent intent = new Intent(this, SelectLearnWordActivity.class);
            intent.putIntegerArrayListExtra(
                    "listMethods", (ArrayList<Integer>) listMethods
            );
            intent.putExtra(
                    "loops",
                    countLoopsReserved
            );
            intent.putExtra(
                    "flAuthorization",
                    true
            );
            startActivity(intent);
        } else {
            Toast.makeText(this, "Check minimum one method", Toast.LENGTH_SHORT).show();
        }//if(listMethods.size()>0)
    }//selectWords

    //старт изучения обучения
    private void startLearn() {
        listMethods.clear();
        if (cbChooseRussianMethodPPAc.isChecked()) listMethods.add(1);
        if (cbChooseHebrewMethodPPAc.isChecked()) listMethods.add(2);
        if (cbChooseCoupleMethodPPAc.isChecked()) listMethods.add(3);
        if (cbWriteRussianMethodPPAc.isChecked()) listMethods.add(4);
        if (cbWriteHebrewMethodPPAc.isChecked()) listMethods.add(5);
        //проверка на случай невыбранного метода
        if (listMethods.size() > 0) {
            //проверка достаточно ли у вас слов в словоре для изучения
            if (countCursor < wordsCount) {
                Toast.makeText(this, "You need add more words to DB!", Toast.LENGTH_SHORT).show();
            } else {
                String nextInter;
                listIdLearnWords.clear();
                //количество слов в БД
                String query = "SELECT hebrew.id FROM hebrew ";
                cursor = dbUtilities.getDb().rawQuery(query, null);
                cursor.moveToPosition(0);

                int n;
                int s = idWordLastLearn;
                //проверяем не вылезли ли мы за рамки количества слова
                if ((s + wordsCount) > (cursor.getCount())) {
                    s = cursor.getCount() - wordsCount;
                    n = cursor.getCount();
                    idWordLastLearn = cursor.getInt(0);
                } else {
                    n = s + wordsCount;
                    idWordLastLearn = n;
                }//if ((s + wordsCount) > (cursor.getCount()))

                //подготавливаем коллекцию для изучения слов
                for (int i = s; i < n; i++) {
                    //проверка повторяющегося варианта
                    while (true) {
                        nextInter = String.valueOf(Utils.getRandom(0, countCursor));
                        if (!listIdLearnWords.contains(nextInter)) break;
                    }//while
                    listIdLearnWords.add(nextInter);
                }//for
                //перемешать коллекцию выбранных слов
                Collections.shuffle(listIdLearnWords);
                startMethod();
            } //if(countCursor<wordsCount)
        } else {
            Toast.makeText(this, "Check minimum one method", Toast.LENGTH_SHORT).show();
        }//if(listMethods.size()>0)
    }//randomWords

    //запуск активности любого метода изучения слов
    private void startMethod() {
        dbUtilities.updTableProfile(String.valueOf(idProfile), idWordLastLearn, wordsCount, countLoopsReserved);
        //получаем слово с которого начинается изучение
        String query = "SELECT hebrew.word_he FROM hebrew " +
                "WHERE hebrew.id = \"" + String.valueOf(idWordLastLearn) + "\"";
        cursor = dbUtilities.getDb().rawQuery(query, null);
        cursor.moveToPosition(0);
        //id последнего изучаемого слова оно же первое
        tvNextWordLearnPPAc.setText(cursor.getString(0));
        Intent intent = new Intent(this, BackgroundMethodActivity.class);
        intent.putStringArrayListExtra(
                "idList",
                (ArrayList<String>) listIdLearnWords
        );
        intent.putIntegerArrayListExtra(
                "listMethods", (ArrayList<Integer>) listMethods
        );
        intent.putExtra(
                "loops",
                countLoopsReserved
        );
        intent.putExtra(
                "wordsCount",
                wordsCount
        );
        startActivity(intent);
    }//startAnyMethod
}//ProfileParametersActivity
