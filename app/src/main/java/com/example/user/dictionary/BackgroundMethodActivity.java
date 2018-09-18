package com.example.user.dictionary;

import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ProgressBar;

import com.example.user.dictionary.fragments.ChooseCoupleFragment;
import com.example.user.dictionary.fragments.ChooseHebrewWordFragment;
import com.example.user.dictionary.fragments.ChooseTranslationFragment;
import com.example.user.dictionary.fragments.MixMethodFragment;
import com.example.user.dictionary.fragments.WriteInHebrewFragment;
import com.example.user.dictionary.fragments.WriteInTranslationFragment;

import java.util.ArrayList;

public class BackgroundMethodActivity extends AppCompatActivity {
    Context context;
    ChooseTranslationFragment chooseTranslationFragment;
    ChooseHebrewWordFragment chooseHebrewWordFragment;
    ChooseCoupleFragment chooseCoupleFragment;
    WriteInTranslationFragment writeInTranslationFragment;
    WriteInHebrewFragment writeInHebrewFragment;
    MixMethodFragment mixMethodFragment;
    FragmentTransaction fTrans;

    int wordsCount= 10;
    int progressTime = 0;
    int progressIter;
    ProgressBar pbBaMeAc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_background_method);
        context = getBaseContext();
        pbBaMeAc = findViewById(R.id.pbBaMeAc);

        wordsCount = getIntent().getIntExtra("wordsCount",0);
        chooseTranslationFragment = new ChooseTranslationFragment();
        chooseHebrewWordFragment = new ChooseHebrewWordFragment();
        chooseCoupleFragment = new ChooseCoupleFragment();
        writeInTranslationFragment = new WriteInTranslationFragment();
        writeInHebrewFragment = new WriteInHebrewFragment();
        mixMethodFragment = new MixMethodFragment();
        Bundle args = new Bundle();    // объект для передачи параметров в диалог
        args.putStringArrayList("idList",getIntent().getStringArrayListExtra("idList"));
        args.putInt("wordsCount", wordsCount);
        progressIter = 100 / wordsCount;
        progressTime = 0;

        //выбираем метод изучения слов
        fTrans = getFragmentManager().beginTransaction();
        switch (getIntent().getIntExtra("method",0)) {
            case 1:   //метод "к слову на иврите необходимо выбрать перевод на русском"
                chooseTranslationFragment.setArguments(args);
                fTrans.replace(R.id.frameLayoutBaMeAc, chooseTranslationFragment);
                fTrans.commit();
                break;
            case 2:   //метод "к слову на русском необходимо выбрать перевод на иврите"
                chooseHebrewWordFragment.setArguments(args);
                fTrans.replace(R.id.frameLayoutBaMeAc, chooseHebrewWordFragment);
                fTrans.commit();
                break;
            case 3:   //метод "необходимо подобрать пары переводов русский-иврит"
                chooseCoupleFragment.setArguments(args);
                fTrans.replace(R.id.frameLayoutBaMeAc, chooseCoupleFragment);
                fTrans.commit();
                break;
            case 4:   //метод "необходимо напечатать слова переводов с иврита на русский"
                writeInTranslationFragment.setArguments(args);
                fTrans.replace(R.id.frameLayoutBaMeAc, writeInTranslationFragment);
                fTrans.commit();
                break;
            case 5:   //метод "необходимо напечатать слова переводов с русского на иврит"
                writeInHebrewFragment.setArguments(args);
                fTrans.replace(R.id.frameLayoutBaMeAc, writeInHebrewFragment);
                fTrans.commit();
                break;
            case 6:   //метод "произвольного запуска методов"
                mixMethodFragment.setArguments(args);
                fTrans.replace(R.id.frameLayoutBaMeAc, mixMethodFragment);
                fTrans.commit();
                break;
        }//switch

    }//onCreate

}//BackgroundMethodActivity
