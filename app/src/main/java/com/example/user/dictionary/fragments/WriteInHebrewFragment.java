package com.example.user.dictionary.Fragments;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.dictionary.DBUtilities;
import com.example.user.dictionary.FileUtilities;
import com.example.user.dictionary.R;
import com.example.user.dictionary.Word;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

//Фрагмент в которой происходит работа
//по изучению слов
//выводится слово на русском
//и нужно написать правельный перевод на иврите
public class WriteInHebrewFragment extends Fragment {
    Context context;
    DBUtilities dbUtilities;
    com.example.user.dictionary.FileUtilities FileUtilities;
    List<Word> listWords; // коллекция слов для изучения
    TextView tvWordWrInHeFr;
    EditText etTransWrInHeFr;
    Button btnCheckWrInHeFr;
    Button btnNextWrInHeFr;
    //переменная необходимая для итераци слов если фрагмент вызван из mixMethod
    int interForMixMethod;
    int selectPos;  //выбранная позиция
    int wordsCount;
    int progressTime;
    int progressIter;
    CountDownTimer countDownTimer;
    ProgressBar pbBaMeAc;
    Switch swHelpWrInHeFr;
    List<String> listCursorNumFromActivity; // коллекция слов для изучения полученая из Activity
    TextView tvMixMethodPos;//TextView для глобальной позиция для mixMethod
    boolean isMixMethod;    //флаг проверки если фрагмент вызван из mixMethod
    Button btnActivity;

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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbUtilities.open();
        listWords = new ArrayList<>();
        listCursorNumFromActivity = new ArrayList<>();
        isMixMethod = getArguments().getBoolean("isMixMethod");
        tvMixMethodPos = getActivity().findViewById(R.id.tvMixMethodPos);
        listCursorNumFromActivity.addAll(getArguments().getStringArrayList("idList"));
    }//onCreate

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View resultView = inflater.inflate(R.layout.fragment_write_in_hebrew, container, false);
        swHelpWrInHeFr = resultView.findViewById(R.id.swHelpWrInHeFr);
        tvWordWrInHeFr = resultView.findViewById(R.id.tvWordWrInHeFr);
        etTransWrInHeFr = resultView.findViewById(R.id.etTransWrInHeFr);
        btnCheckWrInHeFr = resultView.findViewById(R.id.btnCheckWrInHeFr);
        btnNextWrInHeFr = resultView.findViewById(R.id.btnNextWrInHeFr);

        btnActivity = getActivity().findViewById(R.id.btnActivity);
        pbBaMeAc = getActivity().findViewById(R.id.pbBaMeAc);

        btnCheckWrInHeFr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pressBtnTest(0);
            }
        });
        btnNextWrInHeFr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pressBtnTest(1);
            }
        });
        swHelpWrInHeFr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pressBtnTest(2);
            }
        });

        cursor = dbUtilities.getDb().rawQuery(mainQuery, null);
        //устанавливаем текущую позицию для коллекции слов
        if (isMixMethod) {
            selectPos = Integer.parseInt(tvMixMethodPos.getText().toString());
        } else selectPos = 0;
        //оставшееся количество слов
        wordsCount = listCursorNumFromActivity.size() - selectPos;
        if (wordsCount > 0) progressIter = 100 / wordsCount;
        progressTime = pbBaMeAc.getProgress();
        createWordList();
        startLearnWord();
        return resultView;
    }//onCreateView

    //обработчик любой нажатой клавиши
    private void pressBtnTest(int k) {
        switch (k) {
            case 0:
                if (listWords.get(selectPos).getStrHeb().equals(etTransWrInHeFr.getText().toString())) {
                    translationOK();
                } else {
                    Toast.makeText(context, "wrong translation!", Toast.LENGTH_SHORT).show();
                }//if-else
                break;
            case 1:
                nextWord();
                break;
            case 2:
                helpSwitch();
                break;
        }//switch
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

    //запуск начала изучения
    private void startLearnWord() {
        tvWordWrInHeFr.setText(listWords.get(selectPos).getStrRus());

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
            word.setIdWord(cursor.getString(0));     //id слова
            word.setStrRus(cursor.getString(1));     //слово на русском
            word.setStrHeb(cursor.getString(2));     //слово на иврите

            //добавляем новое слово в коллекцию
            listWords.add(word);
            word = new Word();
        }//while
        //перемешаем полученную коллекцию
        Collections.shuffle(listWords);
    }//createWordList

    //обработчик нажатия переключатиля "HELP"
    private void helpSwitch() {
        //если нажимаем на переключатель то вместо слова
        // на иврите появляется слово перевод на русском
        //для того что бы убрать перевод на русском
        // необходимо выключить переключатель
        if (swHelpWrInHeFr.isChecked())
            tvWordWrInHeFr.setText(listWords.get(selectPos).getStrHeb());
        else
            tvWordWrInHeFr.setText(listWords.get(selectPos).getStrRus());
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
        pbBaMeAc.setProgress(progressTime);
        tvWordWrInHeFr.setText("CORRECT!");
        //отключаем кнопки для исключения случайного нажатия
        btnCheckWrInHeFr.setEnabled(false);
        btnNextWrInHeFr.setEnabled(false);
        nextWord();
    }//translationOK

    //процедура перехода к следующему слову
    private void nextWord() {
        //возвращаем в исходное состояние
        if (swHelpWrInHeFr.isChecked()) swHelpWrInHeFr.setChecked(false);
        //спрятать клавиатуру
        hideKeyboard(getActivity());
        //для создания небольшой задерки в 500 миллисекунд
        //Создаем таймер обратного отсчета на 500 миллисекунд с шагом отсчета
        //в 1 секунду (задаем значения в миллисекундах):
        countDownTimer = new CountDownTimer(500, 1000) {
            //Здесь можно выполнить какието дейстивия через кажду секунду
            //до конца счета таймера
            public void onTick(long millisUntilFinished) {
            }

            //Задаем действия после завершения отсчета (запускаем главную активность)
            public void onFinish() {
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
                        if(loops>1) {
                            //сбрасываем все необходимые счетчики
                            interForMixMethod = 0; //итерация MixMethod
                            selectPos = 0; //selectPos текущая позиция в коллекции изучаемых слов
                            //сбрасываем счетчик для глобальной позиции MixMethodPos
                            tvMixMethodPos.setText(String.valueOf("0"));
                            loops--;// уменьшаем количество цыклов изучения выбранной коллекции
                            tvLoopsBaMeAc.setText(String.valueOf(loops)); //отмечаем это значение в TextView
                            //включаем кнопки обратно
                            btnCheckWrInHeFr.setEnabled(true);
                            btnNextWrInHeFr.setEnabled(true);
                            etTransWrInHeFr.setText("");
                            etTransWrInHeFr.clearFocus();
                            startLearnWord();//запускаем заново изучение
                        }else getActivity().finish();
                    }else btnActivity.callOnClick();
                } else {
                    interForMixMethod++;
                    //переход к следующему слову или выход из режима изучения
                    //так как закончились слова
                    if (selectPos != listCursorNumFromActivity.size() - 1) {
                        selectPos++;
                        //включаем кнопки обратно
                        btnCheckWrInHeFr.setEnabled(true);
                        btnNextWrInHeFr.setEnabled(true);
                        etTransWrInHeFr.setText("");
                        etTransWrInHeFr.clearFocus();
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
                            btnCheckWrInHeFr.setEnabled(true);
                            btnNextWrInHeFr.setEnabled(true);
                            etTransWrInHeFr.setText("");
                            etTransWrInHeFr.clearFocus();
                            startLearnWord();//запускаем заново изучение
                        }else getActivity().finish();
                    }//if-else
                }//if(isMixMethod&&(interForMixMethod==4))
            }//onFinish
        };//countDownTimer
        //запускам таймер
        countDownTimer.start();
    }//nextWord

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
}//WriteInHebrewFragment
