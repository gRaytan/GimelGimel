package com.teamagam.gimelgimel.app.view.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.teamagam.gimelgimel.R;
import com.teamagam.gimelgimel.app.GGApplication;
import com.teamagam.gimelgimel.app.view.viewer.GGMapView;
import com.teamagam.gimelgimel.app.view.viewer.data.GGLayer;
import com.teamagam.gimelgimel.app.view.viewer.data.KMLLayer;
import com.teamagam.gimelgimel.app.view.viewer.data.VectorLayer;
import com.teamagam.gimelgimel.app.view.viewer.data.entities.Entity;
import com.teamagam.gimelgimel.app.view.viewer.data.entities.Point;
import com.teamagam.gimelgimel.app.view.viewer.data.entities.Polygon;
import com.teamagam.gimelgimel.app.view.viewer.data.entities.Polyline;
import com.teamagam.gimelgimel.app.view.viewer.data.geometries.Geometry;
import com.teamagam.gimelgimel.app.view.viewer.data.geometries.MultiPointGeometry;
import com.teamagam.gimelgimel.app.view.viewer.data.geometries.PointGeometry;
import com.teamagam.gimelgimel.app.view.viewer.data.symbols.PointImageSymbol;
import com.teamagam.gimelgimel.app.view.viewer.data.symbols.PointSymbol;
import com.teamagam.gimelgimel.app.view.viewer.data.symbols.PointTextSymbol;
import com.teamagam.gimelgimel.app.view.viewer.data.symbols.PolygonSymbol;
import com.teamagam.gimelgimel.app.view.viewer.data.symbols.PolylineSymbol;
import com.teamagam.gimelgimel.app.view.viewer.data.symbols.Symbol;

