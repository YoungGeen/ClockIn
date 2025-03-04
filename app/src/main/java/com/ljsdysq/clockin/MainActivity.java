package com.ljsdysq.clockin;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.ljsdysq.clockin.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        showInfo();
    }

    private void showInfo() {
        binding.usage.setText(R.string.usage);
        binding.state.setText(isActivate());
    }

    private String isActivate() {
        return "未激活";
    }

}