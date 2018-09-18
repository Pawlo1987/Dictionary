package com.example.user.dictionary;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

public class SelectLearnWordsRecyclerAdapter  extends
        RecyclerView.Adapter<SelectLearnWordsRecyclerAdapter.ViewHolder>{

    //поля класса SelectLearnWordsRecyclerAdapter
    private LayoutInflater inflater;
    Context context;
    private Cursor cursor;
    DBUtilities dbUtilities;
    private String filter;
    private String idRussian;
    private String translations, word;
    private List<String> listCursorNum;

    //конструктор
    SelectLearnWordsRecyclerAdapter(Context context, String mainQuery,
                                    String filter, List<String> listCursorNum) {
        this.inflater = LayoutInflater.from(context);
        //получение интерфеса из класса Фрагмента
        //для обработки нажатия элементов RecyclerAdapter
        this.context = context;
        this.filter = filter;
        this.listCursorNum = listCursorNum;
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

        //получаем перевод
        String queryTr = "SELECT russian.word_ru FROM translations " +
                "INNER JOIN russian ON russian.id = translations.russian_id " +
                "WHERE translations.hebrew_id = \"" + cursor.getString(0) + "\"";
        Cursor cursorTr = dbUtilities.getDb().rawQuery(queryTr, null);
        cursorTr.moveToFirst();

        //получаем данные из курсора для фильтрации
        translations = cursorTr.getString(0);     //слово на русском
        word = cursor.getString(1);     //слово на иврите
        if(listCursorNum.contains(String.valueOf(position)))
            holder.cbSelectLearnWordsRA.setChecked(true);

        // получение данных
        //фильтрация элементов для бинарного поиска
        if((filter.equals(""))||(translations.contains(filter))
                ||(word.contains(filter))){

            //устанавливаем данные в текстовые поля адаптера
            holder.tvWordSLWRA.setText(word);
            holder.tvTranslationsSLWRA.setText(translations);
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
    class ViewHolder extends RecyclerView.ViewHolder {
        final TextView tvWordSLWRA, tvTranslationsSLWRA;
        final CardView cvMainSLWRA;
        final CheckBox cbSelectLearnWordsRA;

        ViewHolder(final View view) {
            super(view);
            cbSelectLearnWordsRA = view.findViewById(R.id.cbSelectLearnWordsRA);
            cvMainSLWRA = view.findViewById(R.id.cvMainSLWRA);
            tvWordSLWRA = view.findViewById(R.id.tvWordSLWRA);
            tvTranslationsSLWRA = view.findViewById(R.id.tvTranslationsSLWRA);

            cbSelectLearnWordsRA.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // переходим в курсоре на текущую позицию
                    cursor.moveToPosition(getAdapterPosition());
                    //получаем данные из курсора для фильтрации
                    if(cbSelectLearnWordsRA.isChecked())
                        listCursorNum.add(String.valueOf(getAdapterPosition()));
                    else listCursorNum.remove(String.valueOf(getAdapterPosition()));
                }//onClick
            });//setOnClickListener
        } // ViewHolder
    }//class ViewHolder

}//SelectLearnWordsRecyclerAdapter
