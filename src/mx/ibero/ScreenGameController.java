package mx.ibero;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import mx.ibero.exception.DemasiadasMinasException;
import mx.ibero.exception.NumeroDeVidasFueraDeRangoException;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Controlador de elementos UI del primaryStage.
 *
 * @author Hector Jair Hernandez Cortes
 * @author Josue Mosiah Contreras Rocha
 */
public class ScreenGameController implements Initializable
{
    // Atributos de Scene Builder
    @FXML private BorderPane borderPane;
    @FXML private GridPane terrenoMinas;
    @FXML private Text textoVidas;
    @FXML private Text totMinas;
    @FXML private Text totBanderas;

    // Atributos de instancia
    private Node[][] casillas;   // Referencias a los botones del tablero, con sus coordenadas (x,y)
    private Buscaminas juego;
    private Image imgBandera = new Image("/resources/bandera.png");
    private Image imgMina=new Image("/resources/mina1.jpg");
    private Configuracion guiaUsuario = new Configuracion("src/resources/manual.txt");  // Instrucciones para jugar al buscaminas
    private Configuracion acercaDe = new Configuracion("src/resources/about.txt");      // Información de desarrolladores
    private Ranking ranking = new Ranking();   // Para las puntuaciones

    /**
     * Método que preparará el juego antes de desplegarse en pantalla.
     *
     * @param location Path del controllador.
     * @param resources Elementos cargados de la carpeta resources.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        // Iniciar juego por default
        juegoNuevo();
    }

    /**
     * Iniciar con la creación del objeto Buscaminas.
     */
    private void juegoNuevo()
    {
        juego = new Buscaminas();
        construirTablero();
        ranking.startTimer();
    }

    /**
     * Iniciar con la creación del objeto Buscaminas.
     *
     * @param ancho Ancho del tablero.
     * @param alto Alto del tablero.
     * @param totalMinas Total de minas que hay en el tablero.
     * @param vidas Total de vidas en la partida.
     */
    private void juegoNuevo(int ancho, int alto, int totalMinas, int vidas)
    {
        try
        {
            juego = new Buscaminas(ancho, alto, totalMinas, vidas);
        }
        catch (DemasiadasMinasException e)
        {
            System.out.println("Has puesto demasiadas minas");
        }
        catch (NumeroDeVidasFueraDeRangoException e)
        {
            System.out.println("El número de vidas es incorrecto");
        }

        construirTablero();
        ranking.startTimer();
    }

    /**
     * Creación del tablero para el juego.
     */
    private void construirTablero()
    {
        // Primero limpiamos lo que hay dentro
        casillas = new Node[juego.getAlto()][juego.getAncho()];
        terrenoMinas.getChildren().clear();

        // Tamaño dado por la imagen en cada casilla
        terrenoMinas.setPrefHeight(35*juego.getAlto());
        terrenoMinas.setPrefWidth(35*juego.getAncho());

        /*
         * Generamos las casillas en función de la configuración que haya en el
         * objeto "juego" (de tipo Buscaminas).
         */
        for (int y = 0; y < juego.getAlto(); y++)
            for (int x = 0; x < juego.getAncho(); x++)
            {
                // Creo una casilla personalizada con las coordenadas y las imágenes
                Casilla casilla = new Casilla(x, y, " ");

                // Le doy una acción al ser presionado.
                casilla.setOnMouseClicked(p ->
                {
                    // Si el jugador pulsa el botón izquierdo del ratón, cavo la casilla,
                    // pero si pulsa el derecho, pongo una bandera.

                    if (p.getButton() == MouseButton.PRIMARY)  // CAVO
                        juego.cavar(casilla.getX(), casilla.getY());
                    else if (p.getButton() == MouseButton.SECONDARY)  // BANDERA
                        juego.marcarBandera(casilla.getX(), casilla.getY());

                    actualizarTablero();
                });

                // Añadir casilla al Grid
                terrenoMinas.add(casilla, x, y);

                /*
                 * Cada casilla se mete en un array de tipo Nodo que
                 * contiene los elementos a los que deseo no perder referencia.
                 * De esta forma, tengo en una matriz del tamaño del tablero las
                 * referencias a los objetos Casilla cuando necesite recorrerlas
                 * en base a su posición (coordenadas x y).
                 */
               casillas[y][x] = casilla;

               // Agregar su respectivo tooltip
               Tooltip tooltip = new Tooltip();
               tooltip.setText("Bandera = Clic derecho\nCavar = Clic izquierdo");
               tooltip.setShowDuration(new Duration(5D*1000));
               tooltip.setShowDelay(new Duration(4D*1000));
               casilla.setTooltip(tooltip);
            }

        totBanderas.setText("00");

        if(juego.getTotalMinas() > 9)
            totMinas.setText(String.valueOf(juego.getTotalMinas()));
        else
            totMinas.setText("0"+ String.valueOf(juego.getTotalMinas()));

        if(juego.getVidas() == 1)
            textoVidas.setText("Solo tienes 1 vida.");

        if (juego.getVidasRestantes() > 1)
            textoVidas.setText("Vidas restantes: " + Integer.toString(juego.getVidasRestantes()));
    }

