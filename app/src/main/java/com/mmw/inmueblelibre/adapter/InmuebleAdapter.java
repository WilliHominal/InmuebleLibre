package com.mmw.inmueblelibre.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mmw.inmueblelibre.R;
import com.mmw.inmueblelibre.model.InmuebleModel;

import java.util.List;

public class InmuebleAdapter extends RecyclerView.Adapter<InmuebleAdapter.ViewHolder> {
    private List<InmuebleModel> inmuebleDataSet;
    private static OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        InmuebleAdapter.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements OnMapReadyCallback {
        TextView idInput;
        MapView minimapa;
        GoogleMap map;
        TextView precioInput;
        View view;

        public ViewHolder(View vistaInmueble){
            super(vistaInmueble);
            view = vistaInmueble;
            idInput = vistaInmueble.findViewById(R.id.INM_id_TV);
            minimapa = vistaInmueble.findViewById(R.id.INM_minimapa);
            precioInput = vistaInmueble.findViewById(R.id.INM_precio_TV);

            if (minimapa != null) {
                minimapa.onCreate(null);
                minimapa.onResume();
                minimapa.getMapAsync(this);
            }

        }

        public View contenedor(){
            return view;
        }

        public TextView getIdInput(){
            return idInput;
        }

        public MapView getDireccionInput(){
            return minimapa;
        }

        public TextView getPrecioInput(){
            return precioInput;
        }

        @Override
        public void onMapReady(@NonNull GoogleMap googleMap) {
            MapsInitializer.initialize(view.getContext());
            map = googleMap;
            map.getUiSettings().setAllGesturesEnabled(false);
            setearUbicacion();
        }

        private void setearUbicacion(){
            if (map == null) return;

            LatLng ubi = (LatLng) minimapa.getTag();
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(ubi, 18));
            map.addMarker(new MarkerOptions().position(ubi));

            map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        }
    }

    public InmuebleAdapter(List<InmuebleModel> dataSet){
        inmuebleDataSet = dataSet;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.vista_inmueble, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        InmuebleModel inmuebleTemp = inmuebleDataSet.get(position);

        String coordsString = inmuebleTemp.getDireccion().substring(1, inmuebleTemp.getDireccion().length()-1);
        Double latTemp = Double.valueOf(coordsString.split(",")[0]);
        Double lonTemp = Double.valueOf(coordsString.split(",")[1]);
        LatLng coords = new LatLng(latTemp, lonTemp);

        viewHolder.minimapa.setTag(coords);

        viewHolder.idInput.setText("#" + inmuebleTemp.getId());
        viewHolder.precioInput.setText("$" + inmuebleTemp.getPrecio());
    }

    @Override
    public int getItemCount() {
        if (inmuebleDataSet == null) return 0;
        return inmuebleDataSet.size();
    }
}
