package mx.com.srosales.junit5app.ejemplos.models;

import mx.com.srosales.junit5app.ejemplos.exceptions.DineroInsuficienteException;
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
        //El saldo no es nulo
        assertNotNull(cuenta.getSaldo());
        //El saldo tiene el valor esperado
        assertEquals(1000.12234, cuenta.getSaldo().doubleValue());
        //El saldo no es negativo
        assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
        assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    void textReferenciaCuenta() {
        Cuenta cuenta1 = new Cuenta("John Lennon", new BigDecimal("8999.87"));
        Cuenta cuenta2 = new Cuenta("John Lennon", new BigDecimal("8999.87"));
        //Cuenta cuenta2 = new Cuenta("John Legend", new BigDecimal("8999.87"));

        //assertNotEquals(cuenta2, cuenta1);
        assertEquals(cuenta1, cuenta2);
    }

    @Test
    void testDebitoCuenta() {
        Cuenta cuenta = new Cuenta("Sharon", new BigDecimal("1000.1234"));
        cuenta.debito(new BigDecimal(100));
        assertNotNull(cuenta.getSaldo());
        assertEquals(900, cuenta.getSaldo().intValue());
        assertEquals("900.1234", cuenta.getSaldo().toPlainString());
    }

    @Test
    void testCreditoCuenta() {
        Cuenta cuenta = new Cuenta("Sharon", new BigDecimal("1000.1234"));
        cuenta.credito(new BigDecimal(100));
        assertNotNull(cuenta.getSaldo());
        assertEquals(1100, cuenta.getSaldo().intValue());
        assertEquals("1100.1234", cuenta.getSaldo().toPlainString());
    }

    @Test
    void testDineroInsuficienteExceptionCuenta() {
        Cuenta cuenta = new Cuenta("Sharon", new BigDecimal("1000.1234"));
        //Evalua si se lanza la excepcion
        Exception exception = assertThrows(DineroInsuficienteException.class, ()-> {
            cuenta.debito(new BigDecimal("1500.00"));
        });
        //Evalua que el mensaje de error sea el correcto
        String real = exception.getMessage();
        String esperado = "Dinero Insuficiente";
        assertEquals(esperado, real);
    }
}