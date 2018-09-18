package com.example.user.dictionary;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import static android.content.Context.CLIPBOARD_SERVICE;

public class SelectLearnWordsRecyclerAdapter  extends
        RecyclerView.Adapter<SelectLearnWordsRecyclerAdapter.ViewHolder>{

    //поля класса SelectLearnWordsRecyclerAdapter
    private LayoutInflater inflater;
    Context context;
    private Cursor cursor;
    private String mainQuery;
    DBUtilities dbUtilities;
    private String filter;
    private String idRussian, idHebrew;
    private String ruWord, heWord;

    //конструктор
    public SelectLearnWordsRecyclerAdapter(Context context, String mainQuery, String filter) {
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
    public SelectLearnWordsRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.recycler_adapter_select_learn_words, parent, false);
        return new SelectLearnWordsRecyclerAdapter.ViewHolder(view);
    }

    //привязываем элементы разметки к переменным объекта(в данном случае к курсору)
    @Override
    public void onBindViewHolder(SelectLearnWordsRecyclerAdapter.ViewHolder holder, int position) {
        // переходим в курсоре на текущую позицию
        cursor.moveToPosition(position);

        //получаем данные из курсора для фильтрации
        ruWord = cursor.getString(1);   //слово на русском
        heWord = cursor.getString(2);   //слово на иврите

        // получение данных
        //фильтрация элементов для бинарного поиска
        if((filter == "")||(ruWord.contains(filter))
                ||(heWord.contains(filter))){

            //устанавливаем данные в текстовые поля адаптера
            holder.tvRUWordSLWRA.setText(ruWord);
            holder.tvHEWordSLWRA.setText(heWord);
        }else {
            //setVisibility(View.GONE) отключаем ненужные элементы для просмотра
            holder.cvMainSLWRA.setVisibility(View.GONE);
        }//if-else

    } // onBindViewHolder

    //получаем количество элементов объекта(курсора)
    @Override
    public int getItemCount() { return cursor.getCount(); }

    //Создаем класс ViewHolder с помощью которого мы получаем ссылку на каждый элемент
    //отдельного пункта списка и подключаем слушателя события нажатия меню
    public class ViewHolder extends RecyclerView.ViewHolder {
        final TextView tvRUWordSLWRA, tvHEWordSLWRA;
        final CardView cvMainSLWRA;
        final CheckBox cbSelectLearnWordsRA;

        ViewHolder(final View view) {
            super(view);
            cbSelectLearnWordsRA = view.findViewById(R.id.cbSelectLearnWordsRA);
            cvMainSLWRA = view.findViewById(R.id.cvMainSLWRA);
            tvRUWordSLWRA = view.findViewById(R.id.tvRUWordSLWRA);
            tvHEWordSLWRA = view.findViewById(R.id.tvHEWordSLWRA);

            cbSelectLearnWordsRA.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }//onClick
            });//setOnClickListener

        } // ViewHolder

    }//class ViewHolder

}//SelectLearnWordsRecyclerAdapter
