package mx.ibero;

import mx.ibero.exception.DemasiadasMinasException;
import mx.ibero.exception.NumeroDeVidasFueraDeRangoException;

/**
 * Clase que simula al juego de buscaminas.
 *
 * @author Hector Jair Hernandez Cortes
 * @author Josue Mosiah Contreras Rocha
 */
public class Buscaminas
{
    private boolean[][] tablero_minas;
    private int[][] minas_alrededor;
    private boolean[][] destapadas;
    private boolean[][] banderas;

    // Establecer valores por default para partida tradicional en modo fácil
    private int vidas = 1;
    private int totalMinas = 10;
    private int ancho = 8;
    private int alto = 8;
    private boolean soportaBanderas = true;

    private boolean gameOver;
    private boolean ganador;
    private int vidasRestantes = vidas;
    private int casillasTapadasRestantes;
    private int minasSinBanderaRestantes;

    /**
     * Constructor principal. Crea una instancia de un tablero de juego con valores por defecto.
     */
    public Buscaminas()
    {
        // Inicializar arreglos
        tablero_minas = new boolean[alto][ancho];
        minas_alrededor = new int[alto][ancho];
        destapadas = new boolean[alto][ancho];
        banderas = new boolean[alto][ancho];

        casillasTapadasRestantes = ancho * alto - totalMinas;
        minasSinBanderaRestantes = totalMinas;

        // Ponemos minas alrededor
        ponerMinas(totalMinas);

        // Generamos la matriz de pistas
        minasAlrededor();
    }

    /**
     * Constructor principal. Crea una instancia de un tablero de juego.
     *
     * @param ancho
     *            Ancho del tablero (Número de columnas).
     * @param alto
     *            Alto del tablero (Número de filas).
     * @param minas
     *            Número de minas
     * @param vidas
     *            Número de errores permitidos.
     * @throws DemasiadasMinasException
     *             Esta excepción debe ser capturada para saber si ha sido
     *             posible construir el tablero dado su tamaño y número de
     *             minas. No se puede crear un tablero con más minas que
     *             casillas disponibles. En caso de intentar crearlo, el objeto
     *             no se creará, y lanzará esta excepción.
     * @throws NumeroDeVidasFueraDeRangoException
     *             Esta excepción se lanza cuando trates de crear un nuevo juego
     *             con un número de vidas superior o inferior al número de
     *             minas.
     */
    public Buscaminas(int ancho, int alto, int minas, int vidas) throws DemasiadasMinasException, NumeroDeVidasFueraDeRangoException
    {
        if (minas > ancho * alto)
            throw new DemasiadasMinasException();
        if (vidas > minas || vidas < 1)
            throw new NumeroDeVidasFueraDeRangoException();

        // Inicializar arreglos
        tablero_minas = new boolean[alto][ancho];
        minas_alrededor = new int[alto][ancho];
        destapadas = new boolean[alto][ancho];
        banderas = new boolean[alto][ancho];

        // Asignar valores
        this.vidas = vidas;
        vidasRestantes = vidas;
        totalMinas = minas;
        this.ancho = ancho;
        this.alto = alto;

        casillasTapadasRestantes = ancho * alto - totalMinas;
        minasSinBanderaRestantes = minas;

        // Ponemos minas alrededor.
        ponerMinas(minas);

        // Generamos la matriz de pistas.
        minasAlrededor();
    }

    /**
     * Este constructor crea un juego de Buscaminas sin necesidad de especificar
     * el número de vidas. El número de vidas será el mismo número que el de
     * minas. Por tanto, el usuario tendrá tantas oportunidades como minas
     * halla.
     *
     * @param x
     *            Número de columnas del tablero.
     * @param y
     *            Número de filas del tablero.
     * @param minas
     *            Número de minas que tendrá el tablero.
     * @throws DemasiadasMinasException
     *             Esta excepción debe ser capturada para saber si ha sido
     *             posible construir el tablero dado su tamaño y número de
     *             minas. No se puede crear un tablero con más minas que
     *             casillas disponibles. En caso de intentar crearlo, el objeto
     *             no se creará, y lanzará esta excepción.
     * @throws NumeroDeVidasFueraDeRangoException
     *             Esta excepción se lanza cuando trates de crear un nuevo juego
     *             con un número de vidas superior o inferior al número de
     *             minas.
     */
    public Buscaminas(int x, int y, int minas) throws DemasiadasMinasException, NumeroDeVidasFueraDeRangoException
    {
        this(x, y, minas, minas);
    }

