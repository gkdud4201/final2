package com.example.cafemoa;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.content.Intent;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import noman.googleplaces.NRPlaces;
import noman.googleplaces.Place;
import noman.googleplaces.PlaceType;
import noman.googleplaces.PlacesException;
import noman.googleplaces.PlacesListener;

//로그인 안했을 경우 홈화면

public class HomeActivity extends AppCompatActivity
        implements
        OnMapReadyCallback,
        PlacesListener {

    private GoogleMap mMap;

    List<Marker> previous_marker = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        previous_marker = new ArrayList<Marker>();



       ImageButton cafeButton = (ImageButton)findViewById(R.id.cafeButton);
        cafeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LatLng location = null;
                showPlaceInformation(location);
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {

        mMap = googleMap;

        LatLng SUWON = new LatLng(37.2848145,127.0118573);

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(SUWON);
        markerOptions.title("행리단길");

     
        
        mMap.addMarker(markerOptions);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(SUWON));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(10));

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {

            @Override
            public void onInfoWindowClick(Marker marker) {

                Intent intent = new Intent(getBaseContext(), CafelistActivity.class);

                String title = marker.getTitle();
                String address = marker.getSnippet();

                intent.putExtra("title", title);
                intent.putExtra( "address", address);

                startActivity(intent);
            }
        });
    }


    @Override
    public void onPlacesFailure(PlacesException e) {

    }

    @Override
    public void onPlacesStart() {

    }

    @Override
    public void onPlacesSuccess(final List<Place> places) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (noman.googleplaces.Place place : places) {

                    LatLng latLng
                            = new LatLng(place.getLatitude()
                            , place.getLongitude());

                    

                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(latLng);
                    markerOptions.title(place.getName());
                    String markerSnippet = null;
                    markerOptions.snippet(markerSnippet);
                    Marker item = mMap.addMarker(markerOptions);
                    previous_marker.add(item);

                }

                //중복 마커 제거
                HashSet<Marker> hashSet = new HashSet<Marker>();
                hashSet.addAll(previous_marker);
                previous_marker.clear();
                previous_marker.addAll(hashSet);

            }
        });

    }

    @Override
    public void onPlacesFinished() {

    }
    public void showPlaceInformation(LatLng location)
    {
        mMap.clear();//지도 클리어




        if (previous_marker != null)
            previous_marker.clear();//지역정보 마커 클리어

        new NRPlaces.Builder()
                .listener(HomeActivity.this)
                .key("AIzaSyCZquZhwnyY-cdpWU9KiZCk-N_nWqF6aqY")
                .latlng(37.2848145,127.0118573)
                .radius(500) //500 미터 내에서 검색
                .type(PlaceType.CAFE) //카페
                .build()
                .execute();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.homemenu,menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.loginMenu:
                Intent intent =new Intent(HomeActivity.this,LoginActivity.class);
                HomeActivity.this.startActivity(intent);
                break;
            case R.id.signupMenu:
                Intent intent1 =new Intent(HomeActivity.this,SignupActivity.class);
                HomeActivity.this.startActivity(intent1);
                break;
            case R.id.action_search:
                Intent intent2=new Intent(HomeActivity.this, Information2Activity.class);
                HomeActivity.this.startActivity(intent2);
                break;
        }
        return  true;
    }



}

