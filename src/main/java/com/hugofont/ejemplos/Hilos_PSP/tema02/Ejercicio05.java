package com.hugofont.ejemplos.Hilos_PSP.tema02;

public class Ejercicio05 {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Uso: java com.hugofont.ejemplos.Hilos_PSP.tema02 <num_hilos>");
            return;
        }

        int n = Integer.parseInt(args[0]);

        for (int i = 1; i <= n; i++) {
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