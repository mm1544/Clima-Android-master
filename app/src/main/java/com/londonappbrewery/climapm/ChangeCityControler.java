package com.londonappbrewery.climapm;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ViewAnimator;

public class ChangeCityControler extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // setContentView() method links ChangeCityControler to city layout file
        setContentView(R.layout.change_city_layout);

        //change_city_layout.xml has two views that we need to link to the activity
        //#editText where user can type in city name and
        //#ImageButton which takes user back to our first activity

        final EditText editTextField = (EditText) findViewById(R.id.queryET);
        ImageButton backButton = (ImageButton) findViewById(R.id.backButton);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                finish(); // will close ChangeCityControler activity. As a consequence, the screen
                // will go away and ChangeCityControler activity will be destroyed and dropped from
                // devices memory
            }
        });

        editTextField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            // it is triggered when enter is pressed on the 'soft' keyboard
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                //takes the text entered by the user
                String newCity = editTextField.getText().toString();

                // new Intent that will navigate back to the WeatherController activity
               // we have to spesify the context -- the ChangeCityController activity AND the
                // component that should pick up the Intent -- the WeatherController class
                Intent newCityIntent = new Intent(ChangeCityControler.this, WeatherController.class);

                // to let the weatherControler activity know about the new city name, we package the
                // CITY NAME into the Intent as  an EXTRA. This extra is simply a key:value pair.
                // The key is the name of the data, which will be an arbitrary string called "city"
                // and the value of the extra are the contents -- newCity.
                newCityIntent.putExtra("city", newCity);

                startActivity(newCityIntent);

                // now we need to retrieve an Intent at the other end -- in WeatherController

                return false;
            }
        });

    }
}
