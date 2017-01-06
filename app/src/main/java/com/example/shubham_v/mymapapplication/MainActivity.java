package com.example.shubham_v.mymapapplication;

import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.example.shubham_v.mymapapplication.CurrentPostionUpdate.GpsTracker;
import com.example.shubham_v.mymapapplication.GraphandRouting.MapRouting;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.graphhopper.GHResponse;
import com.graphhopper.GraphHopper;
import com.graphhopper.util.Constants;
import com.graphhopper.util.Instruction;

import org.mapsforge.map.android.graphics.AndroidGraphicFactory;
import org.oscim.android.MapView;
import org.oscim.core.GeoPoint;
import org.oscim.layers.tile.buildings.BuildingLayer;
import org.oscim.layers.tile.vector.VectorTileLayer;
import org.oscim.layers.tile.vector.labeling.LabelLayer;

import org.oscim.theme.VtmThemes;
import org.oscim.tiling.source.mapfile.MapFileTileSource;


import java.io.File;


public class MainActivity extends AppCompatActivity {

    private MapView mapView;
    public  String Mapfilename = "/bangalore.map";
    public File mapsFolder;
    Boolean CanDographlayerprocess = false;
    GraphHopper hopper;
    private org.oscim.layers.vector.PathLayer pathLayer;
    LinearLayout linearLayout;
    MapRouting mapRouting;
    Button path_button,current_Position;
    GpsTracker  gpsTracker;
    Double current_Lattitude,current_longitude;
    TextView cur_loc_text;
    GHResponse response;
    Instruction instruction;
    Double lat = 0.0 ,lon = 0.0;
    String instructionStr = "  ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mapView = new MapView(this);

        AndroidGraphicFactory.createInstance(this.getApplication());

        linearLayout = (LinearLayout)findViewById(R.id.mapLinearLayout);

        path_button = (Button) findViewById(R.id.find_Path_btn_id);
        current_Position = (Button)findViewById(R.id.current_position_id);
        cur_loc_text = (TextView)findViewById(R.id.cur_lat_textView_id);
        // load map in linear layout

        gpsTracker = new GpsTracker(this);
        lat = gpsTracker.getLatitude();
        lon = gpsTracker.getLongitude();
        current_Lattitude = lat; //currentGeoPoint.getLatitude();
        current_longitude =  lon; // currentGeoPoint.getLongitude();
        cur_loc_text.setText(""+current_Lattitude +  current_longitude+"   ");

        Loadmap();
        loadGraphStorage();

        current_Position.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyMarker myMarker = new MyMarker(mapView);
                Drawable drawable = getResources().getDrawable(R.drawable.current_pos, null);
                myMarker.Setmarker(current_Lattitude,current_longitude,drawable);
                mapView.map().setMapPosition(current_Lattitude,current_longitude, 1 << 15);
                mapView.map().updateMap(true);

            }
        });
        path_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (CanDographlayerprocess == true)
                {
                     mapRouting.calcPath(current_Lattitude ,current_longitude,12.984825, 77.577870);



                    }
                else {
                    logUser("Not Finished loading graph. Press wait some time then find path again.");
                }
            }
        });


             if(instruction == null)
             {



             }
        else
             {
                 logUser("instruction is null");
             }























    }

    void Loadmap()
    {
        //Load map in linearlayout
        MapFileTileSource tileSource = new MapFileTileSource();
        mapsFolder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "/graphhopper/maps/");
        tileSource.setMapFile(new File(mapsFolder + Mapfilename).getAbsolutePath());
        VectorTileLayer l = mapView.map().setBaseMap(tileSource);
        mapView.map().setTheme(VtmThemes.DEFAULT);
        mapView.map().layers().add(new BuildingLayer(mapView.map(), l));
        mapView.map().layers().add(new LabelLayer(mapView.map(), l));
        //this line set map postion and zoom level auotmaticly
        mapView.map().setMapPosition(current_Lattitude,current_longitude, 1 << 15);
        linearLayout.addView(mapView);

    }
    void loadGraphStorage() {
        logUser("loading graph (" + Constants.VERSION + ") ... ");

            new GHAsyncTask<Void, Void, Path>() {
                protected Path saveDoInBackground(Void... v) throws Exception {
                    GraphHopper tmpHopp = new GraphHopper().forMobile();
                    tmpHopp.load(new File(mapsFolder,"").getAbsolutePath());
                    log("found graph " + tmpHopp.getGraphHopperStorage().toString() + ", nodes:" + tmpHopp.getGraphHopperStorage().getNodes());
                    hopper = tmpHopp;
                    return null;
                }

                protected void onPostExecute(Path o) {
                    if (hasError()) {
                        logUser("An error happened while creating graph:"
                                + getErrorMessage());
                    } else {
                        logUser("Finished loading graph. Press long to define where to start and end the route.");

                        //calculate path by maprouting
                        mapRouting = new MapRouting(getApplication(),mapView,hopper);
                        CanDographlayerprocess = true;

                    }
                }
            }.execute();
    }
    private void log(String str) {
        Log.i("GH", str);
    }

    private void log(String str, Throwable t) {
        Log.i("GH", str, t);
    }

    private void logUser(String str) {
        log(str);
        Toast.makeText(this, str, Toast.LENGTH_LONG).show();
    }

  }
