package dev.sarti.goals;

import java.util.*;
import java.util.concurrent.*;

import dev.sarti.goals.Clases.AdministradorRecursos;
import dev.sarti.goals.Clases.CargaTrabajo;
import dev.sarti.goals.Clases.Servidor;

public class Main {
    public static void main(String[] args) {
        List<Servidor> servidores = Arrays.asList(
                new Servidor("S1", 100, 1000, 1.2),
                new Servidor("S2", 120, 800, 0.9),
                new Servidor("S3", 90, 700, 1.5));

        AdministradorRecursos administrador = new AdministradorRecursos(servidores);
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        // Simula asignación de cargas cada 2 segundos
        scheduler.scheduleAtFixedRate(() -> {
            CargaTrabajo carga = generarCarga();
            System.out.println("<-> Nueva carga: " + carga);
            administrador.asignarCarga(carga);
        }, 0, 2, TimeUnit.SECONDS);

        // Simula balanceo cada 5 segundos
        scheduler.scheduleAtFixedRate(() -> {
            System.out.println("\n <-> Estado del sistema:");
            administrador.mostrarEstado();
            administrador.balancear();
            System.out.println();
        }, 5, 5, TimeUnit.SECONDS);
    }

    private static CargaTrabajo generarCarga() {
        Random random = new Random();
        int cpu = 10 + random.nextInt(20);
        int alm = 50 + random.nextInt(200);
        return new CargaTrabajo(UUID.randomUUID().toString(), cpu, alm);
    }
}