    /**
     * Si se ha configurado para que el juego soporte banderas, se ejecutará
     * el método. De lo contrario, no debe hacer nada.
     *
     * @param x Coordenada en X.
     * @param y Coordenada en Y.
     */
    public void marcarBandera(int x, int y)
    {
        if (soportaBanderas && !gameOver)
        {
            banderas[y][x] = !banderas[y][x];

            // Al poner una bandera, calculo si es correcta o no para saber si
            // el juego ha terminado.

            if (hayMina(x, y) && tieneBandera(x, y))
                minasSinBanderaRestantes--;
            else if (hayMina(x, y) && !tieneBandera(x, y))
                minasSinBanderaRestantes++;
            else if (!hayMina(x, y) && tieneBandera(x, y))
                minasSinBanderaRestantes++;
            else if (!hayMina(x, y) && !tieneBandera(x, y))
                minasSinBanderaRestantes--;

            if (minasSinBanderaRestantes == 0 && casillasTapadasRestantes == 0)
            {
                gameOver = true;
                ganador = true;
            }
        }
    }

    /**
     * Te dice si la casilla especificada tiene o no una bandera puesta.
     * Recuerda que si una casilla tiene bandera, el método cavar() no tendrá
     * efecto.
     *
     * @param x
     *            Columna
     * @param y
     *            Fila
     * @return true = tiene bandera, false = no tiene bandera.
     */
    public boolean tieneBandera(int x, int y)
    {
        return banderas[y][x];
    }

    /**
     * Da información sobre el estado actual del juego.
     *
     * @return false si el juego prosigue. true si el juego ha terminado.
     */
    public boolean isGameOver()
    {
        return gameOver;
    }

    /**
     * Cava en la casilla seleccionada.
     *
     * Si la casilla tiene una mina, el número de vidas decrementa.
     *
     * Si se han perdido todas las vidas, el juego termina. Consultar
     * isGameOver()
     *
     * Si la casilla está despejada, se despeja junto a las adyacentes.
     *
     * @param x Columna
     * @param y Fila
     */
    public void cavar(int x, int y)
    {
        // Si el juego ha terminado, o si la casilla está protegida por una
        // bandera, no ejecutaremos ninguna acción. Si la casilla tiene bandera
        // y el usuario quiere cavar en ella, tendrá que retirar la bandera
        // previamente.
        if (gameOver || tieneBandera(x, y))
            return;

        // Si hay mina
        if (hayMina(x, y))
        {
            if (soportaBanderas)
                minasSinBanderaRestantes--;

            // Si quedan vidas
            if (vidasRestantes > 0)
                vidasRestantes--;
            else
            {
                // Acaba el juego
                gameOver = true;
                ganador = false;
            }
        }

        destapar(x, y);

        if (vidasRestantes == 0)
            gameOver = true;
    }

    /**
     * Este método es privado, porque ni el usuario ni la interfaz tienen por
     * qué conocerlo ni utilizarlo. Sirve de forma interna para situar las minas
     * al azar por el tablero una vez que se ha creado.
     *
     * @param totalMinas Minas a poner
     */
    private void ponerMinas(int totalMinas)
    {
        int x, y;

        while (totalMinas > 0)
        {
            x = (int) (Math.random() * ancho);
            y = (int) (Math.random() * alto);

            if (!tablero_minas[y][x])
            {
                tablero_minas[y][x] = true;
                totalMinas--;
            }
        }
    }

