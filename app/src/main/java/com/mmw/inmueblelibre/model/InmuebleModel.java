package com.mmw.inmueblelibre.model;

public class InmuebleModel {
    String id;
    String direccion;
    String precio;

    public InmuebleModel(String id, String direccion, String precio) {
        this.id = id;
        this.direccion = direccion;
        this.precio = precio;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getPrecio() {
        return precio;
    }

    public void setPrecio(String precio) {
        this.precio = precio;
    }

    @Override
    public String toString() {
        return "InmuebleModel{" +
                "id=" + id +
                ", direccion='" + direccion + '\'' +
                ", precio='" + precio + '\'' +
                '}';
    }
}
