/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lab7_binarios;


import javazoom.jl.player.Player;
import java.io.*;
import java.util.ArrayList;

public class Logica {
    private final String RUTA_BINARIO = "playlist.bin";
    
    private Player reproductorMp3;
    private FileInputStream fis;
    private BufferedInputStream bis;
    
    private long pausaPosicion = 0;
    private long totalLongitud = 0;
    private String rutaActual = "";
    private Thread hiloMusica;


    public void reproducir(String ruta) {
        try {
            if (!rutaActual.equals(ruta)) {
                rutaActual = ruta;
                pausaPosicion = 0;
            }

            detenerHilosPrevios();

            hiloMusica = new Thread(() -> {
                try {
                    File archivo = new File(rutaActual);
                    totalLongitud = archivo.length();
                    
                    fis = new FileInputStream(archivo);
                    bis = new BufferedInputStream(fis);
                    fis.skip(pausaPosicion);

                    reproductorMp3 = new Player(bis);
                    reproductorMp3.play();
                    
                } catch (Exception e) {
                    System.err.println("Error en el hilo de audio: " + e.getMessage());
                }
            });
            hiloMusica.start();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void pausar() {
        try {
            if (reproductorMp3 != null && fis != null) {
                pausaPosicion = totalLongitud - fis.available();
                detenerHilosPrevios();
            }
        } catch (IOException e) {
            System.err.println("Error al obtener posición de pausa.");
        }
    }

    public void detener() {
        if (reproductorMp3 != null) {
            reproductorMp3.close();
            reproductorMp3 = null;
        }
        if (hiloMusica != null && hiloMusica.isAlive()) {
            hiloMusica.interrupt();
        }
        pausaPosicion = 0;
    }

    private void detenerHilosPrevios() {
        if (reproductorMp3 != null) {
            reproductorMp3.close();
            reproductorMp3 = null;
        }
        if (hiloMusica != null && hiloMusica.isAlive()) {
            hiloMusica.interrupt();
        }
    }


    public int obtenerDuracionSegundos(String ruta) {
        File f = new File(ruta);
        return (int) (f.length() / (128 * 1024 / 8)); 
    }

    public int buscarCancionRecursivo(ArrayList<DatosCancion> lista, String nombre, int i) {
        if (i >= lista.size()) return -1;
        if (lista.get(i).getNombre().equalsIgnoreCase(nombre)) return i;
        return buscarCancionRecursivo(lista, nombre, i + 1);
    }

    public void guardarBinario(ArrayList<DatosCancion> lista) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(RUTA_BINARIO))) {
            oos.writeObject(lista);
        } catch (IOException e) { e.printStackTrace(); }
    }

    public ArrayList<DatosCancion> cargarBinario() {
        File f = new File(RUTA_BINARIO);
        if (!f.exists()) return new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
            return (ArrayList<DatosCancion>) ois.readObject();
        } catch (Exception e) { return new ArrayList<>(); }
    }
}