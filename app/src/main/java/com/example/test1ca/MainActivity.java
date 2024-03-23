package com.example.test1ca;

import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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

    public void csvWriter( String content ) throws IOException {
        File file = new File(getFilesDir() , "data.csv");
        FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
        BufferedWriter bw = new BufferedWriter(fw);

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        if(file.length()<=0){
            bw.write("X,Y, GTM, GTm, EA, AA, bID\n");
        }

        bw.write(content);
        bw.close();
    }

    public void initializeButtons() {
        String buttonTag = "button";
        ViewGroup rootLayout = findViewById(android.R.id.content);
        List<View> buttons = findViewsByTag(rootLayout, buttonTag);


        if (buttons.size() != 10) {
            Log.e("MainActivity", "Expected 10 buttons with tag 'myButton', found: " + buttons.size());
            return;
        }

        int[] numbers = {1, 2, 3, 4, 5, 6, 7, 8, 9, 0};

//        Convert int array to Integer array
        Integer[] integerNumbers = Arrays.stream(numbers).boxed().toArray(Integer[]::new);

//        Shuffle the array
        Collections.shuffle(Arrays.asList(integerNumbers));

//        setId() for each button in ascending order
        int id=0;
        for (View button : buttons) {
            button.setId(id);
            id++;
        }

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

    public void onClicked(View v) throws IOException {
        //display_text box
        View displayTextView = findViewById(R.id.display_text);
        EditText displayText = (EditText) displayTextView;

        //display_ids box
        View displayIdsView = findViewById(R.id.display_ids);
        EditText displayIds = (EditText) displayIdsView;

        //button
        Button b = (Button) v;

//        String idsDisplay = String.valueOf(displayIds.getText());
//        idsDisplay += String.valueOf(b.getId());
//        displayIds.setText(idsDisplay);

        Log.d("button_ID", String.valueOf(b.getId()));
        csvWriter(String.valueOf(b.getId())+ "\n");

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

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        EditText displayData = findViewById(R.id.display_data);
        //DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        String toDisplay = String.valueOf(displayData.getText());
        String toWrite = "";

        // Calcola il numero di pixel toccati utilizzando la grandezza totale dello schermo
        //int screenWidth = displayMetrics.widthPixels; // Larghezza totale dello schermo in pixel
        //int screenHeight = displayMetrics.heightPixels; // Altezza totale dello schermo in pixel
//        int totalPixels = screenWidth * screenHeight;
//        float actualArea = event.getSize() * totalPixels;

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height=0;
        if(showNavigationBar(getResources())){
            height = displayMetrics.heightPixels + getNavigationBarHeight();
        }
        else {
            height = displayMetrics.heightPixels;
        }
        int width = displayMetrics.widthPixels;

        int totalPixels = height + width;
        float actualArea = event.getSize() * totalPixels;


        //estimated area size using the ellipse axis
        double touchMajor = event.getTouchMajor();
        double touchMinor = event.getTouchMinor();
        double ellipseArea = (3.1415*(touchMajor*0.5)*(touchMinor*0.5));

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            toDisplay =   "X:" + event.getX() + "\n Y:" + event.getY() + "\n"
                    + " GTM: " + touchMajor + ", GTm: " + touchMinor
                    + "\ngetSize " + event.getSize() +
                    "\nEllipse Area : " + ellipseArea + "\n Actual Area : " + actualArea;
            Log.d("TouchEvent", event.getPressure() +  " (" + event.getX() + "," + event.getY() + ")");
            Log.d("TouchEvent",  "GetSize() --- Size (%): " + event.getSize() + ", Size (px): " + actualArea);
            Log.d("TouchEvent",  "GetTouchMajor() GetTouchMinor() --- GetTouchMajor: " + touchMajor + ", GetTouchMinor: " + touchMinor + ", Size (px): " + ellipseArea);
            toWrite = event.getX() + ","
                    + event.getY() + ","
                    + touchMajor + ","
                    + touchMinor + ","
                    + ellipseArea + ","
                    + actualArea + ",";
            try {
                csvWriter(toWrite);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }



        displayData.setText(toDisplay);

        return super.dispatchTouchEvent(event);
    }
    public boolean showNavigationBar(Resources resources)
    {
        int id = resources.getIdentifier("config_showNavigationBar", "bool", "android");
        return id > 0 && resources.getBoolean(id);
    }
    private int getNavigationBarHeight() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);
            int usableHeight = metrics.heightPixels;
            getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
            int realHeight = metrics.heightPixels;
            if (realHeight > usableHeight)
                return realHeight - usableHeight;
            else
                return 0;
        }
        return 0;
    }

}