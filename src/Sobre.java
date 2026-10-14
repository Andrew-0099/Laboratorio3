public final class Sobre extends Envio {

    public Sobre(
            String codigo,
            String remitente,
            String destinatario,
            double pesoRealKg,
            TipoDestino destino
    ) {
        super(codigo, remitente, destinatario, pesoRealKg, destino);

        if (pesoRealKg > 0.5) {
            throw new IllegalArgumentException(
                    "Un sobre no puede superar 0.5 kg."
            );
        }
    }

    @Override
    public double calcularCosto() {
        return getDestino() == TipoDestino.URBANO ? 20 : 35;
    }

    @Override
    public int calcularDiasEntrega() {
        return getDestino() == TipoDestino.URBANO ? 1 : 2;
    }

    @Override
    public int getMaximoIntentos() {
        return 3;
    }

    @Override
    public String getCategoria() {
        return "Sobre";
    }
}
