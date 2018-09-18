package com.example.user.dictionary;

import android.app.Activity;
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
import android.widget.TextView;
import android.widget.Toast;

import static android.content.Context.CLIPBOARD_SERVICE;
import static android.view.View.GONE;

public class ViewDictionaryRecyclerAdapter  extends
        RecyclerView.Adapter<ViewDictionaryRecyclerAdapter.ViewHolder>{

    //поля класса ViewDictionaryRecyclerAdapter
    private LayoutInflater inflater;
    Context context;
    private Cursor cursor;
    private String mainQuery;
    DBUtilities dbUtilities;
    private String filter;
    private String idRussian, idHebrew, idTranscription;
    private String ruWord, ruGender, heWord, heGender,
                   trans, meaning, quantity;
    //Объекты фрейморка clipboard framework
    //(фреймворк буфера обмена) для копирования и вставки различных типов данных
    ClipboardManager clipboardManager;
    ClipData clipData;

    //конструктор
    public ViewDictionaryRecyclerAdapter(Context context, String mainQuery, String filter) {
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
        ruWord = cursor.getString(1);   //слово на русском
        heWord = cursor.getString(2);   //слово на иврите
        trans = cursor.getString(3);    //транскрпция слова на иврите

        // получение данных
        //фильтрация элементов для бинарного поиска
        if((filter == "")||(ruWord.contains(filter))
                ||(heWord.contains(filter))||(trans.contains(filter))){
            //получаем остальные данные из курсора
            ruGender = cursor.getString(4); //род слова в русском
            heGender = cursor.getString(5); //род слова в иврите
            meaning = cursor.getString(6);  //значение слова в предложении
            quantity = cursor.getString(7); //множественное или едиственное слово
            //устанавливаем данные в текстовые поля адаптера
            holder.tvRUWordVDRA.setText(ruWord);
            holder.tvGenderRUVDRA.setText(ruGender);
            holder.tvHEWordVDRA.setText(heWord);
            holder.tvGenderHEVDRA.setText(heGender);
            holder.tvTransVDRA.setText(trans);
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
    public class ViewHolder extends RecyclerView.ViewHolder {
        final TextView tvRUWordVDRA, tvGenderRUVDRA, tvHEWordVDRA,
                tvGenderHEVDRA, tvTransVDRA, tvMeaningVDRA, tvQuantityVDRA;
        final CardView cvMainVDRA;
        final Button btnEditVDRA, btnDellVDRA;

        ViewHolder(final View view) {
            super(view);
            btnDellVDRA = view.findViewById(R.id.btnDellVDRA);
            btnEditVDRA = view. findViewById(R.id.btnEditVDRA);
            cvMainVDRA = view.findViewById(R.id.cvMainVDRA);
            tvRUWordVDRA = view.findViewById(R.id.tvRUWordVDRA);
            tvGenderRUVDRA = view.findViewById(R.id.tvGenderRUVDRA);
            tvHEWordVDRA = view.findViewById(R.id.tvHEWordVDRA);
            tvGenderHEVDRA = view.findViewById(R.id.tvGenderHEVDRA);
            tvTransVDRA = view.findViewById(R.id.tvTransVDRA);
            tvMeaningVDRA = view.findViewById(R.id.tvMeaningVDRA);
            tvQuantityVDRA = view.findViewById(R.id.tvQuantityVDRA);
            clipboardManager=(ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);

            btnEditVDRA.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cursor.moveToPosition(getAdapterPosition());
                    //id из таблицы russian
                    idRussian = cursor.getString(0);
                    alertDialogTwoButton(view.getContext(), "Edit", idRussian);
                }
            });
            btnDellVDRA.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cursor.moveToPosition(getAdapterPosition());
                    //id из таблицы russian
                    idRussian = cursor.getString(0);
                    alertDialogTwoButton(view.getContext(), "Delete", idRussian);
                }// onClick
            });// btnDellVDRA.setOnClickListener
            cvMainVDRA.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    cursor.moveToPosition(getAdapterPosition());
                    //id из таблицы russian
                    heWord = cursor.getString(2);   //слово на иврите
                    clipData = ClipData.newPlainText("text",heWord);
                    clipboardManager.setPrimaryClip(clipData);

                    Toast.makeText(context,"Translation copied ",Toast.LENGTH_SHORT).show();
                    return false;
                }
            });
        } // ViewHolder

        //AlertDialog с двумя кнопками
        private void alertDialogTwoButton(final Context context, final String message, final String idRussian) {
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
                        intent.putExtra("id", idRussian);
                        context.startActivity(intent);
                    }else{
                        //подготавливаем информацию для удаления
                        //получаем idHebrew
                        String query = "SELECT hebrew_id FROM russians " +
                                "WHERE russians.id = \"" + idRussian + "\"";
                        cursor = dbUtilities.getDb().rawQuery(query, null);
                        cursor.moveToPosition(0);
                        idHebrew = cursor.getString(0);
                        //получаем idTranscription
                        query = "SELECT transcription_id FROM hebrew " +
                                "WHERE hebrew.id = \"" + String.valueOf(idHebrew) + "\"";
                        cursor = dbUtilities.getDb().rawQuery(query, null);
                        cursor.moveToPosition(0);
                        idTranscription = cursor.getString(0);

                        //удаляем поочереди таблицы
                        dbUtilities.removeColumnById(idRussian, "russians");
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
