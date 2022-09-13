package mx.com.srosales.junit5app.ejemplos.models;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class CuentaTest {
    /**
     * Método default encapsulado en el entorno de ejecución de pruebas
     */
    @Test
    void testNombreCuenta() {
        Cuenta cuenta = new Cuenta("Sharon", new BigDecimal("1000.12345"));
//        cuenta.setPersona("Sharon");
        String expectativa = "Sharon";
        String realidad = cuenta.getPersona();
        assertEquals(expectativa, realidad);
        assertTrue(realidad.equals("Sharon"));
    }

    @Test
    void testSaldoCuenta() {
        Cuenta cuenta = new Cuenta("Sharon", new BigDecimal("1000.12234"));
        //El saldo tiene el valor esperado
        assertEquals(1000.12234, cuenta.getSaldo().doubleValue());
        //El saldo no es negativo
        assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
        assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
    }
}