package com.example.test1ca;


import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    static String PIN = "1234";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeButtons();


    }


    public void initializeButtons() {
        String buttonTag = "button";
        ViewGroup rootLayout = findViewById(android.R.id.content);
        List<View> buttons = findViewsByTag(rootLayout, buttonTag);

        if (buttons.size() != 10) {
            Log.e("MainActivity", "Expected 10 buttons with tag 'myButton', found: " + buttons.size());
            return;
        }

        int[] numbers = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};

        // Convert int array to Integer array
        Integer[] integerNumbers = Arrays.stream(numbers).boxed().toArray(Integer[]::new);

        // Shuffle the array
        Collections.shuffle(Arrays.asList(integerNumbers));

        int index = 0;
        for (View buttonView : buttons) {
            if (buttonView instanceof Button) {
                Button button = (Button) buttonView;
                button.setText(String.valueOf(integerNumbers[index++]));
            }
        }
    }

    private List<View> findViewsByTag(ViewGroup root, String tag) {
        List<View> views = new ArrayList<>();
        final int childCount = root.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = root.getChildAt(i);
            if (child instanceof ViewGroup) {
                views.addAll(findViewsByTag((ViewGroup) child, tag));
            }
            final Object tagObj = child.getTag();
            if (tagObj != null && tagObj.equals(tag)) {
                views.add(child);
            }
        }
        return views;
    }

    public void onClicked(View v){
        View displayTextView = findViewById(R.id.display_text);
        EditText displayText = (EditText) displayTextView;
        Button b = (Button) v;
        String textToWrite = String.valueOf(displayText.getText()) + String.valueOf(b.getText());

        if(textToWrite.length() >= PIN.length()){
            if(textToWrite.equals(PIN)){
                Log.d("PINRESULT", "Correct PIN");
            }
            else{
                Log.d("PINRESULT", "Wrong PIN");
            }

            textToWrite = "";
            initializeButtons();
        }

        displayText.setText(textToWrite);

        return;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        EditText displayData = (EditText) findViewById(R.id.display_data);
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int screenWidth = displayMetrics.widthPixels; // Larghezza totale dello schermo in pixel
        int screenHeight = displayMetrics.heightPixels; // Altezza totale dello schermo in pixel
        String toDisplay = String.valueOf(displayData.getText());

        // Calcola il numero di pixel toccati utilizzando la grandezza totale dello schermo
        int totalPixels = screenWidth * screenHeight;
        float touchedAreaSize = event.getSize() * totalPixels;

        float touchMajor = event.getTouchMajor();
        float touchMinor = event.getTouchMinor();

        float touchedArea = (float) (Math.PI * (touchMajor / 2) * (touchMinor / 2));

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            toDisplay = event.getPressure() +  " (" + event.getX() + "," + event.getY() + ")\n" + "Size (px): " + touchedAreaSize + "\n GTM: " + touchMajor + ", GTm: " + touchMinor + ", Size (px): " + touchedArea;
            Log.d("TouchEvent", event.getPressure() +  " (" + event.getX() + "," + event.getY() + ")");
            Log.d("TouchEvent",  "GetSize() --- Size (%): " + event.getSize() + ", Size (px): " + touchedAreaSize);
            Log.d("TouchEvent",  "GetTouchMajor() GetTouchMinor() --- GetTouchMajor: " + touchMajor + ", GetTouchMinor: " + touchMinor + ", Size (px): " + touchedArea);

        }

        displayData.setText(toDisplay);

        return super.dispatchTouchEvent(event);
    }


}