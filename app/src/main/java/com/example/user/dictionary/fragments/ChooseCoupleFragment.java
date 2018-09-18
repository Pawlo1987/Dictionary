package com.example.user.dictionary.Fragments;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.user.dictionary.DBUtilities;
import com.example.user.dictionary.FileUtilities;
import com.example.user.dictionary.R;
import com.example.user.dictionary.Utils;
import com.example.user.dictionary.Word;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

//Фрагмент в которой происходит работа
//по изучению слов
//нужно выбрать правельные пары иврита и перевода
public class ChooseCoupleFragment extends Fragment {
    Context context;
    DBUtilities dbUtilities;
    FileUtilities FileUtilities;
    List<Word> listWords; // коллекция слов для изучения
    List<Word> list5WordsOnScreen; // коллекция 5 слов для изучения
    //переменная необходимая для итераци слов если фрагмент вызван из mixMethod
    int interForMixMethod = 0;
    Button btnTr1ChCoFr;
    Button btnTr2ChCoFr;
    Button btnTr3ChCoFr;
    Button btnTr4ChCoFr;
    Button btnTr5ChCoFr;
    Button btnHe1ChCoFr;
    Button btnHe2ChCoFr;
    Button btnHe3ChCoFr;
    Button btnHe4ChCoFr;
    Button btnHe5ChCoFr;
    List<Button> buttonsList;//коллекция кнопок
    int wordsCount = 10;
    int progressTime = 0;
    int progressIter;
    List<String> wordTr; //коллекция слов для проверки нажатых клавиш
    List<String> wordHe; //коллекция слов для проверки нажатых клавиш
    String selectWordTr; //слово нажатой клавишы
    String selectWordHe; //слово нажатой клавишы
    Boolean[] flagPressBtnTr = {false, false, false, false, false}; //массив флагов нажатых кнопок
    Boolean[] flagPressBtnHe = {false, false, false, false, false}; //массив флагов нажатых кнопок
    boolean flagAnyBtnPressTr = false; //проверка нажата какая-нибудь кнопка
    boolean flagAnyBtnPressHe = false; //проверка нажата какая-нибудь кнопка
    int countRemainingWords; //счетчик оставшихся неотгаданых слов
    ProgressBar pbBaMeAc;
    private Cursor cursor;
    List<String> listCursorNumFromActivity; // коллекция слов для изучения полученая из Activity
    TextView tvMixMethodPos;//TextView для глобальной позиция для mixMethod
    boolean isMixMethod;    //флаг проверки если фрагмент вызван из mixMethod
    Button btnActivity;

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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbUtilities.open();
        wordTr = new ArrayList<>();
        wordHe = new ArrayList<>();
        list5WordsOnScreen = new ArrayList<>();
        buttonsList = new ArrayList<>();
        listWords = new ArrayList<>();
        listCursorNumFromActivity = new ArrayList<>();
        isMixMethod = getArguments().getBoolean("isMixMethod");
        listCursorNumFromActivity.addAll(getArguments().getStringArrayList("idList"));
    }//onCreate

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View resultView = inflater.inflate(R.layout.fragment_choose_couple, container, false);
        btnTr1ChCoFr = resultView.findViewById(R.id.btnTr1ChCoFr);
        btnTr2ChCoFr = resultView.findViewById(R.id.btnTr2ChCoFr);
        btnTr3ChCoFr = resultView.findViewById(R.id.btnTr3ChCoFr);
        btnTr4ChCoFr = resultView.findViewById(R.id.btnTr4ChCoFr);
        btnTr5ChCoFr = resultView.findViewById(R.id.btnTr5ChCoFr);
        btnHe1ChCoFr = resultView.findViewById(R.id.btnHe1ChCoFr);
        btnHe2ChCoFr = resultView.findViewById(R.id.btnHe2ChCoFr);
        btnHe3ChCoFr = resultView.findViewById(R.id.btnHe3ChCoFr);
        btnHe4ChCoFr = resultView.findViewById(R.id.btnHe4ChCoFr);
        btnHe5ChCoFr = resultView.findViewById(R.id.btnHe5ChCoFr);

        buttonsList.add(btnTr1ChCoFr);
        buttonsList.add(btnTr2ChCoFr);
        buttonsList.add(btnTr3ChCoFr);
        buttonsList.add(btnTr4ChCoFr);
        buttonsList.add(btnTr5ChCoFr);
        buttonsList.add(btnHe1ChCoFr);
        buttonsList.add(btnHe2ChCoFr);
        buttonsList.add(btnHe3ChCoFr);
        buttonsList.add(btnHe4ChCoFr);
        buttonsList.add(btnHe5ChCoFr);

        btnActivity = getActivity().findViewById(R.id.btnActivity);
        tvMixMethodPos = getActivity().findViewById(R.id.tvMixMethodPos);
        pbBaMeAc = getActivity().findViewById(R.id.pbBaMeAc);

        //обработчик кнопок при изучении
        btnTr1ChCoFr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pressBtnTest(0);
            }//onClick
        });//setOnClickListener
        btnTr2ChCoFr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pressBtnTest(1);
            }//onClick
        });//setOnClickListener
        btnTr3ChCoFr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pressBtnTest(2);
            }//onClick
        });//setOnClickListener
        btnTr4ChCoFr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pressBtnTest(3);
            }//onClick
        });//setOnClickListener
        btnTr5ChCoFr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pressBtnTest(4);
            }//onClick
        });//setOnClickListener
        btnHe1ChCoFr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pressBtnTest(5);
            }//onClick
        });//setOnClickListener
        btnHe2ChCoFr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pressBtnTest(6);
            }//onClick
        });//setOnClickListener
        btnHe3ChCoFr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pressBtnTest(7);
            }//onClick
        });//setOnClickListener
        btnHe4ChCoFr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pressBtnTest(8);
            }//onClick
        });//setOnClickListener
        btnHe5ChCoFr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pressBtnTest(9);
            }//onClick
        });//setOnClickListener

        cursor = dbUtilities.getDb().rawQuery(mainQuery, null);
        //оставшееся количество слов
        if (isMixMethod) {
            wordsCount = listCursorNumFromActivity.size() -
                    Integer.parseInt(tvMixMethodPos.getText().toString());
        } else wordsCount = listCursorNumFromActivity.size();
        progressIter = 100 / wordsCount;
        progressTime = pbBaMeAc.getProgress();
        createWordList();
        startLearnWord();
        return resultView;
    }//onCreateView

    // Метод onAttach() вызывается в начале жизненного цикла фрагмента, и именно здесь
    // мы можем получить контекст фрагмента, в качестве которого выступает класс MainActivity.
    //onAttach(Context) не вызовется до API 23 версии вместо этого будет вызван onAttach(Activity),
    //коий устарел с 23 API
    //Так что вызовем onAttachToContext
    //https://ru.stackoverflow.com/questions/507008/%D0%9D%D0%B5-%D1%80%D0%B0%D0%B1%D0%BE%D1%82%D0%B0%D0%B5%D1%82-onattach
    @TargetApi(23)
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onAttachToContext(context);
    }//onAttach

    //устарел с 23 API
    //Так что вызовем onAttachToContext
    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            onAttachToContext(activity);
        }//if
    }//onAttach

    //Вызовется в момент присоединения фрагмента к активити
    protected void onAttachToContext(Context context) {
        //здесь всегда есть контекст и метод всегда вызовется.
        //тут можно кастовать контест к активити.
        //но лучше к реализуемому ею интерфейсу
        //чтоб не проверять из какого пакета активити в каждом из случаев
        this.context = context;
        FileUtilities = new FileUtilities(context);
        dbUtilities = new DBUtilities(context);
    }//onAttachToContext

    //проверяем нажатие кнопок
    private void pressBtnTest(int i) {
        if (i <= 4) {
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
        } else {
            int j = i - 5;
            //проверяем флаг и выполняем действие в layout и правим массив флагов
            if (!flagAnyBtnPressHe) {
                flagPressBtnHe[j] = true;
                flagAnyBtnPressHe = true;
                selectWordHe = buttonsList.get(i).getText().toString();
                buttonsList.get(i).setBackgroundColor(context.getResources().getColor(R.color.colorButtonPress));
                //проверяем флаг нажатия кнопок в соседнем столбце
                //если "да" проверяем совпадение перевода и слова
                if (flagAnyBtnPressTr) checkPressBtn();
            } else {
                if (flagPressBtnHe[j]) {
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
        String strHe = wordHe.get(indexTr);
        String strTr = wordTr.get(indexHe);
        if ((selectWordHe.equals(strHe)) || (selectWordTr.equals(strTr))) {
            //снимаем флаги нажатых клавиш
            flagAnyBtnPressHe = false;
            flagAnyBtnPressTr = false;
            //увеличиваем прогресс бар
            progressTime = progressTime + progressIter;
            pbBaMeAc.setProgress(progressTime);
            // убираем с экрана элементы которые совпали
            //setVisibility(View.GONE) отключаем ненужные элементы для просмотра
            for (int i = 0; i < 10; i++) {
                if (i <= 4) {
                    if (flagPressBtnTr[i]) {
                        buttonsList.get(i).setEnabled(false);
                        buttonsList.get(i).setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
                        flagPressBtnTr[i] = false;
                    }//if
                } else {
                    int j = i - 5;
                    if (flagPressBtnHe[j]) {
                        buttonsList.get(i).setEnabled(false);
                        buttonsList.get(i).setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
                        flagPressBtnHe[j] = false;
                    }//if
                }////if-else
            }//for

            //проверка сколько слов осталось угадать
            //если все угаданы формируем экран заново
            if (countRemainingWords > 0) countRemainingWords--;
            else {
                int n = list5WordsOnScreen.size();
                //удаляем из коллекции listWords отгаданые слова
                for (int i = 0; i < n; i++) {
                    listWords.remove(list5WordsOnScreen.get(i));
                }//for
                //если фрагмент запущени из mixMethod
                //и количество изученных слов в фрагменте уже 5
                if (isMixMethod && (interForMixMethod == 4)) {
                    interForMixMethod = 0;
                    //вызываем onClick активности для продажения работы mixMethod
                    //устанавливаем глобальную переменую tvMixMethodPos
                    int temp = Integer.parseInt(tvMixMethodPos.getText().toString());
                    tvMixMethodPos.setText(String.valueOf(temp + 5));
                    //если количество слов для изучения кратно 5
                    //необходимо такая проверка
                    if (temp + 5 == listCursorNumFromActivity.size()){
                        //считываем заказаное число повторений коллекции слов
                        TextView tvLoopsBaMeAc = getActivity().findViewById(R.id.tvLoopsBaMeAc);
                        int loops = Integer.parseInt(tvLoopsBaMeAc.getText().toString());
                        //проверяем считанное значение
                        if (loops > 1) {
                            //оставшееся количество слов
                            wordsCount = listCursorNumFromActivity.size();
                            //сбрасываем счетчик для глобальной позиции MixMethodPos
                            tvMixMethodPos.setText(String.valueOf("0"));
                            createWordList();//заполняем заново WordList
                            loops--;// уменьшаем количество цыклов изучения выбранной коллекции
                            tvLoopsBaMeAc.setText(String.valueOf(loops)); //отмечаем это значение в TextView
                            //очищяем временую коллекцию для следующей порции слов
                            list5WordsOnScreen.clear();
                            startLearnWord();//запускаем заново изучение
                        } else getActivity().finish();
                    }//if (temp + 5 == listCursorNumFromActivity.size())
                    else btnActivity.callOnClick();
                } else {
                    //если в коллекции еще остались слова продолжаем
                    //иначе возврат в предыдущую активность
                    if (listWords.size() > 0) {
                        //очищяем временую коллекцию для следующей порции слов
                        list5WordsOnScreen.clear();
                        startLearnWord();
                    } else {
                        //считываем заказаное число повторений коллекции слов
                        TextView tvLoopsBaMeAc = getActivity().findViewById(R.id.tvLoopsBaMeAc);
                        int loops = Integer.parseInt(tvLoopsBaMeAc.getText().toString());
                        //проверяем считанное значение
                        if (loops > 1) {
                            //оставшееся количество слов
                            wordsCount = listCursorNumFromActivity.size();
                            //сбрасываем счетчик для глобальной позиции MixMethodPos
                            tvMixMethodPos.setText(String.valueOf("0"));
                            createWordList();//заполняем заново WordList
                            loops--;// уменьшаем количество цыклов изучения выбранной коллекции
                            tvLoopsBaMeAc.setText(String.valueOf(loops)); //отмечаем это значение в TextView
                            //очищяем временую коллекцию для следующей порции слов
                            list5WordsOnScreen.clear();
                            startLearnWord();//запускаем заново изучение
                        } else getActivity().finish();
                    }//if-else
                }
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
        if (n >= 5) {
            n = 5;
            iFirstStopEn = n;
            iSecondStopEn = n;
            //устанавливаем счетчик оставшихся значений
            countRemainingWords = n - 1;
        } else {
            //устанавливаем счетчик оставшихся значений
            countRemainingWords = n - 1;
            iFirstStopEn = n - 1;
            iSecondStopEn = n + 4;
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

        int j = 0; //итерационная переменная для коллекции list5WordsOnScreen
        for (int i = 0; i < 10; i++) {
            //если i==5, первым делом перемешаем
            //коллекцию порядковых номеров,
            //чтоб порядок слов в первом столбике отличались
            //от порядка слов во втором столбике.
            //перемешиваем пару раз чтоб эффективнее
            //и обнуляем j порядковый номер коллекции list5WordsOnScreen
            if (i == n) {
                Collections.shuffle(listIdNumForBtn);
                Collections.shuffle(listIdNumForBtn);
                j = 0;
            }//if
            //выключаем активность кнопок если итерация
            // попадает в условия
            if ((((i < 5) && (iFirstStopEn < i)) || (iSecondStopEn < i)) && (iSecondStopEn != iFirstStopEn)) {
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
            if (i <= 4) {
                buttonsList.get(i).setText(list5WordsOnScreen.get(listIdNumForBtn.get(j)).getStrRus());
            } else {
                buttonsList.get(i).setText(list5WordsOnScreen.get(listIdNumForBtn.get(j)).getStrHeb());
            }//if-else
            j++;
        }//for
        if (isMixMethod) {
            //делаем кнопки которые не учавствуют в изучении слов
            // неактивные и другого цвета
            for (int i = 0; i < 10; i++) {
                if (buttonsList.get(i).getText().toString().equals("")) {
                    buttonsList.get(i).setEnabled(false);
                    buttonsList.get(i).setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
                }//if
            }//for
        }
        if (wordsCount > 5) interForMixMethod = 4;
    }//startLearnWord

    //создаем коллекцию объектов слов для изучения
    private void createWordList() {
        listWords.clear();
        Word word = new Word();
        int firstNumber = 0;
        //если метод запущен из MixMethod
        if (isMixMethod)
            firstNumber = Integer.parseInt(tvMixMethodPos.getText().toString());
        int n = listCursorNumFromActivity.size();
        for (int i = firstNumber; i < n; i++) {
            cursor.moveToPosition(
                    Integer.parseInt(listCursorNumFromActivity.get(i))
            );

            //получаем данные из курсора для фильтрации
            String idHebrew = cursor.getString(0);
            word.setIdWord(idHebrew);     //id слова
            word.setStrHeb(cursor.getString(1));     //слово на иврите
            word.setStrTrans(cursor.getString(2));   //транскрпция слова на иврите

            //получаем перевод
            String queryTr = "SELECT russian.word_ru FROM translations " +
                    "INNER JOIN russian ON russian.id = translations.russian_id " +
                    "WHERE translations.hebrew_id = \"" + idHebrew + "\"";
            Cursor cursorTr = dbUtilities.getDb().rawQuery(queryTr, null);
            if(cursorTr.getCount()-1 > 1)
                cursorTr.moveToPosition(Utils.getRandom(0, cursorTr.getCount()-1));
            else
                cursorTr.moveToPosition(0);
            word.setStrRus(cursorTr.getString(0));     //слово на русском

            //заполняем коллекции слов для проверки нажатых клавиш
            wordHe.add(cursor.getString(1));     //слово на иврите
            wordTr.add(cursorTr.getString(0));     //слово на русском

            //добавляем новое слово в коллекцию
            listWords.add(word);
            word = new Word();
        }//while
        //перемешаем полученную коллекцию
//        Collections.shuffle(listWords);
    }//createWordList

    @Override
    public void onStop() {
        super.onStop();
    }//onStop

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }//onDestroyView

    @Override
    public void onDestroy() {
        super.onDestroy();
    }//onDestroyView

    @Override
    public void onDetach() {
        super.onDetach();
    }//onDetach
}//ChooseCoupleFragment
