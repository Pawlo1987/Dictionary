package com.example.user.dictionary;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

public class SelectLearnWordActivity extends AppCompatActivity {
    FileUtilities FileUtilities;
    EditText etSelectLearnWordsSLWAc;
    String filter = "";
    DBUtilities dbUtilities;
    Context context;
    RecyclerView rvSelectLearnWordsSLWAc;
    // адаптер для отображения recyclerView
    SelectLearnWordsRecyclerAdapter selectLearnWordsRecyclerAdapter;

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
        setContentView(R.layout.activity_select_learn_word);
        FileUtilities = new FileUtilities(this);

        context = getBaseContext();
        dbUtilities = new DBUtilities(context);
        dbUtilities.open();
        etSelectLearnWordsSLWAc = findViewById(R.id.etSelectLearnWordsSLWAc);
        etSelectLearnWordsSLWAc.isFocused();
        rvSelectLearnWordsSLWAc = findViewById(R.id.rvSelectLearnWordsSLWAc);
        //Строим RecyclerView
        buildUserRecyclerView();

        // установка слушателя изменения текста в EditText для бинарного поиска
        // и фильтрации RecyclerView по изменению текста в EditText
        etSelectLearnWordsSLWAc.addTextChangedListener(new TextWatcher() {
            // при изменении текста выполняем фильтрацию
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //получаем фильтрующие слово
                filter = etSelectLearnWordsSLWAc.getText().toString();
                //Строим RecyclerView
                buildUserRecyclerView();
            }//onTextChanged

            public void afterTextChanged(Editable s) { }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
        });
    }//onCreate

    //Строим RecyclerView
    private void buildUserRecyclerView() {
        // получаем данные из БД в виде курсора
        // создаем адаптер, передаем в него курсор
        selectLearnWordsRecyclerAdapter
                = new SelectLearnWordsRecyclerAdapter(context, mainQuery, filter);

        rvSelectLearnWordsSLWAc.setAdapter(selectLearnWordsRecyclerAdapter);
    }//buildUserRecyclerView

}//SelectLearnWordActivity
