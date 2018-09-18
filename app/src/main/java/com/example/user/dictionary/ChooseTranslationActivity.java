package com.example.user.dictionary;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
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
    int selectPos = 0;  //выбранная позиция

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
        startLearnWord();
    }//onCreate

    //обработчик кнопок при изучении
    public void onClick(View view) {
        String selectWord;
        switch (view.getId()) {
            case R.id.btnTr1ChTrAc:
                selectWord = btnTr1ChTrAc.getText().toString();
                if(!listWords.get(selectPos).getStrRus().toString().equals(selectWord)){
                    btnTr1ChTrAc.setBackgroundColor(context.getResources().getColor(R.color.colorAccent));
                }else {
                    btnTr1ChTrAc.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
                    btnSelectTrue();
                }//if-else
                break;
            case R.id.btnTr2ChTrAc:
                selectWord = btnTr2ChTrAc.getText().toString();
                if(!listWords.get(selectPos).getStrRus().toString().equals(selectWord)){
                    btnTr2ChTrAc.setBackgroundColor(context.getResources().getColor(R.color.colorAccent));
                }else {
                    btnTr2ChTrAc.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
                    btnSelectTrue();
                }//if-else
                break;
            case R.id.btnTr3ChTrAc:
                selectWord = btnTr3ChTrAc.getText().toString();
                if(!listWords.get(selectPos).getStrRus().toString().equals(selectWord)){
                    btnTr3ChTrAc.setBackgroundColor(context.getResources().getColor(R.color.colorAccent));
                }else {
                    btnTr3ChTrAc.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
                    btnSelectTrue();
                }//if-else
                break;
            case R.id.btnTr4ChTrAc:
                selectWord = btnTr4ChTrAc.getText().toString();
                if(!listWords.get(selectPos).getStrRus().toString().equals(selectWord)){
                    btnTr4ChTrAc.setBackgroundColor(context.getResources().getColor(R.color.colorAccent));
                }else {
                    btnTr4ChTrAc.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
                    btnSelectTrue();
                }//if-else
                break;
            case R.id.btnTr5ChTrAc:
                selectWord = btnTr5ChTrAc.getText().toString();
                if(!listWords.get(selectPos).getStrRus().toString().equals(selectWord)){
                    btnTr5ChTrAc.setBackgroundColor(context.getResources().getColor(R.color.colorAccent));
                }else {
                    btnTr5ChTrAc.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
                    btnSelectTrue();
                }//if-else
                break;
        }//switch
    }//onClick

    //обработка правельно выбранного перевода
    private void btnSelectTrue() {
        if(selectPos < 19){
            selectPos++;
            startLearnWord();
        }else{
            finish();
        }//if-else
    }//btnSelect

    //запуск начала изучения
    private void startLearnWord() {
        tvWordChTrAc.setText(listWords.get(selectPos).getStrHeb().toString());
        List<Integer> listNumForBtn = new ArrayList<>();
        listNumForBtn.add(selectPos);
        for (int i = 0; i < 4; i++) {
            int k;
            while(true) {
                k = Utils.getRandom(0, 19);
                if((k>0)&&(k<19)&&(!listNumForBtn.contains(k))) break;
            }//while
            listNumForBtn.add(k);
        }//for

        //перемешать коллекцию переводов для вывода названия кнопок
        Collections.shuffle(listNumForBtn);
        //устанавливаем нормальный цвет кнопок
        btnTr1ChTrAc.setBackgroundColor(context.getResources().getColor(R.color.colorButtonNormal));
        btnTr2ChTrAc.setBackgroundColor(context.getResources().getColor(R.color.colorButtonNormal));
        btnTr3ChTrAc.setBackgroundColor(context.getResources().getColor(R.color.colorButtonNormal));
        btnTr4ChTrAc.setBackgroundColor(context.getResources().getColor(R.color.colorButtonNormal));
        btnTr5ChTrAc.setBackgroundColor(context.getResources().getColor(R.color.colorButtonNormal));
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
