package com.example.shubham_v.mymapapplication;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;

import org.oscim.android.MapView;
import org.oscim.android.canvas.AndroidGraphics;
import org.oscim.backend.canvas.Bitmap;
import org.oscim.core.GeoPoint;
import org.oscim.layers.marker.ItemizedLayer;
import org.oscim.layers.marker.MarkerItem;
import org.oscim.layers.marker.MarkerSymbol;

/**
 * Created by shubham_v on 05-01-2017.
 */

public class MyMarker {
    private MapView mapView;
   Context mcontext;
    public  ItemizedLayer<MarkerItem> itemizedLayer;

    public MyMarker(MapView MarkerMapview)
   {

       mapView = MarkerMapview;
  }
    public void Setmarker(Double markerLattitude, Double marerLogitude, Drawable markerimage) {

        GeoPoint geoPoint = new GeoPoint(markerLattitude,marerLogitude);
        itemizedLayer = new ItemizedLayer<>(mapView.map(), (MarkerSymbol) null);
        itemizedLayer.addItem(createMarkerItem(geoPoint, markerimage));
        mapView.map().layers().add(itemizedLayer);

    }

    private MarkerItem createMarkerItem(GeoPoint p, Drawable resource) {

        Drawable drawable = resource;
        Bitmap bitmap = AndroidGraphics.drawableToBitmap(drawable);
        MarkerSymbol markerSymbol = new MarkerSymbol(bitmap, 0.5f, 1);
        MarkerItem markerItem = new MarkerItem(" hello ", " shubham", p);
        markerItem.setMarker(markerSymbol);
        return markerItem;
    }
}
