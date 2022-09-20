package mx.com.srosales.junit5app.ejemplos.models;

import mx.com.srosales.junit5app.ejemplos.exceptions.DineroInsuficienteException;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class CuentaTest {
    Cuenta cuenta;

    @BeforeEach
    void initMetodoTest() {
        this.cuenta = new Cuenta("Sharon", new BigDecimal("1000.12345"));
        System.out.println("Iniciando el método");
    }

    @AfterEach
    void tearDown() {
        System.out.println("Finalizando método de prueba");
    }

    /**
     * Método default encapsulado en el entorno de ejecución de pruebas
     */
    @Test
    @DisplayName("Probando nombre de la cuenta corriente")
    void testNombreCuenta() {
//        cuenta.setPersona("Sharon");
        String expectativa = "Sharon";
        String realidad = cuenta.getPersona();
        assertNotNull(realidad, ()->"La cuenta no puede ser nula");
        assertEquals(expectativa, realidad, ()->"El nombre d ela cuenta no es el que se esperaba; se esperaba");
        assertTrue(realidad.equals("Sharon"), ()->"Nombre cuenta esperada debe ser igual a la real");
    }

    @Test
    @DisplayName("Probando el saldo de la cuent corriente, que no sea null, mayor que cero, valor esperado")
    void testSaldoCuenta() {
        cuenta = new Cuenta("Sharon", new BigDecimal("1000.12234"));
        //El saldo no es nulo
        assertNotNull(cuenta.getSaldo());
        //El saldo tiene el valor esperado
        assertEquals(1000.12234, cuenta.getSaldo().doubleValue());
        //El saldo no es negativo
        assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
        assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    @DisplayName("Probando referencias que sean iguales con el metodo equals")
    void textReferenciaCuenta() {
        Cuenta cuenta1 = new Cuenta("John Lennon", new BigDecimal("8999.87"));
        Cuenta cuenta2 = new Cuenta("John Lennon", new BigDecimal("8999.87"));
        //Cuenta cuenta2 = new Cuenta("John Legend", new BigDecimal("8999.87"));

        //assertNotEquals(cuenta2, cuenta1);
        assertEquals(cuenta1, cuenta2);
    }

    @Test
    void testDebitoCuenta() {
        cuenta = new Cuenta("Sharon", new BigDecimal("1000.1234"));
        cuenta.debito(new BigDecimal(100));
        assertNotNull(cuenta.getSaldo());
        assertEquals(900, cuenta.getSaldo().intValue());
        assertEquals("900.1234", cuenta.getSaldo().toPlainString());
    }

    @Test
    void testCreditoCuenta() {
        cuenta = new Cuenta("Sharon", new BigDecimal("1000.1234"));
        cuenta.credito(new BigDecimal(100));
        assertNotNull(cuenta.getSaldo());
        assertEquals(1100, cuenta.getSaldo().intValue());
        assertEquals("1100.1234", cuenta.getSaldo().toPlainString());
    }

    @Test
    void testDineroInsuficienteExceptionCuenta() {
        cuenta = new Cuenta("Sharon", new BigDecimal("1000.1234"));
        //Evalua si se lanza la excepcion
        Exception exception = assertThrows(DineroInsuficienteException.class, ()-> {
            cuenta.debito(new BigDecimal("1500.00"));
        });
        //Evalua que el mensaje de error sea el correcto
        String real = exception.getMessage();
        String esperado = "Dinero Insuficiente";
        assertEquals(esperado, real);
    }

    @Test
    void testTransferirDineroCuentas() {
        Cuenta cuenta1 = new Cuenta("Rubí", new BigDecimal("2500"));
        Cuenta cuenta2 = new Cuenta("Andrés", new BigDecimal("1500.8989"));
        Banco banco = new Banco();
        banco.setNombre("Banco del Estado");
        banco.transferir(cuenta2, cuenta1, new BigDecimal("500"));
        assertEquals("1000.8989", cuenta2.getSaldo().toPlainString());
        assertEquals("3000", cuenta1.getSaldo().toPlainString());
    }

    @Test
    //@Disabled
    @DisplayName("Probando relaciones entre las cuentas y el banco con assertAll")
    void testRelacionBancoCuentas() {
        //fail();//Forza el error
        Cuenta cuenta1 = new Cuenta("Rubí", new BigDecimal("2500"));
        Cuenta cuenta2 = new Cuenta("Andrés", new BigDecimal("1500.8989"));
        Banco banco = new Banco();
        banco.addCuenta(cuenta1);
        banco.addCuenta(cuenta2);
        banco.setNombre("Banco del Estado");
        banco.transferir(cuenta2, cuenta1, new BigDecimal("500"));

        assertAll(
                () -> assertEquals("1000.8989", cuenta2.getSaldo().toPlainString(),
                        ()->"El valor del saldo de la cuenta2 no es el esperado"),
                () -> assertEquals("3000", cuenta1.getSaldo().toPlainString(),
                        ()->"El valor del saldo de la cuenta1 no es el esperado"),
                () -> assertEquals(2, banco.getCuentas().size()),
                () -> assertEquals("Banco del Estado", cuenta1.getBanco().getNombre()),
                () -> {assertEquals("Rubí", banco.getCuentas().stream().filter(cuenta ->
                        cuenta.getPersona().equals("Rubí")
                    ).findFirst().get().getPersona());},
                () -> assertTrue(banco.getCuentas().stream().anyMatch(cuenta -> cuenta.getPersona().equals("Rubí")))
        );
    }
}