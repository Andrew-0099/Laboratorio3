package vista;

import java.util.List;
import java.util.Map;
import java.util.Scanner;
import modelo.Despacho;
import modelo.Envio;
import modelo.EstadoEnvio;
import modelo.Vehiculo;

public final class VistaConsola {
    private final Scanner scanner = new Scanner(System.in);

    public void mostrarMenu() {
        System.out.println("\n=== ENVÍAGT ===");
        System.out.println("1. Cotizar o registrar envío");
        System.out.println("2. Registrar vehículo");
        System.out.println("3. Crear despacho");
        System.out.println("4. Asignar envío a despacho");
        System.out.println("5. Retirar envío de despacho");
        System.out.println("6. Hacer salir despacho");
        System.out.println("7. Registrar resultado de entrega");
        System.out.println("8. Consultar envíos");
        System.out.println("9. Consultar vehículos");
        System.out.println("10. Mostrar reportes");
        System.out.println("0. Salir");
    }

    public int leerOpcion() {
        return leerEntero("Seleccione una opción: ");
    }

    public String leerTexto(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String valor = scanner.nextLine().trim();
            if (!valor.isEmpty()) {
                return valor;
            }
            mostrarError("El valor no puede estar vacío.");
        }
    }

    public int leerEntero(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException ex) {
                mostrarError("Ingrese un número entero válido.");
            }
        }
    }

    public double leerDouble(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            try {
                return Double.parseDouble(scanner.nextLine().trim().replace(',', '.'));
            } catch (NumberFormatException ex) {
                mostrarError("Ingrese un número válido.");
            }
        }
    }

    public boolean leerSiNo(String mensaje) {
        while (true) {
            String valor = leerTexto(mensaje + " (s/n): ").toLowerCase();
            if (valor.equals("s") || valor.equals("si") || valor.equals("sí")) {
                return true;
            }
            if (valor.equals("n") || valor.equals("no")) {
                return false;
            }
            mostrarError("Responda s o n.");
        }
    }

    public void mostrarCotizacion(double costo, int diasEntrega) {
        System.out.printf("Costo: Q%.2f | Entrega estimada: %d día(s)%n", costo, diasEntrega);
    }

    public void mostrarEnvios(List<Envio> envios) {
        if (envios.isEmpty()) {
            System.out.println("No hay envíos registrados.");
            return;
        }
        System.out.println("\nCÓDIGO | CATEGORÍA | ESTADO | PESO | INTENTOS");
        for (Envio envio : envios) {
            System.out.printf("%s | %s | %s | %.2f kg | %d/%d%n",
                    envio.getCodigo(), envio.getCategoria(), envio.getEstado(),
                    envio.getPesoRealKg(), envio.getIntentosRealizados(),
                    envio.getMaximoIntentos());
        }
    }

    public void mostrarVehiculos(List<Vehiculo> vehiculos) {
        if (vehiculos.isEmpty()) {
            System.out.println("No hay vehículos registrados.");
            return;
        }
        System.out.println("\nPLACA | CAPACIDAD | REFRIGERADO | DISPONIBLE");
        for (Vehiculo vehiculo : vehiculos) {
            System.out.printf("%s | %.2f kg | %s | %s%n", vehiculo.getPlaca(),
                    vehiculo.getCapacidadMaximaKg(), siNo(vehiculo.isRefrigerado()),
                    siNo(vehiculo.isDisponible()));
        }
    }

    public void mostrarDespachos(List<Despacho> despachos) {
        if (despachos.isEmpty()) {
            System.out.println("No hay despachos.");
            return;
        }
        for (int i = 0; i < despachos.size(); i++) {
            Despacho despacho = despachos.get(i);
            System.out.printf("%d. %s | %s | %.2f kg cargados | %.2f kg disponibles%n",
                    i + 1, despacho.getVehiculo().getPlaca(), despacho.getEstado(),
                    despacho.getPesoCargado(), despacho.getCapacidadDisponible());
        }
    }

    public void mostrarReporte(Map<String, Map<EstadoEnvio, Integer>> conteos,
            double ingresoRegistro, double ingresoReintentos,
            Map<String, Double> ingresosPorCategoria, List<Despacho> activos) {
        System.out.println("\n--- Envíos por categoría y estado ---");
        for (Map.Entry<String, Map<EstadoEnvio, Integer>> categoria : conteos.entrySet()) {
            System.out.println(categoria.getKey() + ": " + categoria.getValue());
        }
        System.out.printf("%nIngresos por registro: Q%.2f%n", ingresoRegistro);
        System.out.printf("Ingresos por reintentos: Q%.2f%n", ingresoReintentos);
        System.out.printf("Ingreso total: Q%.2f%n", ingresoRegistro + ingresoReintentos);
        System.out.println("Ingresos por categoría: " + ingresosPorCategoria);
        System.out.println("\n--- Despachos activos ---");
        mostrarDespachos(activos);
    }

    public void mostrarMensaje(String mensaje) {
        System.out.println(mensaje);
    }

    public void mostrarError(String mensaje) {
        System.out.println("Error: " + mensaje);
    }

    private String siNo(boolean valor) {
        return valor ? "sí" : "no";
    }
}
