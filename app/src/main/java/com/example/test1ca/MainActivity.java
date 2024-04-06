package com.example.test1ca;

import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.WindowManager;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeButtons();
    }

    public void csvWriter( String content ) throws IOException {
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "logdata.csv");
        FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
        BufferedWriter bw = new BufferedWriter(fw);

        //Log.d("FILEDIT", String.valueOf(file.getAbsoluteFile()));

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        if(file.length()<=0){
            bw.write("UsID, X,Y, GTM, GTm, EA, EAmm, AA, bID\n");
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
        //Collections.shuffle(Arrays.asList(integerNumbers));

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

        //button
        Button b = (Button) v;

        //Log.d("button_ID", String.valueOf(b.getId()));
        // Write in the csv file the buttonID (bID)
        csvWriter(String.valueOf(b.getId()) + "\n");

        String textToWrite = String.valueOf(displayText.getText()) + String.valueOf(b.getText());

        if(textToWrite.length() >= 4 ){
            textToWrite = "";
            initializeButtons();
        }

        displayText.setText(textToWrite);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        EditText displayData = findViewById(R.id.display_data);
        String toDisplay = String.valueOf(displayData.getText());
        String toWrite = "";

        //Height, Width, actualArea
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getDisplay().getMetrics(displayMetrics);
        int height=0;
        if(showNavigationBar(getResources())){
            height = displayMetrics.heightPixels + getNavigationBarHeight();
        }
        else {
            height = displayMetrics.heightPixels;
        }
        int width = displayMetrics.widthPixels;

        int totalPixels = height * width;
        double actualArea = event.getSize() * totalPixels;


        //Ellipse: estimated area size using axis
        double touchMajor = event.getTouchMajor();
        double touchMinor = event.getTouchMinor();
        double ellipseArea = (Math.PI*(touchMajor*0.5)*(touchMinor*0.5));

        //ellispeArea pX->mm^2
        double ellipseAreamm =areaFromPxtoMm2((float) width, (float) height, (float) ellipseArea);

        //actualArea pX->mm^2
        double areamm = areaFromPxtoMm2(width, height, actualArea);

        //userID
        TextView userIdText = findViewById(R.id.selected_number);
        String usId = String.valueOf(userIdText.getText());

        //rapporti
        double rapporto1 = (double) (ellipseArea)/(width * height);

        //assumiamo che la il tocco sia circolare: allora, sapendo l'ActuaArea posso calcolare il raggio
        //e calcolare (r*r*pi)/(w*h) -> dovrebbe essere uguale a getSize(), in quanto è la formula inversa
        //=> questo però confermerebbe che il tocco viene calcolato come un cerchio
        double raggio = Math.sqrt(actualArea / Math.PI);
        double rapporto2 = (raggio * raggio* Math.PI) / (width * height);

        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            toDisplay = "{X:" + event.getX() + ",Y:" + event.getY() + "}\n"
                    + " GTM: " + touchMajor + ", GTm: " + touchMinor
                    + "\ngetSize " + event.getSize()
                    + "\nEllipse Area (px^2): " + ellipseArea + "\n Ellipse Area (mm^2): " + String.format("%.2f", ellipseAreamm)
                    + "\n Actual Area : " + actualArea
                    + "\n Actual Area (mm^2) : " + String.format("%.2f", areamm)
                    + "\n rapporto1: " + String.format("%.6f", rapporto1) + "\nrapporto2: " + String.format("%.6f", rapporto2);


            toWrite = usId + ","
                    + event.getX() + ","
                    + event.getY() + ","
                    + touchMajor + ","
                    + touchMinor + ","
                    + ellipseArea + ","
                    + ellipseAreamm+ ","
                    + actualArea + ",";
            try {
                csvWriter(toWrite);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            Log.d("TouchEvent", event.getPressure() +  " (" + event.getX() + "," + event.getY() + ")");
            Log.d("TouchEvent",  "GetSize: " + event.getSize() + ", actualArea: " + actualArea);
            Log.d("TouchEvent",  " GetTouchMajor: " + touchMajor + ", GetTouchMinor: " + touchMinor + ", Size (px): " + ellipseArea);


        }
        displayData.setText(toDisplay);

        return super.dispatchTouchEvent(event);
    }
    public boolean showNavigationBar(Resources resources){
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
    public void changeUser(View v) {
        TextView userIdText = findViewById(R.id.selected_number);
        int currId = Integer.parseInt(String.valueOf(userIdText.getText()));
        String tag = String.valueOf(v.getTag());

        if(Integer.parseInt(tag) == -1 && currId > 1){
            userIdText.setText(String.valueOf(currId - 1));
        }else if(Integer.parseInt(tag) == 1){
            userIdText.setText(String.valueOf(currId + 1));
        }
    }
    public double areaFromPxToMm(double areaInPx){
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        double densityPPI = DisplayMetrics.DENSITY_DEFAULT * metrics.density; //convert dp into pixel per inch
        double densityPPMM = densityPPI / 25.4; // convert from ppi into ppmm

        Log.d("metrics.density", String.valueOf(metrics.density));

        return areaInPx / (densityPPMM  * densityPPMM); //scale are from pixel^2 into mm^2

    }
    public float areaFromPxtoMm2(float width, float height, double area){

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        //pixel to inches conversion
        float widthInches = width / displayMetrics.xdpi;
        float heightInches = height / displayMetrics.ydpi;
        float diagonalInches = (float) Math.sqrt(Math.pow(widthInches, 2) + Math.pow(heightInches, 2));
        float ppi = (float) Math.sqrt(Math.pow(width, 2) + Math.pow(height, 2)) / diagonalInches;
        float ppmm = (float) (ppi/25.4);
        float areamm= (float) (area/Math.pow(ppmm, 2));

        //Log.d("areatopx", "ppi: "+ppi+ "ppmm: "+ppmm+"areapxtomm: "+ areamm);

        return areamm;
    }
}