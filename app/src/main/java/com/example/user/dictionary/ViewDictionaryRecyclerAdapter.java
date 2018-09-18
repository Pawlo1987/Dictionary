package com.example.user.dictionary;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ViewDictionaryRecyclerAdapter  extends
        RecyclerView.Adapter<ViewDictionaryRecyclerAdapter.ViewHolder>{

    //поля класса ViewDictionaryRecyclerAdapter
    private LayoutInflater inflater;
    private Cursor cursor;
    private Context context;

    //конструктор
    public ViewDictionaryRecyclerAdapter(Context context, Cursor cursor) {
        this.inflater = LayoutInflater.from(context);
        //получение интерфеса из класса Фрагмента
        //для обработки нажатия элементов RecyclerAdapter
        this.context = context;
        this.cursor = cursor;
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

        // получение данных
        //слово на русском
        holder.tvRUWordVDRA.setText(cursor.getString(1));
        //род слова в русском
        holder.tvGenderRUVDRA.setText(cursor.getString(4));
        //слово на иврите
        holder.tvHEWordVDRA.setText(cursor.getString(2));
        //род слова в иврите
        holder.tvGenderHEVDRA.setText(cursor.getString(5));
        //транскрпция слова на иврите
        holder.tvTransVDRA.setText(cursor.getString(3));
        //значение слова в предложении
        holder.tvMeaningVDRA.setText(cursor.getString(6));
        //множественное или едиственное слово
        holder.tvQuantityVDRA.setText(cursor.getString(7));
    } // onBindViewHolder

    //получаем количество элементов объекта(курсора)
    @Override
    public int getItemCount() { return cursor.getCount(); }

    //Создаем класс ViewHolder с помощью которого мы получаем ссылку на каждый элемент
    //отдельного пункта списка и подключаем слушателя события нажатия меню
    public class ViewHolder extends RecyclerView.ViewHolder {
        final TextView tvRUWordVDRA, tvGenderRUVDRA, tvHEWordVDRA,
                tvGenderHEVDRA, tvTransVDRA, tvMeaningVDRA, tvQuantityVDRA;

        ViewHolder(View view) {
            super(view);
            tvRUWordVDRA = view.findViewById(R.id.tvRUWordVDRA);
            tvGenderRUVDRA = view.findViewById(R.id.tvGenderRUVDRA);
            tvHEWordVDRA = view.findViewById(R.id.tvHEWordVDRA);
            tvGenderHEVDRA = view.findViewById(R.id.tvGenderHEVDRA);
            tvTransVDRA = view.findViewById(R.id.tvTransVDRA);
            tvMeaningVDRA = view.findViewById(R.id.tvMeaningVDRA);
            tvQuantityVDRA = view.findViewById(R.id.tvQuantityVDRA);
        } // ViewHolder

    }//class ViewHolder

}//ViewDictionaryRecyclerAdapter
