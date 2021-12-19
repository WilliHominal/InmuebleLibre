package com.mmw.inmueblelibre.model;

public class MensajeFirebaseModel {
    String to;
    DatosMensaje data;

    public MensajeFirebaseModel(String receptor, String titulo, String texto) {
        to = receptor;
        data = new DatosMensaje(titulo, texto);
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public DatosMensaje getData() {
        return data;
    }

    public void setData(DatosMensaje data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "MensajeFirebaseModel{" +
                "to='" + to + '\'' +
                ", data=" + data +
                '}';
    }



    private class DatosMensaje {
        String titulo;
        String texto;

        public DatosMensaje(String titulo, String texto) {
            this.titulo = titulo;
            this.texto = texto;
        }

        public String getTitulo() {
            return titulo;
        }

        public void setTitulo(String titulo) {
            this.titulo = titulo;
        }

        public String getTexto() {
            return texto;
        }

        public void setTexto(String texto) {
            this.texto = texto;
        }

        @Override
        public String toString() {
            return "DatosMensaje{" +
                    "titulo='" + titulo + '\'' +
                    ", texto='" + texto + '\'' +
                    '}';
        }
    }
}
