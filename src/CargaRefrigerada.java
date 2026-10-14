public final class CargaRefrigerada extends Envio {
    private final double temperaturaRequerida;

    public CargaRefrigerada(String codigo, String remitente, String destinatario,double pesoRealKg, TipoDestino destino, double temperaturaRequerida
    ) {
        super(codigo, remitente, destinatario, pesoRealKg, destino);

        if (destino != TipoDestino.URBANO) {
            throw new IllegalArgumentException(
                    "La carga refrigerada solo admite destino urbano."
            );
        }

        if (!Double.isFinite(temperaturaRequerida)
                || temperaturaRequerida < -25
                || temperaturaRequerida > 8) {
            throw new IllegalArgumentException(
                    "La temperatura debe estar entre -25 y 8 °C."
            );
        }

        this.temperaturaRequerida = temperaturaRequerida;
    }

    @Override
    public double calcularCosto() {
        double costo = 25 + 12 * getPesoRealKg() + 60;

        if (!Double.isFinite(costo)) {
            throw new IllegalArgumentException(
                    "Costo fuera de rango."
            );
        }

        return costo;
    }

    @Override
    public int calcularDiasEntrega() {
        return 1;
    }

    @Override
    public int getMaximoIntentos() {
        return 1;
    }

    @Override
    public String getCategoria() {
        return "Carga refrigerada";
    }

    @Override
    public boolean esCompatibleCon(Vehiculo vehiculo) {
        return super.esCompatibleCon(vehiculo)
                && vehiculo.isRefrigerado();
    }

    public double getTemperaturaRequerida() {
        return temperaturaRequerida;
    }
}
