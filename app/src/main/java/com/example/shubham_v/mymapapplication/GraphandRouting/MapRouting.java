package com.example.shubham_v.mymapapplication.GraphandRouting;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.shubham_v.mymapapplication.MyMarker;
import com.example.shubham_v.mymapapplication.R;
import com.graphhopper.GHRequest;
import com.graphhopper.GHResponse;
import com.graphhopper.GraphHopper;
import com.graphhopper.PathWrapper;
import com.graphhopper.util.Instruction;
import com.graphhopper.util.InstructionList;
import com.graphhopper.util.Parameters;
import com.graphhopper.util.PointList;
import com.graphhopper.util.StopWatch;

import org.oscim.android.MapView;
import org.oscim.core.GeoPoint;
import org.oscim.layers.PathLayer;
import org.oscim.layers.vector.geometries.Style;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shubham_v on 05-01-2017.
 */

public class MapRouting {

    Context mContext;
    MapView mapView;
    GraphHopper hopper;
    public GHResponse resp;
    org.oscim.layers.vector.PathLayer pathLayer;
    public List<GeoPoint> setCurrentLocationBywayGeoPoints = new ArrayList<>();

    public MapRouting(Context context, MapView MainMapView, GraphHopper hopper1 )
    {
        mContext = context;
        mapView = MainMapView;
        hopper = hopper1;
    }

    public GHResponse calcPath(final double fromLat, final double fromLon, final double toLat, final double toLon) {

        log("calculating path ...");
        new AsyncTask<Void, Void, PathWrapper>() {
            float time;

            protected PathWrapper doInBackground(Void... v) {
                StopWatch sw = new StopWatch().start();
                GHRequest req = new GHRequest(fromLat, fromLon, toLat, toLon).setAlgorithm(Parameters.Algorithms.DIJKSTRA_BI);
                req.getHints().put(Parameters.Routing.INSTRUCTIONS, "true");
                resp = hopper.route(req);
                time = sw.stop().getSeconds();
                return resp.getBest();
            }

            protected void onPostExecute(PathWrapper resp) {
                if (!resp.hasErrors()) {
                    log("from:" + fromLat + "," + fromLon + " to:" + toLat + ","
                            + toLon + " found path with distance:" + resp.getDistance()
                            / 1000f + ", nodes:" + resp.getPoints().getSize() + ", time:"
                            + time + " " + resp.getDebugInfo());
                    logUser("the route is " + (int) (resp.getDistance() / 100) / 10f
                            + "km long, time:" + resp.getTime() / 60000f + "min, debug:" + time);



                    pathLayer = createPathLayer(resp);
                    mapView.map().layers().add(pathLayer);
                    mapView.map().updateMap(true);


                   int  n = resp.getInstructions().getSize();
                    for (int i = 0; i < n; i++) {
                        Instruction instruction = resp.getInstructions().get(i);
                        int direction = instruction.getSign();
                        String instructionStr = "--";
                        switch (direction) {
                            case 0:
                                instructionStr += "CONTINUE_ON_STREET";
                                break;
                            case 1:
                                instructionStr += "TURN_SLIGHT_RIGHT";
                                break;
                            case 2:
                                instructionStr += "TURN_RIGHT";
                                break;
                            case 3:
                                instructionStr += "\n" + "TURN_SHARP_RIGHT";
                                break;
                            case -1:
                                instructionStr += "\n" + "TURN_SLIGHT_LEFT";
                                break;
                            case -2:
                                instructionStr += "\n" + "TURN_LEFT";
                                break;
                            case -3:
                                instructionStr += "\n" + "TURN_SHARP_LEFT ";
                                break;
                            case 4:
                                instructionStr += "FINISH";
                                break;
                            case 5:
                                instructionStr += "VIA_REACHED";
                                break;
                        }
                        log(instructionStr);

                    }



                } else {

                    logUser("Error:" + resp.getErrors());
                }
            }
        }.execute();
        return resp;
    }

    private org.oscim.layers.vector.PathLayer createPathLayer(PathWrapper response) {

        Style style = Style.builder().generalization(Style.GENERALIZATION_SMALL).strokeColor(0x9900cc33).strokeWidth(4 * mContext.getResources().getDisplayMetrics().density).build();
        org.oscim.layers.vector.PathLayer pathLayer = new org.oscim.layers.vector.PathLayer(mapView.map(), style);
        List<GeoPoint> geoPoints = new ArrayList<>();
        PointList pointList = response.getPoints();


        for (int i = 0; i < pointList.getSize(); i++) {
            geoPoints.add(new GeoPoint(pointList.getLatitude(i), pointList.getLongitude(i)));
            setCurrentLocationBywayGeoPoints.add(new GeoPoint(pointList.getLatitude(i),pointList.getLongitude(i)));
        }
        pathLayer.setPoints(geoPoints);

        return pathLayer;
    }

    public void log(String str) {
        Log.i("GH", str);
    }

    public void log(String str, Throwable t) {
        Log.i("GH", str, t);
    }

    public void logUser(String str) {
        log(str);
        Toast.makeText(mContext, str, Toast.LENGTH_LONG).show();
    }
}
