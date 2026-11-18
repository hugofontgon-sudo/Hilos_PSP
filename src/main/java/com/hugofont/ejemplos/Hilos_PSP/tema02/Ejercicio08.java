package com.hugofont.ejemplos.Hilos_PSP.tema02;

import java.util.Random;

public class Ejercicio08 {

    private static boolean esPrimo(int n) {
        if (n <= 1) return false;
        if (n <= 3) return true;
        if (n % 2 == 0 || n % 3 == 0) return false;
        for (int i = 5; i * i <= n; i += 6) {
            if (n % i == 0 || n % (i + 2) == 0) return false;
        }
        return true;
    }

    public static int contarPrimos(int[] array) {
        if (array == null || array.length == 0) return 0;

        int nucleos = Runtime.getRuntime().availableProcessors();
        int numHilos = Math.min(nucleos, array.length);
        Thread[] hilos = new Thread[numHilos];
        int[] conteosParciales = new int[numHilos];

        int tamBloque = array.length / numHilos;
        int resto = array.length % numHilos;
        int inicio = 0;

        for (int i = 0; i < numHilos; i++) {
            int fin = inicio + tamBloque + (i < resto ? 1 : 0);
            final int idHilo = i;
            final int idxInicio = inicio;
            final int idxFin = fin;

            hilos[i] = new Thread(() -> {
                int contadorLocal = 0;
                for (int j = idxInicio; j < idxFin; j++) {
                    if (esPrimo(array[j])) {
                        contadorLocal++;
                    }
                }
                conteosParciales[idHilo] = contadorLocal;
            });
            hilos[i].start();
            inicio = fin;
        }

        int totalPrimos = 0;
        for (int i = 0; i < numHilos; i++) {
            try {
                hilos[i].join();
                totalPrimos += conteosParciales[i];
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        return totalPrimos;
    }

    public static void main(String[] args) {
        int[] numeros = new int[1_000_000];
        Random rnd = new Random();
        for (int i = 0; i < numeros.length; i++) {
            numeros[i] = rnd.nextInt(100_000) + 1;
        }

        long t0 = System.nanoTime();
        int total = contarPrimos(numeros);
        double segs = (System.nanoTime() - t0) / 1_000_000_000.0;

        System.out.printf("Primos encontrados: %d (en %.4f s)%n", total, segs);
    }
}