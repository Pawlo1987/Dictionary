package com.example.user.dictionary;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.ViewDebug;
import android.widget.EditText;
import android.widget.TextView;

public class ViewDictionaryActivity extends AppCompatActivity {

    EditText edSearchWordVDAc;
    DBUtilities dbUtilities;
    Context context;
    RecyclerView rvWordsVDAc;
    // адаптер для отображения recyclerView
    ViewDictionaryRecyclerAdapter viewDictionaryRecyclerAdapter;
    // поля для доступа к записям БД
    Cursor cursor;                // прочитанные данные

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_dictionary);

        context = getBaseContext();
        dbUtilities = new DBUtilities(context);
        dbUtilities.open();
        // получаем данные из БД в виде курсора
        // при запросе с INNER JOIN обязательно указываем в запросе:
        // имя таблицы и имя столбца
        // SELECT таблица.столбец FROM таблица
        String query = "SELECT russian.id, russian.word, hebrew.word, transcription.word, " +
                "russian.gender, hebrew.gender, meaning.option, russian.quantity FROM russian " +
                "INNER JOIN hebrew ON hebrew.id = russian.hebrew_id " +
                "INNER JOIN meaning ON meaning.id = russian.meaning_id " +
                "INNER JOIN transcription ON transcription.id = hebrew.transcription_id;";
        cursor = dbUtilities.getDb().rawQuery(query, null);

        // создаем адаптер, передаем в него курсор
        viewDictionaryRecyclerAdapter = new ViewDictionaryRecyclerAdapter(context, cursor);
        rvWordsVDAc = findViewById(R.id.rvWordsVDAc);
        rvWordsVDAc.setAdapter(viewDictionaryRecyclerAdapter);

//        int n = cursor.getCount();
//        //перейти на позицию, перед тем как считать данные
//        cursor.moveToPosition(0);
//        edSearchWordVDAc = findViewById(R.id.etSearchWordVDAc);
//        //cursor.getString(0) - читаем даные курсора
//        edSearchWordVDAc.setText(cursor.getString(1) + " " +
//                cursor.getString(2) + " " + cursor.getString(3));

    }

}
