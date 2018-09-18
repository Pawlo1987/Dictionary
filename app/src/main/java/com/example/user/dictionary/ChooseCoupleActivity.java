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
    List<Word> list5WordsOnScreen; // коллекция 5 слов для изучения
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
    List<Button> buttonsList;//коллекция кнопок
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
    int countRemainingWords; //счетчик оставшихся неотгаданых слов
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
        list5WordsOnScreen  = new ArrayList<>();
        pbChCoAc = findViewById(R.id.pbChCoAc);
        buttonsList = new ArrayList<>();
        btnTr1ChCoAc = findViewById(R.id.btnTr1ChCoAc);
        buttonsList.add(btnTr1ChCoAc);
        btnTr2ChCoAc = findViewById(R.id.btnTr2ChCoAc);
        buttonsList.add(btnTr2ChCoAc);
        btnTr3ChCoAc = findViewById(R.id.btnTr3ChCoAc);
        buttonsList.add(btnTr3ChCoAc);
        btnTr4ChCoAc = findViewById(R.id.btnTr4ChCoAc);
        buttonsList.add(btnTr4ChCoAc);
        btnTr5ChCoAc = findViewById(R.id.btnTr5ChCoAc);
        buttonsList.add(btnTr5ChCoAc);
        btnHe1ChCoAc = findViewById(R.id.btnHe1ChCoAc);
        buttonsList.add(btnHe1ChCoAc);
        btnHe2ChCoAc = findViewById(R.id.btnHe2ChCoAc);
        buttonsList.add(btnHe2ChCoAc);
        btnHe3ChCoAc = findViewById(R.id.btnHe3ChCoAc);
        buttonsList.add(btnHe3ChCoAc);
        btnHe4ChCoAc = findViewById(R.id.btnHe4ChCoAc);
        buttonsList.add(btnHe4ChCoAc);
        btnHe5ChCoAc = findViewById(R.id.btnHe5ChCoAc);
        buttonsList.add(btnHe5ChCoAc);
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
                pressBtnTest(0);
                break;
            case R.id.btnTr2ChCoAc:
                pressBtnTest(1);
                break;
            case R.id.btnTr3ChCoAc:
                pressBtnTest(2);
                break;
            case R.id.btnTr4ChCoAc:
                pressBtnTest(3);
                break;
            case R.id.btnTr5ChCoAc:
                pressBtnTest(4);
                break;
            case R.id.btnHe1ChCoAc:
                pressBtnTest(5);
                break;
            case R.id.btnHe2ChCoAc:
                pressBtnTest(6);
                break;
            case R.id.btnHe3ChCoAc:
                pressBtnTest(7);
                break;
            case R.id.btnHe4ChCoAc:
                pressBtnTest(8);
                break;
            case R.id.btnHe5ChCoAc:
                pressBtnTest(9);
                break;
        }//switch
    }//onClick

    //проверяем нажатие кнопок
    private void pressBtnTest(int i) {
        if(i<=4) {
            //проверяем флаг и выполняем действие в layout и правим массив флагов
            if (!flagAnyBtnPressTr) {
                flagPressBtnTr[i] = true;
                flagAnyBtnPressTr = true;
                selectWordTr = buttonsList.get(i).getText().toString();
                buttonsList.get(i).setBackgroundColor(context.getResources().getColor(R.color.colorButtonPress));
                //проверяем флаг нажатия кнопок в соседнем столбце
                //если "да" проверяем совпадение перевода и слова
                if (flagAnyBtnPressHe) checkPressBtn();
            } else {
                if (flagPressBtnTr[i]) {
                    flagPressBtnTr[i] = false;
                    flagAnyBtnPressTr = false;
                    buttonsList.get(i).setBackgroundColor(context.getResources().getColor(R.color.colorButtonNormal));
                }
            }//if-else
        }else{
            int j = i-5;
            //проверяем флаг и выполняем действие в layout и правим массив флагов
                if(!flagAnyBtnPressHe) {
                    flagPressBtnHe[j] = true;
                    flagAnyBtnPressHe = true;
                    selectWordHe = buttonsList.get(i).getText().toString();
                    buttonsList.get(i).setBackgroundColor(context.getResources().getColor(R.color.colorButtonPress));
                    //проверяем флаг нажатия кнопок в соседнем столбце
                    //если "да" проверяем совпадение перевода и слова
                    if(flagAnyBtnPressTr) checkPressBtn();
                }else{
                    if(flagPressBtnHe[j]) {
                        flagPressBtnHe[j] = false;
                        flagAnyBtnPressHe = false;
                        buttonsList.get(i).setBackgroundColor(context.getResources().getColor(R.color.colorButtonNormal));
                    }
                }//if-else
        }//if-else
    }//pressBtnTest

    //проверка совпадения нажатой пары клавиш
    private void checkPressBtn() {
        //сложная проверка))
        //по русскому слову паралельно и по ивритовскому слово
        //находим порядковый номер в коллекции
        int indexHe = wordHe.indexOf(selectWordHe);
        int indexTr = wordTr.indexOf(selectWordTr);

        //и проверяем совподает ли слово на кнопке со словом в коллекции
        // ивритовских слов и паралельно русских слов
        String strHe  = wordHe.get(indexTr);
        String strTr  = wordTr.get(indexHe);
        if((selectWordHe.equals(strHe))||(selectWordTr.equals(strTr))){
            //снимаем флаги нажатых клавиш
            flagAnyBtnPressHe = false;
            flagAnyBtnPressTr = false;
            //увеличиваем прогресс бар
            progressTime = progressTime + progressIter;
            pbChCoAc.setProgress(progressTime);
            // убираем с экрана элементы которые совпали
            //setVisibility(View.GONE) отключаем ненужные элементы для просмотра
            for (int i = 0; i < 10; i++) {
                if(i<=4) {
                    if(flagPressBtnTr[i]) {
                        buttonsList.get(i).setEnabled(false);
                        buttonsList.get(i).setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
                        flagPressBtnTr[i] = false;
                    }//if
                }else{
                    int j = i-5;
                    if(flagPressBtnHe[j]) {
                        buttonsList.get(i).setEnabled(false);
                        buttonsList.get(i).setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
                        flagPressBtnHe[j] = false;
                    }//if
                }////if-else
            }//for

            //проверка сколько слов осталось угадать
            //если все угаданы формируем экран заново
            if(countRemainingWords > 0) countRemainingWords--;
            else{
                int n = list5WordsOnScreen.size();
                //удаляем из коллекции listWords отгаданые слова
                for (int i = 0; i < n; i++) {
                    listWords.remove(list5WordsOnScreen.get(i));
                }//for

                //если в коллекции еще остались слова продолжаем
                //иначе возврат в предыдущую активность
                if(listWords.size()>0) {
                    //очищяем временую коллекцию для следующей порции слов
                    list5WordsOnScreen.clear();
                    startLearnWord();
                }else finish();
            }//if-else
        }//if
    }//checkPressBtn

    //запуск начала изучения
    //формирование экранна со словами для изучения
    private void startLearnWord() {
        int iFirstStopEn; //первая итерация после которой кнопки должны быть не активны
        int iSecondStopEn;//вторая итерация после которой кнопки должны быть не активны
        //5 id-ков из коллекции значений коллекция listWords
        // для установки в название кнопок
        List<Integer> listIdNumForBtn = new ArrayList<>();

        //проверяем сколько слов осталось
        //в коллекции listWords для изучения
        //и устанавлеваем их id в коллекцию listIdNumForBtn
        int n = listWords.size();

        //если слов больше пяти
        //устанавливаем n=5 т.к. кнопки будут заполнены все
        // заведомо устанавливаем ложную информацию
        // iFirstStopEn=n; iSecondStopEn=n;
        //если слов менше пяти
        //устанавливаем iFirstStopEn=n-1; iSecondStopEn=n-1;
        if(n >= 5) {
            n=5;
            iFirstStopEn=n;
            iSecondStopEn=n;
            //устанавливаем счетчик оставшихся значений
            countRemainingWords = n-1;
        }else{
            //устанавливаем счетчик оставшихся значений
            countRemainingWords = n-1;
            iFirstStopEn=n-1;
            iSecondStopEn=n+4;
        }//if-else
        for (int i = 0; i < n; i++) {
            //заполняем временую коллекцию id слов для вывода на экран
            listIdNumForBtn.add(i);

            //заполняем временую коллекцию слов для вывода на экран
            list5WordsOnScreen.add(listWords.get(i));
        }//for

        //устанавливаем слова на кнопки
        //перемешать коллекцию
        Collections.shuffle(listIdNumForBtn);

        int j=0; //итерационная переменная для коллекции list5WordsOnScreen
        for (int i = 0; i < 10; i++) {
            //если i==5, первым делом перемешаем
            //коллекцию порядковых номеров,
            //чтоб порядок слов в первом столбике отличались
            //от порядка слов во втором столбике.
            //перемешиваем пару раз чтоб эффективнее
            //и обнуляем j порядковый номер коллекции list5WordsOnScreen
            if(i==n) {
                Collections.shuffle(listIdNumForBtn);
                Collections.shuffle(listIdNumForBtn);
                j=0;
            }//if
            //выключаем активность кнопок если итерация
            // попадает в условия
            if((((i<5)&&(iFirstStopEn<i))||(iSecondStopEn<i))&&(iSecondStopEn!=iFirstStopEn)){
                buttonsList.get(i).setText("");
                continue;
            }//if
            //делаем кнопки активными,
            // так как они становятся неактивные
            // после первого отгаданого окна
            buttonsList.get(i).setEnabled(true);
            //устанавливаем нормальный цвет кнопок
            buttonsList.get(i).setBackgroundColor(context.getResources().getColor(R.color.colorButtonNormal));
            //i:0-4 устанавливаем слова в первом столбике
            //i:5-9 устанавливаем слова во втором столбике
            if(i<=4){
                buttonsList.get(i).setText(list5WordsOnScreen.get(listIdNumForBtn.get(j)).getStrRus());
            }else{
                buttonsList.get(i).setText(list5WordsOnScreen.get(listIdNumForBtn.get(j)).getStrHeb());
            }//if-else
            j++;
        }//for
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
//            word.setGenRus(cursor.getString(4));     //род слова в русском
//            word.setGenHeb(cursor.getString(5));     //род слова в иврите
//            word.setMeaning(cursor.getString(6));    //значение слова в предложении
//            word.setQuantity(cursor.getString(7));   //множественное или едиственное слово

            //добавляем новое слово в коллекцию
            listWords.add(word);
            word = new Word();
        }//while
        //перемешаем полученную коллекцию
//        Collections.shuffle(listWords);
    }//createWordList
}//ChooseCoupleActivity
