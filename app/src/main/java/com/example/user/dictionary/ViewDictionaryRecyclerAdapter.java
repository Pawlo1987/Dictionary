package com.example.user.dictionary;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import static android.view.View.GONE;

public class ViewDictionaryRecyclerAdapter  extends
        RecyclerView.Adapter<ViewDictionaryRecyclerAdapter.ViewHolder>{

    //поля класса ViewDictionaryRecyclerAdapter
    private LayoutInflater inflater;
    private Cursor cursor;
    private Context context;
    private String filter;
    private String ruWord, ruGender, heWord, heGender,
                   trans, meaning, quantity;

    //конструктор
    public ViewDictionaryRecyclerAdapter(Context context, Cursor cursor, String filter) {
        this.inflater = LayoutInflater.from(context);
        //получение интерфеса из класса Фрагмента
        //для обработки нажатия элементов RecyclerAdapter
        this.context = context;
        this.cursor = cursor;
        this.filter = filter;
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
            //setVisibility(GONE) отключаем ненужные элементы для просмотра
            holder.cvMainVDRA.setVisibility(GONE);
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

        ViewHolder(View view) {
            super(view);
            cvMainVDRA = view.findViewById(R.id.cvMainVDRA);
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
