package com.pioneers.pathfinder;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.pioneers.pathfinder.activity.PathList;
import com.pioneers.pathfinder.adapter.PlaceAutocompleteAdapter;
import com.pioneers.pathfinder.adapter.ViewPagerAdapter;
import com.pioneers.pathfinder.common.libs.SlidingTabLayout;
import com.pioneers.pathfinder.util.ApiConnector;

import org.json.JSONArray;


public class PathFinderActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    Toolbar toolbar;
    ViewPager pager;
    ViewPagerAdapter adapter;
    SlidingTabLayout tabs;
    CharSequence Titles[]={"Shortest Path","Cheapest Path","Find Bus stop","Settings"};
    int Numboftabs =4;
    private Button findShortestPath;

    //For auto complete location
    protected GoogleApiClient mGoogleApiClient;

    private PlaceAutocompleteAdapter mAdapter;

    //private AutoCompleteTextView mAutocompleteView;

    private AutoCompleteTextView mSourceTextView;

    private AutoCompleteTextView mDestTextView;
    private Button btnClearSrc,btnClearDestination;
    Double sourceLatitude, sourceLongitude,destinationLatitudde,destinationLongitude;
    CharSequence sourceName;
    private AdView mAdView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_path_finder_v2);


        // Creating The Toolbar and setting it as the Toolbar for the activity

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        //Changing Title from Toolbar
        getSupportActionBar().setTitle(R.string.app_name);

        //Populating shortest path options
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.path_options, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        //Adding event listener to the button
        findShortestPath= (Button)findViewById(R.id.btnFindPath);

        //setting onclick listener for find shortest path button

        findShortestPath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("PathFinder","Shortest path found");
//                                                    Intent showOnMap = new Intent(PathFinderActivity.this, MapsActivity.class);
//                                                    showOnMap.putExtra("SourceLat",sourceLatitude);
//                                                    showOnMap.putExtra("SourceLong", sourceLongitude);
//                                                    showOnMap.putExtra("DestinationLat",destinationLatitudde);
//                                                    showOnMap.putExtra("DestinationLong",destinationLongitude);
//                                                    startActivity(showOnMap);



                Intent showOnMap = new Intent(PathFinderActivity.this, PathList.class);
                showOnMap.putExtra("SourceLat",sourceLatitude);
                showOnMap.putExtra("SourceLong", sourceLongitude);
                showOnMap.putExtra("DestinationLat",destinationLatitudde);
                showOnMap.putExtra("DestinationLong",destinationLongitude);
                startActivity(showOnMap);
            }



        });

        //Auto complete location code

        // Construct a GoogleApiClient for the {@link Places#GEO_DATA_API} using AutoManage
        // functionality, which automatically sets up the API client to handle Activity lifecycle
        // events. If your activity does not extend FragmentActivity, make sure to call connect()
        // and disconnect() explicitly.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();

        // Retrieve the AutoCompleteTextView that will display Source place suggestions.
        mSourceTextView = (AutoCompleteTextView) findViewById(R.id.sourceText);

        // Register a listener that receives callbacks when a suggestion has been selected
        mSourceTextView.setOnItemClickListener(mAutocompleteClickListener);

        // Retrieve the AutoCompleteTextView that will display Destination place suggestions.
        mDestTextView = (AutoCompleteTextView) findViewById(R.id.destText);

        // Register a listener that receives callbacks when a suggestion has been selected
        mDestTextView.setOnItemClickListener(mListen);

        mAdapter = new PlaceAutocompleteAdapter(this, mGoogleApiClient,
                null);
        mSourceTextView.setAdapter(mAdapter);
        mDestTextView.setAdapter(mAdapter);

        btnClearSrc = (Button) findViewById(R.id.btnClearSrc);
        btnClearSrc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mSourceTextView.getText().toString().equals("")){
                    mSourceTextView.setText("");
                    Log.d("PathFinder", "Clicked");
                }
            }
        });
        btnClearDestination = (Button) findViewById(R.id.btnClearDestination);
        btnClearDestination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mDestTextView.getText().toString().equals("")){
                    mDestTextView.setText("");
                    Log.d("PathFinder", "Clicked");
                }
            }
        });



