/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lab7_binarios;

/**
 *
 * @author jerem
 */import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class ReproductorGUI extends JFrame {
    private DefaultListModel<DatosCancion> modeloLista;
    private JList<DatosCancion> listaVisual;
    private JLabel lblPortadaGrande;
    private JLabel lblTituloActual;
    private JProgressBar barraProgreso;
    private JLabel lblCronometro;
    
    private Logica logica;
    private javax.swing.Timer contadorTiempo;
    private int segundosTranscurridos;

    private DatosCancion cancionCargada;

    private Color colorFondo = new Color(18, 18, 18);
    private Color colorPanel = new Color(25, 25, 25);
    private Color colorAcento = new Color(255, 102, 0); 

    public ReproductorGUI() {
        logica = new Logica();
        setTitle("Jeretify");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(colorFondo);
        this.setLocationRelativeTo(null);

        JPanel panelReproductor = new JPanel();
        panelReproductor.setLayout(new BoxLayout(panelReproductor, BoxLayout.Y_AXIS));
        panelReproductor.setBackground(colorFondo);
        panelReproductor.setPreferredSize(new Dimension(350, 0));
        panelReproductor.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        lblPortadaGrande = new JLabel("Seleccione una canción", SwingConstants.CENTER);
        lblPortadaGrande.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblPortadaGrande.setForeground(Color.GRAY);
        
        lblTituloActual = new JLabel("Ninguna canción seleccionada");
        lblTituloActual.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblTituloActual.setForeground(Color.WHITE);
        lblTituloActual.setFont(new Font("SansSerif", Font.BOLD, 16));

        JPanel panelControlesAudio = new JPanel(new FlowLayout());
        panelControlesAudio.setBackground(colorFondo);
        JButton btnPlay = crearBotonCircular("▶");
        JButton btnPause = crearBotonCircular("II");
        JButton btnStop = crearBotonCircular("■");

        panelControlesAudio.add(btnPlay);
        panelControlesAudio.add(btnPause);
        panelControlesAudio.add(btnStop);

        barraProgreso = new JProgressBar(0, 100);
        lblCronometro = new JLabel("00:00");
        lblCronometro.setForeground(colorAcento);
        lblCronometro.setAlignmentX(Component.CENTER_ALIGNMENT);

        panelReproductor.add(lblPortadaGrande);
        panelReproductor.add(Box.createVerticalStrut(20));
        panelReproductor.add(lblTituloActual);
        panelReproductor.add(Box.createVerticalStrut(10));
        panelReproductor.add(panelControlesAudio);
        panelReproductor.add(barraProgreso);
        panelReproductor.add(lblCronometro);

        add(panelReproductor, BorderLayout.WEST);

        modeloLista = new DefaultListModel<>();
        cargarDatosAlInicio();
        listaVisual = new JList<>(modeloLista);
        listaVisual.setBackground(colorPanel);
        listaVisual.setCellRenderer(new CancionRenderer());
        add(new JScrollPane(listaVisual), BorderLayout.CENTER);

        JPanel panelGestion = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 15));
        panelGestion.setBackground(new Color(10, 10, 10));

        JButton btnAdd = crearBoton("ADD");
        JButton btnSelect = crearBoton("SELECT");
        JButton btnRemove = crearBoton("REMOVE");

        panelGestion.add(btnAdd);
        panelGestion.add(btnSelect);
        panelGestion.add(btnRemove);
        add(panelGestion, BorderLayout.SOUTH);

        btnSelect.addActionListener(e -> {
            cancionCargada = listaVisual.getSelectedValue();
            if (cancionCargada != null) {
                mostrarImagen(cancionCargada.getRutaImagen(), lblPortadaGrande, 300);
                lblTituloActual.setText(cancionCargada.getNombre());
                // Reiniciar visuales
                barraProgreso.setValue(0);
                lblCronometro.setText("00:00");
                logica.detener();
                if(contadorTiempo != null) contadorTiempo.stop();
            }
        });

        btnPlay.addActionListener(e -> {
            if (cancionCargada != null) {
                logica.reproducir(cancionCargada.getRutaAudio());
                iniciarSincronizacion(cancionCargada.getRutaAudio());
            } else {
                JOptionPane.showMessageDialog(this, "Primero usa SELECT en una canción.");
            }
        });

        btnPause.addActionListener(e -> {
            logica.pausar();
            if(contadorTiempo != null) contadorTiempo.stop();
        });

       btnStop.addActionListener(e -> {
            logica.detener();

            if (contadorTiempo != null) {
                contadorTiempo.stop();
            }
            segundosTranscurridos = 0;
            barraProgreso.setValue(0);
            lblCronometro.setText("00:00");
        });

        btnAdd.addActionListener(e -> {
            DatosCancion nueva = mostrarFormularioAgregar();
            if (nueva != null) {
                modeloLista.addElement(nueva);
                actualizarBinario();
            }
        });

        btnRemove.addActionListener(e -> {
            int i = listaVisual.getSelectedIndex();
            if (i != -1) {
                modeloLista.remove(i);
                actualizarBinario();
            }
        });
    }

    private DatosCancion mostrarFormularioAgregar() {
        JDialog f = new JDialog(this, "Agregar Canción", true);
        f.setLayout(new BorderLayout(10, 10));
        f.getContentPane().setBackground(colorFondo);

        JPanel pCampos = new JPanel(new GridLayout(5, 2, 10, 10));
        pCampos.setBackground(colorFondo);
        pCampos.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTextField tNom = new JTextField();
        JTextField tArt = new JTextField();
        JComboBox<GeneroMus> cbGen = new JComboBox<>(GeneroMus.values());
        JLabel lblPreview = new JLabel("Vista Previa", SwingConstants.CENTER);
        lblPreview.setPreferredSize(new Dimension(100, 100));
        lblPreview.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        final String[] paths = {"", ""}; // 0: img, 1: aud

        JButton bImg = new JButton("Cargar Foto");
        bImg.addActionListener(e -> {
            JFileChooser jfc = new JFileChooser();
            javax.swing.filechooser.FileNameExtensionFilter filtroImagen = 
                new javax.swing.filechooser.FileNameExtensionFilter("Imágenes (JPG, PNG)", "jpg", "png", "jpeg");
            jfc.setFileFilter(filtroImagen);
            jfc.setAcceptAllFileFilterUsed(false);

            if(jfc.showOpenDialog(f) == JFileChooser.APPROVE_OPTION) {
                paths[0] = jfc.getSelectedFile().getAbsolutePath();

                mostrarImagen(paths[0], lblPreview, 100); 
                bImg.setText("Foto Lista");
                bImg.setBackground(Color.GREEN);
                bImg.setOpaque(true);

                System.out.println("Imagen cargada: " + paths[0]);
            }
        });

        JButton bAud = new JButton("Cargar MP3");
        bAud.addActionListener(e -> {
            JFileChooser jfc = new JFileChooser();
            javax.swing.filechooser.FileNameExtensionFilter filtro = 
                new javax.swing.filechooser.FileNameExtensionFilter("Archivos MP3", "mp3", "MP3");
            jfc.setFileFilter(filtro);
            jfc.setAcceptAllFileFilterUsed(false); 

            if(jfc.showOpenDialog(f) == JFileChooser.APPROVE_OPTION) {
                paths[1] = jfc.getSelectedFile().getAbsolutePath();

                bAud.setText("MP3 Listo"); 
                bAud.setBackground(Color.GREEN); 
                System.out.println("Audio detectado: " + paths[1]); 
            }
        });

        pCampos.add(new JLabel("Nombre:")).setForeground(Color.WHITE); pCampos.add(tNom);
        pCampos.add(new JLabel("Artista:")).setForeground(Color.WHITE); pCampos.add(tArt);
        pCampos.add(new JLabel("Género:")).setForeground(Color.WHITE); pCampos.add(cbGen);
        pCampos.add(bImg); pCampos.add(bAud);

        JButton bSave = crearBoton("GUARDAR");
        final DatosCancion[] res = {null};
        bSave.addActionListener(e -> {
            if(!tNom.getText().isEmpty() && !paths[1].isEmpty()) {
                int totalSegundos = logica.obtenerDuracionSegundos(paths[1]);
                int min = totalSegundos / 60;
                int seg = totalSegundos % 60;
                String duracionReal = String.format("%d:%02d", min, seg);

                res[0] = new DatosCancion(
                    tNom.getText(), 
                    tArt.getText(), 
                    duracionReal,
                    paths[0], 
                    (GeneroMus)cbGen.getSelectedItem(), 
                    paths[1]
                );
                f.dispose();
            }
        });

        f.add(pCampos, BorderLayout.CENTER);
        f.add(lblPreview, BorderLayout.EAST);
        f.add(bSave, BorderLayout.SOUTH);
        f.pack();
        f.setLocationRelativeTo(this);
        f.setVisible(true);
        return res[0];
    }

    class CancionRenderer extends JPanel implements ListCellRenderer<DatosCancion> {
        private JLabel lblImg = new JLabel();
        private JLabel lblTexto = new JLabel();
        private JLabel lblDuracion = new JLabel();

        public CancionRenderer() {
            setLayout(new BorderLayout(10, 10));
            setBackground(colorPanel);
            setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            lblTexto.setForeground(Color.WHITE);
            lblDuracion.setForeground(Color.GRAY);
            add(lblImg, BorderLayout.WEST);
            add(lblTexto, BorderLayout.CENTER);
            add(lblDuracion, BorderLayout.EAST);
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends DatosCancion> list, DatosCancion cancion, int index, boolean isSelected, boolean cellHasFocus) {
            lblTexto.setText("<html><b>" + cancion.getNombre() + "</b><br>" + cancion.getArtista() + " • " + cancion.getGenero() + "</html>");
            lblDuracion.setText(cancion.getDuracion());
            
            // Icono pequeño en la lista
            ImageIcon icon = new ImageIcon(cancion.getRutaImagen());
            Image img = icon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
            lblImg.setIcon(new ImageIcon(img));

            setBackground(isSelected ? colorAcento : colorPanel);
            lblTexto.setForeground(isSelected ? Color.BLACK : Color.WHITE);
            return this;
        }
    }
    
    private void iniciarSincronizacion(String ruta) {
     
        if (contadorTiempo != null && contadorTiempo.isRunning()) {
            contadorTiempo.stop();
        }
        segundosTranscurridos = 0; 

        int duracionTotal = logica.obtenerDuracionSegundos(ruta);
        barraProgreso.setMaximum(duracionTotal);
        barraProgreso.setValue(0);
        lblCronometro.setText("00:00");

        contadorTiempo = new javax.swing.Timer(1000, e -> {
            segundosTranscurridos++;
            barraProgreso.setValue(segundosTranscurridos);

            int min = segundosTranscurridos / 60;
            int seg = segundosTranscurridos % 60;
            lblCronometro.setText(String.format("%02d:%02d", min, seg));

            if (segundosTranscurridos >= duracionTotal) {
                contadorTiempo.stop();
            }
        });

        contadorTiempo.start();
    }

    private void iniciarSimulacionCarga() {
        new Thread(() -> {
            for (int i = 0; i <= 100; i++) {
                try { Thread.sleep(500); barraProgreso.setValue(i); } catch (Exception e) {}
            }
        }).start();
    }

    private void mostrarImagen(String ruta, JLabel label, int size) {
        try {
            ImageIcon icon = new ImageIcon(ruta);
            Image img = icon.getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH);
            label.setIcon(new ImageIcon(img));
            label.setText("");
        } catch (Exception e) { label.setText("No Image"); }
    }

    private JButton crearBoton(String t) {
        JButton b = new JButton(t);
        b.setBackground(colorAcento);
        b.setForeground(Color.BLACK);
        b.setFont(new Font("Arial", Font.BOLD, 12));
        return b;
    }
    
    private JButton crearBotonCircular(String texto) {
        JButton b = new JButton(texto);
        b.setFont(new Font("SansSerif", Font.BOLD, 18));
        b.setForeground(Color.WHITE);
        b.setBackground(colorPanel);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createLineBorder(colorAcento, 2));
        b.setPreferredSize(new Dimension(60, 60));
        return b;
    }

    private ArrayList<DatosCancion> obtenerListaActual() {
        ArrayList<DatosCancion> l = new ArrayList<>();
        for (int i = 0; i < modeloLista.getSize(); i++) l.add(modeloLista.get(i));
        return l;
    }

    private void actualizarBinario() { logica.guardarBinario(obtenerListaActual()); }
    private void cargarDatosAlInicio() { for (DatosCancion c : logica.cargarBinario()) modeloLista.addElement(c); }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ReproductorGUI().setVisible(true));
    }
}