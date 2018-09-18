package com.example.user.dictionary;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SelectLearnWordActivity extends AppCompatActivity {
    FileUtilities FileUtilities;
    EditText etSelectLearnWordsSLWAc;
    String filter = "";
    DBUtilities dbUtilities;
    Context context;
    List<String> listCursorNum;
    RecyclerView rvSelectLearnWordsSLWAc;
    // адаптер для отображения recyclerView
    SelectLearnWordsRecyclerAdapter selectLearnWordsRecyclerAdapter;
    ActionBar actionBar;                //стрелка НАЗАД
    boolean flAuthorization;

    // при запросе с INNER JOIN обязательно указываем в запросе:
    // имя таблицы и имя столбца
    // SELECT таблица.столбец FROM таблица
    //основной запрос
    String mainQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_learn_word);

        //добавляем actionBar (стрелка сверху слева)
        actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        FileUtilities = new FileUtilities(this);

        context = getBaseContext();
        dbUtilities = new DBUtilities(context);
        dbUtilities.open();
        mainQuery = dbUtilities.mainQuery;
        flAuthorization = getIntent().getBooleanExtra("flAuthorization",false);
        listCursorNum = new ArrayList<>();
        etSelectLearnWordsSLWAc = findViewById(R.id.etSelectLearnWordsSLWAc);
        etSelectLearnWordsSLWAc.isFocused();
        rvSelectLearnWordsSLWAc = findViewById(R.id.rvSelectLearnWordsSLWAc);
        //Строим RecyclerView
        buildUserRecyclerView("");

        // установка слушателя изменения текста в EditText для бинарного поиска
        // и фильтрации RecyclerView по изменению текста в EditText
        etSelectLearnWordsSLWAc.addTextChangedListener(new TextWatcher() {
            // при изменении текста выполняем фильтрацию
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //получаем фильтрующие слово
                filter = etSelectLearnWordsSLWAc.getText().toString();

                //запрос для бинарного посика
                mainQuery = "SELECT hebrew.id, hebrew.word_he, transcriptions.word_tr, " +
                        "semantic.name, meanings.option, gender.option, quantity.option FROM hebrew " +
                        "INNER JOIN transcriptions ON transcriptions.id = hebrew.transcription_id " +
                        "INNER JOIN semantic ON semantic.id = hebrew.semantic_id " +
                        "INNER JOIN meanings ON meanings.id = hebrew.meaning_id " +
                        "INNER JOIN gender ON gender.id = hebrew.gender_id " +
                        "INNER JOIN quantity ON quantity.id = hebrew.quantity_id " +
                        "WHERE hebrew.word_he LIKE '%" + filter + "%'";

                //Строим RecyclerView
                buildUserRecyclerView("");
            }//onTextChanged

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
        });
    }//onCreate

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_select_next_learn_word, menu);
        return true;
    }//onCreateOptionsMenu

    //обработчик actionBar (стрелка сверху слева)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {

            case R.id.item_select_semantic_group :
                selectSemanticGroup();
                return true;

            case android.R.id.home:
                setResult(RESULT_CANCELED);
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }//switch
    }//onOptionsItemSelected

    //выбрать семантическую группу
    private void selectSemanticGroup() {
            final AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("Select semantic group!");
//        alert.setMessage("Create new data!");
            alert.setIcon(R.drawable.icon_information);
        final RadioGroup rg = new RadioGroup(this);
        rg.setOrientation(RadioGroup.VERTICAL);
        //коллекиция со списком имен симантических групп
        String query = "SELECT semantic.name FROM semantic";
        Cursor cursor = dbUtilities.getDb().rawQuery(query, null);
        final int l = cursor.getCount();
        final RadioButton[] masRB = new RadioButton[l];
        for (int i = 0; i < l; i++) {
            cursor.moveToPosition(i);
            masRB[i] = new RadioButton(this);
            masRB[i].setText(cursor.getString(0));
            rg.addView(masRB[i]);
        }//for (int i = 0; i < l; i++)
            alert.setView(rg);
            alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    for (int i = 0; i < l; i++) {
                        if(masRB[i].isChecked()){
                            buildUserRecyclerView(masRB[i].getText().toString());
                            break;
                        }//if(masRB[i].isChecked())
                    }//for (int i = 0; i < l; i++)
                }//onClick
            });
            alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    // Canceled.
                }
            });
            alert.show();
    }//selectSemanticGroup

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnOkSLWAc:
                //проверка - чтоб отмеченных слов для изучения было минимум 8
                if (listCursorNum.size() >= 8){
                    //перемешать коллекцию выбранных слов
                    Collections.shuffle(listCursorNum);
                    startAnyMethod();
                }
                else
                    Toast.makeText(context, "You need 8 word minimum", Toast.LENGTH_SHORT).show();
                break;
        }//switch
    }//onClick

    //запуск активности любого метода изучения слов
    private void startAnyMethod() {
        int loops = getIntent().getIntExtra("loops", 0);

        Intent intent = new Intent(this, BackgroundMethodActivity.class);
        intent.putStringArrayListExtra(
                "idList",
                (ArrayList<String>) listCursorNum
        );
        intent.putExtra(
                "wordsCount",
                listCursorNum.size()
        );
        intent.putIntegerArrayListExtra(
                "listMethods", getIntent().getIntegerArrayListExtra("listMethods")
        );
        intent.putExtra(
                "loops",
                loops
        );
        startActivity(intent);
    }//startAnyMethod

    //Строим RecyclerView
    private void buildUserRecyclerView(String semantic) {
        //заполняем выбранную семантическую группу для изучения
        listCursorNum.clear();
        Cursor cursor = dbUtilities.getDb().rawQuery(mainQuery, null);
        int t = cursor.getCount();
        for (int i = 0; i < t; i++) {
            cursor.moveToPosition(i);
            //записываем значение в listCursorNum
            if (semantic.equals(cursor.getString(3))) {
                listCursorNum.add(String.valueOf(i));
            }//if(semantic.equals(cursor.getString(3)))
        }// for (int i = 0; i < t; i++)
        // получаем данные из БД в виде курсора
        // создаем адаптер, передаем в него курсор
        selectLearnWordsRecyclerAdapter
                = new SelectLearnWordsRecyclerAdapter(context, mainQuery, listCursorNum, semantic);

        rvSelectLearnWordsSLWAc.setAdapter(selectLearnWordsRecyclerAdapter);

        // для сохранения отмеченных checkbox при скролинге пропишем доп. функции для recyclerView
        rvSelectLearnWordsSLWAc.setDrawingCacheEnabled(true);
        rvSelectLearnWordsSLWAc.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        rvSelectLearnWordsSLWAc.setItemViewCacheSize(t);
    }//buildUserRecyclerView

}//SelectLearnWordActivity
