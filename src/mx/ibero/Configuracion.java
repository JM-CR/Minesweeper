package mx.ibero;

import javafx.application.Platform;
import javafx.scene.control.Alert;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Esta clase se encarga de leer cualquier archivo de configuración del juego.
 *
 * @author Josue Mosiah Contreras Rocha
 */
public class Configuracion
{
    private String text = "";
    private List<String> listText = new ArrayList<>();
    private List<Long> tiempo = new ArrayList<>();

    /**
     * Cargar archivo en paralelo.
     *
     * @param pathname Path del archivo de configuración a cargar.
     */
    public Configuracion(String pathname)
    {
        File file = new File(pathname);

        try(BufferedReader br = new BufferedReader(new FileReader(file)))
        {
            String line;
            while((line = br.readLine()) != null)
                text = text +"\n"+ line;
        }
        catch (FileNotFoundException e)
        {
            alertaUsuario("No se pudo cargar un archivo de configuración.");
        }
        catch (IOException e)
        {
            alertaUsuario("Error inesperado de ejecución, terminando aplicación.");
            Platform.exit();
        }
    }

    /**
     * Cargar archivos de puntuaciones.
     *
     * @param pathname Path del archivo de configuración a cargar.
     */
    public Configuracion(File pathname)
    {
        try(BufferedReader br = new BufferedReader(new FileReader(pathname)))
        {
            String line;
            int pos = 0;
            while((line = br.readLine()) != null)
            {
                String[] words = line.split(",");
                if(pos > 10 || words[0].equals("null"))
                    break;

                tiempo.add(pos, Long.valueOf(words[2]));
                listText.add(pos++, line);
            }
        }
        catch (FileNotFoundException e)
        {
            alertaUsuario("No se pudo cargar un archivo de configuración.");
        }
        catch (IOException e)
        {
            alertaUsuario("Error inesperado de ejecución, terminando aplicación.");
            Platform.exit();
        }
    }

    /**
     * Mensaje de alerta en la lectura del manual de usuario.
     *
     * @param mensaje Texto del mensaje.
     */
    private void alertaUsuario(String mensaje)
    {
        Alert temp = new Alert(Alert.AlertType.ERROR);
        temp.setTitle("Error encontrado");
        temp.setContentText(mensaje);
        temp.showAndWait();
    }

    /**
     * Getter del texto.
     *
     * @return Texto del manual.
     */
    public String getText()
    {
        return text;
    }

    /**
     * Tiempos de rankings leídos de archivos de configuración.
     *
     * @return Arreglo con tiempos de cada dificultad.
     */
    public Long[] getTiempo()
    {
        return tiempo.toArray(new Long[10]);
    }

    /**
     * Jugadores de rankings leídos de archivos de configuración.
     *
     * @return Arreglo con tiempos de cada dificultad.
     */
    public String[] getListText()
    {
        return listText.toArray(new String[10]);
    }
}
