import javax.swing.*;
import java.awt.*;

public class Main extends JFrame {

// 0 es vacio
// 1 es roja
// 2 es negra
// 3  es la dama roja y 4 la dama negra

int[][] tablero = new int[8][8];
JButton[][] botones = new JButton[8][8];

JLabel estado = new JLabel("", SwingConstants.CENTER);

int turno = 1;

int filaSel = -1;
int colSel = -1;

boolean capturaMultiple = false;
boolean terminado = false;


public Main() {

    setTitle("Damas Inglesas");
    setSize(620, 680);

    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setLocationRelativeTo(null);

    setLayout(new BorderLayout());


    estado.setFont(
            new Font("Arial", Font.BOLD, 20)
    );

    add(estado, BorderLayout.NORTH);


    JPanel panel = new JPanel(
            new GridLayout(8, 8)
    );


    for (int f = 0; f < 8; f++) {

        for (int c = 0; c < 8; c++) {

            JButton boton = new JButton();

            boton.setFont(
                    new Font("Arial", Font.BOLD, 34)
            );

            boton.setFocusPainted(false);


            final int fila = f;
            final int columna = c;


            boton.addActionListener(
                    e -> clic(fila, columna)
            );


            botones[f][c] = boton;

            panel.add(boton);
        }
    }


    add(panel, BorderLayout.CENTER);


    JButton reiniciar = new JButton("Reiniciar");


    reiniciar.addActionListener(
            e -> iniciar()
    );


    add(reiniciar, BorderLayout.SOUTH);


    iniciar();

    setVisible(true);
}


    // fichas

    void iniciar() {

        for (int f = 0; f < 8; f++) {

            for (int c = 0; c < 8; c++) {

                tablero[f][c] = 0;


                if ((f + c) % 2 == 1) {

                    // negras arriba

                    if (f < 3) {

                        tablero[f][c] = 2;
                    }


                    // rojass abajo

                    if (f > 4) {

                        tablero[f][c] = 1;
                    }
                    }
              }
           }


        turno = 1;

        filaSel = -1;
        colSel = -1;

        capturaMultiple = false;

        terminado = false;


        dibujar();
    }

    // casilla

    void clic(int f, int c) {

        if (terminado) {

            return;
        }


        int pieza = tablero[f][c];


        // no  ficha seleccionada

        if (filaSel == -1) {

            seleccionar(f, c);
        }


        // cambiar la ficha seleccionada

        else if (!capturaMultiple && esMia(pieza)) {

            seleccionar(f, c);
        }


        //  mover a casilla vacia

        else if (pieza == 0) {

            mover(f, c);
        }
    }


    void seleccionar(int f, int c) {

        if (!esMia(tablero[f][c])) {

            return;
        }


        // la captura es obligatoria

        if (hayCaptura(turno)
                && !puedeCapturar(f, c)) {

            estado.setText(
                    "Hay una captura obligatoria"
            );

            return;
        }


        filaSel = f;
        colSel = c;


        dibujar();
    }


