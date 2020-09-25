package com.example.gpstrackingsystem;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {
    private GoogleMap mMap;
    LocationManager man;
    Location loc;
    Address address;
    double lati = 0, longi = 0;
    String area = "";
    Geocoder geo; //used to get exact address
    boolean isNet, isGps;
    LatLng first,prev,curloc;
    Marker mark;

    @Override
    protected void onCreate(Bundle savedInstanceState) {//will be called when our app will be in mobile buffer
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        man = (LocationManager) getSystemService(LOCATION_SERVICE);//location service is enabled
        isNet = man.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        isGps = man.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (isNet || isGps) {
            if (isNet) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                man.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 2000, 10, this);

                if(man!=null)//means our location has been updated
                {
                    loc=man.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if(loc!=null)
                    {
                        lati=loc.getLatitude();
                        longi=loc.getLongitude();
                    }
                }
            }

            if(isGps)
            {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                man.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, this);

                if(man!=null)//means our location has been updated
                {
                    loc=man.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if(loc!=null)
                    {
                        lati=loc.getLatitude();
                        longi=loc.getLongitude();
                    }
                }
            }
            first=new LatLng(lati,longi);
            prev=first;
            Toast.makeText(getApplicationContext(),"LATI : "+lati+" LONGI : "+longi,Toast.LENGTH_LONG).show();
        }
//        Locale.getDefault() means English
        try {
            Geocoder geo = new Geocoder(this, Locale.getDefault());

            //here we get many addresses from that we want only one
            List<Address> list = geo.getFromLocation(lati, longi, 1);
            address = list.get(0);

            area = address.getAddressLine(0);
            area = area + " , " + address.getLocality();
            area = area + " , " + address.getAdminArea();
            area = area + " , " + address.getCountryName();
            area = area + " , " + address.getCountryCode();

            Toast.makeText(getApplicationContext(),area,Toast.LENGTH_LONG);
        }
        catch(Exception e)
        {}
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    //onCreate and onMapREady is called only once during the starting of app only
    @Override
    public void onMapReady(GoogleMap googleMap) {//when our app will show the googleMap at that time this method will be called
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);//it will change the view in output
//        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        // Add a marker in Sydney and move the camera
        LatLng current = new LatLng(lati,longi);
        mMap.addMarker(new MarkerOptions().position(current).title("YOU ARE HERE"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(current));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));//it will zoom in

        //to draw line we have a special class
        Polyline poly=mMap.addPolyline(new PolylineOptions().add(first,current).width(5).color(Color.RED));
    }


    //when the location is changed then this method will be called automatically
    @Override
    public void onLocationChanged(Location location)
    {
        if(mark!=null)//we have the marker
        {
            mark.setVisible(false);

        }
        double newlati=location.getLatitude();
        double newlongi=location.getLongitude();

        LatLng currloc=new LatLng(newlati,newlongi);//for showing the line LatLng is necessary

        Marker newMark=mMap.addMarker(new MarkerOptions().position(currloc).title("YOU ARE HERE"));
        mark=newMark;

        mMap.moveCamera(CameraUpdateFactory.newLatLng(currloc));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));

        Polyline poly=mMap.addPolyline(new PolylineOptions().add(prev,currloc).width(5).color(Color.RED));
        prev=currloc;


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
}
