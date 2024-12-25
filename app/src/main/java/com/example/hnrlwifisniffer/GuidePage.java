package com.example.hnrlwifisniffer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

public class GuidePage extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_guide_page);

    //去除下方虚拟按键
    View decorView = getWindow().getDecorView();
    int uiOptions =
      View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN;
    decorView.setSystemUiVisibility(uiOptions);
    //去除标题栏
    if (getSupportActionBar() != null) {
      getSupportActionBar().hide();
    }
    init();
  }

  public void init() {
    cdown();
  }

  public void cdown() {
    new Thread() {
      public void run() {
        try {
          sleep(2300);
          jump();
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }
      .start();
  }

  public void jump() {
    startActivity(new Intent(this, MainActivity.class));
  }
}
