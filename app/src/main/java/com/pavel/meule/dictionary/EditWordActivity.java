package com.pavel.meule.dictionary;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EditWordActivity extends AppCompatActivity {
    private String idHebrew;               //id из recyclerView для работы с записью
    private EditText edHebrewEWAc;         //строка с ивритовским словом
    private EditText edTranscriptionEWAc;  //строка с транскрпцией
    private Spinner spGenderEWAc;       //сппинер для рода в иврите
    private Spinner spQuantityEWAc;        //сппинер множест. или единств. число
    private Spinner spSemanticEWAc;         //сппинер c именнами симантических групп
    private Spinner spMeaningEWAc;         //сппинер значения слова в предложении
    private List<String> listTypesPOS;      //коллекция видов частей речи
    private List<String> listSemantic;      //коллекиция со списком имен симантических групп
    private LinearLayout llTranslationsEWAc; //LinearLayout для переводов
    //TextView для вывода количества заказанных переводов
    private TextView tvTranslationsCountEWAc;
    //коллекция EditText для переводов
    List<EditText> listETTranslations;
    //флаг для первого цикла OnItemSelectedListener
    //иначе все первоначальные настройки спинеров сбываются
    boolean flOnItemSelectedListene = false;

    //количество переводов
    private int countTranslations = 0;

    DBUtilities dbUtilities;
    Context context;
    ActionBar actionBar;                //стрелка НАЗАД

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_word);

        //добавляем actionBar (стрелка сверху слева)
        actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        context = getBaseContext();
        dbUtilities = new DBUtilities(context);
        dbUtilities.open();
        idHebrew = getIntent().getStringExtra("idHebrew");
        edHebrewEWAc = findViewById(R.id.edHebrewEWAc);
        edTranscriptionEWAc = findViewById(R.id.edTranscriptionEWAc);
        spGenderEWAc = findViewById(R.id.spGenderEWAc);
        spQuantityEWAc = findViewById(R.id.spQuantityEWAc);
        spSemanticEWAc = findViewById(R.id.spSemanticEWAc);
        spMeaningEWAc = findViewById(R.id.spMeaningEWAc);
        listSemantic = new ArrayList<>();
        String query = "SELECT semantic.name FROM semantic";
        Cursor cursor = dbUtilities.getDb().rawQuery(query, null);
        int k = cursor.getCount();
        for (int i = 0; i < k; i++) {
            cursor.moveToPosition(i);
            listSemantic.add(cursor.getString(0));
        }//for (int i = 0; i < l; i++)
        //коллекиция со списком значений части речи
        listTypesPOS = new ArrayList<>(
                Arrays.asList(
                        "adjective",            //имя прилагательное;
                        "noun",                 //имя существительное;
                        "verb",                 //глагол;
                        "union",                //союз;
                        "collocation",          //словосочетание;
                        "numeral",              //имя числительное;
                        "pronoun",              //местоимение;
                        "pretext",              //предлог;
                        "adverb")               //наречие;
        );
        tvTranslationsCountEWAc = findViewById(R.id.tvTranslationsCountEWAc);
        llTranslationsEWAc = findViewById(R.id.llTranslationsEWAc);
        listETTranslations = new ArrayList<>();

        //строим спиннер для рода в иврите
        spGenderEWAc.setAdapter(
                buildSpinnerAdapter(Arrays.asList("male", "female", "infinitive")));
        //строим спиннер для множ. и ед. числа
        spQuantityEWAc.setAdapter(
                buildSpinnerAdapter(Arrays.asList("one", "many", "infinitive")));
        //строим спиннер с именами симантических групп
        spSemanticEWAc.setAdapter(
                buildSpinnerAdapter(listSemantic));
        //строим спиннер для значения в предложении
        spMeaningEWAc.setAdapter(
                buildSpinnerAdapter(listTypesPOS));

        spMeaningEWAc.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //если выбран verb перестраиваем все spinner ы
                // добавляем пункт infinitive
                if (flOnItemSelectedListene)
                    if (position == 2) {
                        //строим спиннер для рода в иврите
                        spGenderEWAc.setAdapter(
                                buildSpinnerAdapter(Arrays.asList("male", "female", "infinitive")));
                        //строим спиннер для множ. и ед. числа
                        spQuantityEWAc.setAdapter(
                                buildSpinnerAdapter(Arrays.asList("one", "many", "infinitive")));
                    } else {
                        //строим спиннер для рода в иврите
                        spGenderEWAc.setAdapter(
                                buildSpinnerAdapter(Arrays.asList("male", "female")));
                        //строим спиннер для множ. и ед. числа
                        spQuantityEWAc.setAdapter(
                                buildSpinnerAdapter(Arrays.asList("one", "many")));
                    }//if(position == 3)
                else flOnItemSelectedListene = true;
            }//onItemSelected

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }//onNothingSelected
        });

        //подготовка данных для редактирования
        String mainQuery = "SELECT hebrew.id, hebrew.word_he, transcriptions.word_tr, " +
                "semantic.id, meanings.option, gender.option, quantity.option FROM hebrew " +
                "INNER JOIN transcriptions ON transcriptions.id = hebrew.transcription_id " +
                "INNER JOIN semantic ON semantic.id = hebrew.semantic_id " +
                "INNER JOIN meanings ON meanings.id = hebrew.meaning_id " +
                "INNER JOIN gender ON gender.id = hebrew.gender_id " +
                "INNER JOIN quantity ON quantity.id = hebrew.quantity_id " +
                "WHERE hebrew.id = " + idHebrew;
        cursor = dbUtilities.getDb().rawQuery(mainQuery, null);
        cursor.moveToPosition(0);

        //запоняем строки ввода имеющейся информацией
        edHebrewEWAc.setText(cursor.getString(1));
        edTranscriptionEWAc.setText(cursor.getString(2));
        //правельные варианты спинеров
        int iMeaning = 0;
        int iGender = 0;
        int iQuantity = 0;
        int iSemantic = Integer.parseInt(cursor.getString(3)) - 1;
        //максимально 4 значения у спинера spMeaningEWAc
        for (int i = 0; i < 3; i++) {
            spMeaningEWAc.setSelection(i);
            spGenderEWAc.setSelection(i);
            spQuantityEWAc.setSelection(i);
            if (spMeaningEWAc.getSelectedItem().
                    equals(cursor.getString(4))) iMeaning = i;
            if (spGenderEWAc.getSelectedItem().
                    equals(cursor.getString(5))) iGender = i;
            if (spQuantityEWAc.getSelectedItem().
                    equals(cursor.getString(6))) iQuantity = i;
        }//for

        //спинер spMeaningEWAc 4 значения значит еще одно значение проверим вне цикла
        spMeaningEWAc.setSelection(4);
        if (spMeaningEWAc.getSelectedItem().
                equals(cursor.getString(4))) iMeaning = 4;

        //устанавливаем спинерам правельные варианты
        spMeaningEWAc.setSelection(iMeaning);
        spGenderEWAc.setSelection(iGender);
        spQuantityEWAc.setSelection(iQuantity);
        spSemanticEWAc.setSelection(iSemantic);

        //подготавливаем информацию для заполнения переводов
        query = "SELECT russian.word_ru FROM translations " +
                "INNER JOIN russian ON russian.id = translations.russian_id " +
                "WHERE translations.hebrew_id = \"" + idHebrew + "\"";
        cursor = dbUtilities.getDb().rawQuery(query, null);
        int l = cursor.getCount();
        for (int i = 0; i < l; i++) {
            cursor.moveToPosition(i);
            countTranslations++;
            tvTranslationsCountEWAc.setText(String.valueOf(countTranslations));
            // создаем EditText, пишем hint и добавляем в LinearLayout
            EditText newEditText = new EditText(this);
            newEditText.setHint("Translation " + countTranslations);
            newEditText.setText(cursor.getString(0));
            llTranslationsEWAc.addView(newEditText);
            listETTranslations.add(newEditText);
        }// for (int i = 0; i < l; i++)
    }//onCreate

    //AlertDialog для создания новой семантической группы
    private void addAlertDialogEditText() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Add new semantic group?");
