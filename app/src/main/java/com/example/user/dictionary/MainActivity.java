package com.example.user.dictionary;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnAddNewWord:
                addNewWord();
                break;

            case R.id.btnViewDictionary:
                viewDictionary();
                break;

            case R.id.btnLearnWords:
                learnWords();
                break;

        }//switch

    }//onClick

    private void learnWords() {
        Intent intent = new Intent(this, LearnWordActivity.class);
        startActivity(intent);
    }

    private void addNewWord() {
        Intent intent = new Intent(this, AddNewWordActivity.class);
        startActivity(intent);
    }

    private void viewDictionary() {
        Intent intent = new Intent(this, ViewDictionaryActivity.class);
        startActivity(intent);
    }

} //MainActivity
