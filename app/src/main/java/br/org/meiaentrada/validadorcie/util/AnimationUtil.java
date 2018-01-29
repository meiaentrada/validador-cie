package br.org.meiaentrada.validadorcie.util;

import android.view.View;
import android.view.animation.Animation;

import java.util.List;

public class AnimationUtil {

    public static void addAnimation(List<View> containers, Animation animation){
        for (View container: containers) {
            container.startAnimation(animation);
        }
    }

}
