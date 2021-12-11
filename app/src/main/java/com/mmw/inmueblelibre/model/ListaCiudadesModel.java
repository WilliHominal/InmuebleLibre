package com.mmw.inmueblelibre.model;

import java.util.List;

public class ListaCiudadesModel {
    List<CiudadModel> municipios;

    public ListaCiudadesModel(List<CiudadModel> municipios) {
        this.municipios = municipios;
    }

    public List<CiudadModel> getMunicipios() {
        return municipios;
    }

    public void setMunicipios(List<CiudadModel> municipios) {
        this.municipios = municipios;
    }

    @Override
    public String toString() {
        return "ListaCiudadesModel{" +
                "municipios=" + municipios +
                '}';
    }

    public class CiudadModel {
        String nombre;

        public CiudadModel(String nombre) {
            this.nombre = nombre;
        }

        public String getNombre() {
            return nombre;
        }

        public void setNombre(String nombre) {
            this.nombre = nombre;
        }

        @Override
        public String toString() {
            return nombre;
        }
    }
}
