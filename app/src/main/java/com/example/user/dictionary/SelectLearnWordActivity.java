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

        //добавляем actionBar (стрелка сверху слева)
        actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        FileUtilities = new FileUtilities(this);

        context = getBaseContext();
        dbUtilities = new DBUtilities(context);
        dbUtilities.open();
        listCursorNum = new ArrayList<>();
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
        int method = getIntent().getIntExtra("method", 0);
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
        intent.putExtra(
                "method",
                method
        );
        intent.putExtra(
                "loops",
                loops
        );
        startActivity(intent);
    }//startAnyMethod

    //Строим RecyclerView
    private void buildUserRecyclerView() {
        // получаем данные из БД в виде курсора
        // создаем адаптер, передаем в него курсор
        selectLearnWordsRecyclerAdapter
                = new SelectLearnWordsRecyclerAdapter(context, mainQuery, filter, listCursorNum);

        rvSelectLearnWordsSLWAc.setAdapter(selectLearnWordsRecyclerAdapter);

        Cursor cursor = dbUtilities.getDb().rawQuery(mainQuery, null);
        // для сохранения отмеченных checkbox при скролинге пропишем доп. функции для recyclerView
        rvSelectLearnWordsSLWAc.setDrawingCacheEnabled(true);
        rvSelectLearnWordsSLWAc.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        rvSelectLearnWordsSLWAc.setItemViewCacheSize(cursor.getCount());
    }//buildUserRecyclerView

}//SelectLearnWordActivity
