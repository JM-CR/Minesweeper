package mx.ibero;

import javafx.scene.control.Button;

/**
 * Esta clase representa un lugar donde puede haber debajo de él una mina o no.
 *
 * @author Hector Jair Hernandez Cortes
 */
class Casilla extends Button
{
    /*
     * Es un tipo de boton propio para ponerle
     * un texto y las coordenadas.
     *
     * Si no lo hago de esta forma, no tengo acceso a las coordenadas en las
     * que está situado el botón.
     *
     */
    private int x;
    private int y;

    /**
     * Constructor que posiciona la casilla en una coordenada.
     *
     * @param x Coordenada en X.
     * @param y Coordenada en Y.
     * @param texto Texto de descripción.
     */
    public Casilla(int x, int y, String texto)
    {
        super(texto);
        this.x = x;
        this.y = y;

        // Adjudico una ID con las coordenadas a la casilla. Esto me será
        // útil para hacer referencia a dicha casilla en el futuro, aunque
        // aún no sé cómo, lo que hago de momento es meter cada objeto en
        // una matriz.
        this.setId("casilla_" + this.x + "_" + this.y);

        // Le adjudico el estilo llamado "casilla" de la plantilla CSS.
        this.getStyleClass().add("casilla");
    }

    /**
     * Devuelve la coordenada en X de la casilla.
     *
     * @return Coordenada en X.
     */
    public int getX()
    {
        return x;
    }

    /**
     * Devuelve la coordenada en Y de la casilla.
     *
     * @return Coordenada en Y.
     */
    public int getY()
    {
        return y;
    }
}
