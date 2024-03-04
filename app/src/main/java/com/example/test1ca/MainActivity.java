package com.example.test1ca;

import android.os.Bundle;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    EditText displayText;
    String pin = "1234";
    long startTime = 0;
    ArrayList<float[]> touchCoordinates = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        displayText = findViewById(R.id.display_text);
        LinearLayout linearLayout = findViewById(R.id.linearLayout);

        setButtonListeners();

        View.OnTouchListener handleTouch = new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                int x = (int) event.getX();
                int y = (int) event.getY();

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        Log.i("TAG", "touched down");
                        break;
                    case MotionEvent.ACTION_MOVE:
                        Log.i("TAG", "moving: (" + x + ", " + y + ")");
                        break;
                    case MotionEvent.ACTION_UP:
                        Log.i("TAG", "touched up");
                        break;
                }

                Toast.makeText(MainActivity.this, x + " " + y, Toast.LENGTH_SHORT).show();


                return true;
            }
        };

        displayText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                // Controlla se il testo inserito è lungo 4 caratteri
                if (editable.length() >= pin.length()) {
                    // Se sì, confronta il pin inserito con il pin desiderato
                    String enteredPin = editable.toString();
                    if (enteredPin.equals(pin)) {
                        // Pin corretto, calcola il tempo trascorso e visualizza il messaggio
                        long endTime = SystemClock.elapsedRealtime();
                        long timeElapsed = endTime - startTime;
                        Toast.makeText(MainActivity.this, "Pin corretto. Tempo trascorso: " + timeElapsed + " millisecondi", Toast.LENGTH_SHORT).show();
                        displayText.setText("");
                    } else {
                        // Pin errato, mostra messaggio di errore e cancella il testo
                        Toast.makeText(MainActivity.this, "Pin errato", Toast.LENGTH_SHORT).show();
                        displayText.setText("");
                    }
                    // Resettiamo startTime e le coordinate per consentire un nuovo conteggio del tempo e del tocco
                    startTime = 0;
                    touchCoordinates.clear();
                }
            }
        });
    }

    private void setButtonListeners() {
        int[] buttonIds = {R.id.button0, R.id.button1, R.id.button2, R.id.button3, R.id.button4, R.id.button5, R.id.button6, R.id.button7, R.id.button8, R.id.button9};
        for (int buttonId : buttonIds) {
            Button button = findViewById(buttonId);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int[] location = new int[2];
                    v.getLocationOnScreen(location);
                    float x = location[0] + (float) v.getWidth() / 2; // Coordinate X del centro del pulsante rispetto all'intera schermata
                    float y = location[1] + (float) v.getHeight() / 2; // Coordinate Y del centro del pulsante rispetto all'intera schermata

                    // Stampa le coordinate a schermo


                    // Esegui altre azioni necessarie al clic del pulsante
                    onButtonClick(((Button) v).getText().toString());
                }
            });

        }
    }



    private void onButtonClick(String buttonText) {
        displayText.append(buttonText);
        if (startTime == 0) {
            startTime = SystemClock.elapsedRealtime();
        }
    }
}
