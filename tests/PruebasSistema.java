package pruebas;

import modelo.CargaRefrigerada;
import modelo.Despacho;
import modelo.Envio;
import modelo.EstadoDespacho;
import modelo.EstadoEnvio;
import modelo.Paquete;
import modelo.SistemaEnvios;
import modelo.Sobre;
import modelo.TipoDestino;
import modelo.Vehiculo;
import java.util.Map;

public final class PruebasSistema {
    private static int ejecutadas;

    private PruebasSistema() { }

    public static void main(String[] args) {
        probarCostosYTiempos();
        probarRestriccionesDeDespacho();
        probarIntentosEIngresos();
        probarOperacionesSinIngreso();
        probarValidacionesDeEntrada();
        probarEstadosYReportes();
        System.out.printf("%n%d verificaciones completadas correctamente.%n", ejecutadas);
    }

    private static void probarCostosYTiempos() {
        Envio sobreUrbano = new Sobre("S1", "A", "B", 0.2, TipoDestino.URBANO);
        Envio sobreDepartamental = new Sobre("S2", "A", "B", 0.5, TipoDestino.DEPARTAMENTAL);
        Paquete paqueteVolumetrico = new Paquete("P1", "A", "B", 3.2,
                TipoDestino.URBANO, 40, 30, 20, false, 0);
        Paquete paqueteFraccion = new Paquete("P2", "A", "B", 3.2,
                TipoDestino.DEPARTAMENTAL, 10, 10, 10, false, 0);
        Envio fragil = new Paquete("F1", "A", "B", 3.2, TipoDestino.URBANO,
                40, 30, 20, true, 1200);
        Envio refrigerada = new CargaRefrigerada("R1", "A", "B", 10,
                TipoDestino.URBANO, 4);

        verificar("Sobre urbano", 20, sobreUrbano.calcularCosto());
        verificar("Sobre departamental", 35, sobreDepartamental.calcularCosto());
        verificar("Peso volumétrico", 4.8, paqueteVolumetrico.calcularPesoVolumetrico());
        verificar("Paquete por peso volumétrico", 65, paqueteVolumetrico.calcularCosto());
        verificar("Kilogramo por fracción", 72, paqueteFraccion.calcularCosto());
        verificar("Seguro y embalaje frágil", 116, fragil.calcularCosto());
        verificar("Cadena de frío", 205, refrigerada.calcularCosto());
        verificar("Días sobre urbano", 1, sobreUrbano.calcularDiasEntrega());
        verificar("Días sobre departamental", 2, sobreDepartamental.calcularDiasEntrega());
        verificar("Días paquete urbano", 2, paqueteVolumetrico.calcularDiasEntrega());
        verificar("Días paquete frágil", 3, fragil.calcularDiasEntrega());
        verificar("Días refrigerada", 1, refrigerada.calcularDiasEntrega());
    }

    private static void probarRestriccionesDeDespacho() {
        SistemaEnvios sistema = new SistemaEnvios();
        Vehiculo normal = new Vehiculo("N1", 5, false);
        Vehiculo frio = new Vehiculo("R1", 20, true);
        Envio pesado = new Paquete("P1", "A", "B", 6, TipoDestino.URBANO,
                10, 10, 10, false, 0);
        Envio refrigerada = new CargaRefrigerada("C1", "A", "B", 2,
                TipoDestino.URBANO, 2);
        sistema.registrarVehiculo(normal);
        sistema.registrarVehiculo(frio);
        sistema.agregarEnvioInicial(pesado);
        sistema.agregarEnvioInicial(refrigerada);

        Despacho despachoNormal = sistema.crearDespacho(normal);
        esperarError("Rechaza exceso de capacidad", () -> sistema.asignarEnvio(despachoNormal, pesado));
        esperarError("Rechaza refrigeración incompatible",
                () -> sistema.asignarEnvio(despachoNormal, refrigerada));
        Despacho despachoFrio = sistema.crearDespacho(frio);
        sistema.asignarEnvio(despachoFrio, refrigerada);
        verificar("Acepta vehículo refrigerado", EstadoEnvio.ASIGNADO, refrigerada.getEstado());
    }

