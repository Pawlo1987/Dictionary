package com.pavel.meule.dictionary;

import android.app.FragmentTransaction;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.pavel.meule.dictionary.Fragments.ChooseCoupleFragment;
import com.pavel.meule.dictionary.Fragments.ChooseHebrewWordFragment;
import com.pavel.meule.dictionary.Fragments.ChooseTranslationFragment;
import com.pavel.meule.dictionary.Fragments.StudyPreviewFragment;
import com.pavel.meule.dictionary.Fragments.WriteInHebrewFragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BackgroundMethodActivity extends AppCompatActivity {
    Context context;
    StudyPreviewFragment studyPreviewFragment;
    ChooseTranslationFragment chooseTranslationFragment;
    ChooseHebrewWordFragment chooseHebrewWordFragment;
    ChooseCoupleFragment chooseCoupleFragment;
    WriteInHebrewFragment writeInHebrewFragment;
    FragmentTransaction fTrans;
    List<String> listCursorNum; // коллекция id слов для изучения
    List<String> list5WordsOnScreen; // коллекция 5 слов для изучения
    List<Integer> listMethods;
    Button btnActivity;
    TextView tvFirstLoopBaMeAc; //TextView для определения первого цикла
    TextView tvPreviewFlagBaMeAc; //TextView для опеределени предпросмотра
    TextView tvMixMethodPos;//TextView для глобальной позиция для mixMethod
    TextView tvLoopsBaMeAc;//TextView для количества кругов изучаемого набора слов
    TextView tvIterMethodBaMeAc;//TextView для итерации номеров методов в коллекции выбранных методов
    int mixMethodPos;//глобальной позиция для mixMethod
    int wasMethod = 0;//переменная для определения какий метод вызывался последний в mixMethod

    int wordsCount;
    int progressIter;
    ProgressBar pbBaMeAc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_background_method);
        context = getBaseContext();
        pbBaMeAc = findViewById(R.id.pbBaMeAc);
        btnActivity = findViewById(R.id.btnActivity);
        tvMixMethodPos = findViewById(R.id.tvMixMethodPos);
        tvLoopsBaMeAc = findViewById(R.id.tvLoopsBaMeAc);
        tvIterMethodBaMeAc = findViewById(R.id.tvIterMethodBaMeAc);
        tvPreviewFlagBaMeAc = findViewById(R.id.tvPreviewFlagBaMeAc);
        tvFirstLoopBaMeAc = findViewById(R.id.tvPreviewFlagBaMeAc);

        listCursorNum = new ArrayList<>();
        listMethods = new ArrayList<>();
        list5WordsOnScreen = new ArrayList<>();
        wordsCount = getIntent().getIntExtra("wordsCount", 0);
        listCursorNum.addAll(getIntent().getStringArrayListExtra("idList"));
        studyPreviewFragment = new StudyPreviewFragment();
        chooseTranslationFragment = new ChooseTranslationFragment();
        chooseHebrewWordFragment = new ChooseHebrewWordFragment();
        chooseCoupleFragment = new ChooseCoupleFragment();
        writeInHebrewFragment = new WriteInHebrewFragment();
        tvPreviewFlagBaMeAc.setText("0");
        Bundle args = new Bundle();    // объект для передачи параметров в диалог
        args.putStringArrayList("idList", (ArrayList<String>) listCursorNum);
        args.putInt("wordsCount", wordsCount);
        args.putInt("progressIter", progressIter);
        args.putBoolean("isMixMethod", false);
        //количество циклов изучаемой коллекции
        int loops = getIntent().getIntExtra("loops", 0);
        tvLoopsBaMeAc.setText(String.valueOf(loops));
        //формируем шкалу ProgressBar
        //полная шкала итераций зависит от кол-ва слов и кол-ва циклов изучения
        int degreeOfTenCount = 0; //кол-во десяти
        int learnWordsCount = listCursorNum.size()*loops;      //кол-во изучаемых слов
        while(learnWordsCount > 0){
            learnWordsCount = learnWordsCount/10;
            degreeOfTenCount++;
        }//while()
        int maxValueProgressBar = (int)Math. pow(10, degreeOfTenCount);
        progressIter = (maxValueProgressBar)/(listCursorNum.size()*loops);
        //устанавливаем максимальное значение progressBar
        pbBaMeAc.setMax(maxValueProgressBar);
        //переменная method переданная из активности LearnWordActivity
        listMethods.addAll(getIntent().getIntegerArrayListExtra("listMethods"));
        fTrans = getFragmentManager().beginTransaction();
        studyPreviewFragment.setArguments(args);
        fTrans.replace(R.id.frameLayoutBaMeAc, studyPreviewFragment).commit();
    }//onCreate

    //начало работы фрагмента
    private void goFragment(Bundle args, int method) {
        //выбираем метод изучения слов
        fTrans = getFragmentManager().beginTransaction();
        switch (method) {
            case 1:   //метод "к слову на иврите необходимо выбрать перевод на русском"
                chooseTranslationFragment.setArguments(args);
                fTrans.replace(R.id.frameLayoutBaMeAc, chooseTranslationFragment).commit();
                break;
            case 2:   //метод "к слову на русском необходимо выбрать перевод на иврите"
                chooseHebrewWordFragment.setArguments(args);
                fTrans.replace(R.id.frameLayoutBaMeAc, chooseHebrewWordFragment).commit();
                break;
            case 3:   //метод "необходимо подобрать пары переводов русский-иврит"
                chooseCoupleFragment.setArguments(args);
                fTrans.replace(R.id.frameLayoutBaMeAc, chooseCoupleFragment).commit();
                break;
            case 4:   //метод "необходимо напечатать слова переводов с иврита на русский"
//                writeInTranslationFragment.setArguments(args);
//                fTrans.replace(R.id.frameLayoutBaMeAc, writeInTranslationFragment).commit();
                break;
            case 5:   //метод "необходимо напечатать слова переводов с русского на иврит"
                writeInHebrewFragment.setArguments(args);
                fTrans.replace(R.id.frameLayoutBaMeAc, writeInHebrewFragment).commit();
                break;
            case 6:   //метод "произвольного запуска методов"
                Collections.shuffle(listMethods);
                tvIterMethodBaMeAc.setText(String.valueOf(listMethods.size()-1));
                tvMixMethodPos.setText("0");
                mixMethod(listCursorNum, wordsCount, false);
                break;
        }//switch
    }//goFragment

    public void onClick(View view) {
        if(tvPreviewFlagBaMeAc.getText().toString().equals("0")){
            //устанавливаем флаг предпросмотра в 1
            //значит предпросмотр закончен переходим к изучению
            tvPreviewFlagBaMeAc.setText("1");
            //включаем прогресс бар
            pbBaMeAc.setVisibility(View.VISIBLE);
            Bundle args = new Bundle();    // объект для передачи параметров в диалог
            args.putStringArrayList("idList", (ArrayList<String>) listCursorNum);
            args.putInt("wordsCount", wordsCount);
            args.putInt("progressIter", progressIter);
            args.putBoolean("isMixMethod", false);
            //непосредстенное начало работы фрагмента
            goFragment(
                    args,
                    //если количество методов > 1,
                    // то выбираем mix method, иначе выбраный метод
                    listMethods.size()>1?6:listMethods.get(0)
            );
        }else {
            mixMethodPos = Integer.parseInt(tvMixMethodPos.getText().toString());

            //выбираем метод для запуска порции из 5 слов максимум
            //считываем итерацию с textView для работы с методами
            wasMethod = Integer.parseInt(tvIterMethodBaMeAc.getText().toString());
            //получам следующий метод из коллекции
            int nowMethod = listMethods.get(wasMethod);
            //корректируем textView для итераций
            if (wasMethod == 0) {
                tvIterMethodBaMeAc.setText(String.valueOf(listMethods.size() - 1));
            } else {
                wasMethod--;
                tvIterMethodBaMeAc.setText(String.valueOf(wasMethod));
            }//if(wasMethod == 0)

            wordsCount = listCursorNum.size() - mixMethodPos;

            //если закнчились слова для изучения
            if (wordsCount == 0) {
                //считываем заказаное число повторений коллекции слов
                int loops = Integer.parseInt(
                        findViewById(R.id.tvLoopsBaMeAc).toString()
                );
                //проверяем считанное значение
                if (loops > 1) {
                    //сбрасываем счетчик для глобальной позиции MixMethodPos
                    tvMixMethodPos.setText("0");
                    loops--;
                    TextView tvLoopsBaMeAc = findViewById(R.id.tvLoopsBaMeAc);
                    tvLoopsBaMeAc.setText(String.valueOf(loops));
                } else finish();
            }

            Bundle args = new Bundle();    // объект для передачи параметров в диалог
            args.putStringArrayList("idList", (ArrayList<String>) listCursorNum);
            args.putInt("wordsCount", wordsCount);
            args.putInt("progressIter", progressIter);
            args.putBoolean("isMixMethod", true);

            fTrans = getFragmentManager().beginTransaction();
            //выбираем метод изучения слов
            switch (nowMethod) {
                case 1:   //метод "к слову на иврите необходимо выбрать перевод на русском"
                    chooseTranslationFragment.setArguments(args);
                    fTrans.replace(R.id.frameLayoutBaMeAc, chooseTranslationFragment);
                    break;
                case 2:   //метод "к слову на русском необходимо выбрать перевод на иврите"
                    chooseHebrewWordFragment.setArguments(args);
                    fTrans.replace(R.id.frameLayoutBaMeAc, chooseHebrewWordFragment);
                    break;
                case 3:   //метод "необходимо подобрать пары переводов русский-иврит"
                    chooseCoupleFragment.setArguments(args);
                    fTrans.replace(R.id.frameLayoutBaMeAc, chooseCoupleFragment);
                    break;
                case 4:   //метод "необходимо напечатать слова переводов с иврита на русский"
//                    writeInTranslationFragment.setArguments(args);
//                    fTrans.replace(R.id.frameLayoutBaMeAc, writeInTranslationFragment);
                    break;
                case 5:   //метод "необходимо напечатать слова переводов с русского на иврит"
                    writeInHebrewFragment.setArguments(args);
                    fTrans.replace(R.id.frameLayoutBaMeAc, writeInHebrewFragment);
                    break;
            }//switch
            fTrans.commit();
        }// if(tvPreviewFlagBaMeAc.getText().toString().equals("0"))
    }//onClick

    //работа mixMethod
    public void mixMethod(List<String> listCursor, int wordCount, boolean nextIter) {
        //выбираем метод для запуска порции из 5 слов максимум
        //считываем итерацию с textView для работы с методами
        wasMethod = Integer.parseInt(tvIterMethodBaMeAc.getText().toString());
        //получам следующий метод из коллекции
        int method = listMethods.get(wasMethod);
        //корректируем textView для итераций
        if(wasMethod == 0){
            tvIterMethodBaMeAc.setText(String.valueOf(listMethods.size()-1));
        }else{
            wasMethod--;
            tvIterMethodBaMeAc.setText(String.valueOf(wasMethod));
        }//if(wasMethod == 0)

        Bundle args = new Bundle();    // объект для передачи параметров в диалог
        args.putStringArrayList("idList", (ArrayList<String>) listCursor);
        args.putInt("progressIter", progressIter);
        args.putBoolean("isMixMethod", true);

        fTrans = getFragmentManager().beginTransaction();
        //выбираем метод изучения слов
        switch (method) {
            case 1:   //метод "к слову на иврите необходимо выбрать перевод на русском"
                chooseTranslationFragment.setArguments(args);
                fTrans.replace(R.id.frameLayoutBaMeAc, chooseTranslationFragment);
                break;
            case 2:   //метод "к слову на русском необходимо выбрать перевод на иврите"
                chooseHebrewWordFragment.setArguments(args);
                fTrans.replace(R.id.frameLayoutBaMeAc, chooseHebrewWordFragment);
                break;
            case 3:   //метод "необходимо подобрать пары переводов русский-иврит"
                chooseCoupleFragment.setArguments(args);
                fTrans.replace(R.id.frameLayoutBaMeAc, chooseCoupleFragment);
                break;
            case 4:   //метод "необходимо напечатать слова переводов с иврита на русский"
//                writeInTranslationFragment.setArguments(args);
//                fTrans.replace(R.id.frameLayoutBaMeAc, writeInTranslationFragment);
                break;
            case 5:   //метод "необходимо напечатать слова переводов с русского на иврит"
                writeInHebrewFragment.setArguments(args);
                fTrans.replace(R.id.frameLayoutBaMeAc, writeInHebrewFragment);
                break;
        }//switch
        fTrans.commit();
    }//mixMethod

}//BackgroundMethodActivity
