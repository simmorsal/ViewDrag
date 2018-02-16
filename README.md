# View Drag Test

A tutorial on how to move a view on UI by dragging it with your finger.

![drag](https://user-images.githubusercontent.com/24822099/33432273-97733820-d5ec-11e7-82f2-653d44e190e4.gif)

Demo APK: [ViewDrag.zip](https://github.com/SIMMORSAL/ViewDrag/files/1517887/ViewDrag.zip)



## Introduction

Welcome. In this tutorial I will teach you how to create a sliding layout, so it would move out of UI when you swipe it upward, and the layout behind it will come out of darkness.

All we'll cover here will be creating the GIF you see, but once you learn how, you will be able to create some neat and awesome stuff with the knowledge.


## Before we code

Start a new project, head to 'res/values/styles'

* change
`<style name="AppTheme" parent="Theme.AppCompat.Light.DarkActionBar">`
to
`<style name="AppTheme" parent="Theme.AppCompat.Light.NoActionBar">`
so the Toolbar would hide.

* change `<item name="colorPrimaryDark">@color/colorPrimaryDark</item>` to
`<item name="colorPrimaryDark">#E91E63</item>` to change the color
of the status bar.

## Lets get to it...

We'll start by creating the layout, then move to java.



### 1. Creating the layout

This is a semi-collapsed look at our layout.
```XML
<FrameLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent">


    <!-- =================== The views behind the draggable layout -->
    <RelativeLayout
        android:layout_width="match_parent"
        android:layout_height="match_parent".../>
    <!-- The views behind the draggable layout =================== -->


    <!-- ========================== the dragging and related views -->
    <ImageView
        android:id="@+id/imgDarkOverlay"
        android:layout_width="match_parent"
        android:layout_height="match_parent".../>

    <LinearLayout
        android:id="@+id/linDraggable"
        android:layout_width="match_parent"
        android:layout_height="match_parent".../>

    <View
        android:id="@+id/viewTouchListener"
        android:layout_width="match_parent"
        android:layout_height="match_parent".../>
    <!-- the dragging and related views ========================== -->

</FrameLayout>
```

Our layout has 4 main Views inside it, which are divided into two groups.

The first group includes a `RelativeLayout`, which holds all the views behind our draggableLayout.
Inside it there is a GIF and a Button.

The second group includes an `ImageView`, a `LinearLayout`, and a `View`.
The `LinearLayout` named `linDraggable` is the layout that will be dragged.
The `View` named `viewTouchListener` is a transparent but clickable View, that is responsible for registering the user's clicks.
The `ImageView` named `imgDarkOverlay` with a semi-transparent black background, is the darkness behind the `linDraggable`!

[Click here to see the full layout code](https://gist.github.com/SIMMORSAL/1e371d9500f2fbddb6d31f4b39ad9b03)






### 2. Writing the Java code

Before we start with writing our java code I should
explain that for the sake of keeping our code clean, we
create multiple methods in our `MainActivity` class, each
responsible for something, and we call them from the `onCreate()`
method. For example we initialize our UI components like this:

```java
    LinearLayout linDraggable;
    View viewTouchListener;
    ImageView imgDarkOverlay;
    Button btnShowLayout;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializer();
    }
    
    private void initializer() {
        linDraggable = findViewById(R.id.linDraggable);
        viewTouchListener = findViewById(R.id.viewTouchListener);
        imgDarkOverlay = findViewById(R.id.imgDarkOverlay);
        btnShowLayout = findViewById(R.id.btnShowLayout);
    }
```


Then we add these variables just above the `onCreate()` method:


```java
    int linHeight;
    int startX, startY;
    int deltaX, deltaY;
    int animationDuration = 400;
```

Now we create another method in our class named `getHeight` and call it from
`onCreate()` method:

```java
    private void getHeight() {
        linDraggable.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                linDraggable.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                linHeight = linDraggable.getHeight();

                listenForDrag();
            }
        });
    }
```

In this method we wait for the `linDraggable` to get drawn on the UI so we can get
it's height value and store it in `linHeight`.

After we get the height, we start listening for touches on the UI through
`listenForDrag()` method:

```java
    private void listenForDrag() {

        // we must have a stationary View for listening to clicks, otherwise
        // the data we'll get wont be correct
        viewTouchListener.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:
                        startX = (int) event.getX();
                        startY = (int) event.getY();
                        break;

                    case MotionEvent.ACTION_MOVE:
                        performCalculations(event);
                        break;

                    case MotionEvent.ACTION_UP:
                        checkWhatShouldHappen();
                }

                return true;
            }
        });
    }
```

In this method, we listen for the touches being registered on `viewTouchListener`,
and as you can see, they are divided to three parts.

* `case MotionEvent.ACTION_DOWN:` which runs when the user puts finger on
the `viewTouchListener`. All we do here is getting the start position of the touch.

* `case MotionEvent.ACTION_MOVE:` which runs when user drags finger on the view.
In here we call `performCalculations()` and pass the touch `event` through it
which calculates how much the finger has been dragged on the view,
and where our `linDraggable` should be.

* `case MotionEvent.ACTION_UP:` which runs when the user lifts finger off the
view. In here we call `checkWhatShouldHappen()` (don't judge me, naming is hard)
which decides how the elements on the UI should lay out, based on the amount of
drag on the screen


##### Doing the magic

The `performCalculations()` method is where the magic happens:

```java
    private void performCalculations(MotionEvent event) {

        deltaX = (int) (startX - event.getX());
        deltaY = (int) (startY - event.getY());

        float factorFromZero = (float) (deltaY) / linHeight;
        float factorFromOne = (float) (linHeight - deltaY) / linHeight;

        if (deltaY >= 0) {

            linDraggable.setTranslationY(-(int) (deltaY * ((float) 4 / 5)));

            imgDarkOverlay.setAlpha(factorFromOne);
        }
    }
```

This method executes whenever user swipes on the screen. even if
the swipe amount is as small as 1 pixel.

First we calculate the difference between start touch position (`startY` and `X`)
and the amount of touch movement, and store them inside `deltaX` and `deltaY`.


Then we calculate "From zero to one, how much the layout has been moved"
and "From one to zero, how much the layout has been moved" and store them
inside `factorFromZero` and `factorFromOne`. In this project we wont use
`factorFromZero`, but it can be very useful inside the projects you may
build upon this. (Note that the values for both variables may exceed 1
and deceed 0)


Now that we have enough data, we move(translate) our `linDraggable`.

As we want our `linDraggable` to go upwards, we check if `deltaY` is bigger
 or equal to `0`.

 Now to move the `linDraggable`, all we have to do is: `linDraggable.setTranslationY(-deltaY);`
 but as you have noticed, instead of `-deltaY`, i have put `-(int) (deltaY * ((float) 4 / 5))`.
 This decreases the value of `deltaY` a little, and gives a feeling of `linDraggable`
being a little heavy when user drags it up. It is optional and you may
not want to use it in your own projects based on the view you want to move.


Then we decrease the `alpha` value of `imgDarkOverlay`, so the views in the
back would brighten as we drag `linDraggable` up. For this all we need to do is
`imgDarkOverlay.setAlpha(factorFromOne);`. That's it!



##### Laying out components in proper location

Now we should see where the user lifts up finger, and decide what should
happen to `linDraggable`, `imgDarkOverlay` and `viewTouchListener`. And by that
I mean:

IF, the user has dragged a total of one third of `linDraggable`'s height,
then after user lifts finger, `linDraggable` should go all the way up until
its out of the view, `imgDarkOverlay` should completely fade out, and
`viewTouchListener`'s visibility should be set to `View.GONE` so it wouldn't
consume any other touches registered on the UI, and any view behind it would be
interactive.

ELSE, `linDraggable` should come back to it's original location, `imgDarkOverlay`
should get it's original alpha of 1. and nothing would happen to `viewTouchListener`.

We use `ViewAnimator` to animate any changes we want to make, and apply
them in `checkWhatShouldHappen()` method:

```java
    private void checkWhatShouldHappen() {

        if (deltaY > linHeight / 3) {

            linDraggable.animate().translationY(-linHeight).setDuration(animationDuration).withLayer();

            imgDarkOverlay.animate().alpha(0).setDuration(animationDuration).withEndAction(new Runnable() {
                @Override
                public void run() {

                    runFinishingCode();
                }
            });
        }

        else {
            linDraggable.animate().translationY(0).setDuration(animationDuration).withLayer();
            imgDarkOverlay.animate().alpha(1).setDuration(animationDuration).withLayer();
        }
    }
```

In the code above, if user has dragged the `linDraggable` enough:

* we will set `linDraggable`'s `translationY` to `-linHeight` which is the necessary
amount it has to go to completely move out of screen.

* we execute `imgDarkAnimation` with `.withEndAction()` so we know when the
animation has finished, so we can `runFinishingCode()`:

```java
    private void runFinishingCode() {
        viewTouchListener.setVisibility(View.GONE);
        linDraggable.setVisibility(View.GONE);
        imgDarkOverlay.setVisibility(View.GONE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.parseColor("#000000"));
        }
    }
```

###### That's it!

I have optionally added a button to my layout that would reveal the
`linDraggable` again, which resides in a method named `onClicks()`, and
is called in `onCreate()`:

```java
    private void onClicks() {
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
```


[View the full Java code here (Documented)](https://gist.github.com/b393ad1eb44ebc0fe55151c0e594fd4f)



#### I hope this proves useful to you.

