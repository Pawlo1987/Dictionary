package com.example.user.dictionary.fragments;

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
    List<String> listCursorNum; // коллекция id слов для изучения
    List<Word> listWords; // коллекция слов для изучения
    TextView tvWordWrInHeFr;
    EditText etTransWrInHeFr;
    Button btnCheckWrInHeFr;
    Button btnNextWrInHeFr;
    int selectPos = 0;  //выбранная позиция
    int wordsCount = 10;
    int progressTime = 0;
    int progressIter;
    CountDownTimer countDownTimer;
    ProgressBar pbBaMeAc;
    Switch swHelpWrInHeFr;

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
        listCursorNum = new ArrayList<>();
        listWords  = new ArrayList<>();
        listCursorNum.addAll(getArguments().getStringArrayList("idList"));
        wordsCount = getArguments().getInt("wordsCount",0);
    }//onCreate

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View resultView = inflater.inflate(R.layout.fragment_write_in_hebrew,  container, false);
        swHelpWrInHeFr = resultView.findViewById(R.id.swHelpWrInHeFr);
        tvWordWrInHeFr = resultView.findViewById(R.id.tvWordWrInHeFr);
        etTransWrInHeFr = resultView.findViewById(R.id.etTransWrInHeFr);
        btnCheckWrInHeFr = resultView.findViewById(R.id.btnCheckWrInHeFr);
        btnNextWrInHeFr = resultView.findViewById(R.id.btnNextWrInHeFr);

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
        progressIter = 100 / wordsCount;
        progressTime = 0;
        createWordList();
        startLearnWord();
        return resultView;
    }//onCreateView

    //обработчик любой нажатой клавиши
    private void pressBtnTest(int k) {
        switch (k) {
            case 0:
                if(listWords.get(selectPos).getStrHeb().equals(etTransWrInHeFr.getText().toString())){
                    translationOK();
                }else{
                    Toast.makeText(context,"wrong translation!",Toast.LENGTH_SHORT).show();
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

    @Override
    public void onDetach() {
        super.onDetach();

    }//onDetach

    //запуск начала изучения
    private void startLearnWord() {
        tvWordWrInHeFr.setText(listWords.get(selectPos).getStrRus());

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

    //обработчик нажатия переключатиля "HELP"
    private void helpSwitch() {
        //если нажимаем на переключатель то вместо слова
        // на иврите появляется слово перевод на русском
        //для того что бы убрать перевод на русском
        // необходимо выключить переключатель
        if(swHelpWrInHeFr.isChecked())
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
        //спрятать клавиатуру
        hideKeyboard(getActivity());
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
                btnCheckWrInHeFr.setEnabled(true);
                btnNextWrInHeFr.setEnabled(true);
                etTransWrInHeFr.setText("");
                etTransWrInHeFr.clearFocus();
                //переход к следующему слову или выход из режима изучения
                //так как закончились слова
                if(selectPos < wordsCount-1){
                    selectPos++;
                    startLearnWord();
                }else{
                    getActivity().finish();
                }//if-else
            }//onFinish
        };//countDownTimer
        //запускам таймер
        countDownTimer.start();
    }//nextWord
}//WriteInHebrewFragment
