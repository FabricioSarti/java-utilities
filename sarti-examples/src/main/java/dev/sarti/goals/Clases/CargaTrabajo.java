package dev.sarti.goals.Clases;

public class CargaTrabajo {

    private final String id;
    private final int cpu;
    private final int almacenamiento;

    public CargaTrabajo(String id, int cpu, int almacenamiento) {
        this.id = id;
        this.cpu = cpu;
        this.almacenamiento = almacenamiento;
    }

    public int getCpu() {
        return cpu;
    }

    public int getAlmacenamiento() {
        return almacenamiento;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return String.format("Carga[%s] CPU: %d, ALM: %d", id, cpu, almacenamiento);
    }
}