    /**
     * Este método es privado. Sirve para destapar una casilla y sus adyacentes
     * de modo recursivo. El usuario o jugador debe destapar las casillas
     * utilizando el método cavar()
     *
     * @param x Columna
     * @param y Fila
     */
    private void destapar(int x, int y)
    {
        // En este punto, a parte de destapar la casilla, decidimos si el juego
        // ha finalizado.

        // El juego finaliza si el jugador ha destapado todas las casillas en
        // las que NO hay minas sin haber consumido el número de intentos
        // fallidos.

        // En caso de haber habilitado la función de banderas, se tendrá en
        // cuenta que además hayan banderas en todas las casillas tapadas que
        // tengan mina para que finalice el juego.

        if (!destapadas[y][x] && !banderas[y][x])
        {
            destapadas[y][x] = true;

            if (!hayMina(x, y))
                casillasTapadasRestantes--;

            if (soportaBanderas)
            {
                if (casillasTapadasRestantes == 0 && minasSinBanderaRestantes == 0)
                    gameOver = true;
            }
            else
            {
                if (casillasTapadasRestantes == 0)
                    gameOver = true;
            }

            if (gameOver && vidasRestantes > 0)
                ganador = true;

            if (!hayMinasAlrededor(x, y))
            {
                // Compruebo los lados
                // Compruebo arriba y abajo
                if (y > 0 && !estaDestapada(x, y - 1))
                    destapar(x, y - 1);
                if (y < alto && !estaDestapada(x, y + 1))
                    destapar(x, y + 1);

                // Compruebo a la derecha e izquierda
                if (x > 0 && !estaDestapada(x - 1, y))
                    destapar(x - 1, y);
                if (x < ancho && !estaDestapada(x + 1, y))
                    destapar(x + 1, y);

                // Compruebo en las diagonales
                // Diagonal superior izquierda
                if (x > 0 && y > 0 && !estaDestapada(x - 1, y - 1))
                    destapar(x - 1, y - 1);

                // Diagonal superior derecha
                if (x < ancho && y > 0 && !estaDestapada(x + 1, y - 1))
                    destapar(x + 1, y - 1);

                // Diagonal inferior izquierda
                if (x > 0 && y < alto && !estaDestapada(x - 1, y + 1))
                    destapar(x - 1, y + 1);

                // Diagonal inferior derecha
                if (x < ancho && y < alto && !estaDestapada(x + 1, y + 1))
                    destapar(x + 1, y + 1);
            }
        }
    }

    /**
     * Este método es privado y para uso interno del núcleo del juego.
     *
     * Básicamente, este método crea una matriz que representa el mapa de minas
     * detectadas alrededor de cada casilla.
     *
     * Este método se ejecuta al inicializar una instancia del juego, y nos
     * ahorramos tener que volver a calcularlo cada vez que el jugador haga clic
     * en una casilla.
     */
    private void minasAlrededor()
    {
        int[] lasI = { -1, -1, 0, 1, 0, 1, 1, -1, 0 };
        int[] lasJ = { -1, 0, -1, 1, 1, 0, -1, 1, 0 };

        for (int i = 0; i < alto; i++)
            for (int j = 0; j < ancho; j++)
                for (int k = 0; k < 9; k++)
                {
                    // Este try-catch evita que cuente las minas alrededor en
                    // aquellas casillas fuera del rango.
                    try
                    {
                        if (tablero_minas[i + lasI[k]][j + lasJ[k]])
                            minas_alrededor[i][j]++;
                    }
                    catch (ArrayIndexOutOfBoundsException e)
                    {
                        // Si da error, no necesito hacer nada, simplemente que
                        // continúe el bucle.
                    }
                }
    }

    /**
     * Este método nos dice si una casilla determinada tiene alguna minas
     * alrededor en sus casillas adyacentes.
     *
     * @param x La casilla X (horizontal, columna).
     * @param y La casilla Y (vertical, fila).
     * @return true si hay al menos una mina, false si está despejado.
     */
    public boolean hayMinasAlrededor(int x, int y)
    {
        return minas_alrededor[y][x] > 0;
    }

    /**
     * Este método nos dice CUÁNTAS minas hay alrededor de una casilla (sin
     * importar si la misma casilla es una mina). Ten en cuenta que si la misma
     * casilla x,y tiene una mina, el resultado va a ser siempre al menos 1. Si
     * el resultado es 0, será evidente que ni tiene minas alrededor, ni la
     * propia casilla es una mina.
     *
     * @param x La casilla X (horizontal, columna).
     * @param y La casilla Y (vertical, fila).
     * @return El número de minas alrededor.
     */
    public int contarMinasAlrededor(int x, int y)
    {
        return minas_alrededor[y][x];
    }

