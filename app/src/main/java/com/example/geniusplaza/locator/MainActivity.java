package com.example.geniusplaza.locator;

import android.content.DialogInterface;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends FragmentActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback {

    public GoogleMap mMap;
    double lat, lng, afterDragLat, afterDraglng;
    LatLng location, newLatLngLocation;
    String newLocation = "Unknown Location";
    private Marker currentMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
//        setTitle("Locator");
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.action_search) {
            final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    //String serachWord = searchView.getQuery().toString();
                    searchView.getQuery().toString();
                    //return false;
                    Log.d("Onclick:", "sasds");
                    Geocoder gc = new Geocoder(getApplicationContext());
                    List<Address> list = null;
                    try {
                        list = gc.getFromLocationName(searchView.getQuery().toString(), 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (list.size() > 0) {
                        android.location.Address add = list.get(0);
                        lat = add.getLatitude();
                        lng = add.getLongitude();
                        Log.d("latitude:", String.valueOf(lat));

                        location = new LatLng(lat, lng);
                        searchView.getQuery().toString();
                        mMap.addMarker(new MarkerOptions().position(location).title("Click to edit").snippet("Marker in " + searchView.getQuery().toString()));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 4));

                        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                            @Override
                            public void onInfoWindowClick(Marker marker) {
                                currentMarker = marker;
                                new AlertDialog.Builder(MainActivity.this)
                                        .setTitle("Edit Marker")
                                        .setMessage("Do you wanna edit this marker")
                                        .setPositiveButton("Cancel", null)
                                        .setNegativeButton("Edit", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                //currentMarker.remove();
                                                //mMap.addMarker(new MarkerOptions().position(location).draggable(true).title("Click to edit").snippet("Marker in " + searchView.getQuery().toString()));
                                                //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 4));
                                                currentMarker.setDraggable(true);
                                                markerDragMethod();
                                                //currentMarker.remove();
                                            }
                                        }).show();
                            }
                        });
                    } else {
                        Toast.makeText(getApplicationContext(), "Invalid address", Toast.LENGTH_LONG).show();
                        //searchView.setQuery("Reinsert Address");
                    }
                    onBackPressed();
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {

                    return false;
                }
            });


        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    public void markerDragMethod() {
        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {
                currentMarker = marker;
                // TODO Auto-generated method stub
                // Here your code
                Toast.makeText(MainActivity.this, "Drag to location", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                currentMarker = marker;
                LatLng position = marker.getPosition();
                Toast.makeText(MainActivity.this, "Location selected", Toast.LENGTH_LONG).show();
                afterDragLat = position.latitude;
                afterDraglng = position.longitude;
                Log.d("Drag lat: ", String.valueOf(afterDragLat));

                Geocoder gcd = new Geocoder(MainActivity.this, Locale.getDefault());
                List<Address> addresses = null;
                try {
                    addresses = gcd.getFromLocation(afterDragLat, afterDraglng, 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (addresses.size() > 0)
                {
                    //]Log.d("New location name: ",addresses.get(0).getLocality());
                    currentMarker.remove();
                    newLocation = addresses.get(0).getLocality();
                    newLatLngLocation = new LatLng(afterDragLat, afterDraglng);
                    mMap.addMarker(new MarkerOptions().position(newLatLngLocation).draggable(false).title("Click to edit").snippet("Marker in " + newLocation));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newLatLngLocation, 4));
                }
                else
                {
                    newLocation = "Unknown Location";// do your staff
                }
            }

            @Override
            public void onMarkerDrag(Marker marker) {
                // TODO Auto-generated method stub
                //Toast.makeText(PostMapsActivity.this, "Dragging", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
