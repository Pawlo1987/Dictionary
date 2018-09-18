package com.example.user.dictionary;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
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

public class AddNewWordActivity extends AppCompatActivity {
    private EditText edHebrewANWAc;         //строка с ивритовским словом
    private EditText edTranscriptionANWAc;  //строка с транскрпцией
    private Spinner spGenderANWAc;       //сппинер для рода в иврите
    private Spinner spQuantityANWAc;        //сппинер множест. или единств. число
    private Spinner spSemanticANWAc;         //сппинер c именнами симантических групп
    private Spinner spMeaningANWAc;         //сппинер значения слова в предложении
    private List<String> listTypesPOS;      //коллекция видов частей речи
    private List<String> listSemantic;      //коллекиция со списком имен симантических групп
    private LinearLayout llTranslationsANWAc; //LinearLayout для переводов
    //TextView для вывода количества заказанных переводов
    private TextView tvTranslationsCountANWAc;
    //коллекция EditText для переводов
    List<EditText> listETTranslations;

    //количество переводов
    private int countTranslations = 0;

    DBUtilities dbUtilities;
    Context context;
    ActionBar actionBar;                //стрелка НАЗАД

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_word);

        //добавляем actionBar (стрелка сверху слева)
        actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        context = getBaseContext();
        dbUtilities = new DBUtilities(context);
        dbUtilities.open();
        edHebrewANWAc = findViewById(R.id.edWordANWAc);
        edTranscriptionANWAc = findViewById(R.id.edTranscriptionANWAc);
        spGenderANWAc = findViewById(R.id.spGenderANWAc);
        spQuantityANWAc = findViewById(R.id.spQuantityANWAc);
        spSemanticANWAc = findViewById(R.id.spSemanticANWAc);
        spMeaningANWAc = findViewById(R.id.spMeaningANWAc);
        //коллекиция со списком имен симантических групп
        listSemantic = new ArrayList<>();
        String query = "SELECT semantic.name FROM semantic";
        Cursor cursor = dbUtilities.getDb().rawQuery(query, null);
        int l = cursor.getCount();
        for (int i = 0; i < l; i++) {
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
        tvTranslationsCountANWAc = findViewById(R.id.tvTranslationsCountANWAc);
        llTranslationsANWAc = findViewById(R.id.llTranslationsANWAc);
        listETTranslations = new ArrayList<>();
        // создаем EditText, пишем hint и добавляем в LinearLayout
        countTranslationsLayoutBuilding();

        //строим спиннер для рода в иврите
        spGenderANWAc.setAdapter(
                buildSpinnerAdapter(Arrays.asList("male", "female")));
        //строим спиннер для множ. и ед. числа
        spQuantityANWAc.setAdapter(
                buildSpinnerAdapter(Arrays.asList("one", "many")));
        //строим спиннер с именами симантических групп
        spSemanticANWAc.setAdapter(
                buildSpinnerAdapter(listSemantic));
        //строим спиннер для значения в предложении
        spMeaningANWAc.setAdapter(
                buildSpinnerAdapter(listTypesPOS));
        spMeaningANWAc.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //если выбран verb перестраиваем все spinner ы
                // добавляем пункт infinitive
                if(position == 2){
                    //строим спиннер для рода в иврите
                    spGenderANWAc.setAdapter(
                            buildSpinnerAdapter(Arrays.asList("male", "female", "infinitive")));
                    //строим спиннер для множ. и ед. числа
                    spQuantityANWAc.setAdapter(
                            buildSpinnerAdapter(Arrays.asList("one", "many", "infinitive")));
                }else{
                    //строим спиннер для рода в иврите
                    spGenderANWAc.setAdapter(
                            buildSpinnerAdapter(Arrays.asList("male", "female")));
                    //строим спиннер для множ. и ед. числа
                    spQuantityANWAc.setAdapter(
                            buildSpinnerAdapter(Arrays.asList("one", "many")));
                }//if(position == 3)
            }//onItemSelected
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }//onNothingSelected
        });
    }//onCreate

    //AlertDialog для создания новой семантической группы
    public void alertDialogEditText() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Add new group!");
//        alert.setMessage("Create new data!");
        alert.setIcon(R.drawable.icon_information);
        final EditText input = new EditText(this);