    void mover(int filaDestino,
               int colDestino) {


        int pieza =
                tablero[filaSel][colSel];


        int diferenciaFila =
                filaDestino - filaSel;


        int diferenciaCol =
                colDestino - colSel;


        boolean dama =
                esDama(pieza);


        int direccion;


        if (turno == 1) {

            direccion = -1;

        } else {

            direccion = 1;
        }


        // ----------------------
        // MOVIMIENTO NORMAL
        // ----------------------

        if (!capturaMultiple
                && !hayCaptura(turno)
                && Math.abs(diferenciaCol) == 1

                && (

                (dama
                        && Math.abs(diferenciaFila) == 1)

                        ||

                        (!dama
                                && diferenciaFila == direccion)

        )) {


            pasarPieza(
                    filaDestino,
                    colDestino
            );


            coronar(
                    filaDestino,
                    colDestino
            );


            cambiarTurno();

            return;
        }


        // ----------------------
        // CAPTURA
        // ----------------------

        if (Math.abs(diferenciaCol) == 2

                && (

                (dama
                        && Math.abs(diferenciaFila) == 2)

                        ||

                        (!dama
                                && diferenciaFila == direccion * 2)

        )) {


            int filaMedio =
                    (filaSel + filaDestino) / 2;


            int colMedio =
                    (colSel + colDestino) / 2;


            int rival =
                    tablero[filaMedio][colMedio];


            if (rival != 0
                    && !mismoJugador(
                    pieza,
                    rival
            )) {


                boolean eraDama = dama;


                pasarPieza(
                        filaDestino,
                        colDestino
                );


                // eliminar laa ficha capturada

                tablero[filaMedio][colMedio] = 0;


                coronar(
                        filaDestino,
                        colDestino
                );


                // si llega al final durante una captura
                // se convierte en dama y termina su turno

                if (!eraDama
                        && esDama(
                        tablero[filaDestino][colDestino]
                )) {


                    capturaMultiple = false;

                    cambiarTurno();

                    return;
                }


                filaSel = filaDestino;
                colSel = colDestino;


                // Revisar captura multiple

                if (puedeCapturar(
                        filaDestino,
                        colDestino
                )) {


                    capturaMultiple = true;


                    estado.setText(
                            "Debes seguir capturando"
                    );


                    dibujar();

                } else {


                    capturaMultiple = false;

                    cambiarTurno();
                }
            }
        }
    }


    void pasarPieza(int f, int c) {

        tablero[f][c] =
                tablero[filaSel][colSel];


        tablero[filaSel][colSel] = 0;
    }


    // convertir ficha normal en dama

    void coronar(int f, int c) {


        // Roja llega arriba

        if (tablero[f][c] == 1
                && f == 0) {

            tablero[f][c] = 3;
        }


        // negra llega abajo

        if (tablero[f][c] == 2
                && f == 7) {

            tablero[f][c] = 4;
        }
    }


    // se checa si una ficha puede capturar

    boolean puedeCapturar(int f, int c) {

        int pieza = tablero[f][c];


        if (pieza == 0) {

            return false;
        }


        int[][] direcciones;


        // Las damas pueden ir en ambos sentidos

        if (esDama(pieza)) {

            direcciones = new int[][]{

                    {-1, -1},
                    {-1, 1},

                    {1, -1},
                    {1, 1}
            };

        }

        // Roja va hacia arriba

        else if (pieza == 1) {

            direcciones = new int[][]{

                    {-1, -1},
                    {-1, 1}
            };

        }

        // negra va hacia abajo

        else {

            direcciones = new int[][]{

                    {1, -1},
                    {1, 1}
            };
        }


        for (int[] d : direcciones) {


            int filaMedio =
                    f + d[0];


            int colMedio =
                    c + d[1];


            int filaDestino =
                    f + d[0] * 2;


            int colDestino =
                    c + d[1] * 2;


            if (

                    dentro(
                            filaDestino,
                            colDestino
                    )

                            &&

                            dentro(
                                    filaMedio,
                                    colMedio
                            )

                            &&

                            tablero[filaDestino][colDestino] == 0

                            &&

                            tablero[filaMedio][colMedio] != 0

                            &&

                            !mismoJugador(
                                    pieza,
                                    tablero[filaMedio][colMedio]
                            )

            ) {

                return true;
            }
        }


        return false;
    }


    // checaar si alguna ficha tiene captura

    boolean hayCaptura(int jugador) {


        for (int f = 0; f < 8; f++) {

            for (int c = 0; c < 8; c++) {


                if (

                        esDelJugador(
                                jugador,
                                tablero[f][c]
                        )

                                &&

                                puedeCapturar(f, c)

                ) {

                    return true;
                }
            }
        }


        return false;
    }


    // revisar si un jugador puede seguir jugando

