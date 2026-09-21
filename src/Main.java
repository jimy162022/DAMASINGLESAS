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