import java.util.ArrayList;
import java.util.Collection;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ViewerFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ViewerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ViewerFragment extends BaseFragment<GGApplication> implements View.OnClickListener {

    //Tests
    private static int sEntitiesCount = 0;

    private VectorLayer mVL;
    private KMLLayer mKL;
    //

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private GGMapView mGGMapView;

    public ViewerFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ViewerFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ViewerFragment newInstance(String param1, String param2) {
        ViewerFragment fragment = new ViewerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);

        mGGMapView = (GGMapView) rootView.findViewById(R.id.gg_map_view);

        Button addVectorLayerButton = (Button) rootView.findViewById(
                R.id.fab_custom_layer_creation_test);
        Button updateVectorLayerButton = (Button) rootView.findViewById(
                R.id.fab_custom_layer_update_test);
        Button kmlLayerTestButton = (Button) rootView.findViewById(
                R.id.fab_kml_layer_test);
        Button removeLayersButton = (Button) rootView.findViewById(
                R.id.fab_remove_layers_test);
        Button removeEntityButton = (Button) rootView.findViewById(R.id.fab_remove_entity_test);

        registerSetOnClickListener(this, addVectorLayerButton, updateVectorLayerButton,
                kmlLayerTestButton, removeEntityButton, removeLayersButton);

        mVL = new VectorLayer("vl");
        mKL = new KMLLayer("kl", "SampleData/kml/facilities.kml");

        return rootView;
    }

    private void registerSetOnClickListener(View.OnClickListener clickListener, View... views) {
        for (View v : views) {
            v.setOnClickListener(clickListener);
        }
    }

    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_cesium;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_custom_layer_creation_test: {
                onCreateLayerClick();
                break;
            }
            case R.id.fab_custom_layer_update_test: {
                onUpdateVectorLayerClick();
                break;
            }
            case R.id.fab_kml_layer_test: {
                onLoadKmlLayerClick();
                break;
            }
            case R.id.fab_remove_layers_test: {
                onRemoveLayersClick();
                break;
            }
            case R.id.fab_remove_entity_test: {
                onRemoveEntityClick();
                break;
            }
            default:
                break;
        }
    }

    private void onCreateLayerClick() {
        if (mGGMapView.getLayer(mVL.getId()) == null) {
            mGGMapView.addLayer(mVL);
        }

        //Generate a point around given lat/lng values and epsilon
        PointGeometry pointGeometry = generateRandomLocation(32.2, 34.8, 1);
        PointSymbol pointSymbol = generateRandomPointSymbol();
        Point p = new Point.Builder()
                .setGeometry(pointGeometry)
                .setSymbol(pointSymbol)
                .build();

        Point p2 = new Point.Builder()
                .setGeometry(pointGeometry)
                .setSymbol(pointSymbol)
                .build();
//        Point p = new Point("entity" + sEntitiesCount++, pointGeometry, pointSymbol);
        mVL.addEntity(p);
        mVL.addEntity(p2);

        //Generate a random polyline
        MultiPointGeometry polylineMpg = generateRandomLocations(32.2, 34.8, 1);
        PolylineSymbol polylineSymbol = generateRandomPolylineSymbol();
        Polyline pl =  new Polyline.Builder()
                .setId("entity" + sEntitiesCount++)
                .setGeometry(polylineMpg)
                .setSymbol(polylineSymbol)
                .build();



        mVL.addEntity(pl);

        //Generate random polygon
        MultiPointGeometry polygonMpg = generateRandomLocations(32.2, 34.8, 1);
        PolygonSymbol polygonSymbol = generateRandomPolygonSymbol();
        Polygon polygon = new Polygon.Builder()
                .setId("entity" + sEntitiesCount++)
                .setGeometry(polygonMpg)
                .setSymbol(polygonSymbol)
                .build();
        mVL.addEntity(polygon);
    }

    private void onRemoveEntityClick() {
        Entity[] entities = mVL.getEntities().toArray(new Entity[0]);

        if (entities.length == 0) {
            Toast.makeText(getActivity(), "No entities to remove from vector layer",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        Entity randEntity = entities[((int) Math.floor(Math.random() * entities.length))];

        mVL.removeEntity(randEntity.getId());
    }

    private void onUpdateVectorLayerClick() {
        if (mGGMapView.getLayer(mVL.getId()) == null) {
            Toast.makeText(getActivity(), "First, create a layer", Toast.LENGTH_SHORT).show();
            return;
        }

        Collection<Entity> vEntities = mVL.getEntities();

        for (Entity e : vEntities) {
            Symbol s;
            Geometry g;
            if (e instanceof Point) {
                Point p = (Point) e;
                g = generateRandomLocation(32.2, 34.8, 2);
                s = generateRandomPointSymbol();
            } else if (e instanceof Polyline) {
                g = generateRandomLocations(32.2, 34.8, 2);
                s = generateRandomPolylineSymbol();
            } else {
                //polygon
                g = generateRandomLocations(32.2, 34.8, 2);
                s = generateRandomPolygonSymbol();
            }
            e.updateSymbol(s);
            e.updateGeometry(g);
        }
    }

    private void onLoadKmlLayerClick() {
        if (mGGMapView.getLayer(mKL.getId()) == null) {
            mGGMapView.addLayer(mKL);
        }
    }

    private void onRemoveLayersClick() {
        Collection<GGLayer> layers = mGGMapView.getLayers();

        //Avoids concurrent modification while iterating (cannot foreach)
        GGLayer[] layerArray = layers.toArray(new GGLayer[0]);
        for (int i = 0; i < layerArray.length; i++) {
            mGGMapView.removeLayer(layerArray[i].getId());
        }

        Entity[] entities = mVL.getEntities().toArray(new Entity[0]);
        for (int i = 0; i < entities.length; i++) {
            mVL.removeEntity(entities[i].getId());
        }
    }

    private PolygonSymbol generateRandomPolygonSymbol() {
        return new PolygonSymbol(getRandomCssColorStirng(),
                getRandomCssColorStirng(), Math.random());
    }

    private MultiPointGeometry generateRandomLocations(double anchorLat, double anchorLng,
                                                       int radius) {
        MultiPointGeometry mpg = new MultiPointGeometry(new ArrayList<PointGeometry>());

        for (int i = 0; i < 3; i++) {
            mpg.pointsCollection.add(generateRandomLocation(anchorLat, anchorLng, radius));
        }

        return mpg;
    }

    private PolylineSymbol generateRandomPolylineSymbol() {
        PolylineSymbol ps = new PolylineSymbol(4, getRandomCssColorStirng());
        return ps;
    }

    private PointGeometry generateRandomLocation(double anchorLat, double anchorLng,
                                                 double radius) {
        return new PointGeometry(anchorLat + (Math.random() * 2 * radius - radius),
                anchorLng + (Math.random() * 2 * radius - radius));
    }

    private PointSymbol generateRandomPointSymbol() {
        String randomColor = getRandomCssColorStirng();

        PointImageSymbol pis = new PointImageSymbol("Cesium/Assets/Textures/maki/marker.png", 36, 36);
        PointTextSymbol pts =  new PointTextSymbol(randomColor, randomColor, 48);

        if(Math.random() < 0.5){
            return pis;
        } else {
            return pts;
        }
    }

    private String getRandomCssColorStirng() {
        String[] cssColors = new String[]{"#FF1122", "#66FF99", "#00FA12"};
        return cssColors[((int) Math.floor(Math.random() * cssColors.length))];
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
