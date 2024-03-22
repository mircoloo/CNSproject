package com.example.test1ca;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;q
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
        File file = new File( getFilesDir(), "data.csv");
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
        View displayTextView = findViewById(R.id.display_text);
        EditText displayText = (EditText) displayTextView;

        View displayIdsView = findViewById(R.id.display_ids);
        EditText displayIds = (EditText) displayIdsView;
        String idsDisplay = String.valueOf(displayIds.getText());


        Button b = (Button) v;
        Log.d("button_ID", String.valueOf(b.getId()));
        csvWriter(String.valueOf(b.getId())+ "\n");

        idsDisplay += String.valueOf(b.getId());
        displayIds.setText(idsDisplay);

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
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int screenWidth = displayMetrics.widthPixels; // Larghezza totale dello schermo in pixel
        int screenHeight = displayMetrics.heightPixels; // Altezza totale dello schermo in pixel
        String toDisplay = String.valueOf(displayData.getText());
        String toWrite = "";
        // Calcola il numero di pixel toccati utilizzando la grandezza totale dello schermo
        int totalPixels = screenWidth * screenHeight;
        float touchedAreaSize = event.getSize() * totalPixels;

        //estimated area size using the ellipse axis
        float touchMajor = event.getTouchMajor();
        float touchMinor = event.getTouchMinor();
        float touchedArea = (float) (Math.PI * (touchMajor / 2) * (touchMinor / 2));


        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            toDisplay =   "X:" + event.getX() + "\n Y:" + event.getY() + "\n" + " GTM: " + touchMajor + ", GTm: " + touchMinor +
                    "\nEllipse Area : " + touchedAreaSize + "\n Actual Area : " + touchedArea;
            Log.d("TouchEvent", event.getPressure() +  " (" + event.getX() + "," + event.getY() + ")");
            Log.d("TouchEvent",  "GetSize() --- Size (%): " + event.getSize() + ", Size (px): " + touchedAreaSize);
            Log.d("TouchEvent",  "GetTouchMajor() GetTouchMinor() --- GetTouchMajor: " + touchMajor + ", GetTouchMinor: " + touchMinor + ", Size (px): " + touchedArea);
            toWrite = event.getX() + "," + event.getY() + "," + touchMajor + "," + touchMinor +
                    "," + touchedAreaSize + "," + touchedArea + ",";
            try {
                csvWriter(toWrite);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }



        displayData.setText(toDisplay);

        return super.dispatchTouchEvent(event);
    }


}