//        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setText("");
        alert.setView(input);
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String word = input.getText().toString().trim();
                //проверка на совпадение
                String query = "SELECT semantic.id FROM semantic " +
                        "WHERE semantic.name = \"" + word + "\"";
                Cursor cursor = dbUtilities.getDb().rawQuery(query, null);
                if(!word.equals("")&&(cursor.getCount()==0)) {
                    //непосредственно добавляем новое значение в таблицу
                    dbUtilities.insertIntoSemantic(word);
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
                spSemanticANWAc.setAdapter(
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id){
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
            case R.id.btnPlTrANWAc:
                // создаем EditText, пишем hint и добавляем в LinearLayout
                countTranslationsLayoutBuilding();
                break;

            case R.id.btnAddSemanticANWAc:
                // вызываем alertDialog для создания группы
                alertDialogEditText();
                break;

            case R.id.btnOkANWAc:
                addNewWordToDB();
                break;

            case R.id.btnCancelANWAc:
                finish();
                break;

        }//switch
    }//onClick

    //процедура постороения layout для строк переводов
    private void countTranslationsLayoutBuilding() {
        countTranslations++;
        tvTranslationsCountANWAc.setText(String.valueOf(countTranslations));
        // создаем EditText, пишем hint и добавляем в LinearLayout
        EditText newEditText = new EditText(this);
        newEditText.setHint("Translation " + countTranslations);
        llTranslationsANWAc.addView(newEditText);
        listETTranslations.add(newEditText);
    }//countTranslationsLayoutBuilding

    private void addNewWordToDB() {
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
        int hebrewId = 0;


        String ruWord;
        String heWord = edHebrewANWAc.getText().toString();
        String gender = spGenderANWAc.getSelectedItem().toString();
        String transc = edTranscriptionANWAc.getText().toString();
        String semantic = spSemanticANWAc.getSelectedItem().toString();
        String meaning = spMeaningANWAc.getSelectedItem().toString();
        String quantity = spQuantityANWAc.getSelectedItem().toString();

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
            if(!ruWord.equals("")) {
                flEmptyStringTranslations = true;
                break;
            }//if(!ruWord.equals(""))
        }//for (int i = 0; i < l; i++)
        //проверяем пустые строки
        if ((!flEmptyStringTranslations) || (heWord.equals("")) || (transc.equals(""))) {
            if(!flEmptyStringTranslations)
                //если пустые строки включая переводы
                Toast.makeText(context, "Empty lines, you need one Translation!", Toast.LENGTH_SHORT).show();
            else
                //если пустые строки но переводы есть заполненые
                Toast.makeText(context, "Empty lines!", Toast.LENGTH_SHORT).show();
            flEmptyString = true;
        }else{
            //проверяем на повторение ивритовского слова вместе с транскрипцией
            //получаем курсор данных из БД
            String query = "SELECT hebrew.id FROM hebrew " +
                    "INNER JOIN transcriptions ON transcriptions.id = hebrew.transcription_id " +
                    "WHERE hebrew.word_he = \"" + heWord + "\" AND transcriptions.word_tr = \"" + transc + "\"";
            Cursor cursor = dbUtilities.getDb().rawQuery(query, null);
            //если найденно повторение
            if(cursor.getCount() > 0){
                Toast.makeText(context, "Found a match! Correct hebrew word or transcription!", Toast.LENGTH_SHORT).show();
                flEmptyString = true;
            }//for
        }//if-else

        //если нет пустых строк и повторяющихся слов записываем новое слово
        if(!flEmptyString) {
            String query;
            Cursor cursor;

            ///////////////////////работа с русским словом//////////////////////////
            //проверяем на повторение русское слово в БД
            //получаем курсор данных из БД
            for (int i = 0; i < l; i++) {
                //берем значение из коллекции
                //используем предварительно функцию trim чтоб убрать лишние пробелы
                ruWord = listETTranslations.get(i).getText().toString().trim();
                //если строка пустая берем следующу итерацию
                //если не пустая обрабатываем
                if(ruWord.equals("")) continue;
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

            ///////////////////////работа с транскрипцией//////////////////////////
            //проверяем на повторение транскипцию
            //получаем курсор данных из БД
            query = "SELECT transcriptions.id FROM transcriptions WHERE transcriptions.word_tr = \"" + transc + "\"";
            cursor = dbUtilities.getDb().rawQuery(query, null);

            //если слово новое и повторений не найденно
            //т.е. в курсоре пусто
            if(cursor.getCount() == 0){
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
            query = "SELECT meanings.id FROM meanings WHERE meanings.option = \"" + meaning+ "\"";
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
            dbUtilities.insertIntoHebrew(heWord, transcId, meaningId, genderId, quantityId, semanticId);
            //получаем id последней записи
            // c heWord и transcId
            query = "SELECT hebrew.id FROM hebrew " +
                    "WHERE hebrew.word_he = \"" + heWord + "\" " +
                    "AND hebrew.transcription_id = \"" + transcId + "\"";
            cursor = dbUtilities.getDb().rawQuery(query, null);
            cursor.moveToPosition(0);
            hebrewId = Integer.parseInt(cursor.getString(0));

            ///////////////////////работа с таблицей translations////////////
            //получаем кол-во строк для записи
            l = listRussianId.size();
            for (int i = 0; i < l; i++) {
                //записываем новую строку в таблицу translations
                dbUtilities.insertIntoTranslations(hebrewId, listRussianId.get(i));
            }//for (int i = 0; i < l; i++)
            Toast.makeText(context, "New word added!", Toast.LENGTH_SHORT).show();
            finish();
        }//if(!flEmptyString)
    }//addNewWordToDB
}//AddNewWordActivity