//
//        // Creating The ViewPagerAdapter and Passing Fragment Manager, Titles fot the Tabs and Number Of Tabs.
//        adapter =  new ViewPagerAdapter(getSupportFragmentManager(),Titles,Numboftabs);
//
//        // Assigning ViewPager View and setting the adapter
//        pager = (ViewPager) findViewById(R.id.pager);
//        pager.setAdapter(adapter);
//
//        // Assiging the Sliding Tab Layout View
//        tabs = (SlidingTabLayout) findViewById(R.id.tabs);
//        tabs.setDistributeEvenly(true); // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width
//
//
//        // Setting Custom Color for the Scroll bar indicator of the Tab View
//        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
//            @Override
//            public int getIndicatorColor(int position) {
//                return getResources().getColor(R.color.tabsScrollColor);
//            }
//        });
//
//        // Setting the ViewPager For the SlidingTabsLayout
//        tabs.setViewPager(pager);

        //creating a new async task
        //new GetBusStopTask().execute(new ApiConnector());

        initAdMob();
    }

    private void initAdMob(){
        // Initialize the Mobile Ads SDK.
        MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713");

        // Gets the ad view defined in layout/ad_fragment.xml with ad unit ID set in
        // values/strings.xml.
        mAdView = (AdView) findViewById(R.id.ad_view);

        // Create an ad request. Check your logcat output for the hashed device ID to
        // get test ads on a physical device. e.g.
        // "Use AdRequest.Builder.addTestDevice("ABCDEF012345") to get test ads on this device."
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();

        // Start loading the ad in the background.
        mAdView.loadAd(adRequest);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            /*
             Retrieve the place ID of the selected item from the Adapter.
             The adapter stores each Place suggestion in a AutocompletePrediction from which we
             read the place ID and title.
              */
            final AutocompletePrediction item = mAdapter.getItem(position);
            final String placeId = item.getPlaceId();
            final CharSequence primaryText = item.getPrimaryText(null);

            //Log.i(TAG, "Autocomplete item selected: " + primaryText);

            /*
             Issue a request to the Places Geo Data API to retrieve a Place object with additional
             details about the place.

    }
              */
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(new ResultCallback<PlaceBuffer>() {
                @Override
                public void onResult(@NonNull PlaceBuffer places) {
                    if(places.getCount()==1){
                        //Do the things here on Click.....
                        sourceLatitude = places.get(0).getLatLng().latitude;
                        sourceLongitude = places.get(0).getLatLng().longitude;
                        CharSequence sourceName = places.get(0).getName();

                        Toast.makeText(getApplicationContext(),"Latitude:"+sourceLatitude+"Longitude:"+sourceLongitude,Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(getApplicationContext(),"SOMETHING_WENT_WRONG",Toast.LENGTH_SHORT).show();
                    }
                }
            });

          /*  Toast.makeText(getApplicationContext(), "Clicked: " + primaryText,
                    Toast.LENGTH_SHORT).show();*/
            //Log.i(TAG, "Called getPlaceById to get Place details for " + placeId);
        }
    };



    private AdapterView.OnItemClickListener mListen
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            /*
             Retrieve the place ID of the selected item from the Adapter.
             The adapter stores each Place suggestion in a AutocompletePrediction from which we
             read the place ID and title.
              */
            final AutocompletePrediction item = mAdapter.getItem(position);
            final String placeId = item.getPlaceId();
            final CharSequence primaryText = item.getPrimaryText(null);

            //Log.i(TAG, "Autocomplete item selected: " + primaryText);

            /*
             Issue a request to the Places Geo Data API to retrieve a Place object with additional
             details about the place.

    }
              */
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(new ResultCallback<PlaceBuffer>() {
                @Override
                public void onResult(@NonNull PlaceBuffer places) {
                    if(places.getCount()==1){
                        //Do the things here on Click.....

                        destinationLatitudde =  places.get(0).getLatLng().latitude;
                        destinationLongitude = places.get(0).getLatLng().longitude;
                        Toast.makeText(getApplicationContext(),"Latitude:"+sourceLatitude+"Longitude:"+sourceLongitude,Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(getApplicationContext(),"SOMETHING_WENT_WRONG",Toast.LENGTH_SHORT).show();
                    }
                }
            });

          /*  Toast.makeText(getApplicationContext(), "Clicked: " + primaryText,
                    Toast.LENGTH_SHORT).show();*/
            //Log.i(TAG, "Called getPlaceById to get Place details for " + placeId);
        }
    };

    /**
     * Callback for results from a Places Geo Data API query that shows the first place result in
     * the details view on screen.
     *
     */
   /* private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                // Request did not complete successfully
//                Log.e(TAG, "Place query did not complete. Error: " + places.getStatus().toString());
                places.release();
                return;
            }
            // Get the Place object from the buffer.

            final Place place = places.get(0);

//            // Format details of the place for display and show it in a TextView.
//            mPlaceDetailsText.setText(formatPlaceDetails(getResources(), place.getName(),
//                    place.getId(), place.getAddress(), place.getPhoneNumber(),
//                    place.getWebsiteUri()));

            // Display the third party attributions if set.
//            final CharSequence thirdPartyAttribution = places.getAttributions();
//            if (thirdPartyAttribution == null) {
//                mPlaceDetailsAttribution.setVisibility(View.GONE);
//            } else {
//                mPlaceDetailsAttribution.setVisibility(View.VISIBLE);
//                mPlaceDetailsAttribution.setText(Html.fromHtml(thirdPartyAttribution.toString()));
//            }

//            Log.i(TAG, "Place details received: " + place.getName());

            places.release();
        }
    };*/

//    private static Spanned formatPlaceDetails(Resources res, CharSequence name, String id,
//                                              CharSequence address, CharSequence phoneNumber, Uri websiteUri) {
////        Log.e(TAG, res.getString(R.string.place_details, name, id, address, phoneNumber,
////                websiteUri));
//        return Html.fromHtml(res.getString(R.string.place_details, name, id, address, phoneNumber,
//                websiteUri));
//
//    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult)
    {
        // TODO(Developer): Check error code and notify the user of error state and resolution.
        Toast.makeText(this,
                "Could not connect to Google API Client: Error " + connectionResult.getErrorCode(),
                Toast.LENGTH_SHORT).show();
    }

//    public void setTextToTextView(JSONArray jsonArray)
//    {
//        String s  = "";
//        for(int i=0; i<jsonArray.length();i++){
//
//            JSONObject json = null;
//            try {
//                json = jsonArray.getJSONObject(i);
//                s = s +
//                        "Name : "+json.getString("FirstName")+" "+json.getString("LastName")+"\n"+
//                        "Age : "+json.getInt("Age")+"\n"+
//                        "Mobile Using : "+json.getString("Mobile")+"\n\n";
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//        }
//
//       // this.responseTextView.setText(s); //Do all the c
//    }

    private class GetBusStopTask extends AsyncTask<ApiConnector, Long,JSONArray>
    {

        @Override
        protected JSONArray doInBackground(ApiConnector... params)
        {
            //It is executed on Background thread

            return params[0].GetAllCustomers();
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray)
        {
            //It is executed on the main thread

            //setTextToTextView(jsonArray);
            //Do all the functionalities here
        }
    }

    @Override
    public void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }
    }

    @Override
    public void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }
}
