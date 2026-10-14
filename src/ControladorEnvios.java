package controlador;

import java.util.List;
import modelo.CargaRefrigerada;
import modelo.Despacho;
import modelo.Envio;
import modelo.Paquete;
import modelo.SistemaEnvios;
import modelo.Sobre;
import modelo.TipoDestino;
import modelo.Vehiculo;
import vista.VistaConsola;

public final class ControladorEnvios {
    private final SistemaEnvios sistema;
    private final VistaConsola vista;

    public ControladorEnvios(SistemaEnvios sistema, VistaConsola vista) {
        if (sistema == null || vista == null) {
            throw new IllegalArgumentException("El sistema y la vista son obligatorios.");
        }
        this.sistema = sistema;
        this.vista = vista;
    }

    public void ejecutar() {
        int opcion;
        do {
            vista.mostrarMenu();
            opcion = vista.leerOpcion();
            try {
                procesarOpcion(opcion);
            } catch (IllegalArgumentException | IllegalStateException ex) {
                vista.mostrarError(ex.getMessage());
            }
        } while (opcion != 0);
    }

    private void procesarOpcion(int opcion) {
        switch (opcion) {
            case 1 -> cotizarORegistrar();
            case 2 -> registrarVehiculo();
            case 3 -> crearDespacho();
            case 4 -> asignarEnvio();
            case 5 -> retirarEnvio();
            case 6 -> salirDespacho();
            case 7 -> registrarResultado();
            case 8 -> vista.mostrarEnvios(sistema.getEnvios());
            case 9 -> vista.mostrarVehiculos(sistema.getVehiculos());
            case 10 -> mostrarReporte();
            case 0 -> vista.mostrarMensaje("Hasta pronto.");
            default -> vista.mostrarError("La opción no existe.");
        }
    }

    private void cotizarORegistrar() {
        Envio envio = solicitarEnvio();
        vista.mostrarCotizacion(sistema.cotizar(envio), envio.calcularDiasEntrega());
        if (vista.leerSiNo("¿Desea registrar y cobrar este envío?")) {
            sistema.registrarEnvio(envio);
            vista.mostrarMensaje("Envío registrado correctamente.");
        } else {
            vista.mostrarMensaje("Cotización finalizada sin registrar ingresos.");
        }
    }

    private Envio solicitarEnvio() {
        int categoria = vista.leerEntero(
                "Categoría (1 Sobre, 2 Paquete, 3 Paquete frágil, 4 Refrigerada): ");
        String codigo = vista.leerTexto("Código: ");
        String remitente = vista.leerTexto("Remitente: ");
        String destinatario = vista.leerTexto("Destinatario: ");
        double peso = vista.leerDouble("Peso real en kg: ");
        TipoDestino destino = leerDestino();

        return switch (categoria) {
            case 1 -> new Sobre(codigo, remitente, destinatario, peso, destino);
            case 2, 3 -> new Paquete(codigo, remitente, destinatario, peso, destino,
                    vista.leerDouble("Largo en cm: "), vista.leerDouble("Ancho en cm: "),
                    vista.leerDouble("Alto en cm: "), categoria == 3,
                    categoria == 3 ? vista.leerDouble("Valor declarado: Q") : 0);
            case 4 -> new CargaRefrigerada(codigo, remitente, destinatario, peso,
                    destino, vista.leerDouble("Temperatura requerida en °C: "));
            default -> throw new IllegalArgumentException("La categoría no existe.");
        };
    }

    private TipoDestino leerDestino() {
        int opcion = vista.leerEntero("Destino (1 Urbano, 2 Departamental): ");
        return switch (opcion) {
            case 1 -> TipoDestino.URBANO;
            case 2 -> TipoDestino.DEPARTAMENTAL;
            default -> throw new IllegalArgumentException("El destino no existe.");
        };
    }

    private void registrarVehiculo() {
        Vehiculo vehiculo = new Vehiculo(vista.leerTexto("Placa: "),
                vista.leerDouble("Capacidad máxima en kg: "),
                vista.leerSiNo("¿Tiene refrigeración?"));
        sistema.registrarVehiculo(vehiculo);
        vista.mostrarMensaje("Vehículo registrado correctamente.");
    }

    private void crearDespacho() {
        vista.mostrarVehiculos(sistema.getVehiculos());
        Vehiculo vehiculo = requerirVehiculo();
        sistema.crearDespacho(vehiculo);
        vista.mostrarMensaje("Despacho creado correctamente.");
    }

    private void asignarEnvio() {
        Despacho despacho = seleccionarDespacho();
        vista.mostrarEnvios(sistema.getEnvios());
        sistema.asignarEnvio(despacho, requerirEnvio());
        vista.mostrarMensaje("Envío asignado correctamente.");
    }

    private void retirarEnvio() {
        Despacho despacho = seleccionarDespacho();
        vista.mostrarEnvios(despacho.getEnvios());
        sistema.retirarEnvio(despacho, requerirEnvio());
        vista.mostrarMensaje("Envío retirado correctamente.");
    }

    private void salirDespacho() {
        Despacho despacho = seleccionarDespacho();
        sistema.salirDespacho(despacho);
        vista.mostrarMensaje("El despacho está en ruta.");
    }

    private void registrarResultado() {
        Despacho despacho = seleccionarDespacho();
        vista.mostrarEnvios(despacho.getEnvios());
        Envio envio = requerirEnvio();
        boolean entregado = vista.leerSiNo("¿El envío fue entregado? Si responde no, será fallido");
        sistema.registrarResultado(despacho, envio, entregado);
        vista.mostrarMensaje("Resultado registrado correctamente.");
    }

    private Despacho seleccionarDespacho() {
        List<Despacho> despachos = sistema.getDespachos();
        vista.mostrarDespachos(despachos);
        int indice = vista.leerEntero("Número de despacho: ") - 1;
        if (indice < 0 || indice >= despachos.size()) {
            throw new IllegalArgumentException("El despacho no existe.");
        }
        return despachos.get(indice);
    }

    private Envio requerirEnvio() {
        Envio envio = sistema.buscarEnvio(vista.leerTexto("Código del envío: "));
        if (envio == null) {
            throw new IllegalArgumentException("No existe un envío con ese código.");
        }
        return envio;
    }

    private Vehiculo requerirVehiculo() {
        Vehiculo vehiculo = sistema.buscarVehiculo(vista.leerTexto("Placa del vehículo: "));
        if (vehiculo == null) {
            throw new IllegalArgumentException("No existe un vehículo con esa placa.");
        }
        return vehiculo;
    }

    private void mostrarReporte() {
        vista.mostrarReporte(sistema.getConteos(), sistema.getIngresoRegistro(),
                sistema.getIngresoReintentos(), sistema.getIngresosPorCategoria(),
                sistema.getDespachosActivos());
    }
}
