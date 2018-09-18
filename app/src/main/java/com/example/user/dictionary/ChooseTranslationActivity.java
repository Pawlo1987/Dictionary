package com.example.user.dictionary;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ChooseTranslationActivity extends AppCompatActivity {
    Context context;
    DBUtilities dbUtilities;
    FileUtilities FileUtilities;
    List<String> listIdLearnWords; // коллекция id слов для изучения
    List<Word> listWords; // коллекция слов для изучения
    TextView tvWordChTrAc;
    Button btnTr1ChTrAc;
    Button btnTr2ChTrAc;
    Button btnTr3ChTrAc;
    Button btnTr4ChTrAc;
    Button btnTr5ChTrAc;

    private Cursor cursor;

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
        setContentView(R.layout.activity_choose_translation);
        FileUtilities = new FileUtilities(this);

        context = getBaseContext();
        dbUtilities = new DBUtilities(context);
        dbUtilities.open();
        listIdLearnWords = new ArrayList<>();
        listWords  = new ArrayList<>();
        tvWordChTrAc = findViewById(R.id.tvWordChTrAc);
        btnTr1ChTrAc = findViewById(R.id.btnTr1ChTrAc);
        btnTr2ChTrAc = findViewById(R.id.btnTr2ChTrAc);
        btnTr3ChTrAc = findViewById(R.id.btnTr3ChTrAc);
        btnTr4ChTrAc = findViewById(R.id.btnTr4ChTrAc);
        btnTr5ChTrAc = findViewById(R.id.btnTr5ChTrAc);
        cursor = dbUtilities.getDb().rawQuery(mainQuery, null);
        listIdLearnWords.addAll(getIntent().getStringArrayListExtra("idList"));
        createWordList();
//        for (int i = 0; i < 20; i++) {
            startLearnWord(Utils.getRandom(0, 19));
//        }
    }//onCreate

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnTr1ChTrAc:
            case R.id.btnTr2ChTrAc:
            case R.id.btnTr3ChTrAc:
            case R.id.btnTr4ChTrAc:
            case R.id.btnTr5ChTrAc:
                btnSelect();
                break;

        }//switch

    }//onClick

    private void btnSelect() {
    }

    //запуск начала изучения
    private void startLearnWord(int selectPos) {
        tvWordChTrAc.setText(listWords.get(selectPos).getStrHeb().toString());
        List<Integer> listNumForBtn = new ArrayList<>();
        listNumForBtn.add(selectPos);
        for (int i = 0; i < 4; i++) {
            int k = Utils.getRandom(0, 19);
            listNumForBtn.add(k!=selectPos?k:k+1);
        }
        btnTr1ChTrAc.setText(listWords.get(listNumForBtn.get(0)).getStrRus().toString());
        btnTr2ChTrAc.setText(listWords.get(listNumForBtn.get(1)).getStrRus().toString());
        btnTr3ChTrAc.setText(listWords.get(listNumForBtn.get(2)).getStrRus().toString());
        btnTr4ChTrAc.setText(listWords.get(listNumForBtn.get(3)).getStrRus().toString());
        btnTr5ChTrAc.setText(listWords.get(listNumForBtn.get(4)).getStrRus().toString());
    }//startLearnWord

    //создаем коллекцию объектов слов для изучения
    private void createWordList() {
        Word word = new Word();
        for (int i = 0; i < 20; i++) {
            cursor.moveToPosition(
                    Integer.parseInt(listIdLearnWords.get(i))
            );

            //получаем данные из курсора для фильтрации
            word.setIdWord(cursor.getString(0));     //id слова
            word.setStrRus(cursor.getString(1));     //слово на русском
            word.setStrHeb(cursor.getString(2));     //слово на иврите
            word.setStrTrans(cursor.getString(3));   //транскрпция слова на иврите
            word.setGenRus(cursor.getString(4));     //род слова в русском
            word.setGenHeb(cursor.getString(5));     //род слова в иврите
            word.setMeaning(cursor.getString(6));    //значение слова в предложении
            word.setQuantity(cursor.getString(7));   //множественное или едиственное слово

            //добавляем новое слово в коллекцию
            listWords.add(word);
            word = new Word();
        }//while

    }//createWordList
}//ChooseTranslationActivity