    /**
     * Este método debe actualizar el estado de cada casilla en el tablero.
     * Se le debe llamar cada vez que se realice alguna opción.
     */
    private void actualizarTablero()
    {
        int banderas = 0;
        for (int y = 0; y < juego.getAlto(); y++)
            for (int x = 0; x < juego.getAncho(); x++)
            {
                Button cuadro = (Button) casillas[y][x];

                // Si está destapada
                if (juego.estaDestapada(x, y))
                {
                    cuadro.setDisable(true);
                    // Si hay mina
                    if (juego.hayMina(x, y))
                    {
                        cuadro.getStyleClass().add("casillaMina");
                        ImageView imgMina2 = new ImageView(imgMina);
                        imgMina2.setFitWidth(25);
                        imgMina2.setFitHeight(25);
                        cuadro.setGraphic(imgMina2);
                    }
                    else
                    {
                        // Si tiene minas alrededor, le ponemos el número.
                        if (juego.hayMinasAlrededor(x, y) && juego.contarMinasAlrededor(x, y) > 0)
                                cuadro.setText(Integer.toString(juego.contarMinasAlrededor(x, y)));
                                cuadro.getStyleClass().add("casillaNum");
                    }
                }
                else
                {
                    if (juego.tieneBandera(x, y))
                    {
                        ImageView imgBandera2 = new ImageView(imgBandera);
                        imgBandera2.setFitWidth(20);
                        imgBandera2.setFitHeight(30);

                        cuadro.setGraphic(imgBandera2);
                        banderas++;
                    }
                    else
                        cuadro.setGraphic(null);
                }
            }

        // Imprimir total de banderas en el tablero
        if(banderas < 10)
            totBanderas.setText("0"+ banderas);
        else
            totBanderas.setText(""+ banderas);

        // Verificar si se ha perdido
        if (juego.isGameOver())
        {
            if (juego.isGanador())
            {
                alerta("¡Has ganado!");
                textoVidas.setText("Has ganado.");

                // Detener timer de la partida
                if(ranking.isTimerActivado())
                {
                    ranking.stopTimer();

                    if(ranking.consultaRecord())  // Verificar si rompió algún record
                        ranking.nuevoRecord();
                }
            }
            else
            {
                alerta(Alert.AlertType.WARNING, "Has perdido");
                textoVidas.setText("Te has quedado sin vidas.");

                if(ranking.isTimerActivado())
                    ranking.stopTimer();
            }

            return;  // Si accede a esta parte no hace falta imprimir vidas restantes
        }

        // Imprimir total de vidas restantes
        if(juego.getVidas() == 1)
            textoVidas.setText("Solo tienes 1 vida.");
        else if(juego.getVidasRestantes() > 1)
            textoVidas.setText("Vidas restantes: " + Integer.toString(juego.getVidasRestantes()));
    }

