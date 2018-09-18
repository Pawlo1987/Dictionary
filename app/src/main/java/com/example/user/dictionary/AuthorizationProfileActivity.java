package com.example.user.dictionary;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

//активность авторизации профиля
public class AuthorizationProfileActivity extends AppCompatActivity {
    EditText etLoginAPAc;
    EditText etPasswordAPAc;
    Context context;
    DBUtilities dbUtilities;
    ActionBar actionBar;                //стрелка НАЗАД

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authorization_profile);

        //добавляем actionBar (стрелка сверху слева)
        actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        context = getBaseContext();
        dbUtilities = new DBUtilities(context);
        dbUtilities.open();

        etLoginAPAc = findViewById(R.id.etLoginAPAc);
        etPasswordAPAc = findViewById(R.id.etPasswordAPAc);
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
            case R.id.btnOkSignInAPAc:
                authorizationProfile();
                break;

        }//switch
    }//onClick

    private void authorizationProfile() {
        String login = etLoginAPAc.getText().toString().trim();
        String password = etPasswordAPAc.getText().toString().trim();
        //проверка пустых строк
        if(login.equals("")||password.equals("")){
            Toast.makeText(context, "Empty lines!", Toast.LENGTH_SHORT).show();
        }else {
            //получаем id первого элемента в БД
            String query = "SELECT profile.password, profile.id FROM profile " +
                    "WHERE profile.login = \"" + login + "\"";
            Cursor cursor = dbUtilities.getDb().rawQuery(query, null);
            if (cursor.getCount() == 0)
                Toast.makeText(context, "Wrong login!", Toast.LENGTH_SHORT).show();
            else {
                cursor.moveToPosition(0);
                if(password.equals(cursor.getString(0))){
                    Intent intent = new Intent(this, ProfileParametersActivity.class);
                    intent.putExtra(
                            "idProfile",
                            cursor.getInt(1)
                    );
                    startActivity(intent);
                }else Toast.makeText(context, "Wrong password!", Toast.LENGTH_SHORT).show();
            }
        }//if(login.equals("")||password.equals(""))
    }//createProfile
}//AuthorizationProfileActivity
