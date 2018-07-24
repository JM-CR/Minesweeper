package mx.ibero;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static java.time.temporal.ChronoUnit.SECONDS;

/**
 * El objetivo de esta clase es manejar lo referente a las puntuaciones de las partidas.
 *
 * @author Josue Mosiah Contreras Rocha
 */
public class Ranking
{
    // Variables de instancia
    private LocalDateTime ldt1;   // Medir tiempo entre cada partida
    private LocalDateTime ldt2;
    private File[] ranks = new File[4];                     // Archivos de rankings
    private List<String[]> contenido = new ArrayList<>();   // Texto de puntuaciones
    private List<Long[]> tiempo = new ArrayList<>();        // Tiempos registrados en cada dificultad
    private int dificultad;
    private Long duracion;   // Duración de la partida ganada
    private boolean timerActivado;

    /**
     * Al instanciar el objeto se deben cargar los archivos de cada dificultad.
     */
    public Ranking()
    {
        validarArchivos();

        // Leer archivos de rankings
        for(int i=0; i<ranks.length; i++)
        {
            Configuracion rank = new Configuracion(ranks[i]);

            contenido.add(rank.getListText());
            tiempo.add(rank.getTiempo());
        }
    }

    /**
     * Si no existe el archivo de alguna dificultad lo crea.
     */
    private void validarArchivos()
    {
        ranks[0] = new File("src/resources/principante.txt");
        ranks[1] = new File("src/resources/intermedio.txt");
        ranks[2] = new File("src/resources/dificil.txt");
        ranks[3] = new File("src/resources/personalizada.txt");

        try
        {
            for(File file: ranks)
                if(!file.exists())
                    file.createNewFile();
        }
        catch (IOException e)
        {
            alertaArchivo(Alert.AlertType.ERROR, "No se pudieron encontrar los rankings de jugadores.");
            Platform.exit();
        }
    }

    /**
     * Inicia el timer para la partida.
     */
    public void startTimer()
    {
        ldt1 = LocalDateTime.now();
        timerActivado = true;
    }

    /**
     * Detiene el timer para la partida.
     */
    public void stopTimer()
    {
        ldt2 = LocalDateTime.now();
        duracion = SECONDS.between(ldt1, ldt2);  // Diferencia de tiempos
        timerActivado = false;
    }

    /**
     * Regresa la fecha en la que se realizó el record.
     *
     * @return Fecha del record.
     */
    public LocalDate fecha()
    {
        return LocalDate.from(ldt2);
    }

    /**
     * Mensaje de alerta en la lectura del manual de usuario.
     *
     * @param mensaje Texto del mensaje.
     * @param type Tipo de alerta.
     */
    private void alertaArchivo(Alert.AlertType type, String mensaje)
    {
        Alert temp = new Alert(type);
        temp.setTitle("Actualización de estado.");
        temp.setContentText(mensaje);
        temp.showAndWait();
    }

    /**
     * Verifica si el tiempo registrado entra en el top 10.
     *
     * @return True si hay nuevo record.
     */
    public boolean consultaRecord()
    {
        boolean nuevoRecord = false;

        for(Long t: tiempo.get(dificultad))
        {
            if (t == null || duracion < t)
            {
                nuevoRecord = true;
                break;
            }
        }

        return nuevoRecord;
    }

    /**
     * Se encarga de agregar el nuevo record a la lista.
     */
    public void nuevoRecord()
    {
        // Crear Dialog
        Dialog<ButtonType> newPlayer = new Dialog<>();
        newPlayer.setHeaderText("Haz ingresado en el top 10.");
        newPlayer.setTitle("¡Nuevo record!");

        // Crear contenido del form
        GridPane formulario = new GridPane();
        formulario.setHgap(20.0);
        formulario.setVgap(20.0);
        formulario.setAlignment(Pos.CENTER);

        TextField textField = new TextField();

        formulario.add(new Text("Tu tiempo fue de "+ duracion +" segundos."),0,0, 2, 1);
        formulario.add(new Text("Ingresa tu nombre"),0,1);
        formulario.add(textField, 1,1);

        // Preparar y mostrar Dialog
        newPlayer.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        newPlayer.getDialogPane().setContent(formulario);
        Optional<ButtonType> opcion = newPlayer.showAndWait();

        // Agregar a la lista de rankings si es que lo desea el jugador
        if(opcion.isPresent() && opcion.get() == ButtonType.OK)
        {
            actualizarRank(textField.getText());

            // Guardarlo en archivo
            guardarRank();
        }
    }

    /**
     * Se encarga de agregar el nuevo jugador a la lista actual.
     *
     * @param name Nombre del jugador
     */
    private void actualizarRank(String name)
    {
        // Agregar en la lista de tiempos
        tiempo.get(dificultad)[9] = duracion;
        Arrays.sort(tiempo.get(dificultad), (o1, o2) ->
        {
            if (o1 == null && o2 == null)
                return 0;

            if (o1 == null)
                return 1;

            if (o2 == null)
                return -1;

            return o1.compareTo(o2);
        });

        // Corregir lista de contenidos
        contenido.get(dificultad)[9] = name +","+ fecha() +","+ duracion;
        Arrays.sort(contenido.get(dificultad), (o1, o2) ->
        {
            if (o1 == null && o2 == null)
                return 0;

            if (o1 == null)
                return 1;

            if (o2 == null)
                return -1;

            String[] val1 = o1.split(",");
            String[] val2 = o2.split(",");
            int v1 = Integer.valueOf(val1[2]);
            int v2 = Integer.valueOf(val2[2]);

            return v1 - v2;
        });
    }

    /**
     * Si hay nuevo record en el ranking este debe actualizar el archivo de salida.
     */
    private void guardarRank()
    {
        try(BufferedWriter bw = new BufferedWriter(new FileWriter(ranks[dificultad])))
        {
            for(String w: contenido.get(dificultad))
                bw.write(w +"\n");
        }
        catch (IOException e)
        {
            alertaArchivo(Alert.AlertType.ERROR, "No se pudieron encontrar los rankings de jugadores.");
        }
    }

    /**
     * Evalúa si la partida está actualmente en curso.
     *
     * @return Devuelve true si el timer está prendido
     */
    public boolean isTimerActivado()
    {
        return timerActivado;
    }

    /**
     * Setter para la dificultad actual de la partida.
     *
     * @param dificultad 0=Fácil, 1=Normal, 2=Difícil, 3=Personalizada
     */
    public void setDificultad(int dificultad)
    {
        if(dificultad < 4 && dificultad > -1)
            this.dificultad = dificultad;
    }

    /**
     * Devuelve las puntuaciones en base a la dificultad pedida.
     *
     * @param dificultad 0=Fácil 1=Normal 2=Dificil 3=Personalizada
     * @return Arreglo con las puntuaciones
     */
    public String[] puntuaciones(int dificultad)
    {
        return contenido.get(dificultad);
    }
}
