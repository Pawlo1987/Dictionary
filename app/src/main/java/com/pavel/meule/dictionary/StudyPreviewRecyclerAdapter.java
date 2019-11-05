package com.pavel.meule.dictionary;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

//recyclerAdapter для предпросмотра из фрагмента StudyPreviewFragment
public class StudyPreviewRecyclerAdapter extends
        RecyclerView.Adapter<StudyPreviewRecyclerAdapter.ViewHolder>{

    //поля класса StudyPreviewRecyclerAdapter
    private LayoutInflater inflater;
    Context context;

    List<String> listCursorNumFromActivity; // коллекция слов для изучения полученая из Activity
    private Cursor cursor;
    DBUtilities dbUtilities;
    private String translation, word, transcription;

    //конструктор
    public StudyPreviewRecyclerAdapter(Context context, List<String> listCursorNumFromActivity) {
        this.inflater = LayoutInflater.from(context);
        //получение интерфеса из класса Фрагмента
        //для обработки нажатия элементов RecyclerAdapter
        this.context = context;
        dbUtilities = new DBUtilities(context);
        dbUtilities.open();
        this.listCursorNumFromActivity = listCursorNumFromActivity;
        cursor = dbUtilities.getDb().rawQuery(dbUtilities.mainQuery, null);
    } // StudyPreviewRecyclerAdapter

    //создаем новую разметку(View) путем указания разметки
    @Override
    public StudyPreviewRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.recycler_adapter_study_preview, parent, false);
        return new StudyPreviewRecyclerAdapter.ViewHolder(view);
    }

    //привязываем элементы разметки к переменным объекта(в данном случае к курсору)
    @Override
    public void onBindViewHolder(StudyPreviewRecyclerAdapter.ViewHolder holder, final int position) {
        // переходим в курсоре на текущую позицию
        // следующая позиция берется из коллекции listCursorNumFromActivity
        cursor.moveToPosition(Integer.parseInt(listCursorNumFromActivity.get(position)));
        Log.d("myvalue",listCursorNumFromActivity.get(position).toString());
        //получаем перевод
        String queryTr = "SELECT russian.word_ru FROM translations " +
                "INNER JOIN russian ON russian.id = translations.russian_id " +
                "WHERE translations.hebrew_id = \"" + cursor.getString(0) + "\"";
        Cursor cursorTr = dbUtilities.getDb().rawQuery(queryTr, null);
        cursorTr.moveToFirst();

        //получаем данные из курсора для фильтрации
        translation = cursorTr.getString(0);     //слово на русском
        word = cursor.getString(1);     //слово на иврите
        transcription = cursor.getString(2);     //транскрипция слова на иврите

            //устанавливаем данные в текстовые поля адаптера
            holder.tvWordStPrRA.setText(word);
            holder.tvTranscriptionStPrRA.setText(transcription);
            holder.tvTranslationStPrRA.setText(translation);
    } // onBindViewHolder

    //получаем количество элементов объекта(курсора)
    @Override
    public int getItemCount() { return listCursorNumFromActivity.size(); }

    //Создаем класс ViewHolder с помощью которого мы получаем ссылку на каждый элемент
    //отдельного пункта списка и подключаем слушателя события нажатия меню
    class ViewHolder extends RecyclerView.ViewHolder {
        final TextView tvWordStPrRA, tvTranscriptionStPrRA, tvTranslationStPrRA;
        final CardView cvMainStPrRA;

        ViewHolder(final View view) {
            super(view);
            cvMainStPrRA = view.findViewById(R.id.cvMainStPrRA);
            tvWordStPrRA = view.findViewById(R.id.tvWordStPrRA);
            tvTranscriptionStPrRA = view.findViewById(R.id.tvTranscriptionStPrRA);
            tvTranslationStPrRA = view.findViewById(R.id.tvTranslationStPrRA);
        } // ViewHolder
    }//class ViewHolder

}//StudyPreviewRecyclerAdapter
