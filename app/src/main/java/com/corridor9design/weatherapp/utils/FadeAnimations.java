package com.corridor9design.weatherapp.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.view.View;

public class FadeAnimations {

    // Fade-in animation
    public static void fadeIn(View viewToFadeIn, int fadeTime) {
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(viewToFadeIn, "alpha", 0f, 1f);
        fadeIn.setDuration(fadeTime);
        fadeIn.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                viewToFadeIn.setVisibility(View.VISIBLE);
                viewToFadeIn.setAlpha(0);
            }
        });
        fadeIn.start();
    }

    // Fade-out animation
    public static void fadeOut(View viewToFadeOut, int fadeTime) {
        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(viewToFadeOut, "alpha", 1f, 0f);
        fadeOut.setDuration(fadeTime);
        fadeOut.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                // After fading out, set the view to GONE or INVISIBLE
                viewToFadeOut.setVisibility(View.GONE); // Change to INVISIBLE if needed
            }
        });
        fadeOut.start();
    }
}
