package com.hugofont.ejemplos.Hilos_PSP.tema02;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class Ejercicio09 {

    static class Ansi {
        public static final String RESET = "\u001B[0m";
        public static final String CYAN_BG = "\u001B[46m";
        public static final String PURPLE_BG = "\u001B[45m";
        public static final String YELLOW_BG = "\u001B[43m";
        public static final String RED_BG = "\u001B[41m";
        public static final String BLUE_BG = "\u001B[44m";
        public static final String GREEN_BG = "\u001B[42m";
        public static final String WHITE_BG = "\u001B[47m";

        public static void clearScreen() {
            System.out.print("\u001B[2J\u001B[H");
            System.out.flush();
        }

        public static void cursorTo(int row, int col) {
            System.out.print("\u001B[" + row + ";" + col + "H");
        }

        public static void hideCursor() {
            System.out.print("\u001B[?25l");
        }

        public static void showCursor() {
            System.out.print("\u001B[?25h");
        }
    }

    static class Cohete extends Thread {
        private static final Object LOCK_CONSOLA = new Object();
        private static final int ANCHO_PISTA = 60;
        private static final int COL_INICIO_PISTA = 22;

        private final String nombre;
        private final String colorBarra;
        private final String colorTexto;
        private final int filaBase;
        private final int distanciaMeta;
        private final int minAvance;
        private final int maxAvance;
        private double tiempoSegundos;

        public Cohete(String nombre, String colorBarra, String colorTexto, int filaBase,
                      int distanciaMeta, int minAvance, int maxAvance) {
            this.nombre = nombre;
            this.colorBarra = colorBarra;
            this.colorTexto = colorTexto;
            this.filaBase = filaBase;
            this.distanciaMeta = distanciaMeta;
            this.minAvance = minAvance;
            this.maxAvance = maxAvance;
        }

        public String getNombreCohete() {
            return nombre;
        }

        public double getTiempoSegundos() {
            return tiempoSegundos;
        }

        public void dibujarEscenarioInicial() {
            synchronized (LOCK_CONSOLA) {
                // Nombre del cohete
                Ansi.cursorTo(filaBase + 1, 1);
                System.out.print(colorTexto + String.format("%-10s", nombre) + Ansi.RESET);

                // Plataforma de lanzamiento (izquierda)
                for (int r = 0; r < 3; r++) {
                    Ansi.cursorTo(filaBase + r, 11);
                    String c = (r == 1) ? Ansi.GREEN_BG : Ansi.BLUE_BG;
                    System.out.print(c + "   " + Ansi.WHITE_BG + "   " + c + "   " + Ansi.RESET);
                }

                // Zona de aterrizaje (derecha)
                int colMeta = COL_INICIO_PISTA + ANCHO_PISTA + 1;
                for (int r = 0; r < 3; r++) {
                    Ansi.cursorTo(filaBase + r, colMeta);
                    if (r == 1) {
                        System.out.print(Ansi.RED_BG + "         " + Ansi.RESET);
                    } else {
                        System.out.print(Ansi.RED_BG + "  " + Ansi.WHITE_BG + "     " + Ansi.RED_BG + "  " + Ansi.RESET);
                    }
                }
                System.out.flush();
            }
        }

        private void actualizarProgreso(int distanciaActual, boolean aterrizado) {
            int bloques = (int) Math.min(ANCHO_PISTA, Math.round((distanciaActual * 1.0 / distanciaMeta) * ANCHO_PISTA));
            synchronized (LOCK_CONSOLA) {
                Ansi.cursorTo(filaBase + 1, COL_INICIO_PISTA);
                if (bloques > 0) {
                    System.out.print(colorBarra + " ".repeat(bloques) + Ansi.RESET);
                }
                if (aterrizado) {
                    Ansi.cursorTo(filaBase + 1, COL_INICIO_PISTA + ANCHO_PISTA + 1);
                    System.out.print(Ansi.WHITE_BG + "\u001B[30mAterrizado!" + Ansi.RESET);
                }
                System.out.flush();
            }
        }

        @Override
        public void run() {
            Random rnd = new Random();
            int recorrido = 0;
            long inicio = System.nanoTime();

            while (recorrido < distanciaMeta) {
                int avance = rnd.nextInt(maxAvance - minAvance + 1) + minAvance;
                recorrido = Math.min(distanciaMeta, recorrido + avance);
                boolean haLlegado = (recorrido >= distanciaMeta);

                actualizarProgreso(recorrido, haLlegado);

                if (!haLlegado) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
            }

            long fin = System.nanoTime();
            this.tiempoSegundos = (fin - inicio) / 1_000_000_000.0;
        }
    }

    public static void main(String[] args) {
        int distanciaTotal = 1000;
        int minAvance = 5;
        int maxAvance = 18;

        List<Cohete> cohetes = new ArrayList<>();
        cohetes.add(new Cohete("Falcon 9", Ansi.CYAN_BG, "\u001B[36m", 2, distanciaTotal, minAvance, maxAvance));
        cohetes.add(new Cohete("Soyuz 2", Ansi.PURPLE_BG, "\u001B[35m", 6, distanciaTotal, minAvance, maxAvance));
        cohetes.add(new Cohete("Ariane 5", Ansi.YELLOW_BG, "\u001B[33m", 10, distanciaTotal, minAvance, maxAvance));
        cohetes.add(new Cohete("Miura", Ansi.RED_BG, "\u001B[31m", 14, distanciaTotal, minAvance, maxAvance));

        Ansi.clearScreen();
        Ansi.hideCursor();

        for (Cohete c : cohetes) {
            c.dibujarEscenarioInicial();
        }

        for (Cohete c : cohetes) {
            c.start();
        }

        for (Cohete c : cohetes) {
            try {
                c.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        // Ordenar por menor tiempo para mostrar la clasificación final
        cohetes.sort(Comparator.comparingDouble(Cohete::getTiempoSegundos));

        Ansi.cursorTo(19, 1);
        System.out.println("Resultado de la carrera:");
        for (int i = 0; i < cohetes.size(); i++) {
            Cohete c = cohetes.get(i);
            System.out.printf("%d. %10s (%.4f s)%n", i + 1, c.getNombreCohete(), c.getTiempoSegundos());
        }
        Ansi.showCursor();
    }
}