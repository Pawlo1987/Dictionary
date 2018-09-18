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

public class LearnWordActivity extends AppCompatActivity {
    Context context;
    int countCursor;
    DBUtilities dbUtilities;
    FileUtilities FileUtilities;
    TextView tvWordsCountForRandomLWAc;
    TextView tvLoopsLWAc;
    Button btnPlWC;
    Button btnMiWC;
    Button btnPlLo;
    Button btnMiLo;
    CheckBox cbChooseRussianMethod;
    CheckBox cbChooseHebrewMethod;
    CheckBox cbChooseCoupleMethod;
    CheckBox cbWriteRussianMethod;
    CheckBox cbWriteHebrewMethod;
    List<String> listIdLearnWords; // коллекция id слов для изучения
    List<Integer> listMethods; //коллекция выбранных методов
    int loops = 1;
    int wordsCount = 8;
    ActionBar actionBar;                //стрелка НАЗАД

    private Cursor cursor;

    // при запросе с INNER JOIN обязательно указываем в запросе:
    // имя таблицы и имя столбца
    // SELECT таблица.столбец FROM таблица
    //основной запрос
    String mainQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn_word);

        //добавляем actionBar (стрелка сверху слева)
        actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        tvWordsCountForRandomLWAc = findViewById(R.id.tvWordsCountForRandomLWAc);
        tvLoopsLWAc = findViewById(R.id.tvLoopsLWAc);
        btnPlWC = findViewById(R.id.btnPlWC);
        btnMiWC = findViewById(R.id.btnMiWC);
        btnPlLo = findViewById(R.id.btnPlLo);
        btnMiLo = findViewById(R.id.btnMiLo);
        cbChooseRussianMethod = findViewById(R.id.cbChooseRussianMethod);
        cbChooseHebrewMethod = findViewById(R.id.cbChooseHebrewMethod);
        cbChooseCoupleMethod = findViewById(R.id.cbChooseCoupleMethod);
        cbWriteRussianMethod = findViewById(R.id.cbWriteRussianMethod);
        cbWriteHebrewMethod = findViewById(R.id.cbWriteHebrewMethod);
        listMethods = new ArrayList<>();
        FileUtilities = new FileUtilities(this);

        context = getBaseContext();
        dbUtilities = new DBUtilities(context);
        dbUtilities.open();
        mainQuery = dbUtilities.mainQuery;
        listIdLearnWords = new ArrayList<>();
        cursor = dbUtilities.getDb().rawQuery(mainQuery, null);
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
            case R.id.item_authorization :
                authorization();
                return true;
            case R.id.item_create_profile:
                createProfile();
                return true;

            //обработчик actionBar (стрелка сверху слева)
            case android.R.id.home:
                setResult(RESULT_CANCELED);
                onBackPressed();
                return true;
        }//switch
        return super.onOptionsItemSelected(item);
    }//onOptionsItemSelected

    //создание нового профиля
    private void createProfile() {
        Intent intent = new Intent(this, CreateProfileActivity.class);
        startActivity(intent);
    }//createProfile

    //авторизация профиля
    private void authorization() {
        Intent intent = new Intent(this, AuthorizationProfileActivity.class);
        startActivity(intent);
    }//authorization

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_learn_word, menu);
        return true;
    }//onCreateOptionsMenu

    private void selectWords() {
        listMethods.clear();
        if(cbChooseRussianMethod.isChecked())   listMethods.add(1);
        if(cbChooseHebrewMethod.isChecked())    listMethods.add(2);
        if(cbChooseCoupleMethod.isChecked())    listMethods.add(3);
        if(cbWriteRussianMethod.isChecked())    listMethods.add(4);
        if(cbWriteHebrewMethod.isChecked())     listMethods.add(5);
        //проверка на случай невыбранного метода
        if(listMethods.size()>0) {
            Intent intent = new Intent(this, SelectLearnWordActivity.class);
            intent.putIntegerArrayListExtra(
                    "listMethods", (ArrayList<Integer>) listMethods
            );
            intent.putExtra(
                    "loops",
                    loops
            );
            intent.putExtra(
                    "flAuthorization",
                    false
            );
            startActivity(intent);
        }else{
            Toast.makeText(this, "Check minimum one method", Toast.LENGTH_SHORT).show();
        }//if(listMethods.size()>0)
    }//selectWords

    //выбор процедуры при рандомном подборе слов для изучения
    private void randomWords() {
        listMethods.clear();
        if(cbChooseRussianMethod.isChecked())   listMethods.add(1);
        if(cbChooseHebrewMethod.isChecked())    listMethods.add(2);
        if(cbChooseCoupleMethod.isChecked())    listMethods.add(3);
        if(cbWriteRussianMethod.isChecked())    listMethods.add(4);
        if(cbWriteHebrewMethod.isChecked())     listMethods.add(5);
        //проверка на случай невыбранного метода
        if(listMethods.size()>0) {
            //проверка достаточно ли у вас слов в словоре для изучения
            if (countCursor < wordsCount) {
                Toast.makeText(this, "You need add more words to DB!", Toast.LENGTH_SHORT).show();
            } else {
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
            } //if(countCursor<wordsCount)
        }else{
            Toast.makeText(this, "Check minimum one method", Toast.LENGTH_SHORT).show();
        }//if(listMethods.size()>0)
    }//randomWords

    //запуск активности любого метода изучения слов
    private void startMethod() {
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
                loops
        );
        intent.putExtra(
                "wordsCount",
                wordsCount
        );
        startActivity(intent);
    }//startAnyMethod
}//LearnWordActivity



