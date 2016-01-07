package com.project.waylay.ui.Activity;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.project.waylay.R;
import com.project.waylay.Utilities.Util;
import com.project.waylay.adapter.CustomAlertAdapter;
import com.project.waylay.adapter.PlaceAutoCompleteAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import id.zelory.benih.BenihActivity;

public class MainActivity extends BenihActivity implements RoutingListener, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, AdapterView.OnItemClickListener {

    protected GoogleMap map;
    protected LatLng start;
    protected LatLng end;

    @Bind(R.id.start)
    TextView starting;
    @Bind(R.id.destination)
    AutoCompleteTextView destination;
    @Bind(R.id.go_button)
    Button send;
    @Bind(R.id.cardview)
    CardView cardview;
    @Bind(R.id.btn_search)
    ImageView search;
    @Bind(R.id.btn_close)
    ImageView close;
    @Bind(R.id.txt_location)
    TextView locationName;

    private String LOG_TAG = "MyActivity";
    protected GoogleApiClient mGoogleApiClient;
    private PlaceAutoCompleteAdapter mAdapter;
    private ProgressDialog progressDialog;
    private ArrayList<Polyline> polylines;
    private int[] colors = new int[]{R.color.colorPrimaryDark, R.color.colorPrimary, R.color.colorPrimary, R.color.colorAccent, R.color.primary_dark_material_light};

    private static final LatLngBounds BOUNDS_MALANG = new LatLngBounds(new LatLng(-7.965354, 112.632842),
            new LatLng(-7.931692, 112.637821));

    private String TitleName[] = {"Inagata Technosmith", "Stasiun Malang", "Inspired 27 Garage", "Malang Town Square", "Brawijaya University", "Terminal Arjosari"};
    private String Address[] = {"Jalan Soekarno- Hatta, Kecamatan Lowokwaru, Jawa Timur", "Jalan Trunojoyo, Kauman, Jawa Timur", "Jalan Candi Panggung, Mojolangu, Jawa Timur", "Jalan Veteran, Penanggungan, Jawa Timur", "Ketawanggede, Jawa Timur", "Arjosari, Jawa Timur"};
    private String PlaceId[] = {"ChIJhyGmegqCeC4Ro_gxtbnvYQA", "ChIJVVVVlSMo1i0R6NpohjVm75M", "ChIJ0d0W2OEp1i0RYwNO8o6XhHg", "ChIJE6T4s3iCeC4RsnxECir578c", "ChIJUX8903iCeC4RwQ603uAxv8A", "ChIJgeIuA44p1i0Ry4yUk8yZKME"};
    private int IconDrawable[] = {R.drawable.ic_briefcase, R.drawable.ic_train, R.drawable.ic_shopping, R.drawable.ic_shopping, R.drawable.ic_school, R.drawable.ic_bus};
    private ArrayList<String> array_sort;

    private AlertDialog myalertDialog = null;
    private AlertDialog myalertDialogDetail = null;

    @Override
    protected int getActivityView() {
        return R.layout.way_main;
    }

    @Override
    protected void onViewReady(Bundle savedInstanceState) {
        polylines = new ArrayList<>();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        MapsInitializer.initialize(this);
        mGoogleApiClient.connect();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance();
            getSupportFragmentManager().beginTransaction().replace(R.id.map, mapFragment).commit();
        }
        map = mapFragment.getMap();

        mAdapter = new PlaceAutoCompleteAdapter(this, android.R.layout.simple_list_item_1,
                mGoogleApiClient, BOUNDS_MALANG, null);

        map.setOnCameraChangeListener(position -> {
            LatLngBounds bounds = map.getProjection().getVisibleRegion().latLngBounds;
            mAdapter.setBounds(bounds);
        });


        CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(-7.965354, 112.632842));
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(16);

        map.moveCamera(center);
        map.animateCamera(zoom);

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER, 5000, 0,
                new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        start = new LatLng(location.getLatitude(), location.getLongitude());

                        Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                        List<Address> addresses;
                        try {
                            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                            String cityName = addresses.get(0).getAddressLine(0);
                            String stateName = addresses.get(0).getAddressLine(1);
                            String countryName = addresses.get(0).getAddressLine(2);

                            locationName.setText(cityName + " ," + stateName + " ," + countryName);
                        } catch (IOException e) {
                            e.printStackTrace();
                            locationName.setText("Unable to get current location");
                        }
                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {

                    }

                    @Override
                    public void onProviderEnabled(String provider) {

                    }

                    @Override
                    public void onProviderDisabled(String provider) {

                    }
                });


        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                3000, 0, new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        start = new LatLng(location.getLatitude(), location.getLongitude());

                        Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                        List<Address> addresses = null;
                        try {
                            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                            String cityName = addresses.get(0).getAddressLine(0);
                            String stateName = addresses.get(0).getAddressLine(1);
                            String countryName = addresses.get(0).getAddressLine(2);

                            locationName.setText(cityName + " ," + stateName + " ," + countryName);
                        } catch (IOException e) {
                            e.printStackTrace();
                            locationName.setText("Unable to get current location");
                        }
                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {

                    }

                    @Override
                    public void onProviderEnabled(String provider) {

                    }

                    @Override
                    public void onProviderDisabled(String provider) {

                    }
                });



        /*
        * Adds auto complete adapter to both auto complete
        * text views.
        * */
        destination.setAdapter(mAdapter);


        /*
        * Sets the start and destination points based on the values selected
        * from the autocomplete text views.
        * */

        destination.setOnItemClickListener((parent, view, position, id) -> {

            final PlaceAutoCompleteAdapter.PlaceAutocomplete item = mAdapter.getItem(position);
            final String placeId = String.valueOf(item.placeId);
            Log.i(LOG_TAG, "Autocomplete item selected: " + item.description);

        /*
         Issue a request to the Places Geo Data API to retrieve a Place object with additional
          details about the place.
          */
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(places -> {
                if (!places.getStatus().isSuccess()) {
                    // Request did not complete successfully
                    Log.e(LOG_TAG, "Place query did not complete. Error: " + places.getStatus().toString());
                    places.release();
                    return;
                }
                // Get the Place object from the buffer.
                final Place place = places.get(0);

                end = place.getLatLng();
            });

        });

        /*
        These text watchers set the start and end points to null because once there's
        * a change after a value has been selected from the dropdown
        * then the value has to reselected from dropdown to get
        * the correct location.
        * */

        destination.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


                if (end != null) {
                    end = null;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        starting.setOnClickListener(view -> showDialogList());
        send.setOnClickListener(view -> sendRequest());
        search.setOnClickListener(view -> viewSearch());
        close.setOnClickListener(view -> closeView());
    }

    private void showDialogList() {
        closeView();
        AlertDialog.Builder myDialog = new AlertDialog.Builder(MainActivity.this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.way_dialog_view, null);

        final ListView listview = (ListView) dialogView.findViewById(R.id.way_list_places);
        final TextView textSuggest = (TextView) dialogView.findViewById(R.id.text_suggest);

        textSuggest.setOnClickListener(view -> Toast.makeText(this, "Not available on ALPHA test", Toast.LENGTH_LONG).show());

        myDialog.setView(dialogView);
        array_sort = new ArrayList<>(Arrays.asList(TitleName));
        CustomAlertAdapter arrayAdapter = new CustomAlertAdapter(MainActivity.this, array_sort, PlaceId, Address, IconDrawable);
        listview.setAdapter(arrayAdapter);
        listview.setOnItemClickListener(this);
        myalertDialog = myDialog.create();
        WindowManager.LayoutParams wmlp = myalertDialog.getWindow().getAttributes();

        wmlp.gravity = Gravity.BOTTOM | Gravity.RIGHT;
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 280, getResources().getDisplayMetrics());
        wmlp.width = (int) px;
        myalertDialog.show();
    }

    private void showDialogDestination(LatLng endPos, String tempat, String alamat) {
        AlertDialog.Builder myDialog = new AlertDialog.Builder(MainActivity.this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.way_dialog_detail, null);

        final ImageView imageClose = (ImageView) dialogView.findViewById(R.id.btn_close);
        final TextView placeName = (TextView) dialogView.findViewById(R.id.way_nama_tempat);
        final TextView placeAlamat = (TextView) dialogView.findViewById(R.id.way_alamat);
        final Button placeAngkot = (Button) dialogView.findViewById(R.id.button_angkot);

        placeName.setText(tempat);
        placeAlamat.setText(alamat);
        placeAngkot.setOnClickListener(view -> goAngkot(endPos));

        myDialog.setView(dialogView);
        myalertDialogDetail = myDialog.create();
        WindowManager.LayoutParams wmlp = myalertDialogDetail.getWindow().getAttributes();

        wmlp.gravity = Gravity.BOTTOM;
        myalertDialogDetail.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myalertDialogDetail.show();

        imageClose.setOnClickListener(view -> switchDialog());
    }

    private void switchDialog() {
        myalertDialog.show();
        myalertDialogDetail.dismiss();
    }

    private void goAngkot(LatLng endPos) {
            end = endPos;
            progressDialog = ProgressDialog.show(this, "Tunggu Sebentar",
                    "Mengambil Rute.", true);
            Routing routing = new Routing.Builder()
                    .travelMode(AbstractRouting.TravelMode.DRIVING)
                    .withListener(this)
                    .alternativeRoutes(true)
                    .waypoints(start, end)
                    .build();
            routing.execute();
    }

    private void closeView() {
        cardview.setVisibility(View.INVISIBLE);
    }

    private void viewSearch() {
        cardview.setVisibility(View.VISIBLE);
    }

    public void sendRequest() {
        if (Util.Operations.isOnline(this)) {
            route();
        } else {
            Toast.makeText(this, "No internet connectivity", Toast.LENGTH_SHORT).show();
        }
    }

    public void route() {
        if (end == null) {
            if (destination.getText().length() > 0) {
                destination.setError("Choose location from dropdown.");
            } else {
                Toast.makeText(this, "Please choose a destination.", Toast.LENGTH_SHORT).show();
            }
        } else {
            closeView();
            progressDialog = ProgressDialog.show(this, "Tunggu Sebentar",
                    "Mengambil Rute.", true);
            Routing routing = new Routing.Builder()
                    .travelMode(AbstractRouting.TravelMode.DRIVING)
                    .withListener(this)
                    .alternativeRoutes(true)
                    .waypoints(start, end)
                    .build();
            routing.execute();
        }
    }


    @Override
    public void onRoutingFailure() {
        // The Routing request failed
        if (myalertDialog!=null){
            myalertDialog.dismiss();
        }

        if (myalertDialogDetail!=null) {
            myalertDialogDetail.dismiss();
        }
        progressDialog.dismiss();
        Toast.makeText(this, "Something went wrong, Try again", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRoutingStart() {
        // The Routing Request starts
    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex) {
        if (myalertDialog!=null){
            myalertDialog.dismiss();
        }

        if (myalertDialogDetail!=null) {
            myalertDialogDetail.dismiss();
        }
        progressDialog.dismiss();
        map.clear();

        CameraUpdate center = CameraUpdateFactory.newLatLng(end);
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(13);

        map.moveCamera(center);
        map.animateCamera(zoom);

        if (polylines.size() > 0) {
            for (Polyline poly : polylines) {
                poly.remove();
            }
        }

        polylines = new ArrayList<>();
        //add route(s) to the map.
        for (int i = 0; i < route.size(); i++) {

            //In case of more than 5 alternative routes
            int colorIndex = i % colors.length;

            PolylineOptions polyOptions = new PolylineOptions();
            polyOptions.color(getResources().getColor(colors[colorIndex]));
            polyOptions.width(10 + i * 3);
            polyOptions.addAll(route.get(i).getPoints());
            Polyline polyline = map.addPolyline(polyOptions);
            polylines.add(polyline);
        }

        // Start marker
        MarkerOptions options = new MarkerOptions();
        options.position(start);
        options.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_marker_featured));
        map.addMarker(options);

        // End marker
        options = new MarkerOptions();
        options.position(end);
        options.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_marker_featured));
        map.addMarker(options);

    }

    @Override
    public void onRoutingCancelled() {
        Log.i(LOG_TAG, "Routing was cancelled.");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.v(LOG_TAG, connectionResult.toString());
    }

    @Override
    public void onConnected(Bundle bundle) {
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                .getPlaceById(mGoogleApiClient, PlaceId[position]);
        placeResult.setResultCallback(places -> {
            if (!places.getStatus().isSuccess()) {
                // Request did not complete successfully
                Log.e(LOG_TAG, "Place query did not complete. Error: " + places.getStatus().toString());
                places.release();
                return;
            }
            // Get the Place object from the buffer.
            final Place place = places.get(0);
            showDialogDestination(place.getLatLng(), TitleName[position], Address[position]);
            myalertDialog.dismiss();
        });
    }
}
