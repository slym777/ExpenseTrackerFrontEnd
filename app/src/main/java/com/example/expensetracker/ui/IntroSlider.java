package com.example.expensetracker.ui;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.example.expensetracker.MainActivity;
import com.example.expensetracker.R;
import com.example.expensetracker.utils.SharedPreferencesUtils;
import com.github.paolorotolo.appintro.AppIntro2;
import com.github.paolorotolo.appintro.AppIntroFragment;
import com.github.paolorotolo.appintro.model.SliderPage;
import com.github.paolorotolo.appintro.model.SliderPagerBuilder;

;

public class IntroSlider extends AppIntro2 {

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferencesUtils.setFirstAccess();
        showIntroSlides();

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void showIntroSlides(){

        SliderPage pageOne = new SliderPagerBuilder()
                .title(getString(R.string.intro_page1_title))
                .description(getString(R.string.intro_page1_description))
//                .imageDrawable(R.drawable.maxresdefault)
//                .bgColor(getColor(R.color.darkgray))
                .build();

        SliderPage pageTwo = new SliderPagerBuilder()
                .title(getString(R.string.intro_page2_title))
                .description(getString(R.string.intro_page2_description))
//                .imageDrawable(R.drawable.group)
//                .bgColor(getColor(R.color.myGreen))
                .build();

        SliderPage pageThree = new SliderPagerBuilder()
                .title(getString(R.string.intro_page3_title))
                .description(getString(R.string.intro_page3_description))
//                .imageDrawable(R.drawable.debt)
//                .bgColor(getColor(R.color.myOrange))
                .build();

        SliderPage pageFour = new SliderPagerBuilder()
                .title(getString(R.string.intro_page4_title))
                .description(getString(R.string.intro_page4_description))
//                .imageDrawable(R.drawable.simplification)
//                .bgColor(getColor(R.color.intro_page4))
                .build();

        addSlide(AppIntroFragment.newInstance(pageOne));
        addSlide(AppIntroFragment.newInstance(pageTwo));
        addSlide(AppIntroFragment.newInstance(pageThree));
        addSlide(AppIntroFragment.newInstance(pageFour));
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        goToMain();
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        goToMain();
    }

    public void goToMain(){
        startActivity(new Intent(this, MainActivity.class));
    }
}

