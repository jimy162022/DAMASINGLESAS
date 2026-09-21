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


        // Negra llega abajo

        if (tablero[f][c] == 2
                && f == 7) {

            tablero[f][c] = 4;
        }
    }
