package com.example.user.dictionary;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ViewDictionaryActivity extends AppCompatActivity {
    FileUtilities FileUtilities;
    EditText etSearchWordVDAc;
    Spinner spMeaningVDAc;
    int spPos;                      //позиция спинера
    private ArrayAdapter<String> spAdapterMeaning;  //Адаптер для спинера
    List<String> spListMeaningVDAc;
    String filter = "";
    DBUtilities dbUtilities;
    Context context;
    RecyclerView rvWordsVDAc;
    TextView tvWordsCountVDAc;
    // адаптер для отображения recyclerView
    ViewDictionaryRecyclerAdapter viewDictionaryRecyclerAdapter;
    ActionBar actionBar;                //стрелка НАЗАД

    // при запросе с INNER JOIN обязательно указываем в запросе:
    // имя таблицы и имя столбца
    // SELECT таблица.столбец FROM таблица
    //основной запрос
    String mainQuery = "SELECT hebrew.id, hebrew.word_he, transcriptions.word_tr, " +
            "meanings.option, gender.option, quantity.option FROM hebrew " +
            "INNER JOIN transcriptions ON transcriptions.id = hebrew.transcription_id " +
            "INNER JOIN meanings ON meanings.id = hebrew.meaning_id " +
            "INNER JOIN gender ON gender.id = hebrew.gender_id " +
            "INNER JOIN quantity ON quantity.id = hebrew.quantity_id;";


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_dictionary);
        //Для sdk>23 сначала надо предоставить разрешение.
        //без него не будет доступа к общей внутреней памяти телефона
        //и мы не сможеш импортировать и экспортировать БД.
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // Если у нас нет разрешения
            ActivityCompat.requestPermissions(
                    this,
                    new String[] { Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE},1
            );
        }//if (permission != PackageManager.PERMISSION_GRANTED)

        //добавляем actionBar (стрелка сверху слева)
        actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        FileUtilities = new FileUtilities(this);

        context = getBaseContext();
        dbUtilities = new DBUtilities(context);
        dbUtilities.open();
        spListMeaningVDAc = new ArrayList<>();
        spMeaningVDAc = findViewById(R.id.spMeaningVDAc);
        buildSpinner();
        etSearchWordVDAc = findViewById(R.id.etSearchWordVDAc);
        tvWordsCountVDAc = findViewById(R.id.tvWordsCountVDAc);
        etSearchWordVDAc.isFocused();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_view_dictionary, menu);
        return true;
    }//onCreateOptionsMenu

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id){
            case R.id.item_export_db :
                exportDB();
                return true;
            case R.id.item_import_db:
                importDB();
                return true;
            case R.id.item_export_to_csv:
                exportDBtoCSV();
                return true;

            //обработчик actionBar (стрелка сверху слева)
            case android.R.id.home:
                setResult(RESULT_CANCELED);
                onBackPressed();
                return true;

        }//switch
        return super.onOptionsItemSelected(item);
    }//onOptionsItemSelected

    //конвертируем в CSV и экпортируем в корень устройства в папку Dictionary
    private void exportDBtoCSV() {
        FileUtilities.importToCSVFromDB();
    }//exportDBtoCSV

    //процедура импорта БД из кореня устройства из папки Dictionary
    private void importDB() {
        FileUtilities.importDB();

        //обновляем адаптер
        //Строим RecyclerView
        buildUserRecyclerView(
                spMeaningVDAc.getItemAtPosition(spPos).toString(),
                filter
        );
    }//importDB

    //процедура экспорта БД в корень устройства в папку Dictionary
    private void exportDB() {
        FileUtilities.exportDB();
    }//exportDB

    //Строим RecyclerView
    private void buildUserRecyclerView(String meaning, String filter) {
        // получаем данные из БД в виде курсора
        // согласовуем с выбранной позицией спиннера
        if(meaning.equals("ALL")) {
            mainQuery = "SELECT hebrew.id, hebrew.word_he, transcriptions.word_tr, " +
                    "meanings.option, gender.option, quantity.option FROM hebrew " +
                    "INNER JOIN transcriptions ON transcriptions.id = hebrew.transcription_id " +
                    "INNER JOIN meanings ON meanings.id = hebrew.meaning_id " +
                    "INNER JOIN gender ON gender.id = hebrew.gender_id " +
                    "INNER JOIN quantity ON quantity.id = hebrew.quantity_id ORDER BY hebrew.word_he";
        }else {
            mainQuery = "SELECT hebrew.id, hebrew.word_he, transcriptions.word_tr, " +
                    "meanings.option, gender.option, quantity.option FROM hebrew " +
                    "INNER JOIN transcriptions ON transcriptions.id = hebrew.transcription_id " +
                    "INNER JOIN meanings ON meanings.id = hebrew.meaning_id " +
                    "INNER JOIN gender ON gender.id = hebrew.gender_id " +
                    "INNER JOIN quantity ON quantity.id = hebrew.quantity_id " +
                    "WHERE meanings.option = \"" + meaning + "\" ORDER BY hebrew.word_he";
        }//if(meaning.equals("ALL"))

        Cursor cursor = dbUtilities.getDb().rawQuery(mainQuery, null);
        //количество слов в БД
        tvWordsCountVDAc.setText(String.valueOf(cursor.getCount()));

        // создаем адаптер, передаем в него курсор
        viewDictionaryRecyclerAdapter
                = new ViewDictionaryRecyclerAdapter(context, mainQuery, filter);

        rvWordsVDAc.setAdapter(viewDictionaryRecyclerAdapter);
    }//buildUserRecyclerView

    //строим Spinner
    private void buildSpinner() {
        //заполнить spListMeaningVDAc данные для отображения в Spinner
        spListMeaningVDAc.add("ALL");
        //запрос для спиннера
        String query = "SELECT meanings.option FROM meanings";
        spListMeaningVDAc.addAll(dbUtilities.fillList(query));

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
