package com.londonappbrewery.climapm;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;


public class WeatherController extends AppCompatActivity {

    // Constants:
    final int REQUEST_CODE = 123;
    final String WEATHER_URL = "http://api.openweathermap.org/data/2.5/weather";
    // App ID to use OpenWeather data
    final String APP_ID = "33b7f5609514da0513a4009a0f29b365";
    // Time between location updates (5000 milliseconds or 5 seconds)
    final long MIN_TIME = 5000;
    // Distance between location updates (1000m or 1km)
    final float MIN_DISTANCE = 1000;

    // TODO: Set LOCATION_PROVIDER here:
    String LOCATION_PROVIDER = LocationManager.GPS_PROVIDER;


    // Member Variables:
    TextView mCityLabel;
    ImageView mWeatherImage;
    TextView mTemperatureLabel;

    // TODO: Declare a LocationManager and a LocationListener here:
    // Location Manager is the component, that will start or stop
    //requesting location updates

    LocationManager mLocationManager;

    //Location Listener is the component that will be notified if the location is
    // actually changed

    LocationListener mLocationListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_controller_layout);

        // Linking the elements in the layout to Java code
        mCityLabel = (TextView) findViewById(R.id.locationTV);
        mWeatherImage = (ImageView) findViewById(R.id.weatherSymbolIV);
        mTemperatureLabel = (TextView) findViewById(R.id.tempTV);
        ImageButton changeCityButton = (ImageButton) findViewById(R.id.changeCityButton);


        // TODO: Add an OnClickListener to the changeCityButton here:
        changeCityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // navigation between different apps and activities is done by Android component
                // called 'Intent'

                //creating Intent by calling Intent constructor
                // for this Intent we must specify 2 things:
                // 1.context for the Intent -- in this case: context is where we are --
                // WheatherController activity.
                //2.the component, the Intent is for -- this is where we want to send user when he
                // clicks the button -- ChangeCityControler activity class.
                Intent myIntent = new Intent(WeatherController.this, ChangeCityControler.class);

                // NOW we need to fire the command to START a new activity
                startActivity(myIntent); // passing Intent that we have created

                /* Certain Android components like activities communicate via Intents. This is true
                * WITHIN and Between apps.
                * In this example we created the Intent to start the ChangeCityControler activity,
                * and we have used that intent to call StartActivity(..), to launch ChangeCity
                * Controler. In this case we were explicit about exact class, that we want that
                * Intent to run.
                *   Intents go far beound navigation.
                *   Intents allow Android apps to send and receive requests for jobs to be done
                *   even if no activity name is provided.
                *   E.G.: Everytime you click 'share' on an app or website, a share Intent is
                *   created. At this point warious apps such as FB or Twitter which can respond to
                *   those shared intents will 'put their hand up' and will say that they can handle
                 *   sharing job for you. That is why FB, Twitter and others are all listed on the
                 *   sharing menu.
                */
            }
        });
    }


    // TODO: Add onResume() here:
    @Override
    protected void onResume() { // onResume is Androids lifecycle
        // methods. It is called just before the activity comes on screen.

        // This is right place to check wheather we got new city name.

        // It gets executed jus after onCreate and jus before user can interact with
        // activity
        super.onResume();
        Log.d("Clima", "onResume() called");

        // retrieve Intent with the call to getIntent() method
        Intent myIntent = getIntent();

        // NOW lets retrieve the EXTRA
        // since the city name is a pease of text, we can get access to the EXTRA with
        // getStringExtra() method. Recap: - the key for the extra was "city"
        String city = myIntent.getStringExtra("city");

        // since there can be multiple EXTRAS in the single Intent, it is important that the key
        // that we used to retrieve the EXTRA, maches the key that we used to put the extra into the
        // Intent.


        // If we DON'T have a new city name we just have to retrieve the weather for the current
        // location.

        // if statement checks if string called "city" is NOT equal to NULL.
        if(city != null){

            getWeatherForNewCity(city);

        } else { // if there is no city provided, then app should get weather for current location

            Log.d("Clima", "Getting weather for current location");
            getWeatherForCurrentLocation();
        }

    }


    // TODO: Add getWeatherForNewCity(String city) here:
    private void getWeatherForNewCity(String city){

        //creating new RequestParams object by calling constructor
        RequestParams newCityParams = new RequestParams(); // with keys and values for the city name
        // and app id
        //It is required in documentation to provide key name'q' and 'appid'
        newCityParams.put("q", city);
        newCityParams.put("appid", APP_ID);

        //REQUESTING THE WEATHER BY CITY NAME
        letsDoSomeNetworking(newCityParams);
    }



    // TODO: Add getWeatherForCurrentLocation() here:
    private void getWeatherForCurrentLocation() {
        //instance of location manager,
        // requesting location service
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // SO it gets hold of the Location Manager and assigns that Location Manager object
        // to our mLocationManager member variable


        //THE component that will do the checking for updates for the device location is the
        // Location Listener
        mLocationListener = new LocationListener() {
            // DOWN are 4 callbacks of the Location Listener
            // the way it work is similar to how onClick Listener works for the button


            @Override
            public void onLocationChanged(Location location) {

                Log.d("Clima", "onLocationChanged() callback received");

                String longitude = String.valueOf(location.getLongitude()); // extracting longitude location data
                // from LOCATION OBJECT using getLongitude() AND representing it as a String

                String latitude = String.valueOf(location.getLatitude());

                Log.d("Clima", "longitude is: " + longitude);
                Log.d("Clima", "latitude is: " + latitude);

                // taking APP ID together with latitude and longitude
                RequestParams params = new RequestParams(); // new variable holds the query parameters for openwhethermap
                // the way to add those parameters is with put() method, it requires the 'key'
                // and the 'value'
                params.put("lat", latitude); // we store 3 parameters in the 'params' object
                params.put("lon", longitude);
                params.put("appid", APP_ID);

                //REQUESTING THE WEATHER BY LATITUDE AND LONGITUDE
                // we will put actual networking call into separate method, and give 'params' as an input
                letsDoSomeNetworking(params);


        }


            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

                Log.d("Clima", "onProviderDisabled() callback received");

            }
        };

        // instructing Location Manager to start requesting updates
        // we need to specify the location provider that we are going to use,
        //the min amount of time in miliseconds that should take between updates,
        // the min distance between updates and
        // the component that will be notified of the updates - the Location Listener
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.


            //WE can request Loation permission from the user :
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
                                    // it requires 3 inputs:
                                    //1. the activity itself, which we supply with the keyword "this"
            //2. an array of strings containing permissions beying requested
            //3. AND request code used to track permission request


            return;
        }
        mLocationManager.requestLocationUpdates(LOCATION_PROVIDER, MIN_TIME, MIN_DISTANCE, mLocationListener);
    }

   //we need to check if user have granted the app the location permission
    //our app will be notified from the operating system via callback called
    // onRequestPermissionsResult(). We will meke A.S. to autogenerate the code for this method

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // when our app has received this callback from our operating system,
        // we want to make sure that the result in the callback pertains(dera/tinka)
        // to our location permission request...
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        //...so we insert if() statement that checks if the request code in the callback matches
        // constant that we have supplied when we made the request
        if(requestCode == REQUEST_CODE){
            // the result is stored in grantResults parameter

            //this statement will check for two conditions that has to be true
            //1. grantResults array contains at least one element
            //2. first element in the array grantResults[] should be "permission granted"
            //only if both conditions are true, then WE KNOW THAT OUR APP HAS LOCATION PERMISSION
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Log.d("Clima", "onRequestPermissionResult(): Permission granted");

                getWeatherForCurrentLocation();
                // since we have permission here we can call
                // to request weather data
            } else {
                Log.d("Clima", "Permission denied =( ");
            }


        }

    }


    // TODO: Add letsDoSomeNetworking(RequestParams params) here:
    private void letsDoSomeNetworking(RequestParams params){
        // to fetch the data from the internet we will need help from special object, that helps
        // with networking 'things'. We will use James' networking library in here.

        AsyncHttpClient client = new AsyncHttpClient();
        // creating new object of type 'AsyncHttpClient'. We wont be typing any code into
        // the browser - instead, to get the weather data we will need to perform 'Http request'.
        // James' library will help us to make those requests and simplify networking. This library
        // takes care about doing time consuming network requests in the background. HIS MEANS: - our
        // app remains responsive while it is waiting for data from the openwheathermap server. IF
        // our networking code wasn't executed in the background (i.e. asynchronosly), the app would
        // completely freeze UNTIL the response came back from the server. BUT WITH 'AsyncHttpClient'
        // object we don't have to worry about that. We will use it to get a 'get' request(???).
        client.get(WEATHER_URL, params, new JsonHttpResponseHandler() {
            @Override // to redefine onSuccess
            public  void onSuccess(int statusCode, Header[] headers, JSONObject response){
                // onSccess is trigered when the response is successful

                //onSuccess will print the weather data to the console
                // All the weather data that we are getting back is stored inside 'JSONObject
                // response' object
                Log.d("Clima", "Success! JSON: " + response.toString());

                // Creating weatherDataModel object that holds weather data from the JSON, that we are
                // getting back from the openweathermap api call
                WeatherDataModel weatherData = WeatherDataModel.fromJson(response);
                // this static method is taking the place of the constructor. We are creating a
                // 'weatherDataModel', using 'fromJson' instead of writing 'new weatherDataModel'.
                // This is handy way of passing that json, that we got as a responce and passing
                // it to our model. IN THIS WAY  we are letting the model class to worry about how
                // to create that Java object by parsing json.

                updateUI(weatherData);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject response){
                Log.e("Clima", "Fail " + e.toString());
                Log.d("Clima", "Status code " + statusCode); // printing statuscode, that is shipped
                // with onFailure method

                //adding a 'tost' message so that user of an app can monitor the outcome of their
                // request, when the app is running
                Toast.makeText(WeatherController.this, "Request failed", Toast.LENGTH_SHORT).show();

            }



                });
                                        // 'new JsonHttpResponseHandler()' -- we need an object that
        // will be notified of the response to this get request. The component, that will be listening
        // for the responce from the server is an object of type JsonHttpResponseHandler(). This class
        // is also from James' library.
        //JsonHttpResponseHandler() will receive one of two messages: -onsuccess, or -onfalier, depending
        // whether get(..) request was successful or not.
        // LIKE ALWAYS, we have to specify witch instructions should be executed in each case.




    }



    // TODO: Add updateUI() here:
    //creating a method that will take care of updating all the views on screen, using WheatherDataModel
    private void updateUI(WeatherDataModel weather){
        mTemperatureLabel.setText(weather.getTemperature());
        mCityLabel.setText(weather.getCity());

        // to get particular weather icon's resource id we use:
        int resourceID = getResources().getIdentifier(weather.getIconName(), "drawable", getPackageName());
        // "drawable" -- is resource folder where to look
        mWeatherImage.setImageResource(resourceID);
    }




    // TODO: Add onPause() here:

    //FINAL: We need to stop checking for location updates whean this app is closing, or is no
    // longer in the forground(???)
    //!!!: This app is not some kind of fittness app, that needs to track user's every step. As
    // well, freeing-up resources (like e.g. Location Manager) is a good practise to preserve
    // battery life.
    // RECAP: -From Android activity lifecycle: - Lifecycle method, that gets called when app
    // leaves forground(??) is onPause().
    // SO onPause() lifeCycle callback ia a good place to start freeing-up resources.

    // Freeing resources involves 'telling' your app to stop receiving location updates from the
    // Location Listener.

    @Override
    protected void onPause(){
        super.onPause();

        //if mLocationManager has a value AND it is not equal to null, ten we call 'removeUpdates'
        // to stop receiving location updates from the Location Listener
        if (mLocationManager != null) mLocationManager.removeUpdates(mLocationListener);

    }




}