    private static void probarIntentosEIngresos() {
        verificarIntentos("Sobre", new Sobre("S", "A", "B", 0.2, TipoDestino.URBANO), 3, 30);
        verificarIntentos("Paquete", new Paquete("P", "A", "B", 1, TipoDestino.URBANO,
                10, 10, 10, false, 0), 3, 30);
        verificarIntentos("Paquete frágil", new Paquete("F", "A", "B", 1,
                TipoDestino.URBANO, 10, 10, 10, true, 100), 2, 15);
        verificarIntentos("Carga refrigerada", new CargaRefrigerada("R", "A", "B", 1,
                TipoDestino.URBANO, 3), 1, 0);
    }

    private static void verificarIntentos(String nombre, Envio envio,
            int intentosEsperados, double reintentosEsperados) {
        SistemaEnvios sistema = new SistemaEnvios();
        Vehiculo vehiculo = new Vehiculo("V", 20, true);
        sistema.registrarVehiculo(vehiculo);
        sistema.agregarEnvioInicial(envio);
        for (int i = 0; i < intentosEsperados; i++) {
            Despacho despacho = sistema.crearDespacho(vehiculo);
            sistema.asignarEnvio(despacho, envio);
            sistema.salirDespacho(despacho);
            sistema.registrarResultado(despacho, envio, false);
        }
        verificar(nombre + " llega a devuelto", EstadoEnvio.DEVUELTO, envio.getEstado());
        verificar(nombre + " cantidad de intentos", intentosEsperados, envio.getIntentosRealizados());
        verificar(nombre + " cargos de reintento", reintentosEsperados,
                sistema.getIngresoReintentos());
    }

    private static void probarOperacionesSinIngreso() {
        SistemaEnvios sistema = new SistemaEnvios();
        Envio envio = new Sobre("S1", "A", "B", 0.2, TipoDestino.URBANO);
        verificar("Cotización", 20, sistema.cotizar(envio));
        verificar("Cotizar no genera ingreso", 0, sistema.getIngresoTotal());
        sistema.registrarEnvio(envio);
        verificar("Registro cobra costo completo", 20, sistema.getIngresoRegistro());
        esperarError("Código repetido rechazado", () -> sistema.registrarEnvio(
                new Sobre("S1", "C", "D", 0.3, TipoDestino.URBANO)));
        verificar("Rechazo no altera ingresos", 20, sistema.getIngresoTotal());

        Vehiculo vehiculo = new Vehiculo("V1", 10, false);
        sistema.registrarVehiculo(vehiculo);
        Despacho despacho = sistema.crearDespacho(vehiculo);
        sistema.asignarEnvio(despacho, envio);
        sistema.retirarEnvio(despacho, envio);
        verificar("Retiro no altera ingresos", 20, sistema.getIngresoTotal());
    }

    private static void probarValidacionesDeEntrada() {
        esperarError("Código vacío", () -> new Sobre(
                "  ", "A", "B", 0.2, TipoDestino.URBANO));
        esperarError("Peso no positivo", () -> new Sobre(
                "S", "A", "B", 0, TipoDestino.URBANO));
        esperarError("Sobre mayor de 0.5 kg", () -> new Sobre(
                "S", "A", "B", 0.6, TipoDestino.URBANO));
        esperarError("Dimensión no positiva", () -> new Paquete(
                "P", "A", "B", 1, TipoDestino.URBANO,
                0, 10, 10, false, 0));
        esperarError("Valor declarado no positivo", () -> new Paquete(
                "F", "A", "B", 1, TipoDestino.URBANO,
                10, 10, 10, true, 0));
        esperarError("Temperatura fuera de rango", () -> new CargaRefrigerada(
                "R", "A", "B", 1, TipoDestino.URBANO, -26));
        esperarError("Refrigerada departamental", () -> new CargaRefrigerada(
                "R", "A", "B", 1, TipoDestino.DEPARTAMENTAL, 4));
        esperarError("Capacidad no positiva", () -> new Vehiculo("V", 0, false));

        SistemaEnvios sistema = new SistemaEnvios();
        sistema.registrarVehiculo(new Vehiculo("V1", 10, false));
        esperarError("Placa repetida", () -> sistema.registrarVehiculo(
                new Vehiculo("V1", 20, true)));
        verificar("Placa rechazada no se agrega", 1, sistema.getVehiculos().size());
    }

