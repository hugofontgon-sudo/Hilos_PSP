package com.hugofont.ejemplos.Hilos_PSP.tema02;

public class Ejercicio01 {
    public static void main(String[] args) {
        Thread hilo = new Thread(() -> {
            for (int i = 1; i <= 100; i++) {
                System.out.println(i);
            }
        });

        hilo.start();
        System.out.println("fin de programa");
    }
}