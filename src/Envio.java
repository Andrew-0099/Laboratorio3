public abstract class Envio {
    private final String codigo;
    private final String remitente;
    private final String destinatario;
    private final double pesoRealKg;
    private final TipoDestino destino;

    private EstadoEnvio estado;
    private int intentosRealizados;

    protected Envio(String codigo,String remitente,String destinatario,double pesoRealKg,TipoDestino destino) {
        this.codigo = textoObligatorio(codigo, "código");
        this.remitente = textoObligatorio(remitente, "remitente");
        this.destinatario = textoObligatorio(destinatario, "destinatario");

        validarPositivo(pesoRealKg, "peso real");

        if (destino == null) {
            throw new IllegalArgumentException("El destino es obligatorio.");
        }

        this.pesoRealKg = pesoRealKg;
        this.destino = destino;
        this.estado = EstadoEnvio.BODEGA;
        this.intentosRealizados = 0;
    }

    protected static String textoObligatorio(String valor, String campo) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "El " + campo + " no puede estar vacío."
            );
        }

        return valor.trim();
    }

    protected static void validarPositivo(double valor, String campo) {
        if (!Double.isFinite(valor) || valor <= 0) {
            throw new IllegalArgumentException(
                    "El " + campo + " debe ser un número positivo y finito."
            );
        }
    }

    public abstract double calcularCosto();

    public abstract int calcularDiasEntrega();

    public abstract int getMaximoIntentos();

    public abstract String getCategoria();

    public boolean esCompatibleCon(Vehiculo vehiculo) {
        if (vehiculo == null) {
            throw new IllegalArgumentException("El vehículo es obligatorio.");
        }

        return true;
    }

    public final void asignar() {
        if (estado != EstadoEnvio.BODEGA
                || intentosRealizados >= getMaximoIntentos()) {
            throw new IllegalStateException(
                    "El envío no está disponible para asignación."
            );
        }

        estado = EstadoEnvio.ASIGNADO;
    }

    public final void retirar() {
        if (estado != EstadoEnvio.ASIGNADO) {
            throw new IllegalStateException(
                    "Solo puede retirarse un envío asignado."
            );
        }

        estado = EstadoEnvio.BODEGA;
    }

    // Devuelve true si este intento es un reintento que debe cobrarse.
    public final boolean iniciarIntento() {
        if (estado != EstadoEnvio.ASIGNADO
                || intentosRealizados >= getMaximoIntentos()) {
            throw new IllegalStateException(
                    "El envío no puede iniciar otro intento."
            );
        }

        intentosRealizados++;
        estado = EstadoEnvio.EN_RUTA;

        return intentosRealizados > 1;
    }

    public final void registrarEntrega(boolean entregado) {
        if (estado != EstadoEnvio.EN_RUTA) {
            throw new IllegalStateException(
                    "Solo un envío en ruta admite un resultado."
            );
        }

        if (entregado) {
            estado = EstadoEnvio.ENTREGADO;
        } else if (intentosRealizados >= getMaximoIntentos()) {
            estado = EstadoEnvio.DEVUELTO;
        } else {
            estado = EstadoEnvio.BODEGA;
        }
    }

    public final String getCodigo() {
        return codigo;
    }

    public final String getRemitente() {
        return remitente;
    }

    public final String getDestinatario() {
        return destinatario;
    }

    public final double getPesoRealKg() {
        return pesoRealKg;
    }

    public final TipoDestino getDestino() {
        return destino;
    }

    public final EstadoEnvio getEstado() {
        return estado;
    }

    public final int getIntentosRealizados() {
        return intentosRealizados;
    }
}
