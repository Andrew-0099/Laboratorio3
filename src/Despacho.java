import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class Despacho {
    private final Vehiculo vehiculo;
    private final List<Envio> envios = new ArrayList<>();
    private final Set<Envio> resultados = new HashSet<>();
    private EstadoDespacho estado = EstadoDespacho.PREPARACION;

    public Despacho(Vehiculo vehiculo) {
        if (vehiculo == null) {
            throw new IllegalArgumentException("El vehículo es obligatorio.");
        }

        vehiculo.ocupar();
        this.vehiculo = vehiculo;
    }

    public Vehiculo getVehiculo() {
        return vehiculo;
    }

    public EstadoDespacho getEstado() {
        return estado;
    }

    public List<Envio> getEnvios() {
        return Collections.unmodifiableList(envios);
    }

    public double getPesoCargado() {
        double total = 0;

        for (Envio envio : envios) {
            total += envio.getPesoRealKg();
        }

        return total;
    }

    public double getCapacidadDisponible() {
        return vehiculo.getCapacidadMaximaKg() - getPesoCargado();
    }

    public void asignarEnvio(Envio envio) {
        exigirPreparacion();

        if (envio == null) {
            throw new IllegalArgumentException("El envío es obligatorio.");
        }
        if (envios.contains(envio)) {
            throw new IllegalArgumentException("El envío ya está asignado.");
        }
        if (envio.getEstado() != EstadoEnvio.BODEGA
                || envio.getIntentosRealizados() >= envio.getMaximoIntentos()) {
            throw new IllegalStateException(
                    "El envío no está disponible para asignación."
            );
        }
        if (!envio.esCompatibleCon(vehiculo)) {
            throw new IllegalArgumentException(
                    "El vehículo no es compatible con el envío."
            );
        }
        if (envio.getPesoRealKg() > getCapacidadDisponible()) {
            throw new IllegalArgumentException(
                    "La carga supera la capacidad del vehículo."
            );
        }

        envio.asignar();
        envios.add(envio);
    }

    public void retirarEnvio(Envio envio) {
        exigirPreparacion();

        if (!envios.contains(envio)) {
            throw new IllegalArgumentException(
                    "El envío no pertenece a este despacho."
            );
        }

        envio.retirar();
        envios.remove(envio);
    }

    public void salir() {
        exigirPreparacion();

        if (envios.isEmpty()) {
            throw new IllegalStateException(
                    "No puede salir un despacho vacío."
            );
        }

        for (Envio envio : envios) {
            if (envio.getEstado() != EstadoEnvio.ASIGNADO
                    || envio.getIntentosRealizados() >= envio.getMaximoIntentos()) {
                throw new IllegalStateException(
                        "Un envío no puede iniciar otro intento."
                );
            }
        }

        for (Envio envio : envios) {
            envio.iniciarIntento();
        }

        estado = EstadoDespacho.EN_RUTA;
    }

    public void registrarResultado(Envio envio, boolean entregado) {
        if (estado != EstadoDespacho.EN_RUTA) {
            throw new IllegalStateException(
                    "El despacho debe estar en ruta."
            );
        }
        if (!envios.contains(envio)) {
            throw new IllegalArgumentException(
                    "El envío no pertenece a este despacho."
            );
        }
        if (resultados.contains(envio)) {
            throw new IllegalStateException(
                    "El resultado ya fue registrado."
            );
        }

        envio.registrarEntrega(entregado);
        resultados.add(envio);

        if (resultados.size() == envios.size()) {
            estado = EstadoDespacho.CERRADO;
            vehiculo.liberar();
        }
    }

    private void exigirPreparacion() {
        if (estado != EstadoDespacho.PREPARACION) {
            throw new IllegalStateException(
                    "El despacho ya no está en preparación."
            );
        }
    }
}
