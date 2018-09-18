package com.example.user.dictionary;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.user.dictionary.Interface.DataFromRecyclerToActivityInterface;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SelectNextLearnWordActivity extends AppCompatActivity
        implements DataFromRecyclerToActivityInterface{
    FileUtilities FileUtilities;
    EditText etSelectNextLearnWordSNLWAc;
    String filter = "";
    DBUtilities dbUtilities;
    Context context;
    List<String> listCursorNum;
    int selectIdHebrew;
    RecyclerView rvSelectNextLearnWordSNLWAc;
    // адаптер для отображения recyclerView
    SelectNextLearnWordRecyclerAdapter selectNextLearnWordRecyclerAdapter;
    ActionBar actionBar;                //стрелка НАЗАД
    boolean flAuthorization;

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
        setContentView(R.layout.activity_select_next_learn_word);

        //добавляем actionBar (стрелка сверху слева)
        actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        FileUtilities = new FileUtilities(this);

        context = getBaseContext();
        dbUtilities = new DBUtilities(context);
        dbUtilities.open();
        flAuthorization = getIntent().getBooleanExtra("flAuthorization",false);
        listCursorNum = new ArrayList<>();
        etSelectNextLearnWordSNLWAc = findViewById(R.id.etSelectNextLearnWordSNLWAc);
        etSelectNextLearnWordSNLWAc.isFocused();
        rvSelectNextLearnWordSNLWAc = findViewById(R.id.rvSelectNextLearnWordSNLWAc);
        //Строим RecyclerView
        buildUserRecyclerView();

        // установка слушателя изменения текста в EditText для бинарного поиска
        // и фильтрации RecyclerView по изменению текста в EditText
        etSelectNextLearnWordSNLWAc.addTextChangedListener(new TextWatcher() {
            // при изменении текста выполняем фильтрацию
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //получаем фильтрующие слово
                filter = etSelectNextLearnWordSNLWAc.getText().toString();
                //Строим RecyclerView
                buildUserRecyclerView();
            }//onTextChanged

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
        });
    }//onCreate

    //обработчик actionBar (стрелка сверху слева)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case android.R.id.home:
                setResult(RESULT_CANCELED);
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }//switch
    }//onOptionsItemSelected

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnOkSNLWAc:
                //возврат параметров в ProfileParametersActivity
                Intent intent = new Intent();
                intent.putExtra("selectNextIdHebrew", selectIdHebrew);
                setResult(RESULT_OK, intent);
                finish();
        }//switch
    }//onClick

    //Строим RecyclerView
    private void buildUserRecyclerView() {
        // получаем данные из БД в виде курсора
        // создаем адаптер, передаем в него курсор
        selectNextLearnWordRecyclerAdapter
                = new SelectNextLearnWordRecyclerAdapter(this, context, mainQuery, filter);

        rvSelectNextLearnWordSNLWAc.setAdapter(selectNextLearnWordRecyclerAdapter);

        Cursor cursor = dbUtilities.getDb().rawQuery(mainQuery, null);
        // для сохранения отмеченных checkbox при скролинге пропишем доп. функции для recyclerView
        rvSelectNextLearnWordSNLWAc.setDrawingCacheEnabled(true);
        rvSelectNextLearnWordSNLWAc.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        rvSelectNextLearnWordSNLWAc.setItemViewCacheSize(cursor.getCount());
    }//buildUserRecyclerView

    //интерфейс передачи данных из RecyclerView в Activity
    @Override
    public void dataFromRecyclerToActivityInterface(int data) {
        selectIdHebrew = data;
    }//dataFromRecyclerToActivityInterface
}//SelectLearnWordActivity
