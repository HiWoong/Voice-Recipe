package com.penelope.acousticrecipe.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.os.Bundle;

import com.penelope.acousticrecipe.R;
import com.penelope.acousticrecipe.databinding.ActivityMainBinding;
import com.penelope.acousticrecipe.services.TimerService;
import com.penelope.acousticrecipe.ui.home.HomeFragment;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity implements HomeFragment.HomeFragmentListener {

    private ActivityMainBinding binding;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 뷰 바인딩을 실행한다
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 액션바 타이틀을 숨긴다
        setSupportActionBar(binding.toolBar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        // 네비게이션 호스트 프래그먼트로부터 네비게이션 컨트롤러를 획득한다
        NavHostFragment navHostFragment =
                (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);

        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
            // 액션바를 네비게이션 컨트롤러와 연동한다
            NavigationUI.setupActionBarWithNavController(this, navController);
            // 바텀 네비게이션 뷰를 네비게이션 컨트롤러와 연동한다
            NavigationUI.setupWithNavController(binding.bottomNav, navController);
        }

    }

    @Override
    protected void onDestroy() {

        Intent intent = new Intent(this, TimerService.class);
        stopService(intent);

        super.onDestroy();
    }

    @Override
    public boolean onSupportNavigateUp() {
        // 네비게이션 컨트롤러에 뒤로가기 버튼 연동
        return navController.navigateUp() || super.onSupportNavigateUp();
    }

    @Override
    public void onSearchClick() {
        binding.bottomNav.setSelectedItemId(R.id.searchFragment);
    }

}