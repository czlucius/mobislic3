package dev.czlucius.mobislic3;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.animation.ObjectAnimator;
import java.util.Random;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import dev.czlucius.mobislic3.databinding.FragmentGameBinding;

public class GameFragment extends Fragment {

    FragmentGameBinding binding;

    static int nomore = 0;
    static int trigger1 = 0;
    static int trigger2 = 0;
    static int trigger3 = 0;
    static int scorekeeper = 0;
    static int lane = 2;
    static int nemy1 = 0;
    static int nemy2 = 0;
    static int nemy3 = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        binding = FragmentGameBinding.inflate(inflater, container, false);

        return binding.getRoot();


    }

    @Override
    public void onViewCreated(@NonNull View root, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(root, savedInstanceState);
        ImageView protag = binding.protag;
        ImageView enemy1 = binding.enemy1;
        ImageView enemy2 = binding.enemy2;
        ImageView enemy3 = binding.enemy3;
        ImageButton up = binding.up;
        ImageButton down = binding.down;
        ImageButton fire = binding.fire;


        trigger1 = new Random().nextInt(61) + 20; // [0, 60] + 20 => [20, 80];
        if (trigger1 >= 40 && nomore == 0) {
            enemy1.setAlpha(1f);
            ObjectAnimator animation = ObjectAnimator.ofFloat(enemy1, "translationX", -1300f);
            animation.setDuration(10000);
            animation.start();
            nomore = 1;
            nemy1 = 1;


        }
        trigger2 = new Random().nextInt(61) + 20; // [0, 60] + 20 => [20, 80];
        if (trigger2 >= 40 && nomore == 0) {
            enemy2.setAlpha(1f);
            ObjectAnimator animation2 = ObjectAnimator.ofFloat(enemy2, "translationX", -1300f);
            animation2.setDuration(10000);
            animation2.start();
            nomore = 1;
            nemy2 = 1;


        }
        trigger3 = new Random().nextInt(61) + 20; // [0, 60] + 20 => [20, 80];
        if (trigger3 >= 40 && nomore == 0) {
            enemy3.setAlpha(1f);
            ObjectAnimator animation3 = ObjectAnimator.ofFloat(enemy3, "translationX", -1300f);
            animation3.setDuration(10000);
            animation3.start();
            nomore = 1;
            nemy3 = 1;


        }
        up.setOnClickListener(view -> {
            if (lane > 1) {


                ObjectAnimator animation = ObjectAnimator.ofFloat(protag, "translationY", -200f);
                animation.setDuration(1000);
                animation.start();
                lane--;
            }

        });
        down.setOnClickListener(view -> {
            if (lane < 3) {
                ObjectAnimator animation = ObjectAnimator.ofFloat(protag, "translationY", 200f);
                animation.setDuration(1000);
                animation.start();
                lane++;
            }

        });
        fire.setOnClickListener(view -> {
            if (lane == nemy1) {
                nemy1 = 0;
                scorekeeper++;
                enemy1.setAlpha(0f);
                ObjectAnimator animation = ObjectAnimator.ofFloat(enemy1, "translationX", 1300f);
                animation.setDuration(1000);
                animation.start();
                trigger3 = new Random().nextInt(61) + 20; // [0, 60] + 20 => [20, 80];
                trigger2 = new Random().nextInt(61) + 20; // [0, 60] + 20 => [20, 80];

            }
            if (lane == nemy2) {
                nemy2 = 0;
                scorekeeper++;
                enemy2.setAlpha(0f);
                ObjectAnimator animation2 = ObjectAnimator.ofFloat(enemy2, "translationX", 1300f);
                animation2.setDuration(1000);
                animation2.start();
                trigger1 = new Random().nextInt(61) + 20; // [0, 60] + 20 => [20, 80];
                trigger3 = new Random().nextInt(61) + 20; // [0, 60] + 20 => [20, 80];
            }
            if (lane == nemy3) {
                nemy3 = 0;
                scorekeeper++;
                enemy3.setAlpha(0f);
                ObjectAnimator animation3 = ObjectAnimator.ofFloat(enemy3, "translationX", 1300f);
                animation3.setDuration(1000);
                animation3.start();
                trigger1 = new Random().nextInt(61) + 20; // [0, 60] + 20 => [20, 80];
                trigger2 = new Random().nextInt(61) + 20; // [0, 60] + 20 => [20, 80];

            }

        });

    }
}