package com.mmw.inmueblelibre.model;

import java.util.List;

public class ListaProvinciasModel {
    List<ProvinciaModel> provincias;

    public ListaProvinciasModel(List<ProvinciaModel> provincias) {
        this.provincias = provincias;
    }

    public List<ProvinciaModel> getProvincias() {
        return provincias;
    }

    public void setProvincias(List<ProvinciaModel> provincias) {
        this.provincias = provincias;
    }

    @Override
    public String toString() {
        return "ListaProvinciasModel{" +
                "provincias=" + provincias +
                '}';
    }

    public class ProvinciaModel {
        String nombre;

        public ProvinciaModel(String nombre) {
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