    boolean tieneMovimientos(int jugador) {


        if (hayCaptura(jugador)) {

            return true;
        }


        for (int f = 0; f < 8; f++) {

            for (int c = 0; c < 8; c++) {


                int pieza = tablero[f][c];


                if (!esDelJugador(
                        jugador,
                        pieza
                )) {

                    continue;
                }


                int[][] direcciones;


                if (esDama(pieza)) {

                    direcciones = new int[][]{

                            {-1, -1},
                            {-1, 1},

                            {1, -1},
                            {1, 1}
                    };

                }

                else if (pieza == 1) {

                    direcciones = new int[][]{

                            {-1, -1},
                            {-1, 1}
                    };

                }

                else {

                    direcciones = new int[][]{

                            {1, -1},
                            {1, 1}
                    };
                }


                for (int[] d : direcciones) {


                    int nuevaFila =
                            f + d[0];


                    int nuevaCol =
                            c + d[1];


                    if (

                            dentro(
                                    nuevaFila,
                                    nuevaCol
                            )

                                    &&

                                    tablero[nuevaFila][nuevaCol] == 0

                    ) {

                        return true;
                    }
                }
            }
        }


        return false;
    }


    // Cambiar de jugador

    void cambiarTurno() {


        int ganador = turno;


        if (turno == 1) {

            turno = 2;

        } else {

            turno = 1;
        }


        filaSel = -1;
        colSel = -1;


        // Si el rival no puede moverse, pierde

        if (!tieneMovimientos(turno)) {


            terminado = true;


            if (ganador == 1) {

                estado.setText(
                        "Ganaron las Rojas"
                );

            } else {

                estado.setText(
                        "Ganaron las Negras"
                );
            }
        }


        dibujar();
    }

    boolean esMia(int pieza) {

        return esDelJugador(
                turno,
                pieza
        );
    }


    boolean esDelJugador(
            int jugador,
            int pieza) {


        if (jugador == 1) {

            return pieza == 1
                    || pieza == 3;

        } else {

            return pieza == 2
                    || pieza == 4;
        }
    }


    boolean esDama(int pieza) {

        return pieza == 3
                || pieza == 4;
    }


    boolean mismoJugador(
            int pieza1,
            int pieza2) {


        boolean primeraRoja =
                pieza1 == 1
                        || pieza1 == 3;


        boolean segundaRoja =
                pieza2 == 1
                        || pieza2 == 3;


        return primeraRoja
                == segundaRoja;
    }


    boolean dentro(
            int f,
            int c) {


        return f >= 0
                && f < 8
                && c >= 0
                && c < 8;
    }


    // Mostrar tablero y fichas

    void dibujar() {


        for (int f = 0; f < 8; f++) {

            for (int c = 0; c < 8; c++) {


                JButton boton =
                        botones[f][c];


                boton.setText("");


                // Colores del tablero

                if ((f + c) % 2 == 0) {

                    boton.setBackground(
                            new Color(
                                    235,
                                    220,
                                    195
                            )
                    );

                } else {

                    boton.setBackground(
                            new Color(
                                    110,
                                    80,
                                    60
                            )
                    );
                }


                int pieza =
                        tablero[f][c];


                // Ficha roja

                if (pieza == 1) {

                    boton.setText("●");

                    boton.setForeground(
                            Color.RED
                    );
                }


                // Ficha negra

                if (pieza == 2) {

                    boton.setText("●");

                    boton.setForeground(
                            Color.BLACK
                    );
                }


                // Dama roja

                if (pieza == 3) {

                    boton.setText("R");

                    boton.setForeground(
                            Color.RED
                    );
                }


                // Dama negra

                if (pieza == 4) {

                    boton.setText("N");

                    boton.setForeground(
                            Color.BLACK
                    );
                }


                // Seleccionada

                if (f == filaSel
                        && c == colSel) {

                    boton.setBackground(
                            Color.YELLOW
                    );
                }
            }
        }


        if (!terminado
                && !capturaMultiple) {


            if (turno == 1) {

                estado.setText(
                        "Turno: Rojas"
                );

            } else {

                estado.setText(
                        "Turno: Negras"
                );
            }
        }
    }


    public static void main(String[] args) {

        SwingUtilities.invokeLater(
                Main::new
        );
    }
}