    /**
     * Nos dice si la casilla ya ha sido destapada. Si está destapada habrá que
     * mostrar el número de minas alrededor.
     *
     * @param x
     *            La casilla X (horizontal, columna).
     * @param y
     *            La casilla Y (vertical, fila).
     * @return true si está destapada, false si aún no se ha destapado.
     */
    public boolean estaDestapada(int x, int y)
    {
        if (x < 0 || x >= destapadas[0].length)
            return true;
        if (y < 0 || y >= destapadas.length)
            return true;

        return destapadas[y][x];
    }

    /**
     * Nos dice si la casilla dada tiene o no una mina.
     *
     * @param x La casilla X (horizontal, columna).
     * @param y La casilla Y (vertical, fila).
     * @return true si hay una mina, false si la casilla está libre de mina.
     */
    public boolean hayMina(int x, int y)
    {
        if (x < 0 || x >= tablero_minas[0].length)
            return true;
        if (y < 0 || y >= tablero_minas.length)
            return true;

        return tablero_minas[y][x];
    }

    /**
     * Devuelve una matriz que representa el mapa de minas. Las celdas con valor
     * 1 tienen mina, y las que tienen valor 0 están vacías.
     *
     * @return Referencia la mapa de minas.
     */
    public boolean[][] mapaDeMinas()
    {
        return tablero_minas;
    }

    /**
     * Devuelve una matriz que representa las minas al alrededor.
     *
     * @return Referencia de mapa de minas alrededor.
     */
    public int[][] mapaMinasAlrededor()
    {
        return minas_alrededor;
    }

    /**
     * Resetea el juego actual sin cambiar la posición de las minas ni el tamaño
     * del tablero. Es decir, como volver a empezar la misma partida.
     */
    public void reset()
    {
        vidasRestantes = vidas;

        gameOver = false;
        ganador = false;

        destapadas = new boolean[alto][ancho];
        banderas = new boolean[alto][ancho];

        casillasTapadasRestantes = ancho * alto - totalMinas;
        minasSinBanderaRestantes = totalMinas;
    }

    /**
     * Devuelve el número de vidas con el cual se ha configurado la partida.
     *
     * @return Número de vidas.
     */
    public int getVidas()
    {
        return vidas;
    }

    /**
     * Devuelve el número de vidas restante según el estado actual del juego.
     *
     * @return Vidas restantes.
     */
    public int getVidasRestantes()
    {
        return vidasRestantes;
    }

    /**
     * Determina si el juego está ganado o perdido.
     *
     * @return True si es ganandor, false en caso contrario.
     */
    public boolean isGanador()
    {
        return ganador;
    }

    /**
     * Determina si el juego está configurado para soportar banderas.
     *
     * @return True si las banderas están activadas
     */
    public boolean hayBanderas()
    {
        return soportaBanderas;
    }

    /**
     * Setter del atributo soportaBanderas.
     *
     * @param soportaBanderas Activado o desactivada la funcionalidad.
     */
    public void setSoportaBanderas(boolean soportaBanderas)
    {
        this.soportaBanderas = soportaBanderas;
    }

    /**
     * Nos dice el tamaño actual del tablero. Concretamente, el ancho, width,
     * "x" o columnas.
     *
     * @return Ancho o width.
     */
    public int getAncho()
    {
        return ancho;
    }

    /**
     * Nos dice el tamaño actual del tablero. Concretamente, el alto, height,
     * "y" o filas.
     *
     * @return Alto o height
     */
    public int getAlto()
    {
        return alto;
    }

    /**
     * Nos dice el total de minas en el tablero.
     *
     * @return Total de minas en el tablero.
     */
    public int getTotalMinas()
    {
        return totalMinas;
    }

    /**
     * Establece el total del minas.
     *
     * @param totalMinas Total de minas a agregar.
     */
    public void setTotalMinas(int totalMinas) {
        this.totalMinas = totalMinas;
    }

    /**
     * Establece el número de casillas de alto para el tablero.
     *
     * @param alto Número de casillas.
     */
    public void setAlto(int alto)
    {
        this.alto = alto;
    }

    /**
     * Establece el número de casillas de ancho para el tablero.
     *
     * @param ancho Número de casillas.
     */
    public void setAncho(int ancho)
    {
        this.ancho = ancho;
    }

    /**
     * Establece el número de vidas iniciales de la partida.
     *
     * @param vidas Número de vidas en la partida.
     */
    public void setVidas(int vidas)
    {
        this.vidas = vidas;
    }
}