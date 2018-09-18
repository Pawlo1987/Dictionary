package com.example.user.dictionary;

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

//стартовая анимацияч
public class SplashActivity extends AppCompatActivity {
    CountDownTimer countDownTimer;
    RelativeLayout RlMainSpAc;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        startMainActivity();



//        RlMainSpAc = (RelativeLayout)findViewById(R.id.RlMainSpAc);
//        RlMainSpAc.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                countDownTimer.cancel();
//                startMainActivity();
//            }
//        });
//        //Создаем таймер обратного отсчета на 10 секунд с шагом отсчета
//        //в 1 секунду (задаем значения в миллисекундах):
//        countDownTimer = new CountDownTimer(3000, 1000) {
//
//            //Здесь можно выполнить какието дейстивия через кажду секунду
//            //до конца счета таймера
//            public void onTick(long millisUntilFinished) { }
//            //Задаем действия после завершения отсчета (запускаем главную активность)
//            public void onFinish(){ startMainActivity(); }
//        };
//        countDownTimer.start();
    }//onCreate

    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        //флаг отчистки стека активностей
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }//startMainActivity
}//SplashActivity
