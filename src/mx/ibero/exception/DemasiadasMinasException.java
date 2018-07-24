package mx.ibero.exception;

/**
 * Excepción que es lanzada si el usuario inserta un número de minas mayor
 * al total de casillas del tablero.
 *
 * @author Hector Jair Hernandez Cortes
 */
public class DemasiadasMinasException extends Exception
{
    /**
     * Invocar comportamiento heredado para ser lanzado.
     */
    public DemasiadasMinasException()
    {
        super();
    }

    /**
     * Lanza una excepción con un mensaje de texto.
     *
     * @param msg Mensaje del error.
     */
    public DemasiadasMinasException(String msg)
    {
        super(msg);
    }
}