package com.example.user.dictionary;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

//Активность для создания профиля
public class CreateProfileActivity extends AppCompatActivity {
    EditText edCreateLoginProfileCPAc;
    EditText edCreatePasswordProfileCPAc;
    Context context;
    DBUtilities dbUtilities;
    ActionBar actionBar;                //стрелка НАЗАД

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);

        //добавляем actionBar (стрелка сверху слева)
        actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        context = getBaseContext();
        dbUtilities = new DBUtilities(context);
        dbUtilities.open();

        edCreateLoginProfileCPAc = findViewById(R.id.edCreateLoginProfileCPAc);
        edCreatePasswordProfileCPAc = findViewById(R.id.edCreatePasswordProfileCPAc);
    }//onCreate

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id){
            //обработчик actionBar (стрелка сверху слева)
            case android.R.id.home:
                setResult(RESULT_CANCELED);
                onBackPressed();
                return true;
        }//switch
        return super.onOptionsItemSelected(item);
    }//onOptionsItemSelected

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnOkCreateCPAc:
                createProfile();
                break;

        }//switch
    }//onClick

    private void createProfile() {
        String login = edCreateLoginProfileCPAc.getText().toString().trim();
        String password = edCreatePasswordProfileCPAc.getText().toString().trim();
        //проверка пустых строк
        if(login.equals("")||password.equals("")){
            Toast.makeText(context, "Empty lines!", Toast.LENGTH_SHORT).show();
        }else {
            //получаем id первого элемента в БД
            String query = "SELECT hebrew.id FROM hebrew";
            Cursor cursor = dbUtilities.getDb().rawQuery(query, null);
            cursor.moveToPosition(0);
            //записываем новую строку в таблицу profile
            dbUtilities.insertIntoProfile(login, password, cursor.getInt(0),
                    10, 10);
            Toast.makeText(context, "New profile added!", Toast.LENGTH_SHORT).show();
            finish();
        }//if(login.equals("")||password.equals(""))
    }//createProfile
}//CreateProfileActivity
