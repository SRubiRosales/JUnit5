package mx.com.srosales.junit5app.ejemplos.exceptions;

public class DineroInsuficienteException extends RuntimeException {
    public DineroInsuficienteException(String mensaje) {
        super(mensaje);
    }
}
