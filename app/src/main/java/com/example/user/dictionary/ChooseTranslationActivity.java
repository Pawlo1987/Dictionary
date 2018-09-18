package com.example.user.dictionary;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

//активность в которой происходит работа
//по изучению слов
//выводится слово на иврите
//и нужно выбрать правельный перевод
public class ChooseTranslationActivity extends AppCompatActivity {
    Context context;
    DBUtilities dbUtilities;
    FileUtilities FileUtilities;
    List<String> listCursorNum; // коллекция id слов для изучения
    List<Word> listWords; // коллекция слов для изучения
    TextView tvWordChTrAc;
    TextView tvTranscChTrAc;
    Button btnTr1ChTrAc;
    Button btnTr2ChTrAc;
    Button btnTr3ChTrAc;
    Button btnTr4ChTrAc;
    Button btnTr5ChTrAc;
    int selectPos = 0;  //выбранная позиция
    int wordsCount = 10;
    int progressTime = 0;
    int progressIter;
    CountDownTimer countDownTimer;
    ProgressBar pbChTrAc;

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
        listCursorNum = new ArrayList<>();
        listWords  = new ArrayList<>();
        pbChTrAc = findViewById(R.id.pbChTrAc);
        tvWordChTrAc = findViewById(R.id.tvWordChTrAc);
        tvTranscChTrAc = findViewById(R.id.tvTranscChTrAc);
        btnTr1ChTrAc = findViewById(R.id.btnTr1ChTrAc);
        btnTr2ChTrAc = findViewById(R.id.btnTr2ChTrAc);
        btnTr3ChTrAc = findViewById(R.id.btnTr3ChTrAc);
        btnTr4ChTrAc = findViewById(R.id.btnTr4ChTrAc);
        btnTr5ChTrAc = findViewById(R.id.btnTr5ChTrAc);
        cursor = dbUtilities.getDb().rawQuery(mainQuery, null);
        listCursorNum.addAll(getIntent().getStringArrayListExtra("idList"));
        wordsCount = getIntent().getIntExtra("wordsCount",0);
        progressIter = 100 / wordsCount;
        progressTime = 0;
        createWordList();
        startLearnWord();
    }//onCreate

    //обработчик кнопок при изучении
    public void onClick(View view) {
        String selectWord;
        switch (view.getId()) {
            case R.id.btnTr1ChTrAc:
                selectWord = btnTr1ChTrAc.getText().toString();
                if(!listWords.get(selectPos).getStrRus().equals(selectWord)){
                    btnTr1ChTrAc.setBackgroundColor(context.getResources().getColor(R.color.colorAccent));
                }else {
                    btnTr1ChTrAc.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
                    btnSelectTrue();
                }//if-else
                break;
            case R.id.btnTr2ChTrAc:
                selectWord = btnTr2ChTrAc.getText().toString();
                if(!listWords.get(selectPos).getStrRus().equals(selectWord)){
                    btnTr2ChTrAc.setBackgroundColor(context.getResources().getColor(R.color.colorAccent));
                }else {
                    btnTr2ChTrAc.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
                    btnSelectTrue();
                }//if-else
                break;
            case R.id.btnTr3ChTrAc:
                selectWord = btnTr3ChTrAc.getText().toString();
                if(!listWords.get(selectPos).getStrRus().equals(selectWord)){
                    btnTr3ChTrAc.setBackgroundColor(context.getResources().getColor(R.color.colorAccent));
                }else {
                    btnTr3ChTrAc.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
                    btnSelectTrue();
                }//if-else
                break;
            case R.id.btnTr4ChTrAc:
                selectWord = btnTr4ChTrAc.getText().toString();
                if(!listWords.get(selectPos).getStrRus().equals(selectWord)){
                    btnTr4ChTrAc.setBackgroundColor(context.getResources().getColor(R.color.colorAccent));
                }else {
                    btnTr4ChTrAc.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
                    btnSelectTrue();
                }//if-else
                break;
            case R.id.btnTr5ChTrAc:
                selectWord = btnTr5ChTrAc.getText().toString();
                if(!listWords.get(selectPos).getStrRus().equals(selectWord)){
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
        //увеличиваем прогресс бар
        progressTime = progressTime + progressIter;
        pbChTrAc.setProgress(progressTime);
        //отключаем кнопки для исключения случайного нажатия
        btnTr1ChTrAc.setEnabled(false);
        btnTr2ChTrAc.setEnabled(false);
        btnTr3ChTrAc.setEnabled(false);
        btnTr4ChTrAc.setEnabled(false);
        btnTr5ChTrAc.setEnabled(false);
        //для создания небольшой задерки в 500 миллисекунд
        //Создаем таймер обратного отсчета на 500 миллисекунд с шагом отсчета
        //в 1 секунду (задаем значения в миллисекундах):
        countDownTimer = new CountDownTimer(500, 1000) {
            //Здесь можно выполнить какието дейстивия через кажду секунду
            //до конца счета таймера
            public void onTick(long millisUntilFinished) { }
            //Задаем действия после завершения отсчета (запускаем главную активность)
            public void onFinish(){
                //включаем кнопки обратно
                btnTr1ChTrAc.setEnabled(true);
                btnTr2ChTrAc.setEnabled(true);
                btnTr3ChTrAc.setEnabled(true);
                btnTr4ChTrAc.setEnabled(true);
                btnTr5ChTrAc.setEnabled(true);
                //переход к следующему слову или выход из режима изучения
                //так как закончились слова
                if(selectPos < wordsCount-1){
                    selectPos++;
                    startLearnWord();
                }else{
                    finish();
                }//if-else
            }//onFinish
        };//countDownTimer
        //запускам таймер
        countDownTimer.start();
    }//btnSelect

    //запуск начала изучения
    private void startLearnWord() {
        tvWordChTrAc.setText(listWords.get(selectPos).getStrHeb());
        tvTranscChTrAc.setText(listWords.get(selectPos).getStrTrans());
        List<Integer> listNumForBtn = new ArrayList<>();
        listNumForBtn.add(selectPos);
        for (int i = 0; i < 4; i++) {
            int k;
            while(true) {
                k = Utils.getRandom(0, wordsCount-1);
                if((k>0)&&(k<wordsCount-1)&&(!listNumForBtn.contains(k))) break;
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
        btnTr1ChTrAc.setText(listWords.get(listNumForBtn.get(0)).getStrRus());
        btnTr2ChTrAc.setText(listWords.get(listNumForBtn.get(1)).getStrRus());
        btnTr3ChTrAc.setText(listWords.get(listNumForBtn.get(2)).getStrRus());
        btnTr4ChTrAc.setText(listWords.get(listNumForBtn.get(3)).getStrRus());
        btnTr5ChTrAc.setText(listWords.get(listNumForBtn.get(4)).getStrRus());
    }//startLearnWord

    //создаем коллекцию объектов слов для изучения
    private void createWordList() {
        Word word = new Word();
        for (int i = 0; i < wordsCount; i++) {
            cursor.moveToPosition(
                    Integer.parseInt(listCursorNum.get(i))
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
