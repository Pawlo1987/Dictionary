package com.example.user.dictionary;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.List;

public class SelectNextLearnWordRecyclerAdapter extends
        RecyclerView.Adapter<SelectNextLearnWordRecyclerAdapter.ViewHolder>{

    //поля класса SelectLearnWordsRecyclerAdapter
    private LayoutInflater inflater;
    Context context;
    private RadioButton lastCheckedRB = null;
    private int selectIdHebrew;

    private Cursor cursor;
    DBUtilities dbUtilities;
    private String filter;
    private String translations, word;
    private List<String> listCursorNum;

    //конструктор
    SelectNextLearnWordRecyclerAdapter(Context context, String mainQuery,
                                       String filter, List<String> listCursorNum, int selectIdHebrew) {
        this.inflater = LayoutInflater.from(context);
        //получение интерфеса из класса Фрагмента
        //для обработки нажатия элементов RecyclerAdapter
        this.context = context;
        this.filter = filter;
        this.listCursorNum = listCursorNum;
        this.selectIdHebrew = selectIdHebrew;
        dbUtilities = new DBUtilities(context);
        dbUtilities.open();
        cursor = dbUtilities.getDb().rawQuery(mainQuery, null);
    } // ViewDictionaryRecyclerAdapter

    //создаем новую разметку(View) путем указания разметки
    @Override
    public SelectNextLearnWordRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.recycler_adapter_select_next_learn_word, parent, false);
        return new SelectNextLearnWordRecyclerAdapter.ViewHolder(view);
    }

    //привязываем элементы разметки к переменным объекта(в данном случае к курсору)
    @Override
    public void onBindViewHolder(SelectNextLearnWordRecyclerAdapter.ViewHolder holder, final int position) {
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
        final RadioGroup rgSelectNextLearnWordRA;

        ViewHolder(final View view) {
            super(view);
            rgSelectNextLearnWordRA = view.findViewById(R.id.rgSelectNextLearnWordRA);
            cvMainSLWRA = view.findViewById(R.id.cvMainSLWRA);
            tvWordSLWRA = view.findViewById(R.id.tvWordSLWRA);
            tvTranslationsSLWRA = view.findViewById(R.id.tvTranslationsSLWRA);

            //обработка нажатой RadioButton
            rgSelectNextLearnWordRA.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    RadioButton rbSelectNextLearnWordRA = (RadioButton) group.findViewById(checkedId);
                    if (lastCheckedRB != null) {
                        lastCheckedRB.setChecked(false);
                    }
                    //store the clicked radiobutton
                    lastCheckedRB = rbSelectNextLearnWordRA;
                    selectIdHebrew = getAdapterPosition();

                }//onCheckedChanged
            });
        } // ViewHolder
    }//class ViewHolder

}//SelectLearnWordsRecyclerAdapter
