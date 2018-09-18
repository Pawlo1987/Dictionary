package com.example.user.dictionary;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.CLIPBOARD_SERVICE;

public class ViewDictionaryRecyclerAdapter  extends
        RecyclerView.Adapter<ViewDictionaryRecyclerAdapter.ViewHolder>{

    //поля класса ViewDictionaryRecyclerAdapter
    private LayoutInflater inflater;
    Context context;
    private Cursor cursor;
    private String mainQuery;
    DBUtilities dbUtilities;
    private String filter;
    private String idHebrew, idTranscription;
    private String ruWord, heWord, gender,
                   trans, meaning, quantity;
    //коллекция содержащая переводы одного слова
    private List<String> listTranslationsOneWord = new ArrayList<>();
    //Объекты фрейморка clipboard framework
    //(фреймворк буфера обмена) для копирования и вставки различных типов данных
    ClipboardManager clipboardManager;
    ClipData clipData;

    //конструктор
    ViewDictionaryRecyclerAdapter(Context context, String mainQuery, String filter) {
        this.inflater = LayoutInflater.from(context);
        //получение интерфеса из класса Фрагмента
        //для обработки нажатия элементов RecyclerAdapter
        this.mainQuery = mainQuery;
        this.context = context;
        this.filter = filter;
        dbUtilities = new DBUtilities(context);
        dbUtilities.open();
        cursor = dbUtilities.getDb().rawQuery(mainQuery, null);
    } // ViewDictionaryRecyclerAdapter

    //создаем новую разметку(View) путем указания разметки
    @Override
    public ViewDictionaryRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.recycler_adapter_view_dictrionary, parent, false);
        return new ViewDictionaryRecyclerAdapter.ViewHolder(view);
    }

    //привязываем элементы разметки к переменным объекта(в данном случае к курсору)
    @Override
    public void onBindViewHolder(ViewDictionaryRecyclerAdapter.ViewHolder holder, int position) {
        // переходим в курсоре на текущую позицию
        cursor.moveToPosition(position);

        //получаем данные из курсора для фильтрации
        idHebrew = cursor.getString(0);   //hebrew.id
        heWord = cursor.getString(1);   //слово на иврите
        trans = cursor.getString(2);    //транскрпция слова на иврите

        //флаг содержания фильтра бинарного поиска в переводе
        boolean flTranslation = false;

        //для получения перевода необходимо сделать запрос к БД
        String query = "SELECT russian.word_ru FROM translations " +
                "INNER JOIN russian ON russian.id = translations.russian_id " +
                "WHERE translations.hebrew_id = \"" + idHebrew + "\"";
        Cursor cursorRu = dbUtilities.getDb().rawQuery(query, null);
        int l = cursorRu.getCount();
        //отчищаем коллекцию перед заполнением воизбижания повторов
        listTranslationsOneWord.clear();
        //отчищаем layout возбежание повторов при работе recyclerView
        holder.llTranslationsVDRA.removeAllViews();
        for (int i = 0; i < l; i++) {
            // создаем TextView, пишем String и добавляем в LinearLayout
            TextView newTextView = new TextView(context);
            cursorRu.moveToPosition(i);
            ruWord = cursorRu.getString(0);
            newTextView.setText((i+1)+". "+ruWord);//устанавливаем слово в в textView
            holder.llTranslationsVDRA.addView(newTextView);
            listTranslationsOneWord.add(ruWord);//собираем коллекцию для бинарного поиска

            //обработка бинарного поиска для перевода
            if(ruWord.contains(filter)) flTranslation = true;
        }//for (int i = 0; i < l; i++)

        // получение данных
        //фильтрация элементов для бинарного поиска
        if((filter.equals("")) ||(heWord.contains(filter))
                ||(trans.contains(filter))||(flTranslation)){
            //получаем остальные данные из курсора
            meaning = cursor.getString(3);  //значение слова в предложении
            gender = cursor.getString(4); //род слова в иврите
            quantity = cursor.getString(5); //множественное или едиственное слово
            //устанавливаем данные в текстовые поля адаптера
            holder.tvWordVDRA.setText(heWord);
            holder.tvGenderVDRA.setText(gender);
            holder.tvTransсVDRA.setText(trans);
            holder.tvMeaningVDRA.setText(meaning);
            holder.tvQuantityVDRA.setText(quantity);
        }else {
            //setVisibility(View.GONE) отключаем ненужные элементы для просмотра
            holder.cvMainVDRA.setVisibility(View.GONE);
        }//if-else

    } // onBindViewHolder

    //получаем количество элементов объекта(курсора)
    @Override
    public int getItemCount() { return cursor.getCount(); }

    //Создаем класс ViewHolder с помощью которого мы получаем ссылку на каждый элемент
    //отдельного пункта списка и подключаем слушателя события нажатия меню
    class ViewHolder extends RecyclerView.ViewHolder {
        final TextView tvWordVDRA, tvTransсVDRA, tvGenderVDRA, tvMeaningVDRA, tvQuantityVDRA;
        final CardView cvMainVDRA;
        final Button btnEditVDRA, btnDellVDRA;
        final LinearLayout llTranslationsVDRA;  //layout для переводов

        ViewHolder(final View view) {
            super(view);
            llTranslationsVDRA = view.findViewById(R.id.llTranslationsVDRA);
            btnDellVDRA = view.findViewById(R.id.btnDellVDRA);
            btnEditVDRA = view. findViewById(R.id.btnEditVDRA);
            cvMainVDRA = view.findViewById(R.id.cvMainVDRA);
            tvWordVDRA = view.findViewById(R.id.tvWordVDRA);
            tvGenderVDRA = view.findViewById(R.id.tvGenderVDRA);
            tvTransсVDRA = view.findViewById(R.id.tvTranscVDRA);
            tvMeaningVDRA = view.findViewById(R.id.tvMeaningVDRA);
            tvQuantityVDRA = view.findViewById(R.id.tvQuantityVDRA);
            clipboardManager=(ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);

            btnEditVDRA.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cursor.moveToPosition(getAdapterPosition());
                    //id из таблицы hebrew
                    idHebrew = cursor.getString(0);
                    alertDialogTwoButton(view.getContext(), "Edit", idHebrew);
                }
            });//btnEditVDRA.setOnClickListener
            btnDellVDRA.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cursor.moveToPosition(getAdapterPosition());
                    //id из таблицы hebrew
                    idHebrew = cursor.getString(0);
                    alertDialogTwoButton(view.getContext(), "Delete", idHebrew);
                }// onClick
            });// btnDellVDRA.setOnClickListener
            cvMainVDRA.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    cursor.moveToPosition(getAdapterPosition());
                    //id из таблицы hebrew
                    heWord = cursor.getString(1);   //слово на иврите
                    clipData = ClipData.newPlainText("text",heWord);
                    clipboardManager.setPrimaryClip(clipData);

                    Toast.makeText(context,"Word copied ",Toast.LENGTH_SHORT).show();
                    return false;
                }
            });//cvMainVDRA.setOnLongClickListener
        } // ViewHolder

        //AlertDialog с двумя кнопками
        private void alertDialogTwoButton(final Context context, final String message, final String idHebrew) {
            AlertDialog.Builder ad;
            String button1String = "OK";
            String button2String = "Cancel";

            ad = new AlertDialog.Builder(context);
            ad.setTitle("Warning");  // заголовок
            ad.setMessage(message + ". Are you sure?"); // сообщение
            ad.setIcon(R.drawable.icon_question);
            ad.setPositiveButton(button1String, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int arg1) {
                    //проверка какая функция вызвала AlertDialog
                    if (message.equals("Edit")){
                        Intent intent = new Intent(context, EditWordActivity.class);
                        intent.putExtra("idHebrew", idHebrew);
                        context.startActivity(intent);
                    }else{
                        //подготавливаем информацию для удаления из таблиц
                        //сначало translations а затем russian
                        //получаем listIdRussian из таблицы translations
                        String query = "SELECT translations.id, translations.russian_id FROM translations " +
                                "WHERE translations.hebrew_id = \"" + idHebrew + "\"";
                        cursor = dbUtilities.getDb().rawQuery(query, null);
                        int l = cursor.getCount();
                        for (int i = 0; i < l; i++) {
                            cursor.moveToPosition(i);
                            //удаляем запись по id из таблицы translations
                            dbUtilities.removeColumnById(cursor.getString(0), "translations");
                            //удаляем запись по id из таблицы russian
                            dbUtilities.removeColumnById(cursor.getString(1), "russian");
                        }// for (int i = 0; i < l; i++)

                        //получаем idTranscription
                        query = "SELECT transcription_id FROM hebrew " +
                                "WHERE hebrew.id = \"" + String.valueOf(idHebrew) + "\"";
                        cursor = dbUtilities.getDb().rawQuery(query, null);
                        cursor.moveToPosition(0);
                        idTranscription = cursor.getString(0);

                        //удаляем поочереди таблицы
                        dbUtilities.removeColumnById(idHebrew, "hebrew");
                        dbUtilities.removeColumnById(idTranscription, "transcriptions");

                        //обновляем данные для Adapter
                        cursor = dbUtilities.getDb().rawQuery(mainQuery, null);
                        notifyDataSetChanged();
                        Toast.makeText(context, "Data removed!", Toast.LENGTH_SHORT).show();
                    }//if-else
                }//onClick
            });
            ad.setNegativeButton(button2String, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int arg1) {}
            });
            ad.setCancelable(true);
            ad.setOnCancelListener(new DialogInterface.OnCancelListener() {
                public void onCancel(DialogInterface dialog) {}
            });
            ad.show();
        }//alertDialogTwoButton

    }//class ViewHolder

}//ViewDictionaryRecyclerAdapter
