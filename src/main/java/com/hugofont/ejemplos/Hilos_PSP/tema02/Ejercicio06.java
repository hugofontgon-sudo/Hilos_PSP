package com.hugofont.ejemplos.Hilos_PSP.tema02;

public class Ejercicio06 {
    public static void main(String[] args) {
        int nucleos = Runtime.getRuntime().availableProcessors();
        System.out.println("Núcleos disponibles: " + nucleos);

        for (int i = 1; i <= nucleos; i++) {
            final int id = i;
            Thread hilo = new Thread(() -> {
                for (int num = 1; num <= 10000; num++) {
                    System.out.print("Hilo: " + id + "(" + num + ")\n");
                }
            });
            hilo.start();
        }
    }
}