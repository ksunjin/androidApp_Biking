
package com.example.biking;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.res.ResourcesCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.example.biking.firebaseDB.mRootRef;
import static com.example.biking.firebaseDB.myUser;
import static com.example.biking.firebaseDB.tempBase;


public class MapsActivity extends AppCompatActivity
        implements
        OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback{

    private GoogleMap mMap;
    private LocationRequest mLocationRequest;
    private Location mCurrentLocation;

    private FusedLocationProviderClient mFusedLocationClient;
    private Marker mCurrentMarker;
    private LatLng startLatLng = new LatLng(0, 0);        //polyline 시작점
    private LatLng endLatLng = new LatLng(0, 0);        //polyline 끝점
    private boolean ridingState = false;                    //걸음 상태


    private long startTime=0;
    private long stopTime=0;
    private long time;
    private boolean reStart = false;
    private long deltaTime = 0;
    private float distance = 0 ;
    private float goalDistance = 0 ;
    private double averageSpeed = 0;
    private long stoppedTime = 0;
    private long allStoppedTime = 0;
    private long speedCount = 0;
    private double sumSpeed = 0;
    private double speed;
    private String stringTime ="00:00:00";




    private FloatingActionButton pathFab;

    private FloatingActionButton startFab;
    private FloatingActionButton pauseFab;
    private FloatingActionButton stopFab;
    private FloatingActionButton myLocationFab;

    private TextView speedText;

    private TextView riddingDistance;
    private TextView calText;
    private Chronometer chrono;
    private TextView smallRiddingTime;
    private TextView smallRiddingDistance;

    private LocationCallback mLocationCallback;

    // 라이딩 모드 관련
    private String mode = "none";
    private int challengeLevel =0;
    private int goalTime = 0;
    private double goalDis = 0;
    private ProgressBar progressbar;
    private Chronometer subChrono;
    private TextView riddingTimeTitle;
    private TextView riddingDistanceTitle;
    private boolean result = false;
    private TextView resultText;
    private boolean resultCheck = false;

    private int getExp = 0;

    private LatLng mOrigin;
    private LatLng mDestination;
    private ArrayList<LatLng> mMarkerPoints;
    private Polyline mPolyline;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        //시작시 권한 체크
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            Toast.makeText(getApplicationContext(), "위치 권한 설정을 허용해주셔야 합니다", Toast.LENGTH_LONG).show();
            return;
        }




        startFab = (FloatingActionButton) findViewById(R.id.fab_start);
        pauseFab = (FloatingActionButton) findViewById(R.id.fab_pause);
        stopFab = (FloatingActionButton) findViewById(R.id.fab_stop);
        myLocationFab =(FloatingActionButton) findViewById(R.id.fab_myLocation);
        pathFab = (FloatingActionButton) findViewById(R.id.fab_path);
        chrono = (Chronometer) findViewById(R.id.chrono) ;
        speedText = (TextView) findViewById(R.id.text_speed);
        riddingDistance = (TextView) findViewById(R.id.text_ridingDistance);
        calText = (TextView) findViewById(R.id.text_calories);
        progressbar = (ProgressBar) findViewById(R.id.progressbar);
        smallRiddingDistance = (TextView) findViewById(R.id.text_ridingDistance_small);
        smallRiddingTime = (TextView) findViewById(R.id.text_ridingTime_small);
        subChrono = (Chronometer) findViewById(R.id.chrono_sub);
        riddingDistanceTitle = (TextView) findViewById(R.id.riddingDistance_title);
        riddingTimeTitle= (TextView) findViewById(R.id.riddingtime_title);
        resultText = (TextView) findViewById(R.id.result_text);

        // 라이딩 모드 받아오기
        resultText.setVisibility(View.GONE);
        Intent intent = getIntent();
        mode = intent.getStringExtra("mode");
        Toast.makeText(getApplicationContext(), "mode"+mode, Toast.LENGTH_SHORT).show();

        if( !mode.equals("none")) {
            if( mode.equals("challenge") ) {
                challengeLevel = intent.getIntExtra("challengeLevel",0);

            }
            goalTime = intent.getIntExtra("time",0);
            goalDis = intent.getIntExtra("km", 5);
            Toast.makeText(getApplicationContext(), "goal"+goalTime+"/"+goalDis, Toast.LENGTH_SHORT).show();
            chrono.setCountDown(true);
            riddingDistanceTitle.setText("남은거리");
            goalDistance = (float)goalDis;
            //goalDistance = 0.1f; //테스트 용
            riddingDistance.setText(String.format("%.1f", goalDistance));
            riddingTimeTitle.setText("남은시간");
            progressbar.setProgress(0);

        }else{
            progressbar.setVisibility(View.GONE);
            smallRiddingTime.setVisibility(View.GONE);
            subChrono.setVisibility(View.GONE);
            smallRiddingDistance.setVisibility(View.GONE);
        }



        startAnimation();






        ridingState=false;

        chrono.setFormat("00:%s");
        chrono.setTypeface(ResourcesCompat.getFont(this, R.font.pfstardust));
        chrono.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            public void onChronometerTick(Chronometer c) {
                long elapsedMillis = SystemClock.elapsedRealtime() -chrono.getBase();
                if(elapsedMillis > 3600000L){
                    chrono.setFormat("0%s");
                }else{
                    chrono.setFormat("00:%s");
                }
            }
        });



        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        startFab.setOnClickListener(new View.OnClickListener() {

            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View view) {
                changeWalkState();        //라이딩 상태 변경
                startFab.setVisibility(View.INVISIBLE);
                pauseFab.setVisibility(View.VISIBLE);



            }
        });

        pauseFab.setOnClickListener(new View.OnClickListener() {

            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View view) {
                changeWalkState();        //라이딩 상태 변경
                startFab.setVisibility(View.VISIBLE);
                pauseFab.setVisibility(View.INVISIBLE);




            }
        });

        pathFab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),MapPathActivity.class);
                startActivityForResult(i, 1);
            }
        });

        stopFab.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                getStringTime();
                wantFinish();
            }
        });


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        createLocationRequest();
        createLocationCallback();
        getCurrentLocation();
    }


    private void getCurrentLocation(){
        OnCompleteListener<Location> mCompleteListener = new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                {
                    if(task.isSuccessful() && task.getResult() != null) {
                        mCurrentLocation = task.getResult();
                    }
                }
            }
        };
        mFusedLocationClient.getLastLocation().addOnCompleteListener( this, mCompleteListener);
        mFusedLocationClient.requestLocationUpdates(mLocationRequest,mLocationCallback,Looper.myLooper() );


    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == 1234){
            if(requestCode == 1) {
                String str_mOrigin_lat = data.getStringExtra("mOrigin_lat");
                String str_mOrigin_lng = data.getStringExtra("mOrigin_lng");
                Double mOrigin_lat = Double.parseDouble(str_mOrigin_lat);
                Double mOrigin_lng = Double.parseDouble(str_mOrigin_lng);

                String str_mDestination_lat = data.getStringExtra("mDestination_lat");
                String str_mDestination_lng = data.getStringExtra("mDestination_lng");
                Double mDestination_lat = Double.parseDouble(str_mDestination_lat);
                Double mDestination_lng = Double.parseDouble(str_mDestination_lng);

                mOrigin = new LatLng(mOrigin_lat, mOrigin_lng);
                mDestination = new LatLng(mDestination_lat, mDestination_lng);

                drawRoute();
            }
        }
    }


    private void createLocationCallback(){
        mLocationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult){
                super.onLocationResult(locationResult);

                Location newLocation = locationResult.getLastLocation();

                double latitude = newLocation.getLatitude(), longtitude = newLocation.getLongitude();

                if( mCurrentLocation != null && ridingState == true  ) { //&&  newLocation.getSpeed()>0.3 - 속도 제한
                    distance += (double)(mCurrentLocation.distanceTo(newLocation))/1000f;

                    if(mode.equals("none") || mode.equals("finish")){
                        riddingDistance.setText(String.valueOf(Math.floor(distance*100)/100));
                    }else{
                        goalDistance -=  (double)(mCurrentLocation.distanceTo(newLocation))/1000f;
                        smallRiddingDistance.setText("주행거리 "+String.valueOf(Math.floor(distance*100)/100)+"km");
                        riddingDistance.setText(String.valueOf(Math.floor(goalDistance*100)/100));
                        progressbar.setProgress((int)((distance/goalDis)*1000f));

                        if( !resultCheck && !result && goalDistance<= 0 &&  System.currentTimeMillis()- startTime < goalTime*60*1000 ){
                            result = true;
                            resultCheck = true;
                            int resultExp = 0;

                            if(mode.equals("challenge")){
                                switch(challengeLevel){
                                    case 1: resultExp = 100;
                                        break;
                                    case 2: resultExp = 200;
                                        break;
                                    case 3: resultExp = 300;
                                }

                            }else{
                                resultExp = 100;

                            }
                            Toast.makeText(getApplicationContext(), "목표를 달성하였습니다. 획득 경험치 "+resultExp+"exp", Toast.LENGTH_SHORT).show();
                            getExp += resultExp;
                            mRootRef.child("회원").child(Integer.toString(myUser.getCustId())).child("exp").setValue(myUser.getExp()+resultExp); // 수정 필요
                            tempBase.put(myUser.getEmail(),myUser);
                            resultText.setText("★성공★");
                            resultText.setTextColor(Color.RED);


                        }else if(!resultCheck  &&  System.currentTimeMillis()- startTime > goalTime*60*1000 ) {
                            resultCheck = true;

                            Toast.makeText(getApplicationContext(), "목표 달성에 실패하셨습니다.", Toast.LENGTH_SHORT).show();

                            resultText.setText("★실패★");
                            resultText.setTextColor(Color.BLUE);


                        }

                        if( resultCheck ){
                            mode = "finish";
                            chrono.stop();
                            chrono.setCountDown(false);
                            chrono.setBase(subChrono.getBase());
                            chrono.start();
                            riddingDistanceTitle.setText("주행거리");
                            riddingDistance.setText(String.format("%.1f", distance ));
                            riddingTimeTitle.setText("주행거리");
                            smallRiddingDistance.setVisibility(View.GONE);
                            smallRiddingTime.setVisibility(View.GONE);
                            subChrono.setVisibility(View.GONE);
                            resultText.setVisibility(View.VISIBLE);

                        }





                    }
                    upDateSpeed(newLocation);
                    upDateAverageSpeed();
                    upDateCal();


                }

                if (mCurrentMarker != null) mCurrentMarker.remove();
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(new LatLng(latitude, longtitude));
                mCurrentMarker =  mMap.addMarker(markerOptions);

                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()), 18));
                if(ridingState){                        //라이딩 시작 버튼이 눌렸을 때
                    endLatLng = new LatLng(latitude, longtitude);        //현재 위치를 끝점으로 설정
                    drawPath();                                            //polyline 그리기
                    startLatLng = new LatLng(latitude, longtitude);        //시작점을 끝점으로 다시 설정

                }



                mCurrentLocation = newLocation;


            }
        };

    }

    private void changeWalkState(){
        if(!ridingState) {
            Toast.makeText(getApplicationContext(), "라이딩 시작", Toast.LENGTH_SHORT).show();
            ridingState = true;
            if( reStart  == false ) {
                distance = 0;
                startTime = System.currentTimeMillis();
                if(!mode.equals("none")){
                    chrono.setBase( SystemClock.elapsedRealtime() + 1000*60*goalTime );
                    chrono.start();
                }

            }
            if(reStart == true) allStoppedTime +=  System.currentTimeMillis() - stopTime;
            hideFab();
            if( mode.equals("none")){
                chrono.setBase(SystemClock.elapsedRealtime() - stoppedTime);
                chrono.start();

                subChrono.setBase(SystemClock.elapsedRealtime() -stoppedTime);
                subChrono.start();

            }else{
                subChrono.setBase(SystemClock.elapsedRealtime() -stoppedTime);
                subChrono.start();

            }
            startLatLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());        //현재 위치를 시작점으로 설정



        }else{
            showFab();
            Toast.makeText(getApplicationContext(), "라이딩 멈춤", Toast.LENGTH_SHORT).show();
            stopTime = System.currentTimeMillis();

            ridingState = false;
            reStart = true;
            if( mode.equals("none")){
                chrono.stop();

                subChrono.stop();
                stoppedTime = SystemClock.elapsedRealtime() - subChrono.getBase();


            }else{
                subChrono.stop();
                stoppedTime = SystemClock.elapsedRealtime() - subChrono.getBase();

            }
        }
    }

    private void drawPath(){        //polyline을 그려주는 메소드
        PolylineOptions options = new PolylineOptions().add(startLatLng).add(endLatLng).width(15).color(Color.YELLOW).geodesic(true);
        //polylines.add(mMap.addPolyline(options));
        mMap.addPolyline(options);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startLatLng, 18));
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if ((ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) &&
                    (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION))) {
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
                return;
            }
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        chrono.stop();
    }

    @Override
    protected void onResume(){
        super.onResume();
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            Toast.makeText(getApplicationContext(), "위치 권한 설정을 허용해주셔야 합니다", Toast.LENGTH_LONG).show();
            return;
        }
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

    }



    private void upDateAverageSpeed() {
        speedCount++;
        sumSpeed += speed;
        averageSpeed = Double.parseDouble(String.format("%.2f",sumSpeed/speedCount));

    }

    private void upDateCal(){
        long sec = getRealTime();
        double kcal =  420*sec/60/60; // 시간당 420 칼로리 소모
        kcal =  Double.parseDouble(String.format("%.2f", kcal));
        calText.setText("칼로리 "+kcal+"kcal");




    }
    private void upDateSpeed(Location location){
        speed = Double.parseDouble(String.format("%.2f", location.getSpeed()));
        speedText.setText("속도 "+speed +"km/s");


    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setMaxWaitTime(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    public void showFab(){
        ObjectAnimator pathAnimation = ObjectAnimator.ofFloat(pathFab, "translationY", 390.0f);
        pathAnimation.setDuration(1000);
        pathAnimation.start();

        ObjectAnimator stopAnimation = ObjectAnimator.ofFloat(stopFab, "translationY", 550.0f);
        stopAnimation.setDuration(1000);
        stopAnimation.start();

    }

    public void hideFab(){

        ObjectAnimator pathAnimation = ObjectAnimator.ofFloat(pathFab, "translationY", 230.0f);
        pathAnimation.setDuration(1000);
        pathAnimation.start();

        ObjectAnimator stopAnimation = ObjectAnimator.ofFloat(stopFab, "translationY", 230.0f);
        stopAnimation.setDuration(1000);
        stopAnimation.start();

    }
    public void startAnimation(){
        showFab();

        ObjectAnimator myLocationAnimation = ObjectAnimator.ofFloat(myLocationFab, "translationY", 230.0f);
        myLocationAnimation.setDuration(1000);
        myLocationAnimation.start();
    }


    @Override
    public void onBackPressed() {
        if( ridingState == true ){
            Toast.makeText(getApplicationContext(), "라이딩을 먼저 멈춰 주세요", Toast.LENGTH_SHORT).show();

        }else{
            wantFinish();

        }


    }

    public void wantFinish(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
        int newGetExp = (int)getRealTime()/1000/60/30*20;
        newGetExp += (int)distance*10;
        newGetExp += getExp;
        if( mode.equals("none")){
            builder.setTitle("정말 라이딩을 종료하시겠습니까?")
                    .setMessage("총주행거리: "+ (Math.floor(distance*100))/100+"km"+
                            "\n총주행시간:  "+  stringTime +
                            "\n평균 속도:  " + averageSpeed+
                            "\n획득한 경험치:  " + newGetExp+ "exp"
                    );
        }
        else{
            String resultText = "실패";
            if(result == true) {resultText ="★성공★";}
            builder.setTitle("정말 라이딩을 종료하시겠습니까?")
                    .setMessage("총주행거리: "+(Math.floor(distance*100))/100+"km"+
                            "\n총주행시간:  "+  stringTime +
                            "\n평균 속도:  " + averageSpeed+
                            "\n획득한 경험치:  " + newGetExp +"exp"+
                            "\n챌린지 결과:  " + resultText
                    );

        }



        builder.setPositiveButton("확인", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int id)
            {
                int newGetExp = (int)getRealTime()/1000/60/30*20;
                newGetExp += (int)distance*10;
                mRootRef.child("회원").child(Integer.toString(myUser.getCustId())).child("exp").setValue(myUser.getExp()+newGetExp); // 수정 필요
                tempBase.put(myUser.getEmail(),myUser);
                finish();
            }
        });

        builder.setNegativeButton("공유", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int id)
            {
                Toast.makeText(getApplicationContext(), "Cancel Click", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNeutralButton("취소", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int id)
            {
                Toast.makeText(getApplicationContext(), "Neutral Click", Toast.LENGTH_SHORT).show();
            }
        });


        AlertDialog alertDialog = builder.create();

        alertDialog.show();

    }

    public void getStringTime(){
        long sec = getRealTime();
        long hour = sec/3600; //1시간 = 60분 = 3600초
        sec %= 3600;
        long min = sec/60;
        sec %= 60;
        stringTime = String.format("%02d:%02d:%02d",hour,min,sec);


    }

    public long getRealTime() {
        if( ridingState) {
            return (System.currentTimeMillis() - startTime - allStoppedTime) / 1000;
        }else{ return (stopTime- startTime - allStoppedTime) / 1000;}
    }


    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> > {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try{
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            }catch(Exception e){
                e.printStackTrace();
            }
            return routes;
        }
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points;
            PolylineOptions lineOptions = null;

            // Traversing through all the routes
            for(int i=0;i<result.size();i++){
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for(int j=0;j<path.size();j++){
                    HashMap<String,String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                lineOptions.addAll(points);
                lineOptions.width(8);
                lineOptions.color(Color.RED);
            }

            if(lineOptions != null) {
                if(mPolyline != null){
                    mPolyline.remove();
                }
                mPolyline = mMap.addPolyline(lineOptions);

            }else
                Toast.makeText(getApplicationContext(),"No route is found", Toast.LENGTH_LONG).show();
        }
    }

    protected class DownloadTask extends AsyncTask<String, Void, String> {

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try{
                // Fetching the data from web service
                data = downloadUrl(url[0]);
                Log.d("DownloadTask","DownloadTask : " + data);
            }catch(Exception e){
                Log.d("Background Task",e.toString());
            }
            return data;
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            MapsActivity.ParserTask parserTask = new MapsActivity.ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }

    protected String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);

            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.connect();

            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb  = new StringBuffer();

            String line = "";
            while( ( line = br.readLine())  != null){
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        }catch(Exception e){
            Log.d("Exception on download", e.toString());
        }finally{
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }


    protected String getDirectionsUrl(LatLng origin,LatLng dest){

        String str_origin = "origin="+origin.latitude+","+origin.longitude;

        String str_dest = "destination="+dest.latitude+","+dest.longitude;

        String key = "key=" + getString(R.string.google_maps_key);

        String parameters = str_origin+"&"+str_dest+"&"+ "mode=transit&departure_time=now" + "&" +key;

        String output = "json";

        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;

        return url;
    }

    void drawRoute(){
        Log.d("mOrigin", String.valueOf(mOrigin));
        String url = getDirectionsUrl(mOrigin, mDestination);

        MapsActivity.DownloadTask downloadTask = new MapsActivity.DownloadTask();

        downloadTask.execute(url);
    }

}