    /**
     * Este método permite crear una partida personalizada.
     */
    private void juegoPersonalizado()
    {
        Dialog<ButtonType> personal = new Dialog<>();
        personal.setTitle("Juego personalizado.");

        terrenoMinas.setPrefHeight(35*juego.getAlto());
        terrenoMinas.setPrefWidth(35*juego.getAncho());

        GridPane formulario = new GridPane();
        formulario.setId("content-personalizado");

        Label paraAltura = new Label("Alto: ");
        Label paraAnchura = new Label("Ancho: ");
        Label paraMinas = new Label("Número de minas: ");
        Label paraVidas = new Label("Número de vidas: ");

        paraAltura.getStyleClass().add("text-personalizado");
        paraAnchura.getStyleClass().add("text-personalizado");
        paraMinas.getStyleClass().add("text-personalizado");
        paraVidas.getStyleClass().add("text-personalizado");

        TextField inputAltura = new TextField();
        TextField inputAnchura = new TextField();
        TextField inputMinas = new TextField();
        TextField inputVidas = new TextField();

        inputAltura.getStyleClass().add("input-personalizado");
        inputAnchura.getStyleClass().add("input-personalizado");
        inputMinas.getStyleClass().add("input-personalizado");
        inputVidas.getStyleClass().add("input-personalizado");

        inputAltura.setText(Integer.toString(juego.getAlto()));
        inputAnchura.setText(Integer.toString(juego.getAncho()));
        inputMinas.setText(Integer.toString(juego.getTotalMinas()));
        inputVidas.setText(Integer.toString(juego.getVidas()));

        formulario.add(paraAltura, 0, 0);
        formulario.add(paraAnchura, 0, 1);
        formulario.add(paraMinas, 0, 2);
        formulario.add(paraVidas, 0, 3);

        formulario.add(inputAltura, 1, 0);
        formulario.add(inputAnchura, 1, 1);
        formulario.add(inputMinas, 1, 2);
        formulario.add(inputVidas, 1, 3);

        personal.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.OK);

        personal.getDialogPane().setContent(formulario);

