package com.example.shubham_v.mymapapplication.GraphandRouting;

import android.content.Context;
import android.graphics.Path;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.shubham_v.mymapapplication.GHAsyncTask;
import com.graphhopper.GHRequest;
import com.graphhopper.GHResponse;
import com.graphhopper.GraphHopper;
import com.graphhopper.PathWrapper;
import com.graphhopper.util.Constants;
import com.graphhopper.util.Parameters;
import com.graphhopper.util.PointList;
import com.graphhopper.util.StopWatch;

import org.oscim.core.GeoPoint;
import org.oscim.layers.vector.geometries.Style;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Created by shubham_v on 05-01-2017.
 */

public class MapGraph {

    Context mcontext;
    File mapsFolder;
    GraphHopper hopper;

   public MapGraph(Context context , File MyFolder){
        mcontext = context;
        mapsFolder = MyFolder;
        loadGraphStorage();
    }

  public  GraphHopper loadGraphStorage() {

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
                }
            }
        }.execute();

        return  hopper;
    }


   /* public void calcPath(final double fromLat, final double fromLon, final double toLat, final double toLon) {

        log("calculating path ...");
        new AsyncTask<Void, Void, PathWrapper>() {
            float time;

            protected PathWrapper doInBackground(Void... v) {
                StopWatch sw = new StopWatch().start();
                GHRequest req = new GHRequest(fromLat, fromLon, toLat, toLon).setAlgorithm(Parameters.Algorithms.DIJKSTRA_BI);
                req.getHints().
                        put(Parameters.Routing.INSTRUCTIONS, "false");
                GHResponse resp = hopper.route(req);
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
                } else {
                    logUser("Error:" + resp.getErrors());
                }

            }
        }.execute();
    }

    private org.oscim.layers.vector.PathLayer createPathLayer(PathWrapper response) {
        Style style = Style.builder()
                .generalization(Style.GENERALIZATION_SMALL)
                .strokeColor(0x9900cc33)
                .strokeWidth(4 * getResources().getDisplayMetrics().density)
                .build();
        org.oscim.layers.vector.PathLayer pathLayer = new org.oscim.layers.vector.PathLayer(mapView.map(), style);
        List<GeoPoint> geoPoints = new ArrayList<>();
        PointList pointList = response.getPoints();
        for (int i = 0; i < pointList.getSize(); i++)
            geoPoints.add(new GeoPoint(pointList.getLatitude(i), pointList.getLongitude(i)));
        pathLayer.setPoints(geoPoints);
        return pathLayer;
    }
*/
    public void log(String str) {
        Log.i("GH", str);
    }

    public void log(String str, Throwable t) {
        Log.i("GH", str, t);
    }

    public void logUser(String str) {
        log(str);
        Toast.makeText(mcontext, str, Toast.LENGTH_LONG).show();
    }


}
