package com.example.user.dictionary;

import android.app.FragmentTransaction;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.user.dictionary.Fragments.ChooseCoupleFragment;
import com.example.user.dictionary.Fragments.ChooseHebrewWordFragment;
import com.example.user.dictionary.Fragments.ChooseTranslationFragment;
import com.example.user.dictionary.Fragments.WriteInHebrewFragment;
import com.example.user.dictionary.Fragments.WriteInTranslationFragment;

import java.util.ArrayList;
import java.util.List;

public class BackgroundMethodActivity extends AppCompatActivity {
    Context context;
    ChooseTranslationFragment chooseTranslationFragment;
    ChooseHebrewWordFragment chooseHebrewWordFragment;
    ChooseCoupleFragment chooseCoupleFragment;
    WriteInTranslationFragment writeInTranslationFragment;
    WriteInHebrewFragment writeInHebrewFragment;
    FragmentTransaction fTrans;
    List<String> listCursorNum; // коллекция id слов для изучения
    List<String> list5WordsOnScreen; // коллекция 5 слов для изучения
    Button btnActivity;
    TextView tvMixMethodPos;//TextView для глобальной позиция для mixMethod
    TextView tvLoopsBaMeAc;//TextView для количества кругов изучаемого набора слов
    int mixMethodPos;//глобальной позиция для mixMethod
    int wasMethod = 0;//переменная для определения какий метод вызывался последний в mixMethod

    int wordsCount;
    int progressTime;
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

        listCursorNum = new ArrayList<>();
        list5WordsOnScreen = new ArrayList<>();
        wordsCount = getIntent().getIntExtra("wordsCount", 0);
        listCursorNum.addAll(getIntent().getStringArrayListExtra("idList"));
        chooseTranslationFragment = new ChooseTranslationFragment();
        chooseHebrewWordFragment = new ChooseHebrewWordFragment();
        chooseCoupleFragment = new ChooseCoupleFragment();
        writeInTranslationFragment = new WriteInTranslationFragment();
        writeInHebrewFragment = new WriteInHebrewFragment();
        Bundle args = new Bundle();    // объект для передачи параметров в диалог
        args.putStringArrayList("idList", (ArrayList<String>) listCursorNum);
        args.putInt("wordsCount", wordsCount);
        args.putBoolean("isMixMethod", false);
        tvLoopsBaMeAc.setText(String.valueOf(getIntent().getIntExtra("loops", 0)));
        progressTime = 0;

        //непосредстенное начало работы фрагмента
        goFragment(
                args,
                //переменная method переданная из активности LearnWordActivity
                getIntent().getIntExtra("method", 0)
        );
    }//onCreate

    //начало работы фрагмента
    private void goFragment(Bundle args, int method) {
        //выбираем метод изучения слов
        fTrans = getFragmentManager().beginTransaction();
        switch (method) {
            case 1:   //метод "произвольного запуска методов"
                tvMixMethodPos.setText("0");
                mixMethod(listCursorNum, wordsCount, false);
                break;
            case 2:   //метод "к слову на иврите необходимо выбрать перевод на русском"
                chooseTranslationFragment.setArguments(args);
                fTrans.replace(R.id.frameLayoutBaMeAc, chooseTranslationFragment).commit();
                break;
            case 3:   //метод "к слову на русском необходимо выбрать перевод на иврите"
                chooseHebrewWordFragment.setArguments(args);
                fTrans.replace(R.id.frameLayoutBaMeAc, chooseHebrewWordFragment).commit();
                break;
            case 4:   //метод "необходимо подобрать пары переводов русский-иврит"
                chooseCoupleFragment.setArguments(args);
                fTrans.replace(R.id.frameLayoutBaMeAc, chooseCoupleFragment).commit();
                break;
            case 5:   //метод "необходимо напечатать слова переводов с иврита на русский"
                writeInTranslationFragment.setArguments(args);
                fTrans.replace(R.id.frameLayoutBaMeAc, writeInTranslationFragment).commit();
                break;
            case 6:   //метод "необходимо напечатать слова переводов с русского на иврит"
                writeInHebrewFragment.setArguments(args);
                fTrans.replace(R.id.frameLayoutBaMeAc, writeInHebrewFragment).commit();
                break;
        }//switch
    }//goFragment

    public void onClick(View view) {
        mixMethodPos = Integer.parseInt(tvMixMethodPos.getText().toString());

        //выбираем метод для запуска порции из 5 слов максимум
        int nowMethod = Utils.getRandom(1, 6);
        while (wasMethod == nowMethod) {
            nowMethod = Utils.getRandom(1, 6);
        }// while (wasMethod == nowMethod)

        wordsCount = listCursorNum.size() - mixMethodPos;

        //если закнчились слова для изучения
        if (wordsCount == 0){
            //считываем заказаное число повторений коллекции слов
            int loops = Integer.parseInt(
                    findViewById(R.id.tvLoopsBaMeAc).toString()
            );
            //проверяем считанное значение
            if(loops>1) {
                //сбрасываем счетчик для глобальной позиции MixMethodPos
                tvMixMethodPos.setText(String.valueOf("0"));
                loops--;
                TextView tvLoopsBaMeAc = findViewById(R.id.tvLoopsBaMeAc);
                tvLoopsBaMeAc.setText(String.valueOf(loops));
            }else finish();
        }

        Bundle args = new Bundle();    // объект для передачи параметров в диалог
        args.putStringArrayList("idList", (ArrayList<String>) listCursorNum);
        args.putInt("wordsCount", wordsCount);
        args.putBoolean("isMixMethod", true);

        wasMethod = nowMethod;
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
                writeInTranslationFragment.setArguments(args);
                fTrans.replace(R.id.frameLayoutBaMeAc, writeInTranslationFragment);
                break;
            case 5:   //метод "необходимо напечатать слова переводов с русского на иврит"
                writeInHebrewFragment.setArguments(args);
                fTrans.replace(R.id.frameLayoutBaMeAc, writeInHebrewFragment);
                break;
        }//switch
        fTrans.commit();
    }//onClick

    //работа mixMethod
    public void mixMethod(List<String> listCursor, int wordCount, boolean nextIter) {
        //выбираем метод для запуска порции из 5 слов максимум
        int method = Utils.getRandom(1, 6);

        //сохраняем номер метода для последующей работы
        wasMethod = method;
        Bundle args = new Bundle();    // объект для передачи параметров в диалог
        args.putStringArrayList("idList", (ArrayList<String>) listCursor);
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
                writeInTranslationFragment.setArguments(args);
                fTrans.replace(R.id.frameLayoutBaMeAc, writeInTranslationFragment);
                break;
            case 5:   //метод "необходимо напечатать слова переводов с русского на иврит"
                writeInHebrewFragment.setArguments(args);
                fTrans.replace(R.id.frameLayoutBaMeAc, writeInHebrewFragment);
                break;
        }//switch
        fTrans.commit();
    }//mixMethod

}//BackgroundMethodActivity
