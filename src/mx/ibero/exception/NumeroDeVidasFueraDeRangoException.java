package mx.ibero.exception;

/**
 * Excepción arrojada si el usuario introduce un número de vidas negativo
 * o mayor al número de minas.
 */
public class NumeroDeVidasFueraDeRangoException extends Exception
{
    /**
     * Lanzar excepción por default.
     */
    public NumeroDeVidasFueraDeRangoException()
    {
        super();
    }

    /**
     * Lanza la excepción con un mensaje de error.
     *
     * @param msg Mensaje del error.
     */
    public NumeroDeVidasFueraDeRangoException(String msg)
    {
        super(msg);
    }
}