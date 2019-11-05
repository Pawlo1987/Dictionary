package com.pavel.meule.dictionary;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
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
    TextView tvWordsCountForSemanticGroupLWAc;
    TextView tvLoopsLWAc;
    Button btnPlWC;
    Button btnMiWC;
    Button btnPlLo;
    Button btnMiLo;
    CheckBox cbChooseRussianMethod;
    CheckBox cbChooseHebrewMethod;
    CheckBox cbChooseCoupleMethod;
    CheckBox cbWriteHebrewMethod;
    private Spinner spSemanticGroupLWAc;         //сппинер c именнами симантических групп
    private List<String> listSemantic;      //коллекиция со списком имен симантических групп
    List<String> listIdLearnWords; // коллекция id слов для изучения
    List<Integer> listMethods; //коллекция выбранных методов
    int loops = 1;
    int wordsCount = 8;  //by default
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
        tvWordsCountForSemanticGroupLWAc = findViewById(R.id.tvWordsCountForSemanticGroupLWAc);
        tvLoopsLWAc = findViewById(R.id.tvLoopsLWAc);
        btnPlWC = findViewById(R.id.btnPlWC);
        btnMiWC = findViewById(R.id.btnMiWC);
        btnPlLo = findViewById(R.id.btnPlLo);
        btnMiLo = findViewById(R.id.btnMiLo);
        cbChooseRussianMethod = findViewById(R.id.cbChooseRussianMethod);
        cbChooseHebrewMethod = findViewById(R.id.cbChooseHebrewMethod);
        cbChooseCoupleMethod = findViewById(R.id.cbChooseCoupleMethod);
        cbWriteHebrewMethod = findViewById(R.id.cbWriteHebrewMethod);
        spSemanticGroupLWAc = findViewById(R.id.spSemanticGroupLWAc);
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
        //коллекиция со списком имен симантических групп
        listSemantic = new ArrayList<>();
        String query = "SELECT semantic.name FROM semantic";
        Cursor cursor = dbUtilities.getDb().rawQuery(query, null);
        int l = cursor.getCount();
        for (int i = 0; i < l; i++) {
            cursor.moveToPosition(i);
            listSemantic.add(cursor.getString(0));
        }//for (int i = 0; i < l; i++)
        // строим спиннер с именами симантических групп
        spSemanticGroupLWAc.setAdapter(
                buildSpinnerAdapter(listSemantic));
        //запрос для получения курсора выбранорй симантической группы в спиннере
        cursor = cursorOfSemGrInSpinner(spSemanticGroupLWAc.getSelectedItem().toString());
        listIdLearnWords.clear();
        tvWordsCountForSemanticGroupLWAc.setText(String.valueOf(cursor.getCount()));
        //прописываем изменение на select listener при изменении выбранной симантической группы в спиннере
        spSemanticGroupLWAc.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //запрос для получения курсора выбранорй симантической группы в спиннере
                Cursor cursor = cursorOfSemGrInSpinner(spSemanticGroupLWAc.getSelectedItem().toString());
                listIdLearnWords.clear();
                tvWordsCountForSemanticGroupLWAc.setText(String.valueOf(cursor.getCount()));
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }//onCreate

    //выбрать семантическую группу
    private void bySemanticGroup() {
        listMethods.clear();
        if (cbChooseRussianMethod.isChecked()) listMethods.add(1);
        if (cbChooseHebrewMethod.isChecked()) listMethods.add(2);
        if (cbChooseCoupleMethod.isChecked()) listMethods.add(3);
        if (cbWriteHebrewMethod.isChecked()) listMethods.add(5);
        //проверка на случай невыбранного метода
        if (listMethods.size() > 0) {
            //запрос для получения курсора выбранорй симантической группы в спиннере
            //заполняем выбранную семантическую группу для изучения
            listIdLearnWords.clear();
            Cursor cursor = dbUtilities.getDb().rawQuery(mainQuery, null);
            int t = cursor.getCount();
            //проверка - чтоб отмеченных слов для изучения было минимум 8
            if (t >= 8) {
            String semantic = spSemanticGroupLWAc.getSelectedItem().toString();
            for (int i = 0; i < t; i++) {
                cursor.moveToPosition(i);
                //записываем значение в listCursorNum
                if (semantic.equals(cursor.getString(3))) {
                    listIdLearnWords.add(String.valueOf(i));
                }//if(semantic.equals(cursor.getString(3)))
            }// for (int i = 0; i < t; i++)
            //перемешать коллекцию выбранных слов
            Collections.shuffle(listIdLearnWords);
                Collections.shuffle(listIdLearnWords);
                //количество изучаемых слов
                wordsCount = t;
                startMethod();
            } else
                Toast.makeText(context, "You need 8 word minimum", Toast.LENGTH_SHORT).show();
            } //if (t >= 8)
        else {
            Toast.makeText(this, "Check minimum one method", Toast.LENGTH_SHORT).show();
        }//if(listMethods.size()>0)
    }//selectSemanticGroup

    //запрос для получения курсора выбранорй симантической группы в спиннере
    private Cursor cursorOfSemGrInSpinner(String toString) {
        //получаем коллекцию со списком выбранной симантической группы
        String query = "SELECT hebrew.id, semantic_id, semantic.name FROM hebrew " +
                "INNER JOIN semantic ON semantic.id = hebrew.semantic_id " +
                "WHERE semantic.name = \"" + spSemanticGroupLWAc.getSelectedItem().toString() + "\"";
        Cursor cursor = dbUtilities.getDb().rawQuery(query, null);
        return cursor;
    }//cursorOfSemGrInSpinner

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

            case R.id.btnSemanticGroupLWAc:
                bySemanticGroup();
                break;

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
            case R.id.item_authorization:
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
        if (cbChooseRussianMethod.isChecked()) listMethods.add(1);
        if (cbChooseHebrewMethod.isChecked()) listMethods.add(2);
        if (cbChooseCoupleMethod.isChecked()) listMethods.add(3);
        if (cbWriteHebrewMethod.isChecked()) listMethods.add(5);
        //проверка на случай невыбранного метода
        if (listMethods.size() > 0) {
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
        } else {
            Toast.makeText(this, "Check minimum one method", Toast.LENGTH_SHORT).show();
        }//if(listMethods.size()>0)
    }//selectWords

    //выбор процедуры при рандомном подборе слов для изучения
    private void randomWords() {
        listMethods.clear();
        if (cbChooseRussianMethod.isChecked()) listMethods.add(1);
        if (cbChooseHebrewMethod.isChecked()) listMethods.add(2);
        if (cbChooseCoupleMethod.isChecked()) listMethods.add(3);
        if (cbWriteHebrewMethod.isChecked()) listMethods.add(5);
        //проверка на случай невыбранного метода
        if (listMethods.size() > 0) {
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
        } else {
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
                listIdLearnWords.size()
        );
        startActivity(intent);
    }//startAnyMethod
}//LearnWordActivity