//        alert.setMessage("Create new data!");
        alert.setIcon(R.drawable.icon_question);
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setText("");
        alert.setView(input);
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String word = input.getText().toString().trim();
                //проверка на совпадение
                String query = "SELECT semantic.id FROM semantic " +
                        "WHERE semantic.name = \"" + word + "\"";
                Cursor cursor = dbUtilities.getDb().rawQuery(query, null);
                if (!word.equals("") && (cursor.getCount() == 0)) {
                    //непосредственно добавляем новое значение в таблицу
                    dbUtilities.insertIntoSemantic(String.valueOf(input.getText()));
                    // Do something with value!
                }//if(!word.equals(""))
                //перезаполняем и переустанавливаем спиннер
                listSemantic.clear();
                query = "SELECT semantic.name FROM semantic";
                cursor = dbUtilities.getDb().rawQuery(query, null);
                int l = cursor.getCount();
                for (int i = 0; i < l; i++) {
                    cursor.moveToPosition(i);
                    listSemantic.add(cursor.getString(0));
                }//for (int i = 0; i < l; i++)
                //перезаполняем и переустанавливаем спиннер
                listSemantic.clear();
                query = "SELECT semantic.name FROM semantic";
                cursor = dbUtilities.getDb().rawQuery(query, null);
                int p = cursor.getCount();
                for (int i = 0; i < p; i++) {
                    cursor.moveToPosition(i);
                    listSemantic.add(cursor.getString(0));
                }//for (int i = 0; i < l; i++)
                //строим спиннер с именами симантических групп
                spSemanticEWAc.setAdapter(
                        buildSpinnerAdapter(listSemantic));
            }
        });
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });
        alert.show();
    }//alertDialogEditText

    //AlertDialog для редактирования семантической группы
    private void editAlertDialogEditText() {
        String mainQuery = "SELECT semantic.id FROM semantic " +
                "WHERE semantic.name = \"" + spSemanticEWAc.getSelectedItem().toString() + "\"";
        Cursor cursor = dbUtilities.getDb().rawQuery(mainQuery, null);
        cursor.moveToPosition(0);
        final String idSemantic = String.valueOf(cursor.getInt(0));
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Edit semantic group?");
//        alert.setMessage("Create new data!");
        alert.setIcon(R.drawable.icon_question);
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setText(spSemanticEWAc.getSelectedItem().toString());
        alert.setView(input);
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String word = input.getText().toString().trim();
                //проверка на совпадение
                String query = "SELECT semantic.id FROM semantic " +
                        "WHERE semantic.name = \"" + word + "\"";
                Cursor cursor = dbUtilities.getDb().rawQuery(query, null);
                if (!word.equals("") && (cursor.getCount() == 0)) {
                    //непосредственно обновляем значение в таблице
                    dbUtilities.updTableSemantic(idSemantic, word);
                    // Do something with value!
                }//if(!word.equals(""))
                //перезаполняем и переустанавливаем спиннер
                listSemantic.clear();
                query = "SELECT semantic.name FROM semantic";
                cursor = dbUtilities.getDb().rawQuery(query, null);
                int l = cursor.getCount();
                for (int i = 0; i < l; i++) {
                    cursor.moveToPosition(i);
                    listSemantic.add(cursor.getString(0));
                }//for (int i = 0; i < l; i++)
                //строим спиннер с именами симантических групп
                spSemanticEWAc.setAdapter(
                        buildSpinnerAdapter(listSemantic));
                //выставляем сохранненую семантическую группу
                String mainQuery = "SELECT semantic.id FROM hebrew " +
                        "INNER JOIN semantic ON semantic.id = hebrew.semantic_id " +
                        "WHERE hebrew.id = " + idHebrew;
                cursor = dbUtilities.getDb().rawQuery(mainQuery, null);
                cursor.moveToPosition(0);
                int iSemantic = cursor.getInt(0) - 1;
                spSemanticEWAc.setSelection(iSemantic);
            }
        });
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });
        alert.show();
    }//editAlertDialogEditText

    //AlertDialog для удаления семантической группы
    private void killAlertDialogEditText() {
        String word = spSemanticEWAc.getSelectedItem().toString();
        String query1 = "SELECT hebrew.id, hebrew.word_he, hebrew.transcription_id, " +
                "hebrew.meaning_id, hebrew.gender_id, hebrew.quantity_id, hebrew.semantic_id FROM hebrew " +
                "INNER JOIN semantic ON semantic.id = hebrew.semantic_id " +
                "WHERE semantic.name = \"" + word + "\"";
        Cursor cursor1 = dbUtilities.getDb().rawQuery(query1, null);
        cursor1.moveToPosition(0);
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Delete semantic group?");
        alert.setIcon(R.drawable.icon_question);
        final TextView tvNameGr = new TextView(this);
        tvNameGr.setInputType(InputType.TYPE_CLASS_TEXT);
        tvNameGr.setText(word);
        alert.setView(tvNameGr);
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //если есть слова с данной семантической группой
                if (cursor1.getCount() > 0) {
                    List<String> listIdTableWorlds = new ArrayList<>(); // коллекция id-ов для таблицы word
                    List<String> listHeWords = new ArrayList<>(); // коллекция слов с данной семантической группой
                    List<Integer> listIdTrans = new ArrayList<>(); // коллекция id transcription для таблицы word
                    List<Integer> listIdMeaning = new ArrayList<>(); // коллекция id meanings для таблицы word
                    List<Integer> listIdGender = new ArrayList<>(); // коллекция id Gender для таблицы word
                    List<Integer> listIdQuantity = new ArrayList<>(); // коллекция id Quantity для таблицы word
                    int t = cursor1.getCount();
                    //получаем значения id word(s) в которых установленое значение данной семантической группы
                    for (int i = 0; i < t; i++) {
                        cursor1.moveToPosition(i);
                        //записываем значение в listCursorNum
                        listIdTableWorlds.add(cursor1.getString(0));
                        listHeWords.add(cursor1.getString(1));
                        listIdTrans.add(cursor1.getInt(2));
                        listIdMeaning.add(cursor1.getInt(3));
                        listIdGender.add(cursor1.getInt(4));
                        listIdQuantity.add(cursor1.getInt(5));
                    }// for (int i = 0; i < t; i++)
                    //изменяем значение данной семантической группы
                    // путем записи семантической группы "global"
                    t = listIdTableWorlds.size();
                    for (int i = 0; i < t; i++) {
                        dbUtilities.updTableHebrew(listIdTableWorlds.get(i), listHeWords.get(i),
                                listIdTrans.get(i), listIdMeaning.get(i), listIdGender.get(i),
                                listIdQuantity.get(i), 0);
                        cursor1.moveToPosition(i);
                    }// for (int i = 0; i < t; i++)
                }//if(cursor1.getCount() > 0)
                String query = "SELECT semantic.id, semantic.name FROM semantic " +
                        "WHERE semantic.name = \"" + word + "\"";
                Cursor cursor = dbUtilities.getDb().rawQuery(query, null);
                cursor.moveToPosition(0);
                String iSemantic = cursor.getString(0);
                //удаляем запись из таблицы semantic
                dbUtilities.removeColumnById(iSemantic, "semantic");
                //перезаполняем и переустанавливаем спиннер
                listSemantic.clear();
                query = "SELECT semantic.name FROM semantic ";
                cursor = dbUtilities.getDb().rawQuery(query, null);
                int l = cursor.getCount();
                for (int i = 0; i < l; i++) {
                    cursor.moveToPosition(i);
                    listSemantic.add(cursor.getString(0));
                }//for (int i = 0; i < l; i++)
                //строим спиннер с именами симантических групп
                spSemanticEWAc.setAdapter(
                        buildSpinnerAdapter(listSemantic));
                //выставляем сохранненую семантическую группу
                String mainQuery = "SELECT semantic.id FROM hebrew " +
                        "INNER JOIN semantic ON semantic.id = hebrew.semantic_id " +
                        "WHERE hebrew.id = " + idHebrew;
                cursor = dbUtilities.getDb().rawQuery(mainQuery, null);
                cursor.moveToPosition(0);
                spSemanticEWAc.setSelection(0);
            }
        });
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });
        alert.show();
    }//killAlertDialogEditText

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            //обработчик actionBar (стрелка сверху слева)
            case android.R.id.home:
                setResult(RESULT_CANCELED);
                onBackPressed();
                return true;
        }//switch
        return super.onOptionsItemSelected(item);
    }//onOptionsItemSelected

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
            case R.id.btnPlTrEWAc:
                // создаем EditText, пишем hint и добавляем в LinearLayout
                countTranslationsLayoutBuilding();
                break;

            case R.id.btnAddSemanticEWAc:
                // вызываем alertDialog для создания семантической группы
                addAlertDialogEditText();
                break;

            case R.id.btnEditSemanticEWAc:
                // вызываем alertDialog для переименования семантической группы
                if (checkForProtectGlobalGroup()) editAlertDialogEditText();
                break;

            case R.id.btnKillSemanticEWAc:
                // вызываем alertDialog для удаления семантической группы
                if (checkForProtectGlobalGroup()) killAlertDialogEditText();
                break;

            case R.id.btnOkEWAc:
                updateWordToDB();
                break;

            case R.id.btnCancelEWAc:
                finish();
                break;

        }//switch
    }//onClick

    //проверка для защиты от изменения и удаления группы global
    private boolean checkForProtectGlobalGroup() {
        if ("global".equals(spSemanticEWAc.getSelectedItem().toString())) {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "This group protected!",
                    Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            return false;
        } else return true;
    }//checkForProtectGlobalGroup

    //процедура постороения layout для строк переводов
    private void countTranslationsLayoutBuilding() {
        countTranslations++;
        tvTranslationsCountEWAc.setText(String.valueOf(countTranslations));
        // создаем EditText, пишем hint и добавляем в LinearLayout
        EditText newEditText = new EditText(this);
        newEditText.setHint("Translation " + countTranslations);
        llTranslationsEWAc.addView(newEditText);
        listETTranslations.add(newEditText);
    }//countTranslationsLayoutBuilding

    private void updateWordToDB() {
        //флаг проверки пустых строк
        boolean flEmptyString = false;
        //флаг проверки пустых строк translations
        boolean flEmptyStringTranslations = false;
        //id из таблицы russian, transcriprion, gender, meaning, quntity
        //для записи в другие таблицы
        List<Integer> listRussianId = new ArrayList<>();
        int transcId = 0;
        int genderId = 0;
        int semanticId = 0;
        int meaningId = 0;
        int quantityId = 0;

        String ruWord;
        String heWord = edHebrewEWAc.getText().toString();
        String gender = spGenderEWAc.getSelectedItem().toString();
        String transc = edTranscriptionEWAc.getText().toString();
        String semantic = spSemanticEWAc.getSelectedItem().toString();
        String meaning = spMeaningEWAc.getSelectedItem().toString();
        String quantity = spQuantityEWAc.getSelectedItem().toString();

        //проверяем на пустые строки переводы
        //если есть хотя бы одина не пустая строка
        //это удовлетворяет наши условия
        int l = countTranslations;
        for (int i = 0; i < l; i++) {
            //берем значение из коллекции
            //используем предварительно функцию trim чтоб убрать лишние пробелы
            ruWord = listETTranslations.get(i).getText().toString().trim();
            //если строка не пустая устанавливаем флаг
            //flEmptyStringTranslations = true
            //и выходим из цикла
            if (!ruWord.equals("")) {
                flEmptyStringTranslations = true;
                break;
            }//if(!ruWord.equals(""))
        }//for (int i = 0; i < l; i++)
        //проверяем пустые строки
        if ((!flEmptyStringTranslations) || (heWord.equals("")) || (transc.equals(""))) {
            if (!flEmptyStringTranslations)
                //если пустые строки включая переводы
                Toast.makeText(context, "Empty lines, you need one Translation!", Toast.LENGTH_SHORT).show();
            else
                //если пустые строки но переводы есть заполненые
                Toast.makeText(context, "Empty lines!", Toast.LENGTH_SHORT).show();
            flEmptyString = true;
        } else {
            //проверяем на повторение ивритовского слова вместе с транскрипцией
            //должно быть одно повторение
            //получаем курсор данных из БД
            String query = "SELECT hebrew.id FROM hebrew " +
                    "INNER JOIN transcriptions ON transcriptions.id = hebrew.transcription_id " +
                    "WHERE hebrew.word_he = \"" + heWord + "\" AND transcriptions.word_tr = \"" + transc + "\"";
            Cursor cursor = dbUtilities.getDb().rawQuery(query, null);

            //если найденны совподения
            if (cursor.getCount() > 0) {
                cursor.moveToPosition(0);
                //если найденно idHebrew не равнное данному
                if (!cursor.getString(0).equals(idHebrew)) {
                    Toast.makeText(context, "Found a match! Correct hebrew word or transcription!", Toast.LENGTH_SHORT).show();
                    flEmptyString = true;
                }//for
            }//if(cursor.getCount()>0)
        }//if-else

        //если нет пустых строк и повторяющихся слов перезаписываем слово
        if (!flEmptyString) {
            String query;
            Cursor cursor;

            ///////////////////////работа с русским словом//////////////////////////
            //подготавливаем информацию для сравнения и проверки запесей
            query = "SELECT translations.id, translations.russian_id FROM translations " +
                    "WHERE translations.hebrew_id = \"" + idHebrew + "\"";
            cursor = dbUtilities.getDb().rawQuery(query, null);
            //количество запесей переводов в БД
            int m = cursor.getCount();

            //записываем старые значения idTranslations до редактирования
            List<Integer> listOldIdTransl = new ArrayList<>();
            //записываем старые значения idRussian до редактирования
            List<Integer> listOldIdRus = new ArrayList<>();
            for (int i = 0; i < m; i++) {
                cursor.moveToPosition(i);
                listOldIdTransl.add(cursor.getInt(0));
                listOldIdRus.add(cursor.getInt(1));
            }//for (int i = 0; i < m; i++)

            //проверяем на повторение русское слово в БД
            //получаем курсор данных из БД
            for (int i = 0; i < l; i++) {
                //берем значение из коллекции
                //используем предварительно функцию trim чтоб убрать лишние пробелы
                ruWord = listETTranslations.get(i).getText().toString().trim();
                //если строка пустая берем следующу итерацию
                //если не пустая обрабатываем
                if (ruWord.equals("")) continue;
                else ruWord = listETTranslations.get(i).getText().toString();

                query = "SELECT russian.id FROM russian WHERE russian.word_ru = \"" + ruWord + "\"";
                cursor = dbUtilities.getDb().rawQuery(query, null);

                //если слово новое и повторений не найденно
                //т.е. в курсоре пусто
                if (cursor.getCount() == 0) {
                    //записываем новое слово в таблицу russian
                    dbUtilities.insertIntoRussians(ruWord);
                    //получаем id последней записи russian
                    query = "SELECT russian.id FROM russian WHERE russian.word_ru = \"" + ruWord + "\"";
                    cursor = dbUtilities.getDb().rawQuery(query, null);
                }//if-else

                //записываем id записи в таблице russian
                //для дальнейшей работы
                cursor.moveToPosition(0);
                listRussianId.add(Integer.parseInt(cursor.getString(0)));
            }//for (int i = 0; i < l; i++)

            //необходимо удалить старые записи
            //которые не записаны в ходе редактирования
            //Выбираем
            int nOld = listOldIdRus.size();
            //коллекция не совпадающих russianId для проверки и удаления
            List<Integer> checkAndRemove = new ArrayList<>();
            //если новых записей больше чем старых
            //мы можем проверить все старые записи
            //на совпадение в новой коллекции
            for (int i = 0; i < nOld; i++) {
                if (!listRussianId.contains(listOldIdRus.get(i)))
                    checkAndRemove.add(listOldIdRus.get(i));
            }//for (int i = 0; i < l; i++)
            //проверка относится ли данное russianId к нескольким словам
            int n = checkAndRemove.size();
            for (int i = 0; i < n; i++) {
                query = "SELECT translations.id, translations.russian_id FROM translations " +
                        "WHERE translations.russian_id = \"" + checkAndRemove.get(i) + "\"";
                cursor = dbUtilities.getDb().rawQuery(query, null);
                cursor.moveToPosition(0);
                //если russianId принадлежит к одному слову
                //т.е. размер курсора = 1
                // если нет то просто удаляем запись из translations
                //т.е. рвем связь между словом и переводом
                if (cursor.getCount() == 1) {
                    //теперь удаляем запись относящуюся к нашему idHebrew
                    query = "SELECT translations.id, translations.russian_id FROM translations " +
                            "WHERE translations.russian_id = \"" + checkAndRemove.get(i) + "\" " +
                            "AND translations.hebrew_id = \"" + idHebrew + "\" ";
                    cursor = dbUtilities.getDb().rawQuery(query, null);
                    cursor.moveToPosition(0);
                    //удаляем запись по id из таблицы translations
                    dbUtilities.removeColumnById(cursor.getString(0), "translations");
                    //удаляем запись по id из таблицы russian
                    dbUtilities.removeColumnById(cursor.getString(1), "russian");
                } else {
                    //теперь удаляем запись относящуюся к нашему idHebrew
                    query = "SELECT translations.id, translations.russian_id FROM translations " +
                            "WHERE translations.russian_id = \"" + checkAndRemove.get(i) + "\" " +
                            "AND translations.hebrew_id = \"" + idHebrew + "\" ";
                    cursor = dbUtilities.getDb().rawQuery(query, null);
                    cursor.moveToPosition(0);
                    //удаляем запись по id из таблицы translations
                    dbUtilities.removeColumnById(cursor.getString(0), "translations");
                }//if (cursor.getCount() == 1)
            }//for (int i = 0; i < n; i++)

            ////////////работа с таблицей translations, если есть необходимость////////
            //если количество записей в таблице russian не равно введеным заново
            //то перезаписываем записи таблицы translations
            l = listRussianId.size();
            for (int i = 0; i < l; i++) {
                //записываем новую строку в таблицу translations
                //проверяем если russianId содержится в старых записях
                // то нету смысла перезаписывать связи в таблицы translations
                if (!listOldIdRus.contains(listRussianId.get(i)))
                    dbUtilities.insertIntoTranslations(Integer.parseInt(idHebrew), listRussianId.get(i));
            }//for (int i = 0; i < l; i++)

            ///////////////////////работа с транскрипцией//////////////////////////
            //проверяем на повторение транскипцию
            //получаем курсор данных из БД
            query = "SELECT hebrew.transcription_id FROM hebrew WHERE hebrew.id = \"" + idHebrew + "\"";
            cursor = dbUtilities.getDb().rawQuery(query, null);

            //если слово новое и повторений не найденно
            //т.е. в курсоре пусто
            if (cursor.getCount() == 0) {
                //записываем новое слово в таблицу transcriptions
                dbUtilities.insertIntoTranscriptions(transc);
                //получаем id последней записи transcriptions
                query = "SELECT transcriptions.id FROM transcriptions WHERE transcriptions.word_tr = \"" + transc + "\"";
                cursor = dbUtilities.getDb().rawQuery(query, null);
            }//if-else
            //записываем id записи в таблице transcriptions
            //для дальнейшей работы
            cursor.moveToPosition(0);
            transcId = Integer.parseInt(cursor.getString(0));
            //перезаписываем запись в transcriptions
            dbUtilities.updTableTranscriptions(String.valueOf(transcId), transc);

            ///////////////////////работа с гендерным признаком////////////////////
            //определяем id из таблицы gender
            //получаем курсор данных из БД
            query = "SELECT gender.id FROM gender WHERE gender.option = \"" + gender + "\"";
            cursor = dbUtilities.getDb().rawQuery(query, null);
            cursor.moveToPosition(0);
            genderId = Integer.parseInt(cursor.getString(0));

            ///////////////////////работа с симантической группой////////////////////
            //определяем id из таблицы semantic
            //получаем курсор данных из БД
            query = "SELECT semantic.id FROM semantic WHERE semantic.name = \"" + semantic + "\"";
            cursor = dbUtilities.getDb().rawQuery(query, null);
            cursor.moveToPosition(0);
            semanticId = Integer.parseInt(cursor.getString(0));

            ///////////////////////работа с признаком части речи////////////////////
            //определяем id из таблицы meaning
            //получаем курсор данных из БД
            query = "SELECT meanings.id FROM meanings WHERE meanings.option = \"" + meaning + "\"";
            cursor = dbUtilities.getDb().rawQuery(query, null);
            cursor.moveToPosition(0);
            meaningId = Integer.parseInt(cursor.getString(0));

            ///////////////////////работа с количественным признаком///////////////
            //определяем id из таблицы quantity
            //получаем курсор данных из БД
            query = "SELECT quantity.id FROM quantity WHERE quantity.option = \"" + quantity + "\"";
            cursor = dbUtilities.getDb().rawQuery(query, null);
            cursor.moveToPosition(0);
            quantityId = Integer.parseInt(cursor.getString(0));

            ///////////////////////работа с ивритовским словом////////////////////
            //записываем новое слово в таблицу hebrew
            dbUtilities.updTableHebrew(idHebrew, heWord, transcId, meaningId, genderId, quantityId, semanticId);

            Toast.makeText(context, "Data updated!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(context, ViewDictionaryActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }//if(!flEmptyString)
    }//updateWordToDB
}//EditWordActivity
