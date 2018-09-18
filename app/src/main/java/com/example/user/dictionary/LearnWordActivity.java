package com.example.user.dictionary;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LearnWordActivity extends AppCompatActivity {
    Context context;
    DBUtilities dbUtilities;
    FileUtilities FileUtilities;
    List<String> listIdLearnWords; // коллекция id слов для изучения

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
        FileUtilities = new FileUtilities(this);

        context = getBaseContext();
        dbUtilities = new DBUtilities(context);
        dbUtilities.open();
        listIdLearnWords = new ArrayList<>();
        cursor = dbUtilities.getDb().rawQuery(mainQuery, null);
    }//onCreate

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnRandomWordsLWAc:
                randomWords();
                break;

            case R.id.btnSelectWordsLWAc:
                selectWords();
                break;

        }//switch

    }//onClick

    private void selectWords() {
    }//selectWords

    //выбор процедуры при рандомном подборе слов для изучения
    private void randomWords() {
        String nextInter;
        listIdLearnWords.clear();
        int countCursor = cursor.getCount();

        for (int i = 0; i < 20; i++) {
            //проверка повторяющегося варианта
            while(true) {
                nextInter = String.valueOf(Utils.getRandom(0, countCursor));
                if(!listIdLearnWords.contains(nextInter)) break;
            }//while
            listIdLearnWords.add(nextInter);
        }//for
        Intent intent = new Intent(this, ChooseTranslationActivity.class);
        intent.putStringArrayListExtra(
                "idList",
                (ArrayList<String>) listIdLearnWords
        );
        startActivity(intent);
    }//randomWords

}//LearnWordActivity
