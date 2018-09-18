package com.example.user.dictionary.Fragments;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.example.user.dictionary.DBUtilities;
import com.example.user.dictionary.FileUtilities;
import com.example.user.dictionary.R;
import com.example.user.dictionary.StudyPreviewRecyclerAdapter;
import com.example.user.dictionary.Word;

import java.util.ArrayList;
import java.util.List;

//фрагмент для предпросмотра изучаемых слов.
public class StudyPreviewFragment extends Fragment {
    Context context;
    DBUtilities dbUtilities;
    com.example.user.dictionary.FileUtilities FileUtilities;
    List<Word> listWords; // коллекция слов для изучения

    int wordsCount = 10;
    RecyclerView rvWordsStPrFr;
    // адаптер для отображения recyclerView
    StudyPreviewRecyclerAdapter studyPreviewRecyclerAdapter;
    List<String> listCursorNumFromActivity; // коллекция слов для изучения полученая из Activity
    Button btnGoStPrFr;
    Button btnActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbUtilities.open();
        listWords  = new ArrayList<>();
        listCursorNumFromActivity  = new ArrayList<>();
        listCursorNumFromActivity.addAll(getArguments().getStringArrayList("idList"));
        wordsCount = getArguments().getInt("wordsCount",0);
    }//onCreate

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View resultView = inflater.inflate(R.layout.fragment_study_preview,  container, false);

        // RecyclerView для отображения таблицы users БД
        rvWordsStPrFr = resultView.findViewById(R.id.rvWordsStPrFr);
        btnGoStPrFr = resultView.findViewById(R.id.btnGoStPrFr);
        //убираем ProgressBar он здесь не нужен
        getActivity().findViewById(R.id.pbBaMeAc).setVisibility(View.GONE);
        btnActivity = getActivity().findViewById(R.id.btnActivity);

        buildRecyclerView();

        //обработчик кнопок btnGoStPrFr
        btnGoStPrFr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnActivity.callOnClick();  //возвращение в активность BackgroundMethodActivity
            }//onClick
        });//setOnClickListener
        return resultView;
    }//onCreateView

    //Строим RecyclerView
    private void buildRecyclerView() {
        // создаем адаптер, передаем в него курсор
        studyPreviewRecyclerAdapter = new StudyPreviewRecyclerAdapter(context, listCursorNumFromActivity);
        //привязываем адаптер к recycler объекту
        rvWordsStPrFr.setAdapter(studyPreviewRecyclerAdapter);
    }//buildUserRecyclerView

    // Метод onAttach() вызывается в начале жизненного цикла фрагмента, и именно здесь
    // мы можем получить контекст фрагмента, в качестве которого выступает класс MainActivity.
    //onAttach(Context) не вызовется до API 23 версии вместо этого будет вызван onAttach(Activity),
    //коий устарел с 23 API
    //Так что вызовем onAttachToContext
    //https://ru.stackoverflow.com/questions/507008/%D0%9D%D0%B5-%D1%80%D0%B0%D0%B1%D0%BE%D1%82%D0%B0%D0%B5%D1%82-onattach
    @TargetApi(23)
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onAttachToContext(context);
    }//onAttach

    //устарел с 23 API
    //Так что вызовем onAttachToContext
    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            onAttachToContext(activity);
        }//if
    }//onAttach

    //Вызовется в момент присоединения фрагмента к активити
    protected void onAttachToContext(Context context) {
        //здесь всегда есть контекст и метод всегда вызовется.
        //тут можно кастовать контест к активити.
        //но лучше к реализуемому ею интерфейсу
        //чтоб не проверять из какого пакета активити в каждом из случаев
        this.context = context;
        FileUtilities = new FileUtilities(context);
        dbUtilities = new DBUtilities(context);
    }//onAttachToContext

    @Override
    public void onStop() {
        super.onStop();
    }//onStop

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }//onDestroyView

    @Override
    public void onDestroy() {
        super.onDestroy();
    }//onDestroyView

    @Override
    public void onDetach() {
        super.onDetach();
    }//onDetach
}
