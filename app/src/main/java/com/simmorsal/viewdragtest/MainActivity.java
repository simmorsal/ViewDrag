package com.simmorsal.viewdragtest;

import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity {

    FrameLayout rootView;
    LinearLayout linDraggable;
    View viewTouchListener;
    ImageView imgDarkOverlay;
    Button btnShowLayout;

    int linHeight;
    int startX, startY;
    int deltaX, deltaY;
    int animationDuration = 400;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializer();
        getHeight();
        onClicks();
    }

    private void initializer() {
        linDraggable = findViewById(R.id.linDraggable);
        viewTouchListener = findViewById(R.id.viewTouchListener);
        imgDarkOverlay = findViewById(R.id.imgDarkOverlay);
        rootView = findViewById(R.id.rootView);
        btnShowLayout = findViewById(R.id.btnShowLayout);
    }

    private void getHeight() {
        // first we get the height of the LinearLayout (or any other view) that we want to move
        // with the code below
        linDraggable.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                linDraggable.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                linHeight = linDraggable.getHeight();

                // then we start listening for clicks and drags on the screen
                listenForDrag();
            }
        });
    }

    private void listenForDrag() {

        // we must have a stationary View to listen for clicks, otherwise
        // the data we'll get wont be correct
        viewTouchListener.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){

                    // this case is run when the user puts finger on the screen, and this is where
                    // we get our start X and Y values
                    case MotionEvent.ACTION_DOWN:
                        startX = (int) event.getX();
                        startY = (int) event.getY();
                        break;

                    // this case runs when the user moves finger on the screen, and this is where
                    // we update the location of the draggableLinearLayout position, and apply
                    // any other updates to the elements on UI we might want to have
                    case MotionEvent.ACTION_MOVE:
                        performCalculations(event);
                        break;

                    // this case is run when the user lifts finger off the screen, and this is where
                    // we check how the elements on the UI should lay out, based on the amount of
                    // drag on the screen
                    case MotionEvent.ACTION_UP:
                        checkWhatShouldHappen();
                }
                return true;
            }
        });
    }

    private void performCalculations(MotionEvent event){

        // calculating how many pixels has the finger been dragged on the screen
        deltaX = (int) (startX - event.getX());
        deltaY = (int) (startY - event.getY());

        // factors below give us an indication of "From zero to one, how much the layout has been moved"
        // and vice versa  -- "factorFromZero" is not used here for now.
        float factorFromZero = (float)(deltaY) / linHeight;
        float factorFromOne = (float)(linHeight - deltaY) / linHeight;

        // in this project, we want to make sure that the layout will only go upwards, so we move
        // the draggableLayout only if deltaY is bigger or equal to 0
        if (deltaY >= 0) {

            // this line sets the appropriate Y translation to the layout we want to move.
            // in this line, setting the Y translation to "-deltaY" would work perfectly,
            // but i've multiplied the deltaY value to 4/5 to basically decrease the value
            // and make it feel and move a little heavier
            linDraggable.setTranslationY(-(int)(deltaY * ((float)4/5)));

            // imgDarkOverlay is an ImageView with a dark background value which resides
            // behind our draggableLinearLayout
            // this line sets an appropriate alpha value to it based on the amount the layout has been moved
            imgDarkOverlay.setAlpha(factorFromOne);
        }
    }

    private void checkWhatShouldHappen() {

        // here we check that if the amount of drag on the screen is more than two thirds of the
        // linDraggable's height ...
        if (deltaY > linHeight / 3){

            // ... we will animate the linDraggable to completely exit the screen, and ...
            linDraggable.animate().translationY(-linHeight).setDuration(animationDuration).withLayer();

            // ... we will animate the imgDarkOverlay's alpha to zero, and by using ".withEndAction" ...

            imgDarkOverlay.animate().alpha(0).setDuration(animationDuration).withEndAction(new Runnable() {
                @Override
                public void run() {

                    // ... we will make any necessary actions we might have to make, after all the animations
                    // are over
                    runFinishingCode();
                }
            });
        }

        // and if the amount of drag on the screen is not sufficient, we will make everything
        // go back to normal
        else {
            linDraggable.animate().translationY(0).setDuration(animationDuration).withLayer();
            imgDarkOverlay.animate().alpha(1).setDuration(animationDuration).withLayer();
        }
    }

    private void runFinishingCode() {
        // we will have to set the visibility of viewTouchListener to GONE, to make the views below
        // it clickable. and we will preferably set the visibility of linDraggable and imgDarkOverlay
        // to GONE too for a possibly prevent any performance hits taken from them.
        viewTouchListener.setVisibility(View.GONE);
        linDraggable.setVisibility(View.GONE);
        imgDarkOverlay.setVisibility(View.GONE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.parseColor("#000000"));
        }
    }

    private void onClicks() {
        // optional button to make the linDraggable reappear.
        btnShowLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                viewTouchListener.setVisibility(View.VISIBLE);
                linDraggable.setVisibility(View.VISIBLE);
                imgDarkOverlay.setVisibility(View.VISIBLE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    getWindow().setStatusBarColor(Color.parseColor("#E91E63"));
                }

                linDraggable.animate().translationY(0).setDuration(animationDuration).withLayer();
                imgDarkOverlay.animate().alpha(1).setDuration(animationDuration).withLayer();
            }
        });
    }
}
