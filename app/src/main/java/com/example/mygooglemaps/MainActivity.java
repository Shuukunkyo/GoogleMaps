package com.example.mygooglemaps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap myMap;
    private PlacesClient placesClient;
    private Polyline currentPolyline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        //// 使用你的API密钥初始化Places API
        Places.initialize(getApplicationContext(),"AIzaSyCYwtmWLooNpYVxGKnjNxbhNrMUUJ6GG24");
        // 初始化 Places API 客户端
        placesClient = Places.createClient(this);

        // 获取SupportMapFragment并在地图准备就绪时得到通知
        // 通过Fragment的ID（R.id.map）在布局中找到SupportMapFragment，
        // 以便在地图准备就绪时能够获取地图实例进行进一步的操作。
        //1.SupportMapFragment 是一个用于在 Android 应用中嵌入 Google 地图的特殊类型的 Fragment。
        //2.getSupportFragmentManager() 用于获取 FragmentManager 的实例，这是用于管理 Fragment 的事务的。
        //3.findFragmentById(R.id.map) 通过布局文件中定义的 ID (R.id.map) 找到 SupportMapFragment。这意味着在布局文件中，你的地图部分是通过一个 Fragment 标签，并分配了一个唯一的 ID（在这里是 map）。
        //4.将找到的 SupportMapFragment 赋值给 mapFragment 变量，以便在之后的代码中使用。这样，你就可以在地图准备就绪时通过 mapFragment 获取 GoogleMap 对象，从而进行地图相关的操作。

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }
    private void getDirections(LatLng origin, LatLng destination){
        // 创建 Directions API 请求
        // 在这里使用你的 Google Maps API 密钥
        String apiKey ="AIzaSyASFUDjlk2dwajNZmZT1vY6aVC8C-oao18";
        String url = "https://maps.googleapis.com/maps/api/directions/json" +
                "?origin=" + origin.latitude + "," + origin.longitude +
                "&destination=" + destination.latitude + "," + destination.longitude +
                "&key=" + apiKey;

        // 发起网络请求获取导航路线数据
        // 此处可以使用 Volley、Retrofit 等网络请求库
        // 这里简化，使用 AsyncTask 进行演示

        new AsyncTask<Void,Void,String>() {
            @Override
            protected String doInBackground(Void... voids) {
                try {
                    URL urlObject = new URL(url);
                    HttpURLConnection urlConnection = (HttpURLConnection) urlObject.openConnection();
                    InputStream inputStream = urlConnection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    return stringBuilder.toString();
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }
                @Override
                protected void onPostExecute(String jsonData) {
                    // 在这里解析获取的 JSON 数据，并绘制导航路线到地图上
                    drawRoute(jsonData);
                }
            }.execute();
        }

    // 添加以下方法，用于解析导航路线数据并绘制到地图上
    private void drawRoute(String jsonData) {
        try {
            // 解析 JSON 数据
            JSONObject jsonObject = new JSONObject(jsonData);
            JSONArray routes = jsonObject.getJSONArray("routes");
            JSONObject route = routes.getJSONObject(0);
            JSONObject polyline = route.getJSONObject("overview_polyline");
            String encodedPolyline = polyline.getString("points");

            // 解码并绘制导航路线到地图上
            List<LatLng> points = PolyUtil.decode(encodedPolyline);
            if (currentPolyline != null) {
                currentPolyline.remove();
            }
            currentPolyline = myMap.addPolyline(new PolylineOptions().addAll(points));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

        @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        // 当地图准备好使用时调用此方法
        myMap = googleMap;

        // 添加缩小和放大按钮
        myMap.getUiSettings().setZoomControlsEnabled(true);

        // 设置地点自动完成组件
//        AutocompleteSupportFragment autocompleteFragment = AutocompleteSupportFragment.newInstance();
//        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));

            // 添加 AutocompleteSupportFragment
            AutocompleteSupportFragment autocompleteFragment = AutocompleteSupportFragment.newInstance();
            Log.d("AutocompleteFragment", "Fragment created: " + (autocompleteFragment != null));

            if (autocompleteFragment != null) {
                autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));
            } else {
                Log.e("AutocompleteFragment", "AutocompleteFragment is null");
            }

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