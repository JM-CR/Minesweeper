package mx.ibero;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * En esta clase se controla el arranque del programa.
 *
 * @author Hector Jair Hernandez Cortes
 * @author Josue Mosiah Contreras Rocha
 * @author Bruno Valerio Fernandez
 */
public class IniciarJuego extends Application
{
    /**
     * Método que carga JVM.
     *
     * @param args Argumentos de shell.
     */
    public static void main(String[] args)
    {
        launch(args);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void start(Stage primaryStage) throws Exception
    {
        // Inicializar Root Node desde FXML
        Parent root = FXMLLoader.load(getClass().getResource("ScreenGame.fxml"));
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/resources/estilos.css").toExternalForm());

        // Propiedades de primaryStage
        primaryStage.setTitle("Buscaminas");
        primaryStage.setScene(scene);
        primaryStage.getIcons().add(new Image("/resources/icon.png"));
        primaryStage.show();
    }
}