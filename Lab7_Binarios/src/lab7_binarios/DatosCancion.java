/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lab7_binarios;

import java.io.Serializable;
/**
 *
 * @author jerem
 */

enum GeneroMus {
    POP, ROCK, ELECTRONICA, REGGAETON, CLASICA, OTRO
}

public class DatosCancion implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String nombre;
    private String artista;
    private String duracion;
    private String rutaImagen;
    private GeneroMus genero;
    private String rutaAudio;

    public DatosCancion(String nombre, String artista, String duracion, String rutaImagen, GeneroMus genero, String rutaAudio) {
        this.nombre = nombre;
        this.artista = artista;
        this.duracion = duracion;
        this.rutaImagen = rutaImagen;
        this.genero = genero;
        this.rutaAudio = rutaAudio;
    }

    public String getNombre() { 
        return nombre; 
    }
    
    public String getRutaImagen() {
        return rutaImagen; 
    }
    
    public String getRutaAudio() {
        return rutaAudio; 
    }

    public String getDuracion() {
        return duracion;
    }

    public String getArtista() {
        return artista;
    }

    public GeneroMus getGenero() {
        return genero;
    }

    @Override
    public String toString() {
        return nombre + " - " + artista + " [" + genero + "]";
    }
}
