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

public class ViewDictionaryActivity extends AppCompatActivity {

    EditText etSearchWordVDAc;
    Spinner spMeaningVDAc;
    int spPos;                      //позиция спинера
    private ArrayAdapter<String> spAdapterMeaning;  //Адаптер для спинера
    List<String> spListMeaningVDAc;
    String filter = "";
    DBUtilities dbUtilities;
    Context context;
    RecyclerView rvWordsVDAc;
    // адаптер для отображения recyclerView
    ViewDictionaryRecyclerAdapter viewDictionaryRecyclerAdapter;

    // при запросе с INNER JOIN обязательно указываем в запросе:
    // имя таблицы и имя столбца
    // SELECT таблица.столбец FROM таблица
    //основной запрос
    String mainQuery = "SELECT russian.id, russian.word, hebrew.word, transcription.word, " +
            "russian.gender, hebrew.gender, meaning.option, russian.quantity FROM russian " +
            "INNER JOIN hebrew ON hebrew.id = russian.hebrew_id " +
            "INNER JOIN meaning ON meaning.id = russian.meaning_id " +
            "INNER JOIN transcription ON transcription.id = hebrew.transcription_id";
    //запрос для спиннера
    String spinnerQuery = "SELECT option FROM meaning";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_dictionary);

        context = getBaseContext();
        dbUtilities = new DBUtilities(context);
        dbUtilities.open();
        spListMeaningVDAc = new ArrayList<>();
        spMeaningVDAc = findViewById(R.id.spMeaningVDAc);
        buildSpinner();
        etSearchWordVDAc = findViewById(R.id.etSearchWordVDAc);
        rvWordsVDAc = findViewById(R.id.rvWordsVDAc);
        //Строим RecyclerView
        buildUserRecyclerView(
                spMeaningVDAc.getItemAtPosition(spPos).toString(),
                filter
        );

        //Слушатель для позиции спинера и фильтрации RecyclerView по изменению позиции
        spMeaningVDAc.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spPos = position;

                //Строим RecyclerView
                buildUserRecyclerView(
                        spMeaningVDAc.getItemAtPosition(spPos).toString(),
                        filter
                );
            }//onItemSelected

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }//onNothingSelected
        });

        // установка слушателя изменения текста в EditText для бинарного поиска
        // и фильтрации RecyclerView по изменению текста в EditText
        etSearchWordVDAc.addTextChangedListener(new TextWatcher() {
            // при изменении текста выполняем фильтрацию
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                //получаем фильтрующие слово
                filter = etSearchWordVDAc.getText().toString();

                //Строим RecyclerView
                buildUserRecyclerView(
                        spMeaningVDAc.getItemAtPosition(spPos).toString(),
                        filter
                );
            }//onTextChanged

            public void afterTextChanged(Editable s) { }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
        });
    }//onCreate

    //Строим RecyclerView
    private void buildUserRecyclerView(String meaning, String filter) {
        // получаем данные из БД в виде курсора
        // согласовуем с выбранной позицией спиннера
        if(meaning.equals("ALL")) {
            mainQuery = "SELECT russian.id, russian.word, hebrew.word, transcription.word, " +
                    "russian.gender, hebrew.gender, meaning.option, russian.quantity FROM russian " +
                    "INNER JOIN hebrew ON hebrew.id = russian.hebrew_id " +
                    "INNER JOIN meaning ON meaning.id = russian.meaning_id " +
                    "INNER JOIN transcription ON transcription.id = hebrew.transcription_id";
        }else {
            mainQuery = "SELECT russian.id, russian.word, hebrew.word, transcription.word, " +
                    "russian.gender, hebrew.gender, meaning.option, russian.quantity FROM russian " +
                    "INNER JOIN hebrew ON hebrew.id = russian.hebrew_id " +
                    "INNER JOIN meaning ON meaning.id = russian.meaning_id " +
                    "INNER JOIN transcription ON transcription.id = hebrew.transcription_id " +
                    "WHERE meaning.option = \"" + meaning + "\"";
        }

        // создаем адаптер, передаем в него курсор
        viewDictionaryRecyclerAdapter
                = new ViewDictionaryRecyclerAdapter(context, mainQuery, filter);

        rvWordsVDAc.setAdapter(viewDictionaryRecyclerAdapter);
    }//buildUserRecyclerView

    //строим Spinner
    private void buildSpinner() {
        //заполнить spListMeaningVDAc данные для отображения в Spinner
        spListMeaningVDAc.add("ALL");
        spListMeaningVDAc.addAll(dbUtilities.fillList(spinnerQuery));

        //создание адаптера для спинера
        spAdapterMeaning = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_item,
                spListMeaningVDAc
        );

        // назначение адапетра для списка
        spAdapterMeaning.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spMeaningVDAc.setAdapter(spAdapterMeaning);
    }//buildCitySpinner

}//ViewDictionaryActivity
