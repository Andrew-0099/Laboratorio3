package modelo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Coordina las colecciones del sistema y el registro de ingresos. */
public final class SistemaEnvios {
    public static final double CARGO_REINTENTO = 15.0;

    private final Map<String, Envio> envios = new LinkedHashMap<>();
    private final Map<String, Vehiculo> vehiculos = new LinkedHashMap<>();
    private final List<Despacho> despachos = new ArrayList<>();
    private final Map<String, Double> ingresosPorCategoria = new LinkedHashMap<>();
    private double ingresoRegistro;
    private double ingresoReintentos;

    public double cotizar(Envio envio) {
        exigirNoNulo(envio, "El envío es obligatorio.");
        return envio.calcularCosto();
    }

    public void registrarEnvio(Envio envio) {
        exigirNoNulo(envio, "El envío es obligatorio.");
        if (envios.containsKey(envio.getCodigo())) {
            throw new IllegalArgumentException("Ya existe un envío con ese código.");
        }

        double costo = envio.calcularCosto();
        envios.put(envio.getCodigo(), envio);
        ingresoRegistro += costo;
        ingresosPorCategoria.merge(envio.getCategoria(), costo, Double::sum);
    }

    /** Agrega datos iniciales sin simular un cobro. */
    public void agregarEnvioInicial(Envio envio) {
        exigirNoNulo(envio, "El envío es obligatorio.");
        if (envios.containsKey(envio.getCodigo())) {
            throw new IllegalArgumentException("Ya existe un envío con ese código.");
        }
        envios.put(envio.getCodigo(), envio);
    }

    public void registrarVehiculo(Vehiculo vehiculo) {
        exigirNoNulo(vehiculo, "El vehículo es obligatorio.");
        if (vehiculos.containsKey(vehiculo.getPlaca())) {
            throw new IllegalArgumentException("Ya existe un vehículo con esa placa.");
        }
        vehiculos.put(vehiculo.getPlaca(), vehiculo);
    }

    public Despacho crearDespacho(Vehiculo vehiculo) {
        exigirNoNulo(vehiculo, "El vehículo es obligatorio.");
        if (!vehiculos.containsValue(vehiculo)) {
            throw new IllegalArgumentException("El vehículo no está registrado.");
        }
        if (!vehiculo.isDisponible()) {
            throw new IllegalStateException("El vehículo no está disponible.");
        }

        Despacho despacho = new Despacho(vehiculo);
        despachos.add(despacho);
        return despacho;
    }

    public void asignarEnvio(Despacho despacho, Envio envio) {
        exigirDespachoRegistrado(despacho);
        exigirEnvioRegistrado(envio);
        despacho.asignarEnvio(envio);
    }

    public void retirarEnvio(Despacho despacho, Envio envio) {
        exigirDespachoRegistrado(despacho);
        exigirEnvioRegistrado(envio);
        despacho.retirarEnvio(envio);
    }

    public void salirDespacho(Despacho despacho) {
        exigirDespachoRegistrado(despacho);

        List<Envio> reintentos = new ArrayList<>();
        for (Envio envio : despacho.getEnvios()) {
            if (envio.getIntentosRealizados() > 0) {
                reintentos.add(envio);
            }
        }

        despacho.salir();
        ingresoReintentos += reintentos.size() * CARGO_REINTENTO;
        for (Envio envio : reintentos) {
            ingresosPorCategoria.merge(
                    envio.getCategoria(), CARGO_REINTENTO, Double::sum);
        }
    }

    public void registrarResultado(Despacho despacho, Envio envio, boolean entregado) {
        exigirDespachoRegistrado(despacho);
        exigirEnvioRegistrado(envio);
        despacho.registrarResultado(envio, entregado);
    }

    public Envio buscarEnvio(String codigo) {
        return envios.get(normalizarClave(codigo, "código"));
    }

    public Vehiculo buscarVehiculo(String placa) {
        return vehiculos.get(normalizarClave(placa, "placa"));
    }

    public List<Envio> getEnvios() {
        return Collections.unmodifiableList(new ArrayList<>(envios.values()));
    }

    public List<Vehiculo> getVehiculos() {
        return Collections.unmodifiableList(new ArrayList<>(vehiculos.values()));
    }

    public List<Despacho> getDespachos() {
        return Collections.unmodifiableList(despachos);
    }

    public List<Despacho> getDespachosActivos() {
        List<Despacho> activos = new ArrayList<>();
        for (Despacho despacho : despachos) {
            if (despacho.getEstado() != EstadoDespacho.CERRADO) {
                activos.add(despacho);
            }
        }
        return Collections.unmodifiableList(activos);
    }

    public Map<String, Map<EstadoEnvio, Integer>> getConteos() {
        Map<String, Map<EstadoEnvio, Integer>> conteos = new LinkedHashMap<>();
        for (Envio envio : envios.values()) {
            Map<EstadoEnvio, Integer> porEstado = conteos.computeIfAbsent(
                    envio.getCategoria(), clave -> new EnumMap<>(EstadoEnvio.class));
            porEstado.merge(envio.getEstado(), 1, Integer::sum);
        }
        return conteos;
    }

    public Map<String, Double> getIngresosPorCategoria() {
        return Collections.unmodifiableMap(
                new LinkedHashMap<>(ingresosPorCategoria));
    }

    public double getIngresoRegistro() {
        return ingresoRegistro;
    }

    public double getIngresoReintentos() {
        return ingresoReintentos;
    }

    public double getIngresoTotal() {
        return ingresoRegistro + ingresoReintentos;
    }

    private void exigirDespachoRegistrado(Despacho despacho) {
        exigirNoNulo(despacho, "El despacho es obligatorio.");
        if (!despachos.contains(despacho)) {
            throw new IllegalArgumentException("El despacho no está registrado.");
        }
    }

    private void exigirEnvioRegistrado(Envio envio) {
        exigirNoNulo(envio, "El envío es obligatorio.");
        if (!envios.containsValue(envio)) {
            throw new IllegalArgumentException("El envío no está registrado.");
        }
    }

    private static void exigirNoNulo(Object valor, String mensaje) {
        if (valor == null) {
            throw new IllegalArgumentException(mensaje);
        }
    }

    private static String normalizarClave(String valor, String campo) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalArgumentException("El " + campo + " no puede estar vacío.");
        }
        return valor.trim();
    }
}
