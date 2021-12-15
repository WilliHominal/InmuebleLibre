package com.mmw.inmueblelibre.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

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

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView idInput;
        TextView direccionInput;
        TextView precioInput;
        View view;

        public ViewHolder(View vistaInmueble){
            super(vistaInmueble);
            view = vistaInmueble;
            idInput = vistaInmueble.findViewById(R.id.INM_id_TV);
            direccionInput = vistaInmueble.findViewById(R.id.INM_direccion_TV);
            precioInput = vistaInmueble.findViewById(R.id.INM_precio_TV);
        }

        public View contenedor(){
            return view;
        }

        public TextView getIdInput(){
            return idInput;
        }

        public TextView getDireccionInput(){
            return direccionInput;
        }

        public TextView getPrecioInput(){
            return precioInput;
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

        viewHolder.idInput.setText("#" + inmuebleTemp.getId());
        viewHolder.direccionInput.setText(inmuebleTemp.getDireccion());
        viewHolder.precioInput.setText(inmuebleTemp.getPrecio());
    }

    @Override
    public int getItemCount() {
        if (inmuebleDataSet == null) return 0;
        return inmuebleDataSet.size();
    }
}
