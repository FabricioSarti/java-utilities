package dev.sarti.goals.Clases;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;

public class AdministradorRecursos {

    private final List<Servidor> servidores;
    private final Queue<CargaTrabajo> cargasEnEspera = new LinkedList<>();

    public AdministradorRecursos(List<Servidor> servidores) {
        this.servidores = servidores;
    }

    /*
     * public void asignarCarga(CargaTrabajo carga) {
     * servidores.stream()
     * .filter(s -> s.puedeAsignar(carga))
     * .sorted(Comparator.comparingDouble(Servidor::getEficienciaEnergetica))
     * .findFirst()
     * .ifPresentOrElse(
     * s -> s.asignarCarga(carga),
     * () -> System.out.println("⚠️ No hay servidores disponibles para " + carga));
     * }
     */

    public void asignarCarga(CargaTrabajo carga) {
        Optional<Servidor> destino = servidores.stream()
                .filter(s -> s.puedeAsignar(carga))
                .sorted(Comparator.comparingDouble(Servidor::getEficienciaEnergetica))
                .findFirst();

        if (destino.isPresent()) {
            destino.get().asignarCarga(carga);
            System.out.printf("<-> Carga %s asignada a %s%n", carga.getId(), destino.get().getId());
        } else {
            cargasEnEspera.add(carga);
            System.out.printf("<-> No hay servidores disponibles para %s, agregada a la cola de espera.%n",
                    carga.getId());
        }
    }

    /*
     * public void balancear() {
     * for (Servidor servidor : servidores) {
     * if (servidor.estaSobrecargado()) {
     * System.out.println("🔄 Balanceando " + servidor.getId());
     * List<CargaTrabajo> cargas = servidor.getCargas();
     * for (CargaTrabajo carga : cargas) {
     * Optional<Servidor> destino = servidores.stream()
     * .filter(s -> !s.getId().equals(servidor.getId()))
     * .filter(s -> s.puedeAsignar(carga))
     * .sorted(Comparator.comparingDouble(Servidor::getEficienciaEnergetica))
     * .findFirst();
     * 
     * if (destino.isPresent()) {
     * servidor.removerCarga(carga);
     * destino.get().asignarCarga(carga);
     * System.out.printf("✅ Migrada %s de %s a %s%n", carga.getId(),
     * servidor.getId(),
     * destino.get().getId());
     * }
     * }
     * }
     * }
     * }
     */

    public void balancear() {
        for (Servidor servidor : servidores) {
            if (servidor.estaSobrecargado()) {
                System.out.println("<-> Balanceando " + servidor.getId());
                List<CargaTrabajo> cargas = new ArrayList<>(servidor.getCargas()); // Copia para evitar
                                                                                   // ConcurrentModificationException

                for (CargaTrabajo carga : cargas) {
                    Optional<Servidor> destino = servidores.stream()
                            .filter(s -> !s.getId().equals(servidor.getId()))
                            .filter(s -> s.puedeAsignar(carga))
                            .sorted(Comparator.comparingDouble(Servidor::getEficienciaEnergetica))
                            .findFirst();

                    if (destino.isPresent()) {
                        servidor.removerCarga(carga);
                        destino.get().asignarCarga(carga);
                        System.out.printf("<-> Migrada %s de %s a %s%n", carga.getId(), servidor.getId(),
                                destino.get().getId());
                    }
                }
            }
        }

        reasignarCargasPendientes();
    }

    public void reasignarCargasPendientes() {
        if (cargasEnEspera.isEmpty())
            return;

        System.out.println("<-> Reintentando asignación de cargas en espera...");
        Queue<CargaTrabajo> pendientes = new LinkedList<>(cargasEnEspera);
        cargasEnEspera.clear();

        for (CargaTrabajo carga : pendientes) {
            Optional<Servidor> destino = servidores.stream()
                    .filter(s -> s.puedeAsignar(carga))
                    .sorted(Comparator.comparingDouble(Servidor::getEficienciaEnergetica))
                    .findFirst();

            if (destino.isPresent()) {
                destino.get().asignarCarga(carga);
                System.out.printf("<-> Carga en espera %s asignada a %s%n", carga.getId(), destino.get().getId());
            } else {
                cargasEnEspera.add(carga); // Sigue sin poder asignarse
            }
        }
    }

    public void mostrarEstado() {
        servidores.forEach(System.out::println);
    }

}
