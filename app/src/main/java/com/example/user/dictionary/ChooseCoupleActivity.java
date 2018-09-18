package com.example.user.dictionary;

import android.content.Context;
import android.database.Cursor;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

//активность в которой происходит работа
//по изучению слов
//нужно выбрать правельные пары иврита и перевода
public class ChooseCoupleActivity extends AppCompatActivity {
    Context context;
    DBUtilities dbUtilities;
    FileUtilities FileUtilities;
    List<String> listCursorNum; // коллекция id слов для изучения
    List<Word> listWords; // коллекция слов для изучения
    Button btnTr1ChCoAc;
    Button btnTr2ChCoAc;
    Button btnTr3ChCoAc;
    Button btnTr4ChCoAc;
    Button btnTr5ChCoAc;
    Button btnHe1ChCoAc;
    Button btnHe2ChCoAc;
    Button btnHe3ChCoAc;
    Button btnHe4ChCoAc;
    Button btnHe5ChCoAc;
    int selectPos = 0;  //выбранная позиция
    int wordsCount = 10;
    int progressTime = 0;
    int progressIter;
    List<String> wordTr; //коллекция слов для проверки нажатых клавиш
    List<String> wordHe; //коллекция слов для проверки нажатых клавиш
    String selectWordTr; //слово нажатой клавишы
    String selectWordHe; //слово нажатой клавишы
    Boolean[] flagPressBtnTr = {false,false,false,false,false}; //массив флагов нажатых кнопок
    Boolean[] flagPressBtnHe = {false,false,false,false,false}; //массив флагов нажатых кнопок
    boolean flagAnyBtnPressTr = false; //проверка нажата какая-нибудь кнопка
    boolean flagAnyBtnPressHe = false; //проверка нажата какая-нибудь кнопка
    CountDownTimer countDownTimer;
    ProgressBar pbChCoAc;

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
        setContentView(R.layout.activity_choose_couple);
        FileUtilities = new FileUtilities(this);

        context = getBaseContext();
        dbUtilities = new DBUtilities(context);
        dbUtilities.open();
        listCursorNum = new ArrayList<>();
        wordTr = new ArrayList<>();
        wordHe = new ArrayList<>();
        listWords  = new ArrayList<>();
        pbChCoAc = findViewById(R.id.pbChCoAc);
        btnTr1ChCoAc = findViewById(R.id.btnTr1ChCoAc);
        btnTr2ChCoAc = findViewById(R.id.btnTr2ChCoAc);
        btnTr3ChCoAc = findViewById(R.id.btnTr3ChCoAc);
        btnTr4ChCoAc = findViewById(R.id.btnTr4ChCoAc);
        btnTr5ChCoAc = findViewById(R.id.btnTr5ChCoAc);
        btnHe1ChCoAc = findViewById(R.id.btnHe1ChCoAc);
        btnHe2ChCoAc = findViewById(R.id.btnHe2ChCoAc);
        btnHe3ChCoAc = findViewById(R.id.btnHe3ChCoAc);
        btnHe4ChCoAc = findViewById(R.id.btnHe4ChCoAc);
        btnHe5ChCoAc = findViewById(R.id.btnHe5ChCoAc);
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