        while (true)
        {
            Optional<ButtonType> resultado = personal.showAndWait();

            if (resultado.get() == ButtonType.OK)
            {
                try
                {
                    if (juego.getTotalMinas() > juego.getAncho() * juego.getAlto())
                        alerta("No puedes poner más minas que casillas");
                    else if (juego.getVidas() < 1 || juego.getVidas() > juego.getTotalMinas())
                        alerta("El número de vidas tiene que ser un valor entre 1 y el número de minas");
                    else
                    {
                        int alto = Integer.valueOf(inputAltura.getText());
                        int ancho = Integer.valueOf(inputAnchura.getText());
                        int totalMinas = Integer.valueOf(inputMinas.getText());
                        int vidas = Integer.valueOf(inputVidas.getText());

                        juegoNuevo(ancho, alto, totalMinas, vidas);
                        break;
                    }
                }
                catch (NumberFormatException e)
                {
                    alerta("Introduce correctamente números enteros en cada casilla.");
                }

            }
            else if (resultado.get() == ButtonType.CANCEL)
                break;
        }
    }

    /**
     * Mensaje de alerta en la partida
     *
     * @param tipo Tipo de alerta.
     * @param mensaje Texto del mensaje.
     */
    private void alerta(Alert.AlertType tipo, String mensaje)
    {
        Alert temp = new Alert(tipo);
        temp.setTitle("Resumen de partida.");
        temp.setHeaderText("Buscaminas");
        temp.setContentText(mensaje);
        temp.showAndWait();
    }

    /**
     * Mensaje informativo de la partida.
     *
     * @param mensaje Texto del mensaje.
     */
    private void alerta(String mensaje)
    {
        alerta(Alert.AlertType.INFORMATION, mensaje);
    }

    /**
     * Construye un formulario con cada puntuación del top 10 en la dificultad solicitada.
     *
     * @param dificultad Dificultad a mostrar.
     * @return Devuelve un {@link GridPane} con las puntuaciones.
     */
    private GridPane crearPuntuaciones(int dificultad)
    {
        GridPane form = new GridPane();
        form.setVgap(10.0);
        form.setHgap(10.0);
        form.setAlignment(Pos.TOP_CENTER);
        form.setId("content-puntuacion");

        // Headers
        form.add(new Text("Top"),0,0);
        form.add(new Text("Jugador"), 1,0);
        form.add(new Text("Fecha"), 2,0);
        form.add(new Text("Tiempo(seg)"), 3,0);

        // Contenido
        String[] tempPuntuacion = ranking.puntuaciones(dificultad);
        int row = 1;

        for(String line: tempPuntuacion)
        {
            if(line == null || line.equals("null"))
                break;

            String[] words = line.split(",");

            Text t1 = new Text(words[0]);
            Text t2 = new Text(words[1]);
            Text t3 = new Text(words[2]);
            t1.getStyleClass().addAll("text-puntuacion");
            t2.getStyleClass().addAll("text-puntuacion");
            t3.getStyleClass().addAll("text-puntuacion");

            form.add(new Text(""+ row), 0,row);
            form.add(t1, 1, row);
            form.add(t2, 2, row);
            form.add(t3, 3, row++);
        }

        return form;
    }

    // ----------------------------------------------------------------------------------
    //                           EventHandlers de Scene Builder
    // ----------------------------------------------------------------------------------

    /**
     * EventHandler para el RadioMenuItem de dificultad personalizada.
     */
    @FXML
    public void radioMenuItemPersonalizada()
    {
        juegoPersonalizado();
        ranking.setDificultad(3);

        Stage primaryStage  = (Stage) borderPane.getScene().getWindow();
        primaryStage.sizeToScene();
        primaryStage.centerOnScreen();
    }

    /**
     * EventHandler para el RadioMenuItem de dificultad dificil.
     */
    @FXML
    public void radioMenuItemDificil()
    {
        juegoNuevo(30,16,99,1);
        ranking.setDificultad(2);

        Stage primaryStage  = (Stage) borderPane.getScene().getWindow();
        primaryStage.sizeToScene();
        primaryStage.centerOnScreen();
    }

    /**
     * EventHandler para el RadioMenuItem de dificultad intermedia.
     */
    @FXML
    public void radioMenuItemIntermedio()
    {
        juegoNuevo(16,16,40,1);
        ranking.setDificultad(1);

        Stage primaryStage  = (Stage) borderPane.getScene().getWindow();
        primaryStage.sizeToScene();
        primaryStage.centerOnScreen();
    }

    /**
     * EventHandler para el RadioMenuItem de dificultad fácil.
     */
    @FXML
    public void radioMenuItemFacil()
    {
        juegoNuevo(8,8,10,1);
        ranking.setDificultad(0);

        Stage primaryStage  = (Stage) borderPane.getScene().getWindow();
        primaryStage.sizeToScene();
        primaryStage.centerOnScreen();
    }

    /**
     * Crear partida con cinco vidas en la dificultad actual.
     */
    @FXML
    public void radioItemVidasMultiples()
    {
        int ancho = juego.getAncho();
        int alto = juego.getAlto();
        int totalMinas = juego.getTotalMinas();

        juegoNuevo(ancho,alto,totalMinas,5);

        Stage primaryStage  = (Stage) borderPane.getScene().getWindow();
        primaryStage.sizeToScene();
        primaryStage.centerOnScreen();
    }

    /**
     * Crear partida con una vida en la dificultad actual.
     */
    public void radioItemTradicional()
    {
        int ancho = juego.getAncho();
        int alto = juego.getAlto();
        int totalMinas = juego.getTotalMinas();

        juegoNuevo(ancho,alto,totalMinas,1);

        Stage primaryStage  = (Stage) borderPane.getScene().getWindow();
        primaryStage.sizeToScene();
        primaryStage.centerOnScreen();
    }

    /**
     * EventHandler para el MenuItem de reiniciar partida.
     */
    @FXML
    public void menuItemReiniciarPartida()
    {
        juego.reset();
        construirTablero();
    }

    /**
     * EventHandler para el MenuItem de reiniciar partida.
     */
    @FXML
    public void menuItemNuevaPartida()
    {
        juegoNuevo(juego.getAncho(), juego.getAlto(), juego.getTotalMinas(), juego.getVidas());
    }

    /**
     * EventHandler para el MenuItem de salir.
     */
    @FXML
    public void menuItemSalir()
    {
        Platform.exit();
    }

    /**
     * Genera {@link Dialog} con el manual de instrucciones para jugar.
     */
    @FXML
    public void menuItemComoJugar()
    {
        Dialog<ButtonType> guia = new Dialog<>();
        guia.setTitle("Manual de usuario.");
        guia.setHeaderText("¿Listo para jugar al buscaminas como un experto? ¡Suerte!");

        // Preparar contenido
        TextArea manual = new TextArea();
        manual.setWrapText(true);
        manual.setEditable(false);
        manual.setText(guiaUsuario.getText());
        manual.setId("content-como-jugar");

        // Prepara y mostrar Dialog
        guia.getDialogPane().getButtonTypes().add(ButtonType.OK);
        guia.getDialogPane().setContent(manual);
        guia.showAndWait();
    }

    /**
     * Genera {@link Dialog} con información de los desarrolladores.
     */
    @FXML
    public void menuItemAcercaDe()
    {
        Dialog<ButtonType> about = new Dialog<>();
        about.setTitle("Equipo de desarrollo");

        // Preparar contenido
        TextArea team = new TextArea();
        team.setEditable(false);
        team.setWrapText(true);
        team.setText(acercaDe.getText());
        team.setId("content-acerca-de");

        // Prepara y mostrar Dialog
        about.getDialogPane().getButtonTypes().add(ButtonType.OK);
        about.getDialogPane().setContent(team);
        about.showAndWait();
    }

    /**
     * Genera {@link Dialog} con los rankings en fácil.
     */
    @FXML
    public void puntuacionFacil()
    {
        Dialog<ButtonType> facil = new Dialog<>();
        facil.setTitle("Dificultad fácil");

        // Preparar contenido
        GridPane gridPane = crearPuntuaciones(0);

        // Prepara y mostrar Dialog
        facil.getDialogPane().getButtonTypes().add(ButtonType.OK);
        facil.getDialogPane().setContent(gridPane);
        facil.showAndWait();
    }

    /**
     * Genera {@link Dialog} con los rankings en intermedio.
     */
    @FXML
    public void puntuacionIntermedio()
    {
        Dialog<ButtonType> intermedio = new Dialog<>();
        intermedio.setTitle("Dificultad intermedia");

        // Preparar contenido
        GridPane gridPane = crearPuntuaciones(1);

        // Prepara y mostrar Dialog
        intermedio.getDialogPane().getButtonTypes().add(ButtonType.OK);
        intermedio.getDialogPane().setContent(gridPane);
        intermedio.showAndWait();
    }

    /**
     * Genera {@link Dialog} con los rankings en dificil.
     */
    @FXML
    public void puntuacionDificil()
    {
        Dialog<ButtonType> dificil = new Dialog<>();
        dificil.setTitle("Dificultad difícil");

        // Preparar contenido
        GridPane gridPane = crearPuntuaciones(2);

        // Prepara y mostrar Dialog
        dificil.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dificil.getDialogPane().setContent(gridPane);
        dificil.showAndWait();
    }

    /**
     * Genera {@link Dialog} con los rankings en personalizada.
     */
    @FXML
    public void puntuacionPersonalizada()
    {
        Dialog<ButtonType> personalizada = new Dialog<>();
        personalizada.setTitle("Dificultad personalizada");

        // Preparar contenido
        GridPane gridPane = crearPuntuaciones(3);

        // Prepara y mostrar Dialog
        personalizada.getDialogPane().getButtonTypes().add(ButtonType.OK);
        personalizada.getDialogPane().setContent(gridPane);
        personalizada.showAndWait();
    }
}
