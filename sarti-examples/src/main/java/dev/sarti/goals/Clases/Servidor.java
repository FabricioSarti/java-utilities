package dev.sarti.goals.Clases;

import java.util.ArrayList;
import java.util.List;

public class Servidor {

    private final String id;
    private final int capacidadCpu;
    private final int capacidadAlmacenamiento;
    private int cpuUsada;
    private int almacenamientoUsado;
    private final double eficienciaEnergetica; // menor = mejor

    private final List<CargaTrabajo> cargas = new ArrayList<>();

    public Servidor(String id, int capacidadCpu, int capacidadAlmacenamiento, double eficienciaEnergetica) {
        this.id = id;
        this.capacidadCpu = capacidadCpu;
        this.capacidadAlmacenamiento = capacidadAlmacenamiento;
        this.eficienciaEnergetica = eficienciaEnergetica;
    }

    public boolean puedeAsignar(CargaTrabajo carga) {
        return (cpuUsada + carga.getCpu() <= capacidadCpu) &&
                (almacenamientoUsado + carga.getAlmacenamiento() <= capacidadAlmacenamiento);
    }

    public void asignarCarga(CargaTrabajo carga) {
        if (!puedeAsignar(carga))
            throw new IllegalStateException("Recursos insuficientes en " + id);
        cargas.add(carga);
        cpuUsada += carga.getCpu();
        almacenamientoUsado += carga.getAlmacenamiento();
    }

    public void removerCarga(CargaTrabajo carga) {
        if (cargas.remove(carga)) {
            cpuUsada -= carga.getCpu();
            almacenamientoUsado -= carga.getAlmacenamiento();
        }
    }

    public boolean estaSobrecargado() {
        return usoCpu() > 0.85 || usoAlmacenamiento() > 0.85;
    }

    public double usoCpu() {
        return (double) cpuUsada / capacidadCpu;
    }

    public double usoAlmacenamiento() {
        return (double) almacenamientoUsado / capacidadAlmacenamiento;
    }

    public double getEficienciaEnergetica() {
        return eficienciaEnergetica;
    }

    public String getId() {
        return id;
    }

    public List<CargaTrabajo> getCargas() {
        return new ArrayList<>(cargas);
    }

    @Override
    public String toString() {
        return String.format("Servidor[%s] CPU: %.2f%%, ALM: %.2f%%", id, usoCpu() * 100, usoAlmacenamiento() * 100);
    }
}
