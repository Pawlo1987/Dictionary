package com.example.user.dictionary.Fragments;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
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
//выводится слово на иврите
//и нужно выбрать правельный перевод
public class ChooseTranslationFragment extends Fragment{
    Context context;
    DBUtilities dbUtilities;
    //переменная необходимая для итераци слов если фрагмент вызван из mixMethod
    int interForMixMethod = 0;
    com.example.user.dictionary.FileUtilities FileUtilities;
    List<Word> listWords; // коллекция слов для изучения
    List<Button> buttonsList;//коллекция кнопок
    TextView tvWordChTrWo;
    TextView tvTranscChTrWo;
    Button btnTr1ChTrWo;
    Button btnTr2ChTrWo;
    Button btnTr3ChTrWo;
    Button btnTr4ChTrWo;
    Button btnTr5ChTrWo;
    int progressTime;
    int progressIter;
    CountDownTimer countDownTimer;
    int selectPos;  //выбранная позиция
    int wordsCount = 10;
    private Cursor cursor;
    ProgressBar pbBaMeAc;
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
            "INNER JOIN quantity ON quantity.id = hebrew.quantity_id;";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbUtilities.open();
        buttonsList = new ArrayList<>();
        listWords  = new ArrayList<>();
        listCursorNumFromActivity  = new ArrayList<>();
        listCursorNumFromActivity.addAll(getArguments().getStringArrayList("idList"));
        isMixMethod = getArguments().getBoolean("isMixMethod");
        tvMixMethodPos = getActivity().findViewById(R.id.tvMixMethodPos);
    }//onCreate

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View resultView = inflater.inflate(R.layout.fragment_choose_word,  container, false);
        tvWordChTrWo = resultView.findViewById(R.id.tvWordChTrWo);
        tvTranscChTrWo = resultView.findViewById(R.id.tvTranscChTrWo);
        btnTr1ChTrWo = resultView.findViewById(R.id.btnTr1ChTrWo);
        btnTr2ChTrWo = resultView.findViewById(R.id.btnTr2ChTrWo);
        btnTr3ChTrWo = resultView.findViewById(R.id.btnTr3ChTrWo);
        btnTr4ChTrWo = resultView.findViewById(R.id.btnTr4ChTrWo);
        btnTr5ChTrWo = resultView.findViewById(R.id.btnTr5ChTrWo);

        buttonsList.add(btnTr1ChTrWo);
        buttonsList.add(btnTr2ChTrWo);
        buttonsList.add(btnTr3ChTrWo);
        buttonsList.add(btnTr4ChTrWo);
        buttonsList.add(btnTr5ChTrWo);

        btnActivity = getActivity().findViewById(R.id.btnActivity);
        tvMixMethodPos = getActivity().findViewById(R.id.tvMixMethodPos);
        pbBaMeAc = getActivity().findViewById(R.id.pbBaMeAc);

        btnTr1ChTrWo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pressBtnTest(0);
            }
        });
        btnTr2ChTrWo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pressBtnTest(1);
            }
        });
        btnTr3ChTrWo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pressBtnTest(2);
            }
        });
        btnTr4ChTrWo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pressBtnTest(3);
            }
        });
        btnTr5ChTrWo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pressBtnTest(4);
            }
        });

        cursor = dbUtilities.getDb().rawQuery(mainQuery, null);
        //устанавливаем текущую позицию для коллекции слов
        if(isMixMethod) {
            selectPos = Integer.parseInt(tvMixMethodPos.getText().toString());
        }else selectPos = 0;
        //оставшееся количество слов
        wordsCount = listCursorNumFromActivity.size() - selectPos;
        if(wordsCount>0) progressIter = 100 / wordsCount;
        progressTime = pbBaMeAc.getProgress();
        createWordList();
        startLearnWord();
        return resultView;
    }//onCreateView

    //обработчик любой нажатой клавиши
    private void pressBtnTest(int k) {
        String selectWord = buttonsList.get(k).getText().toString();
        if(!listWords.get(selectPos).getStrRus().equals(selectWord)){
            buttonsList.get(k).setBackgroundColor(context.getResources().getColor(R.color.colorAccent));
        }else {
            buttonsList.get(k).setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
            btnSelectTrue();
        }//if-else
    }//pressBtnTest

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

    //обработка правельно выбранного перевода
    private void btnSelectTrue() {
        //увеличиваем прогресс бар
        progressTime = progressTime + progressIter;
        pbBaMeAc.setProgress(progressTime);
        //отключаем кнопки для исключения случайного нажатия
        for (int i = 0; i < 5; i++) {
            buttonsList.get(i).setEnabled(false);
        }//for
        //для создания небольшой задерки в 500 миллисекунд
        //Создаем таймер обратного отсчета на 500 миллисекунд с шагом отсчета
        //в 1 секунду (задаем значения в миллисекундах):
        countDownTimer = new CountDownTimer(500, 1000) {
            //Здесь можно выполнить какието дейстивия через кажду секунду
            //до конца счета таймера
            public void onTick(long millisUntilFinished) { }
            //Задаем действия после завершения отсчета (запускаем главную активность)
            public void onFinish(){
                //если фрагмент запущени из mixMethod
                //и количество изученных слов в фрагменте уже 5
                if (isMixMethod && (interForMixMethod == 4)) {
                    interForMixMethod = 0;
                    //вызываем onClick активности для продажения работы mixMethod
                    //устанавливаем глобальную переменую tvMixMethodPos
                    int temp = Integer.parseInt(tvMixMethodPos.getText().toString());
                    tvMixMethodPos.setText(String.valueOf(temp+5));
                    //если количество слов для изучения кратно 5
                    //необходимо такая проверка
                    if(temp+5 == listCursorNumFromActivity.size()){
                        //считываем заказаное число повторений коллекции слов
                        TextView tvLoopsBaMeAc = getActivity().findViewById(R.id.tvLoopsBaMeAc);
                        int loops = Integer.parseInt(tvLoopsBaMeAc.getText().toString());
                        //проверяем считанное значение
                        if(loops>1) {
                            //сбрасываем все необходимые счетчики
                            interForMixMethod = 0; //итерация MixMethod
                            selectPos = 0; //selectPos текущая позиция в коллекции изучаемых слов
                            //сбрасываем счетчик для глобальной позиции MixMethodPos
                            tvMixMethodPos.setText(String.valueOf("0"));
                            loops--;// уменьшаем количество цыклов изучения выбранной коллекции
                            tvLoopsBaMeAc.setText(String.valueOf(loops)); //отмечаем это значение в TextView
                            //включаем кнопки обратно
                            for (int i = 0; i < 5; i++) {
                                buttonsList.get(i).setEnabled(true);
                            }//for
                            startLearnWord();//запускаем заново изучение
                        }else getActivity().finish();
                    }else btnActivity.callOnClick();
                } else {
                    interForMixMethod++;
                    //переход к следующему слову или выход из режима изучения
                    //так как закончились слова
                    if (selectPos != listCursorNumFromActivity.size()-1) {
                        selectPos++;
                        //включаем кнопки обратно
                        for (int i = 0; i < 5; i++) {
                            buttonsList.get(i).setEnabled(true);
                        }//for
                        startLearnWord();
                    } else {
                        //если закнчились слова для изучения
                        //считываем заказаное число повторений коллекции слов
                        TextView tvLoopsBaMeAc = getActivity().findViewById(R.id.tvLoopsBaMeAc);
                        int loops = Integer.parseInt(tvLoopsBaMeAc.getText().toString());
                        //проверяем считанное значение
                        if(loops>1) {
                            //сбрасываем все необходимые счетчики
                            interForMixMethod = 0; //итерация MixMethod
                            selectPos = 0; //selectPos текущая позиция в коллекции изучаемых слов
                            //сбрасываем счетчик для глобальной позиции MixMethodPos
                            tvMixMethodPos.setText(String.valueOf("0"));
                            loops--;// уменьшаем количество цыклов изучения выбранной коллекции
                            tvLoopsBaMeAc.setText(String.valueOf(loops)); //отмечаем это значение в TextView
                            //включаем кнопки обратно
                            for (int i = 0; i < 5; i++) {
                                buttonsList.get(i).setEnabled(true);
                            }//for
                            startLearnWord();//запускаем заново изучение
                        }else getActivity().finish();
                    }//if-else
                }
            }//onFinish
        };//countDownTimer
        //запускам таймер
        countDownTimer.start();
    }//btnSelect

    //запуск начала изучения
    private void startLearnWord() {
        tvWordChTrWo.setText(listWords.get(selectPos).getStrHeb());
        tvTranscChTrWo.setText(listWords.get(selectPos).getStrTrans());
        List<Integer> listNumForBtn = new ArrayList<>();
        listNumForBtn.add(selectPos);
        for (int i = 0; i < 4; i++) {
            int n = listWords.size();
            int k;
            while(true) {
                k = Utils.getRandom(0, n);
                if((k>0)&&(k<n)&&(!listNumForBtn.contains(k))) break;
            }//while
            listNumForBtn.add(k);
        }//for

        //перемешать коллекцию переводов для вывода названия кнопок
        Collections.shuffle(listNumForBtn);
        //устанавливаем нормальный цвет кнопок
        for (int i = 0; i < 5; i++) {
            buttonsList.get(i).setBackgroundColor(
                    context.getResources().getColor(R.color.colorButtonNormal)
            );
        }//for
        //устанавливаем слова на русском в кнопки
        for (int i = 0; i < 5; i++) {
            buttonsList.get(i).setText(
                    listWords.get(listNumForBtn.get(i)).getStrRus()
            );
        }//for
    }//startLearnWord

    //создаем коллекцию объектов слов для изучения
    private void createWordList() {
        Word word = new Word();
        int n = listCursorNumFromActivity.size();
        for (int i = 0; i < n; i++) {
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

            //добавляем новое слово в коллекцию
            listWords.add(word);
            word = new Word();
        }//while
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
}//ChooseTranslationFragment