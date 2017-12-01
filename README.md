# View Drag Test

A tutorial on how to move a view on UI by dragging it with your finger.

![drag](https://user-images.githubusercontent.com/24822099/33432273-97733820-d5ec-11e7-82f2-653d44e190e4.gif)

Demo APK: [ViewDrag.zip](https://github.com/SIMMORSAL/ViewDrag/files/1517887/ViewDrag.zip)



## Introduction

Welcome. In this I will teach you how to create a sliding layout, so so it would move out of UI when you swipe it upward, and the layout behind it will come out of darkness.

All we'll cover here will be creating the GIF you see, but the knowledge is easily transferable,and you will be able to create some neat and awesome stuff with it.



## Lets get to it...

### 1. Creating the layout

We'll start by creating the layout.

This is a collapsed look at our layout.
```
<FrameLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent">


    <RelativeLayout.../>


    <!-- the dragging and related views =============================================-->
    <ImageView.../>

    <LinearLayout.../>

    <View.../>
    <!-- ============================================= the dragging and related views -->

</FrameLayout>
```