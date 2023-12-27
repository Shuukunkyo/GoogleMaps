package com.example.mygooglemaps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap myMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 获取SupportMapFragment并在地图准备就绪时得到通知
        // 通过Fragment的ID（R.id.map）在布局中找到SupportMapFragment，
        // 以便在地图准备就绪时能够获取地图实例进行进一步的操作。
        //1.SupportMapFragment 是一个用于在 Android 应用中嵌入 Google 地图的特殊类型的 Fragment。
        //2.getSupportFragmentManager() 用于获取 FragmentManager 的实例，这是用于管理 Fragment 的事务的。
        //3.findFragmentById(R.id.map) 通过布局文件中定义的 ID (R.id.map) 找到 SupportMapFragment。这意味着在布局文件中，你的地图部分是通过一个 Fragment 标签，并分配了一个唯一的 ID（在这里是 map）。
        //4.将找到的 SupportMapFragment 赋值给 mapFragment 变量，以便在之后的代码中使用。这样，你就可以在地图准备就绪时通过 mapFragment 获取 GoogleMap 对象，从而进行地图相关的操作。
        Places.initialize(getApplicationContext(),"AIzaSyCYwtmWLooNpYVxGKnjNxbhNrMUUJ6GG24");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }
    
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        myMap = googleMap;

        AutocompleteSupportFragment autocompleteFragment = AutocompleteSupportFragment.newInstance();
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                // 处理选择的地点
                LatLng selectedLocation = place.getLatLng();
                myMap.moveCamera(CameraUpdateFactory.newLatLng(selectedLocation));
                myMap.addMarker(new MarkerOptions().position(selectedLocation).title(place.getName()));
            }

            @Override
            public void onError(@NonNull Status status) {
                // 处理错误
            }
        });

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.autocomplete_fragment_container, autocompleteFragment)
                .commit();
    }
}