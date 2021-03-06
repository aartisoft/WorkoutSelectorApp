package com.liversedge.workoutselector.frontend.components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.liversedge.workoutselector.R;
import com.liversedge.workoutselector.backend.firebase.WorkoutDAO;

import java.util.ArrayList;

public class HorizontalWorkoutScrollerView extends FrameLayout {

    private static int ANIMATION_DUR = 120;
    private int NUMBER_OF_VIEWS = 0;
    private WorkoutView current_view, next_view;
    private WorkoutDAO PLACEHOLDER;

    private int last_result=0, old_value=0;

    private IEventEnd eventEnd;

    private ArrayList<WorkoutDAO> workouts;

    public HorizontalWorkoutScrollerView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public HorizontalWorkoutScrollerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public void setEventEnd(IEventEnd eventEnd) {
        this.eventEnd = eventEnd;
    }

    public void setListOfExercises(ArrayList<WorkoutDAO> workouts) {
        this.workouts = workouts;
        this.NUMBER_OF_VIEWS = workouts.size();
    }

    private void init(Context context) {
        PLACEHOLDER = new WorkoutDAO(
                "Spin to find a workout!",
                "",
                "",
                null,
                null,
                0,
                null
        );
        LayoutInflater.from(context).inflate(R.layout.horizontal_workout_scroller, this);
        current_view = (WorkoutView) getRootView().findViewById(R.id.current_view);
        next_view = (WorkoutView) getRootView().findViewById(R.id.next_view);

        current_view.update(PLACEHOLDER);
        next_view.update(PLACEHOLDER);

        next_view.setTranslationX(getWidth());
    }

    public void setValueRandom(final int image, final int rotate_count) {

        current_view.setVisibility(View.VISIBLE);
        current_view.animate().translationX(-current_view.getWidth()).setDuration(ANIMATION_DUR).start();
        next_view.setTranslationX(current_view.getWidth());
        next_view.animate()
                .translationX(0)
                .setDuration(ANIMATION_DUR)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {

                        super.onAnimationEnd(animation);
                        setView(current_view, NUMBER_OF_VIEWS<=1 ? 0 : old_value%(NUMBER_OF_VIEWS-1));
                        current_view.setTranslationX(0);

                        if(old_value != rotate_count) {
                            setValueRandom(image, rotate_count);
                            old_value ++;
                        } else {
                            last_result = 0;
                            old_value = 0;
                            current_view.setVisibility(View.GONE);
                            setView(next_view, image);
                            eventEnd.eventEnd(image, rotate_count);
                        }
                    }
                });
    }

    private void setView(WorkoutView view, int value) {
        view.update(workouts.get(value));

        view.setTag(value);
        last_result = value;
    }

    public int getValue() {
        return Integer.parseInt(next_view.getTag().toString());
    }

    public int getNumberOfWorkouts() {
        return NUMBER_OF_VIEWS;
    }
}
