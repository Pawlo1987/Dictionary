package com.example.user.dictionary;

import android.content.ContentValues;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AddNewWordActivity extends AppCompatActivity {
    private EditText edRussianANWAc;        //строка с русским словом
    private EditText edHebrewANWAc;         //строка с ивритовским словом
    private EditText edTranscriptionANWAc;  //строка с транскрпцией
    private Spinner spGenderRusANWAc;       //сппинер для рода в русском
    private Spinner spGenderHebANWAc;       //сппинер для рода в иврите
    private Spinner spQuantityANWAc;        //сппинер множест. или единств. число
    private Spinner spMeaningANWAc;         //сппинер значения слова в предложении
    DBUtilities dbUtilities;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_word);

        context = getBaseContext();
        dbUtilities = new DBUtilities(context);
        dbUtilities.open();
        edRussianANWAc = findViewById(R.id.edRussianANWAc);
        edHebrewANWAc = findViewById(R.id.edHebrewANWAc);
        edTranscriptionANWAc = findViewById(R.id.edTranscriptionANWAc);
        spGenderRusANWAc = findViewById(R.id.spGenderRusANWAc);
        spGenderHebANWAc = findViewById(R.id.spGenderHebANWAc);
        spQuantityANWAc = findViewById(R.id.spQuantityANWAc);
        spMeaningANWAc = findViewById(R.id.spMeaningANWAc);

        //строим спиннер для рода в русском
        spGenderRusANWAc.setAdapter(
                buildSpinnerAdapter(Arrays.asList("male", "female")));
        //строим спиннер для рода в иврите
        spGenderHebANWAc.setAdapter(
                buildSpinnerAdapter(Arrays.asList("male", "female")));
        //строим спиннер для множ. и ед. числа
        spQuantityANWAc.setAdapter(
                buildSpinnerAdapter(Arrays.asList("one", "many")));
        //строим спиннер для значения в предложении
        spMeaningANWAc.setAdapter(
                buildSpinnerAdapter(
                        Arrays.asList("adjective", "noun", "verb", "binders")));
    }//onCreate

    //строим адаптер для Spinner
    private ArrayAdapter<String> buildSpinnerAdapter(List<String> spList) {
        ArrayAdapter<String> spAdapter;  //Адаптер для спинера
        //создание адаптера для спинера
        spAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_item,
                spList
        );
        // назначение адапетра для списка
        spAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return spAdapter;
    }//buildCitySpinner

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnConfirmANWAc:
//                addNewWordToDB();
                break;

//            case R.id.btnViewDictionary:
//                viewDictionary();
//                break;

        }//switch
    }//onClick

    private void addNewWordToDB() {
        ContentValues cv = new ContentValues();

        cv.put("word", edTranscriptionANWAc.getText().toString());
        //добваить данные через объект ContentValues(cv), в таблицу
        dbUtilities.insertInto(cv, "transcription");


        cv = new ContentValues();


//        cv.put("gender", etPasswordCrAcAc.getText().toString());
//        cv.put("name", etNameCrAcAc.getText().toString());
//        cv.put("phone_number", etPhoneCrAcAc.getText().toString());
//        cv.put("def_city", spListCity.indexOf(spDefCityCrAcAc.getSelectedItem()) + 1);
//        cv.put("email", etEmailCrAcAc.getText().toString());
//
//        //добваить данные через объект ContentValues(cv), в таблицу
//        dbUtilities.insertInto(cv, "users");
//
//        //переходин в актиность LoginPartActivity
//        Intent intent = new Intent(this, LoginPartActivity.class);
//        startActivity(intent);
    }//addNewWordToDB
}//AddNewWordActivity
