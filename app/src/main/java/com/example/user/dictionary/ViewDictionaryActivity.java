package com.example.user.dictionary;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ViewDebug;
import android.widget.EditText;
import android.widget.TextView;

public class ViewDictionaryActivity extends AppCompatActivity {

    EditText etSearchWordVDAc;
    String filter = "";
    DBUtilities dbUtilities;
    Context context;
    RecyclerView rvWordsVDAc;
    // адаптер для отображения recyclerView
    ViewDictionaryRecyclerAdapter viewDictionaryRecyclerAdapter;
    // поля для доступа к записям БД
    Cursor cursor;                // прочитанные данные
    //основной запрос
    // получаем данные из БД в виде курсора
    // при запросе с INNER JOIN обязательно указываем в запросе:
    // имя таблицы и имя столбца
    // SELECT таблица.столбец FROM таблица
    String mainQuery = "SELECT russian.id, russian.word, hebrew.word, transcription.word, " +
            "russian.gender, hebrew.gender, meaning.option, russian.quantity FROM russian " +
            "INNER JOIN hebrew ON hebrew.id = russian.hebrew_id " +
            "INNER JOIN meaning ON meaning.id = russian.meaning_id " +
            "INNER JOIN transcription ON transcription.id = hebrew.transcription_id;";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_dictionary);

        context = getBaseContext();
        etSearchWordVDAc = findViewById(R.id.etSearchWordVDAc);
        dbUtilities = new DBUtilities(context);
        dbUtilities.open();
        rvWordsVDAc = findViewById(R.id.rvWordsVDAc);
        //Строим RecyclerView
        buildUserRecyclerView(
                //spCitySePaAc.getItemAtPosition(spPos).toString(),
                filter
        );

        // установка слушателя изменения текста в EditText для бинарного поиска
        // и фильтрации RecyclerView по изменению текста в EditText
        etSearchWordVDAc.addTextChangedListener(new TextWatcher() {
            // при изменении текста выполняем фильтрацию
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                //получаем фильтрующие слово
                filter = etSearchWordVDAc.getText().toString();

                //Строим RecyclerView
                buildUserRecyclerView(
                        //spCitySePaAc.getItemAtPosition(spPos).toString(),
                        filter
                );
            }//onTextChanged

            public void afterTextChanged(Editable s) { }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
        });
    }//onCreate

    //Строим RecyclerView
    private void buildUserRecyclerView(String filter) {

        cursor = dbUtilities.getDb().rawQuery(mainQuery, null);

        // создаем адаптер, передаем в него курсор
        viewDictionaryRecyclerAdapter
                = new ViewDictionaryRecyclerAdapter(context, cursor, filter);

        rvWordsVDAc.setAdapter(viewDictionaryRecyclerAdapter);
    }//buildUserRecyclerView

//    //строим Spinner City
//    private void buildCitySpinner() {
//        //заполнить spListCity данные для отображения в Spinner
//        spListCity = dbUtilities.getStrListTableFromDB("cities", "name");
//
//        spListCity.add("ВСЕ ГОРОДА");
//
//        //создание адаптера для спинера
//        spAdapterCity = new ArrayAdapter<String>(
//                this,
//                android.R.layout.simple_spinner_item,
//                spListCity
//        );
//
//        // назначение адапетра для списка
//        spAdapterCity.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//
//        spCitySePaAc.setAdapter(spAdapterCity);
//    }//buildCitySpinner

}//ViewDictionaryActivity