    private static void probarEstadosYReportes() {
        SistemaEnvios sistema = new SistemaEnvios();
        Envio envio = new Sobre("S1", "A", "B", 0.2, TipoDestino.URBANO);
        Envio pendiente = new Sobre("S2", "C", "D", 0.3, TipoDestino.URBANO);
        Vehiculo vehiculo = new Vehiculo("V1", 10, false);
        sistema.registrarEnvio(envio);
        sistema.agregarEnvioInicial(pendiente);
        sistema.registrarVehiculo(vehiculo);

        Despacho despacho = sistema.crearDespacho(vehiculo);
        verificar("Despacho inicia en preparación", EstadoDespacho.PREPARACION,
                despacho.getEstado());
        verificar("Despacho aparece activo", 1, sistema.getDespachosActivos().size());
        esperarError("Despacho vacío no sale", () -> sistema.salirDespacho(despacho));
        verificar("Salida rechazada no cobra", 20, sistema.getIngresoTotal());
        esperarError("Vehículo ocupado", () -> sistema.crearDespacho(vehiculo));

        sistema.asignarEnvio(despacho, envio);
        verificar("Asignación cambia estado", EstadoEnvio.ASIGNADO, envio.getEstado());
        esperarError("Asignación duplicada", () -> sistema.asignarEnvio(despacho, envio));
        sistema.asignarEnvio(despacho, pendiente);
        sistema.salirDespacho(despacho);
        verificar("Salida cambia despacho", EstadoDespacho.EN_RUTA, despacho.getEstado());
        verificar("Salida cambia envío", EstadoEnvio.EN_RUTA, envio.getEstado());
        sistema.registrarResultado(despacho, envio, true);
        verificar("Entrega exitosa", EstadoEnvio.ENTREGADO, envio.getEstado());
        verificar("Despacho espera resultados", EstadoDespacho.EN_RUTA, despacho.getEstado());
        esperarError("Resultado duplicado", () -> sistema.registrarResultado(
                despacho, envio, true));
        sistema.registrarResultado(despacho, pendiente, true);
        verificar("Despacho cierra", EstadoDespacho.CERRADO, despacho.getEstado());
        verificar("Vehículo queda libre", true, vehiculo.isDisponible());
        verificar("Cerrado deja de estar activo", 0, sistema.getDespachosActivos().size());
        Map<EstadoEnvio, Integer> conteoSobre = sistema.generarReportes().get("Sobre");
        verificar("Reporte entregados", 2,
                conteoSobre.get(EstadoEnvio.ENTREGADO).intValue());
        verificar("Reporte en bodega", 0,
                conteoSobre.get(EstadoEnvio.BODEGA).intValue());
        verificar("Reporte incluye estado en cero", 0,
                conteoSobre.get(EstadoEnvio.DEVUELTO).intValue());
        verificar("Ingreso por categoría", 20,
                sistema.getIngresosPorCategoria().get("Sobre").doubleValue());
    }

    private static void esperarError(String caso, Runnable operacion) {
        try {
            operacion.run();
            throw new AssertionError(caso + ": se esperaba una excepción.");
        } catch (IllegalArgumentException | IllegalStateException esperado) {
            ejecutadas++;
            System.out.printf("OK %-38s | esperado: rechazo | obtenido: %s%n",
                    caso, esperado.getMessage());
        }
    }

    private static void verificar(String caso, double esperado, double obtenido) {
        if (Math.abs(esperado - obtenido) > 0.000001) {
            throw new AssertionError(caso + ": esperado " + esperado + ", obtenido " + obtenido);
        }
        registrarExito(caso, esperado, obtenido);
    }

    private static void verificar(String caso, int esperado, int obtenido) {
        if (esperado != obtenido) {
            throw new AssertionError(caso + ": esperado " + esperado + ", obtenido " + obtenido);
        }
        registrarExito(caso, esperado, obtenido);
    }

    private static void verificar(String caso, Object esperado, Object obtenido) {
        if (!esperado.equals(obtenido)) {
            throw new AssertionError(caso + ": esperado " + esperado + ", obtenido " + obtenido);
        }
        registrarExito(caso, esperado, obtenido);
    }

    private static void registrarExito(String caso, Object esperado, Object obtenido) {
        ejecutadas++;
        System.out.printf("OK %-38s | esperado: %s | obtenido: %s%n", caso, esperado, obtenido);
    }
}
