public final class Paquete extends Envio {
    private final double largoCm;
    private final double anchoCm;
    private final double altoCm;
    private final boolean fragil;
    private final double valorDeclarado;

    public Paquete(
            String codigo,
            String remitente,
            String destinatario,
            double pesoRealKg,
            TipoDestino destino,
            double largoCm,
            double anchoCm,
            double altoCm,
            boolean fragil,
            double valorDeclarado
    ) {
        super(codigo, remitente, destinatario, pesoRealKg, destino);

        validarPositivo(largoCm, "largo");
        validarPositivo(anchoCm, "ancho");
        validarPositivo(altoCm, "alto");

        if (!Double.isFinite(largoCm * anchoCm * altoCm)) {
            throw new IllegalArgumentException(
                    "El volumen excede el rango permitido."
            );
        }

        if (fragil) {
            validarPositivo(valorDeclarado, "valor declarado");
        } else if (valorDeclarado != 0) {
            throw new IllegalArgumentException(
                    "Un paquete normal no requiere valor declarado."
            );
        }

        this.largoCm = largoCm;
        this.anchoCm = anchoCm;
        this.altoCm = altoCm;
        this.fragil = fragil;
        this.valorDeclarado = valorDeclarado;
    }

    public double calcularPesoVolumetrico() {
        return largoCm * anchoCm * altoCm / 5000.0;
    }

    public int calcularKgFacturables() {
        double kg = Math.ceil(
                Math.max(getPesoRealKg(), calcularPesoVolumetrico())
        );

        if (kg > Integer.MAX_VALUE) {
            throw new IllegalArgumentException(
                    "Peso facturable excesivo."
            );
        }

        return (int) kg;
    }

    @Override
    public double calcularCosto() {
        double base = getDestino() == TipoDestino.URBANO ? 25 : 40;
        double costo = base + 8.0 * calcularKgFacturables();

        if (fragil) {
            costo += 15 + 0.03 * valorDeclarado;
        }

        if (!Double.isFinite(costo)) {
            throw new IllegalArgumentException(
                    "Costo fuera de rango."
            );
        }

        return costo;
    }

    @Override
    public int calcularDiasEntrega() {
        int diasBase = getDestino() == TipoDestino.URBANO ? 2 : 4;
        return diasBase + (fragil ? 1 : 0);
    }

    @Override
    public int getMaximoIntentos() {
        return fragil ? 2 : 3;
    }

    @Override
    public String getCategoria() {
        return fragil ? "Paquete frágil" : "Paquete";
    }

    public boolean isFragil() {
        return fragil;
    }

    public double getValorDeclarado() {
        return valorDeclarado;
    }
}
