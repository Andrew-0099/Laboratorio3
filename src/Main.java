public final class Main {
    private Main() {
    }

    public static void main(String[] args) {
        SistemaEnvios sistema = new SistemaEnvios();
        cargarDatosIniciales(sistema);
        new ControladorEnvios(sistema, new VistaConsola()).ejecutar();
    }

    private static void cargarDatosIniciales(SistemaEnvios sistema) {
        sistema.agregarEnvioInicial(new Sobre(
                "S-001", "Ana", "Luis", 0.20, TipoDestino.URBANO));
        sistema.agregarEnvioInicial(new Sobre(
                "S-002", "Marta", "José", 0.45, TipoDestino.DEPARTAMENTAL));
        sistema.agregarEnvioInicial(new Paquete(
                "P-001", "Pedro", "Lucía", 3.2, TipoDestino.URBANO,
                40, 30, 20, false, 0));
        sistema.agregarEnvioInicial(new Paquete(
                "P-002", "Elena", "Carlos", 2.0, TipoDestino.DEPARTAMENTAL,
                20, 20, 20, false, 0));
        sistema.agregarEnvioInicial(new Paquete(
                "PF-001", "Sofía", "Mario", 3.2, TipoDestino.URBANO,
                40, 30, 20, true, 1200));
        sistema.agregarEnvioInicial(new Paquete(
                "PF-002", "Jorge", "Diana", 1.5, TipoDestino.DEPARTAMENTAL,
                25, 20, 15, true, 800));
        sistema.agregarEnvioInicial(new CargaRefrigerada(
                "R-001", "Hospital A", "Clínica B", 10, TipoDestino.URBANO, 4));
        sistema.agregarEnvioInicial(new CargaRefrigerada(
                "R-002", "Laboratorio C", "Hospital D", 6, TipoDestino.URBANO, -18));

        sistema.registrarVehiculo(new Vehiculo("P-101ABC", 30, false));
        sistema.registrarVehiculo(new Vehiculo("P-202DEF", 80, true));
        sistema.registrarVehiculo(new Vehiculo("P-303GHI", 120, false));
    }
}