        switch (view.getId()) {
            case R.id.btnTr1ChCoAc:
                //проверяем флаг и выполняем действие в layout и правим массив флагов
                if(!flagAnyBtnPressTr) {
                    flagPressBtnTr[0] = true;
                    flagAnyBtnPressTr = true;
                    selectWordTr = btnTr1ChCoAc.getText().toString();
                    btnTr1ChCoAc.setBackgroundColor(context.getResources().getColor(R.color.colorButtonPress));
                    //проверяем флаг нажатия кнопок в соседнем столбце
                    //если "да" проверяем совпадение перевода и слова
                    if(flagAnyBtnPressHe) checkPressBtn();
                }else{
                    if(flagPressBtnTr[0]) {
                        flagPressBtnTr[0] = false;
                        flagAnyBtnPressTr = false;
                        btnTr1ChCoAc.setBackgroundColor(context.getResources().getColor(R.color.colorButtonNormal));
                    }
                }//if-else
                break;

            case R.id.btnTr2ChCoAc:
                //проверяем флаг и выполняем действие в layout и правим массив флагов
                if(!flagAnyBtnPressTr) {
                    flagPressBtnTr[1] = true;
                    flagAnyBtnPressTr = true;
                    selectWordTr = btnTr2ChCoAc.getText().toString();
                    btnTr2ChCoAc.setBackgroundColor(context.getResources().getColor(R.color.colorButtonPress));
                    //проверяем флаг нажатия кнопок в соседнем столбце
                    //если "да" проверяем совпадение перевода и слова
                    if(flagAnyBtnPressHe) checkPressBtn();
                }else{
                    if(flagPressBtnTr[1]) {
                        flagPressBtnTr[1] = false;
                        flagAnyBtnPressTr = false;
                        btnTr2ChCoAc.setBackgroundColor(context.getResources().getColor(R.color.colorButtonNormal));
                    }
                }//if-else
                break;

            case R.id.btnTr3ChCoAc:
                //проверяем флаг и выполняем действие в layout и правим массив флагов
                if(!flagAnyBtnPressTr) {
                    flagPressBtnTr[2] = true;
                    flagAnyBtnPressTr = true;
                    selectWordTr = btnTr3ChCoAc.getText().toString();
                    btnTr3ChCoAc.setBackgroundColor(context.getResources().getColor(R.color.colorButtonPress));
                    //проверяем флаг нажатия кнопок в соседнем столбце
                    //если "да" проверяем совпадение перевода и слова
                    if(flagAnyBtnPressHe) checkPressBtn();
                }else{
                    if(flagPressBtnTr[2]) {
                        flagPressBtnTr[2] = false;
                        flagAnyBtnPressTr = false;
                        btnTr3ChCoAc.setBackgroundColor(context.getResources().getColor(R.color.colorButtonNormal));
                    }
                }//if-else
                break;

            case R.id.btnTr4ChCoAc:
                //проверяем флаг и выполняем действие в layout и правим массив флагов
                if(!flagAnyBtnPressTr) {
                    flagPressBtnTr[3] = true;
                    flagAnyBtnPressTr = true;
                    selectWordTr = btnTr4ChCoAc.getText().toString();
                    btnTr4ChCoAc.setBackgroundColor(context.getResources().getColor(R.color.colorButtonPress));
                    //проверяем флаг нажатия кнопок в соседнем столбце
                    //если "да" проверяем совпадение перевода и слова
                    if(flagAnyBtnPressHe) checkPressBtn();
                }else{
                    if(flagPressBtnTr[3]) {
                        flagPressBtnTr[3] = false;
                        flagAnyBtnPressTr = false;
                        btnTr4ChCoAc.setBackgroundColor(context.getResources().getColor(R.color.colorButtonNormal));
                    }
                }//if-else
                break;

            case R.id.btnTr5ChCoAc:
                //проверяем флаг и выполняем действие в layout и правим массив флагов
                if(!flagAnyBtnPressTr) {
                    flagPressBtnTr[4] = true;
                    flagAnyBtnPressTr = true;
                    selectWordTr = btnTr5ChCoAc.getText().toString();
                    btnTr5ChCoAc.setBackgroundColor(context.getResources().getColor(R.color.colorButtonPress));
                    //проверяем флаг нажатия кнопок в соседнем столбце
                    //если "да" проверяем совпадение перевода и слова
                    if(flagAnyBtnPressHe) checkPressBtn();
                }else{
                    if(flagPressBtnTr[4]) {
                        flagPressBtnTr[4] = false;
                        flagAnyBtnPressTr = false;
                        btnTr5ChCoAc.setBackgroundColor(context.getResources().getColor(R.color.colorButtonNormal));
                    }
                }//if-else
                break;

            case R.id.btnHe1ChCoAc:
                //проверяем флаг и выполняем действие в layout и правим массив флагов
                if(!flagAnyBtnPressHe) {
                    flagPressBtnHe[0] = true;
                    flagAnyBtnPressHe = true;
                    selectWordHe = btnHe1ChCoAc.getText().toString();
                    btnHe1ChCoAc.setBackgroundColor(context.getResources().getColor(R.color.colorButtonPress));
                    //проверяем флаг нажатия кнопок в соседнем столбце
                    //если "да" проверяем совпадение перевода и слова
                    if(flagAnyBtnPressTr) checkPressBtn();
                }else{
                    if(flagPressBtnHe[0]) {
                        flagPressBtnHe[0] = false;
                        flagAnyBtnPressHe = false;
                        btnHe1ChCoAc.setBackgroundColor(context.getResources().getColor(R.color.colorButtonNormal));
                    }
                }//if-else
                break;

            case R.id.btnHe2ChCoAc:
                //проверяем флаг и выполняем действие в layout и правим массив флагов
                if(!flagAnyBtnPressHe) {
                    flagPressBtnHe[1] = true;
                    flagAnyBtnPressHe = true;
                    selectWordHe = btnHe2ChCoAc.getText().toString();
                    btnHe2ChCoAc.setBackgroundColor(context.getResources().getColor(R.color.colorButtonPress));
                    //проверяем флаг нажатия кнопок в соседнем столбце
                    //если "да" проверяем совпадение перевода и слова
                    if(flagAnyBtnPressTr) checkPressBtn();
                }else{
                    if(flagPressBtnHe[1]) {
                        flagPressBtnHe[1] = false;
                        flagAnyBtnPressHe = false;
                        btnHe2ChCoAc.setBackgroundColor(context.getResources().getColor(R.color.colorButtonNormal));
                    }
                }//if-else
                break;

            case R.id.btnHe3ChCoAc:
                //проверяем флаг и выполняем действие в layout и правим массив флагов
                if(!flagAnyBtnPressHe) {
                    flagPressBtnHe[2] = true;
                    flagAnyBtnPressHe = true;
                    selectWordHe = btnHe3ChCoAc.getText().toString();
                    btnHe3ChCoAc.setBackgroundColor(context.getResources().getColor(R.color.colorButtonPress));
                    //проверяем флаг нажатия кнопок в соседнем столбце
                    //если "да" проверяем совпадение перевода и слова
                    if(flagAnyBtnPressTr) checkPressBtn();
                }else{
                    if(flagPressBtnHe[2]) {
                        flagPressBtnHe[2] = false;
                        flagAnyBtnPressHe = false;
                        btnHe3ChCoAc.setBackgroundColor(context.getResources().getColor(R.color.colorButtonNormal));
                    }
                }//if-else
                break;

            case R.id.btnHe4ChCoAc:
                //проверяем флаг и выполняем действие в layout и правим массив флагов
                if(!flagAnyBtnPressHe) {
                    flagPressBtnHe[3] = true;
                    flagAnyBtnPressHe = true;
                    selectWordHe = btnHe4ChCoAc.getText().toString();
                    btnHe4ChCoAc.setBackgroundColor(context.getResources().getColor(R.color.colorButtonPress));
                    //проверяем флаг нажатия кнопок в соседнем столбце
                    //если "да" проверяем совпадение перевода и слова
                    if(flagAnyBtnPressTr) checkPressBtn();
                }else{
                    if(flagPressBtnHe[3]) {
                        flagPressBtnHe[3] = false;
                        flagAnyBtnPressHe = false;
                        btnHe4ChCoAc.setBackgroundColor(context.getResources().getColor(R.color.colorButtonNormal));
                    }
                }//if-else
                break;

            case R.id.btnHe5ChCoAc:
                //проверяем флаг и выполняем действие в layout и правим массив флагов
                if(!flagAnyBtnPressHe) {
                    flagPressBtnHe[4] = true;
                    flagAnyBtnPressHe = true;
                    selectWordHe = btnHe5ChCoAc.getText().toString();
                    btnHe5ChCoAc.setBackgroundColor(context.getResources().getColor(R.color.colorButtonPress));
                    //проверяем флаг нажатия кнопок в соседнем столбце
                    //если "да" проверяем совпадение перевода и слова
                    if(flagAnyBtnPressTr) checkPressBtn();
                }else{
                    if(flagPressBtnHe[4]) {
                        flagPressBtnHe[4] = false;
                        flagAnyBtnPressHe = false;
                        btnHe5ChCoAc.setBackgroundColor(context.getResources().getColor(R.color.colorButtonNormal));
                    }
                }//if-else
                break;

        }//switch
    }//onClick

    //проверка совпадения нажатой пары клавиш
    private void checkPressBtn() {
        //сложная проверка))
        //по русскому слову находим порядковый номер в коллекции
        int indexTr = wordTr.indexOf(selectWordTr);

        //и проверяем совподает ли слово на кнопке со словом в коллекцыи
        // ивритовских слов
        String strHe  = wordHe.get(indexTr);
        if(selectWordHe.equals(strHe)){
            //увеличиваем прогресс бар
            progressTime = progressTime + progressIter;
            pbChCoAc.setProgress(progressTime);
        }
    }//checkPressBtn

    //обработка правельно выбранного перевода
    private void btnSelectTrue() {
        //увеличиваем прогресс бар
        progressTime = progressTime + progressIter;
        pbChCoAc.setProgress(progressTime);
        //отключаем кнопки для исключения случайного нажатия
//        btnTr1ChTrAc.setEnabled(false);
//        btnTr2ChTrAc.setEnabled(false);
//        btnTr3ChTrAc.setEnabled(false);
//        btnTr4ChTrAc.setEnabled(false);
//        btnTr5ChTrAc.setEnabled(false);
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
//                btnTr1ChTrAc.setEnabled(true);
//                btnTr2ChTrAc.setEnabled(true);
//                btnTr3ChTrAc.setEnabled(true);
//                btnTr4ChTrAc.setEnabled(true);
//                btnTr5ChTrAc.setEnabled(true);
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
        btnTr1ChCoAc.setBackgroundColor(context.getResources().getColor(R.color.colorButtonNormal));
        btnTr2ChCoAc.setBackgroundColor(context.getResources().getColor(R.color.colorButtonNormal));
        btnTr3ChCoAc.setBackgroundColor(context.getResources().getColor(R.color.colorButtonNormal));
        btnTr4ChCoAc.setBackgroundColor(context.getResources().getColor(R.color.colorButtonNormal));
        btnTr5ChCoAc.setBackgroundColor(context.getResources().getColor(R.color.colorButtonNormal));
        btnHe1ChCoAc.setBackgroundColor(context.getResources().getColor(R.color.colorButtonNormal));
        btnHe2ChCoAc.setBackgroundColor(context.getResources().getColor(R.color.colorButtonNormal));
        btnHe3ChCoAc.setBackgroundColor(context.getResources().getColor(R.color.colorButtonNormal));
        btnHe4ChCoAc.setBackgroundColor(context.getResources().getColor(R.color.colorButtonNormal));
        btnHe5ChCoAc.setBackgroundColor(context.getResources().getColor(R.color.colorButtonNormal));
        //устанавливаем слова на кнопки
        btnTr1ChCoAc.setText(listWords.get(listNumForBtn.get(0)).getStrRus());
        btnTr2ChCoAc.setText(listWords.get(listNumForBtn.get(1)).getStrRus());
        btnTr3ChCoAc.setText(listWords.get(listNumForBtn.get(2)).getStrRus());
        btnTr4ChCoAc.setText(listWords.get(listNumForBtn.get(3)).getStrRus());
        btnTr5ChCoAc.setText(listWords.get(listNumForBtn.get(4)).getStrRus());
        btnHe1ChCoAc.setText(listWords.get(listNumForBtn.get(0)).getStrHeb());
        btnHe2ChCoAc.setText(listWords.get(listNumForBtn.get(1)).getStrHeb());
        btnHe3ChCoAc.setText(listWords.get(listNumForBtn.get(2)).getStrHeb());
        btnHe4ChCoAc.setText(listWords.get(listNumForBtn.get(3)).getStrHeb());
        btnHe5ChCoAc.setText(listWords.get(listNumForBtn.get(4)).getStrHeb());
    }//startLearnWord

    //создаем коллекцию объектов слов для изучения
    private void createWordList() {
        Word word = new Word();
        for (int i = 0; i < wordsCount; i++) {
            cursor.moveToPosition(
                    Integer.parseInt(listCursorNum.get(i))
            );

            //заполняем коллекции слов для проверки нажатых клавиш
            wordHe.add(cursor.getString(2));     //слово на иврите
            wordTr.add(cursor.getString(1));     //слово на русском

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
}//ChooseCoupleActivity
