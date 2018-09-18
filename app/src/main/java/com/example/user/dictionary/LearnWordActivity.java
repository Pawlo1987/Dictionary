package com.example.user.dictionary;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LearnWordActivity extends AppCompatActivity {
    Context context;
    int countCursor;
    DBUtilities dbUtilities;
    FileUtilities FileUtilities;
    TextView tvWordsCountForRandomLWAc;
    TextView tvMethodLWAc;
    TextView tvLoopsLWAc;
    Button btnPlWC;
    Button btnMiWC;
    Button btnPlMet;
    Button btnMiMet;
    Button btnPlLo;
    Button btnMiLo;
    List<String> listIdLearnWords; // коллекция id слов для изучения
    int method = 1;
    int loops = 1;
    int wordsCount = 10;
    ActionBar actionBar;                //стрелка НАЗАД
    String[] methodName = {
            "Mix",
            "Choose Russian",
            "Choose Hebrew",
            "Choose Couple",
            "Write Russian",
            "Write Hebrew"
    };

    private Cursor cursor;

    // при запросе с INNER JOIN обязательно указываем в запросе:
    // имя таблицы и имя столбца
    // SELECT таблица.столбец FROM таблица
    //основной запрос
    String mainQuery = "SELECT russians.id, russians.word_ru, hebrew.word_he, transcriptions.word_tr, " +
            "russians.gender_ru, hebrew.gender_he, meanings.option, russians.quantity FROM russians " +
            "INNER JOIN hebrew ON hebrew.id = russians.hebrew_id " +
            "INNER JOIN meanings ON meanings.id = russians.meaning_id " +
            "INNER JOIN transcriptions ON transcriptions.id = hebrew.transcription_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn_word);

        //добавляем actionBar (стрелка сверху слева)
        actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        tvWordsCountForRandomLWAc = findViewById(R.id.tvWordsCountForRandomLWAc);
        tvMethodLWAc = findViewById(R.id.tvMethodLWAc);
        tvLoopsLWAc = findViewById(R.id.tvLoopsLWAc);
        btnPlWC = findViewById(R.id.btnPlWC);
        btnMiWC = findViewById(R.id.btnMiWC);
        btnPlMet = findViewById(R.id.btnPlMet);
        btnMiMet = findViewById(R.id.btnMiMet);
        btnPlLo = findViewById(R.id.btnPlLo);
        btnMiLo = findViewById(R.id.btnMiLo);
        FileUtilities = new FileUtilities(this);

        context = getBaseContext();
        dbUtilities = new DBUtilities(context);
        dbUtilities.open();
        listIdLearnWords = new ArrayList<>();
        cursor = dbUtilities.getDb().rawQuery(mainQuery, null);
        tvMethodLWAc.setText(methodName[method-1]);
        tvLoopsLWAc.setText(String.valueOf(loops));
        tvWordsCountForRandomLWAc.setText(String.valueOf(wordsCount));
        countCursor = cursor.getCount();
    }//onCreate

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnRandomWordsLWAc:
                randomWords();
                break;

            case R.id.btnSelectWordsLWAc:
                selectWords();
                break;

            case R.id.btnPlWC:
                if (wordsCount < countCursor) {
                    wordsCount++;
                    tvWordsCountForRandomLWAc.setText(String.valueOf(wordsCount));
                }
                break;

            case R.id.btnMiWC:
                if (wordsCount > 8) {
                    wordsCount--;
                    tvWordsCountForRandomLWAc.setText(String.valueOf(wordsCount));
                }
                break;

            case R.id.btnPlMet:
                if (method < 6) {
                    method++;
                    tvMethodLWAc.setText(methodName[method-1]);
                }
                break;

            case R.id.btnMiMet:
                if (method > 1) {
                    method--;
                    tvMethodLWAc.setText(methodName[method-1]);
                }
                break;

            case R.id.btnPlLo:
                if (loops < 10) {
                    loops++;
                    tvLoopsLWAc.setText(String.valueOf(loops));
                }
                break;

            case R.id.btnMiLo:
                if (loops > 1) {
                    loops--;
                    tvLoopsLWAc.setText(String.valueOf(loops));
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

    private void selectWords() {
        Intent intent = new Intent(this, SelectLearnWordActivity.class);
        intent.putExtra(
                "method",
                method
        );
        intent.putExtra(
                "loops",
                loops
        );
        startActivity(intent);
    }//selectWords

    //выбор процедуры при рандомном подборе слов для изучения
    private void randomWords() {
        String nextInter;
        listIdLearnWords.clear();

        for (int i = 0; i < wordsCount; i++) {
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
    }//randomWords

    //запуск активности любого метода изучения слов
    private void startMethod() {
        Intent intent = new Intent(this, BackgroundMethodActivity.class);
        intent.putStringArrayListExtra(
                "idList",
                (ArrayList<String>) listIdLearnWords
        );
        intent.putExtra(
                "method",
                method
        );
        intent.putExtra(
                "loops",
                loops
        );
        intent.putExtra(
                "wordsCount",
                wordsCount
        );
        startActivity(intent);
    }//startAnyMethod
}//LearnWordActivity



