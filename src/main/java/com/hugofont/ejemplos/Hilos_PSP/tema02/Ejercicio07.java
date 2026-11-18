package com.hugofont.ejemplos.Hilos_PSP.tema02;

import java.util.Random;

public class Ejercicio07 {

    public static boolean contiene(int[] array, int objetivo) {
        if (array == null || array.length == 0) return false;

        int nucleos = Runtime.getRuntime().availableProcessors();
        int numHilos = Math.min(nucleos, array.length);
        Thread[] hilos = new Thread[numHilos];

        final boolean[] encontrado = {false};

        int tamBloque = array.length / numHilos;
        int resto = array.length % numHilos;
        int inicio = 0;

        for (int i = 0; i < numHilos; i++) {
            int fin = inicio + tamBloque + (i < resto ? 1 : 0);
            final int idxInicio = inicio;
            final int idxFin = fin;

            hilos[i] = new Thread(() -> {
                for (int j = idxInicio; j < idxFin && !encontrado[0]; j++) {
                    if (array[j] == objetivo) {
                        encontrado[0] = true;
                        break;
                    }
                }
            });
            hilos[i].start();
            inicio = fin;
        }

        for (Thread h : hilos) {
            try {
                h.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        return encontrado[0];
    }

    public static void main(String[] args) {
        int[] datos = new int[10_000_000];
        Random rnd = new Random();
        for (int i = 0; i < datos.length; i++) {
            datos[i] = rnd.nextInt(50_000_000);
        }
        datos[7_654_321] = 42;

        System.out.println("¿Está el 42?: " + contiene(datos, 42));
        System.out.println("¿Está el -99?: " + contiene(datos, -99));
    }
}