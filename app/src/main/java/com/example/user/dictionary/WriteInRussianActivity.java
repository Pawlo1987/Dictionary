package com.example.user.dictionary;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

//активность в которой происходит работа
//по изучению слов
//выводится слово на иврите
//и нужно написать правельный перевод на русском
public class WriteInRussianActivity extends AppCompatActivity {
    Context context;
    DBUtilities dbUtilities;
    FileUtilities FileUtilities;
    List<String> listCursorNum; // коллекция id слов для изучения
    List<Word> listWords; // коллекция слов для изучения
    TextView tvWordWrInRuAc;
    EditText etTransWrInRuAc;
    Button btnCheckWrInRuAc;
    Button btnNextWrInRuAc;
    int selectPos = 0;  //выбранная позиция
    int wordsCount = 10;
    int progressTime = 0;
    int progressIter;
    CountDownTimer countDownTimer;
    ProgressBar pbWrInRuAc;
    Switch swHelpWrInRuAc;

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
        setContentView(R.layout.activity_write_in_russian);
        FileUtilities = new FileUtilities(this);

        context = getBaseContext();
        dbUtilities = new DBUtilities(context);
        dbUtilities.open();
        listCursorNum = new ArrayList<>();
        listWords  = new ArrayList<>();
        pbWrInRuAc = findViewById(R.id.pbWrInRuAc);
        swHelpWrInRuAc = findViewById(R.id.swHelpWrInRuAc);
        tvWordWrInRuAc = findViewById(R.id.tvWordWrInRuAc);
        etTransWrInRuAc = findViewById(R.id.etTransWrInRuAc);
        btnCheckWrInRuAc = findViewById(R.id.btnCheckWrInRuAc);
        btnNextWrInRuAc = findViewById(R.id.btnNextWrInRuAc);
        cursor = dbUtilities.getDb().rawQuery(mainQuery, null);
        listCursorNum.addAll(getIntent().getStringArrayListExtra("idList"));
        wordsCount = getIntent().getIntExtra("wordsCount",0);
        progressIter = 100 / wordsCount;
        progressTime = 0;
        createWordList();
        startLearnWord();
    }//onCreate

    //запуск начала изучения
    private void startLearnWord() {
        tvWordWrInRuAc.setText(listWords.get(selectPos).getStrHeb());

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
//            word.setStrTrans(cursor.getString(3));   //транскрпция слова на иврите
//            word.setGenRus(cursor.getString(4));     //род слова в русском
//            word.setGenHeb(cursor.getString(5));     //род слова в иврите
//            word.setMeaning(cursor.getString(6));    //значение слова в предложении
//            word.setQuantity(cursor.getString(7));   //множественное или едиственное слово

            //добавляем новое слово в коллекцию
            listWords.add(word);
            word = new Word();
        }//while
        //перемешаем полученную коллекцию
        Collections.shuffle(listWords);
    }//createWordList

    //обработчик нажатия кнопок
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnCheckWrInRuAc:
                if(listWords.get(selectPos).getStrRus().equals(etTransWrInRuAc.getText().toString())){
                translationOK();
                }else{
                    Toast.makeText(context,"wrong translation!",Toast.LENGTH_SHORT).show();
                }//if-else
                break;
            case R.id.btnNextWrInRuAc:
                nextWord();
                break;
            case R.id.swHelpWrInRuAc:
                helpSwitch();
                break;
        }//switch
    }//onClick

    //обработчик нажатия переключатиля "HELP"
    private void helpSwitch() {
        //если нажимаем на переключатель то вместо слова
        // на иврите появляется слово перевод на русском
        //для того что бы убрать перевод на русском
        // необходимо выключить переключатель
        if(swHelpWrInRuAc.isChecked())
            tvWordWrInRuAc.setText(listWords.get(selectPos).getStrRus());
        else
            tvWordWrInRuAc.setText(listWords.get(selectPos).getStrHeb());
    }//helpSwitch

    //метод для того что бы спрятать клавиатуру
    //http://www.ohandroid.com/4079.html
    public static void hideKeyboard(Activity activity) {
        if (activity != null) {
            InputMethodManager inputManager =
                    (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (activity.getCurrentFocus() != null && inputManager != null) {
                inputManager.hideSoftInputFromWindow(
                        activity.getCurrentFocus().getWindowToken(), 0
                );
                inputManager.showSoftInputFromInputMethod(
                        activity.getCurrentFocus().getWindowToken(), 0
                );
            }//if
        }//if
    }//hideKeyboard

    //обработка правельно выбранного перевода
    private void translationOK() {
        //увеличиваем прогресс бар
        progressTime = progressTime + progressIter;
        pbWrInRuAc.setProgress(progressTime);
        tvWordWrInRuAc.setText("CORRECT!");
        //отключаем кнопки для исключения случайного нажатия
        btnCheckWrInRuAc.setEnabled(false);
        btnNextWrInRuAc.setEnabled(false);
        nextWord();
    }//translationOK

    //процедура перехода к следующему слову
    private void nextWord() {
        //спрятать клавиатуру
        hideKeyboard(this);
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
                btnCheckWrInRuAc.setEnabled(true);
                btnNextWrInRuAc.setEnabled(true);
                etTransWrInRuAc.setText("");
                etTransWrInRuAc.clearFocus();
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
    }//nextWord
}//WriteInRussianActivity
