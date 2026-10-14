public final class Vehiculo {
    private final String placa;
    private final double capacidadMaximaKg;
    private final boolean refrigerado;
    private boolean disponible;

    public Vehiculo(String placa, double capacidadMaximaKg, boolean refrigerado) {
        if (placa == null || placa.trim().isEmpty()) {
            throw new IllegalArgumentException("La placa no puede estar vacía.");
        }

        if (!Double.isFinite(capacidadMaximaKg) || capacidadMaximaKg <= 0) {
            throw new IllegalArgumentException(
                    "La capacidad máxima debe ser positiva y finita."
            );
        }

        this.placa = placa.trim();
        this.capacidadMaximaKg = capacidadMaximaKg;
        this.refrigerado = refrigerado;
        this.disponible = true;
    }

    public void ocupar() {
        if (!disponible) {
            throw new IllegalStateException(
                    "El vehículo ya tiene un despacho abierto."
            );
        }
        disponible = false;
    }

    public void liberar() {
        if (disponible) {
            throw new IllegalStateException(
                    "El vehículo ya está disponible."
            );
        }
        disponible = true;
    }

    public String getPlaca() {
        return placa;
    }

    public double getCapacidadMaximaKg() {
        return capacidadMaximaKg;
    }

    public boolean isRefrigerado() {
        return refrigerado;
    }

    public boolean isDisponible() {
        return disponible;
    }
}
