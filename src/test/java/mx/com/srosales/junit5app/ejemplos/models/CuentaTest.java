package mx.com.srosales.junit5app.ejemplos.models;

import mx.com.srosales.junit5app.ejemplos.exceptions.DineroInsuficienteException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;

//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CuentaTest {
    Cuenta cuenta;
    private TestInfo testInfo;
    private TestReporter testReporter;

    @BeforeEach
    void initMetodoTest(TestInfo testInfo, TestReporter testReporter) {
        this.cuenta = new Cuenta("Sharon", new BigDecimal("1000.12345"));
        this.testInfo = testInfo;
        this.testReporter = testReporter;
        System.out.println("Iniciando el método");
        testReporter.publishEntry("Ejecutando " + testInfo.getDisplayName() + " " + testInfo.getTestMethod().orElse(null).getName()
                + " con las etiquetas " + testInfo.getTags());
    }

    @AfterEach
    void tearDown() {
        System.out.println("Finalizando método de prueba");
    }

    @BeforeAll
    static void beforeAll() {
        System.out.println("Inicializando el test");
    }

    @AfterAll
    static void afterAll() {
        System.out.println("Finalizando el test");
    }

    @Tag("cuenta")//Etiqueta aplicada a todos los metodos de la clase
    @Nested
    @DisplayName("Probando atributos de la cuenta corriente")
    class CuentaTestNombreSaldo {
        /**
         * Método default encapsulado en el entorno de ejecución de pruebas
         */
        @Test
        @DisplayName("Probando nombre")
        void testNombreCuenta() {
            testReporter.publishEntry(testInfo.getTags().toString());
            if (testInfo.getTags().contains("cuenta")) {
                testReporter.publishEntry("Hacer algo con la etiqueta cuenta");
            }
//        cuenta.setPersona("Sharon");
            String expectativa = "Sharon";
            String realidad = cuenta.getPersona();
            assertNotNull(realidad, ()->"La cuenta no puede ser nula");
            assertEquals(expectativa, realidad, ()->"El nombre d ela cuenta no es el que se esperaba; se esperaba");
            assertTrue(realidad.equals("Sharon"), ()->"Nombre cuenta esperada debe ser igual a la real");
        }

        @Test
        @DisplayName("Probando el saldo, que no sea null, mayor que cero, valor esperado")
        void testSaldoCuenta() {
            //El saldo no es nulo
            assertNotNull(cuenta.getSaldo());
            //El saldo tiene el valor esperado
            assertEquals(1000.12345, cuenta.getSaldo().doubleValue());
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
    }

    @Nested
    class CuentaOperacionesTest {
        @Tag("cuenta")//Etiqueta aplicada al metodo testDebitoCuenta
        @Test
        void testDebitoCuenta() {
            cuenta = new Cuenta("Sharon", new BigDecimal("1000.1234"));
            cuenta.debito(new BigDecimal(100));
            assertNotNull(cuenta.getSaldo());
            assertEquals(900, cuenta.getSaldo().intValue());
            assertEquals("900.1234", cuenta.getSaldo().toPlainString());
        }

        @Tag("cuenta")
        @Test
        void testCreditoCuenta() {
            cuenta = new Cuenta("Sharon", new BigDecimal("1000.1234"));
            cuenta.credito(new BigDecimal(100));
            assertNotNull(cuenta.getSaldo());
            assertEquals(1100, cuenta.getSaldo().intValue());
            assertEquals("1100.1234", cuenta.getSaldo().toPlainString());
        }

        @Tag("cuenta")
        @Tag("banco")//Pueden aplicarse varias etiquetas
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
    }

    @Tag("cuenta")
    @Tag("error")
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
    @Tag("cuenta")
    @Tag("banco")
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

    @Nested
    class SistemaOperativoTest {
        //Test condicionales segun sistema operativo
        @Test
        @EnabledOnOs(OS.WINDOWS)
        void testSoloWindows() {}

        @Test
        @EnabledOnOs({OS.LINUX, OS.MAC})
        void testSoloLinuxMac() {}

        @Test
        @DisabledOnOs(OS.WINDOWS)
        void testNoWindows() {}
    }

    @Nested
    class JavaVersionTest {
        @Test
        @EnabledOnJre(JRE.JAVA_8)
        void soloJDK8() {
        }

        @Test
        @EnabledOnJre(JRE.JAVA_11)
        void soloJDK15() {
        }

        @Test
        @DisabledOnJre(JRE.JAVA_8)
        void testNoJDK8() {
        }
    }

    @Nested
    class SystemPropertiesTest {
        @Test
        void imprimirSystemProperties() {
            Properties properties = System.getProperties();
            properties.forEach((k, v) -> System.out.println(k + ": " + v));
        }

        @Test
        @EnabledIfSystemProperty(named = "java.version", matches = ".*1.8.*")
        void testJavaVersion() {
        }

        @Test
        @DisabledIfSystemProperty(named = "os.arch", matches = ".*32.*")
        void testSolo64() {
        }

        @Test
        @EnabledIfSystemProperty(named = "os.arch", matches = ".*32.*")
        void testNo64() {
        }

        @Test
        @EnabledIfSystemProperty(named = "user.name", matches = "sharon")
        void testUserName() {
        }

        @Test
        @EnabledIfSystemProperty(named = "ENV", matches = "dev")
        void testDev() {
        }
    }

    @Nested
    class VariableAmbienteTest {
        @Test
        void imprimirVariablesAmbiente() {
            Map<String, String> enviroment = System.getenv();
            enviroment.forEach((k, v) -> System.out.println(k + ": \t" + v));
        }

        @Test
        @EnabledIfEnvironmentVariable(named = "JAVA_HOME", matches = ".*jdk1.8.*")
        void testJavaHome() {
        }

        @Test
        @EnabledIfEnvironmentVariable(named = "NUMBER_OF_PROCESSORS", matches = "10")
        void testProcesadores() {
        }

        @Test
        @EnabledIfEnvironmentVariable(named = "ENVIRONMENT", matches = "prod")
        void testEnv() {
        }

        @Test
        @DisabledIfEnvironmentVariable(named = "ENVIRONMENT", matches = "prod")
        void testEnvProdDisabled() {
        }

        @Test
        @DisplayName("Probando el saldo de la cuenta corriente en entorno de desarrollo")
        void testSaldoCuentaDev() {
            boolean esDev = "dev".equals(System.getProperty("ENV"));
            //Si esDev es true, se ejecuta la prueba. De lo contrario se deshabilita
            assumeTrue(esDev);
            assertNotNull(cuenta.getSaldo());
            assertEquals(1000.12345, cuenta.getSaldo().doubleValue());
            assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }

        @Test
        @DisplayName("Probando el saldo de la cuenta corriente en entorno de desarrollo")
        void testSaldoCuentaDev2() {
            boolean esDev = "dev".equals(System.getProperty("ENV"));
            //Si esDev es true, se ejecutan la pruebas dentro de la expresion lambda. De lo contrario se deshabilita
            assumingThat(esDev, () -> {
                assertNotNull(cuenta.getSaldo());
                assertEquals(1000.12345, cuenta.getSaldo().doubleValue());
            });
            //Los asserts que estan fuera del metodo assuming se ejecutan de todos modos
            assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }
    }

    @DisplayName("Probando Debito Cuenta Repetir")
    @RepeatedTest(value = 5, name = "Repeticion # {currentRepetition} de {totalRepetitions}")
    void testDebitoCuentaRepetir(RepetitionInfo info) {
        if (info.getCurrentRepetition() == 3) {
            System.out.println("Estamos en la repeticion " + info.getCurrentRepetition());
        }
        cuenta = new Cuenta("Sharon", new BigDecimal("1000.1234"));
        cuenta.debito(new BigDecimal(100));
        assertNotNull(cuenta.getSaldo());
        assertEquals(900, cuenta.getSaldo().intValue());
        assertEquals("900.1234", cuenta.getSaldo().toPlainString());
    }

    @Tag("param")//Etiqueta aplicada a todos los metodos de la clase
    @Nested
    class PruebasParametrizadasTest {
        @ParameterizedTest(name = "Repeticion {index} ejecutando con valor {0} - {argumentsWithNames}")
        @ValueSource(strings = {"100", "200", "300", "500", "700", "1000.12345"})
        void testDebitoCuentaValueSource(String monto) {
            cuenta.debito(new BigDecimal(monto));
            assertNotNull(cuenta.getSaldo());
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }

        @ParameterizedTest(name = "Repeticion {index} ejecutando con valor {0} - {argumentsWithNames}")
        @CsvSource({"1, 100", "2, 200", "3, 300", "4, 500", "5, 700", "6, 1000.12345"})
        void testDebitoCuentaCsvSource(String index, String monto) {
            System.out.println(index + " -> " + monto);
            cuenta.debito(new BigDecimal(monto));
            assertNotNull(cuenta.getSaldo());
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }

        @ParameterizedTest(name = "Repeticion {index} ejecutando con valor {0} - {argumentsWithNames}")
        @CsvFileSource(resources = "/data.csv")
        void testDebitoCuentaCsvFile(String monto) {
            cuenta.debito(new BigDecimal(monto));
            assertNotNull(cuenta.getSaldo());
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }

        @ParameterizedTest(name = "Repeticion {index} ejecutando con valor {0} - {argumentsWithNames}")
        @MethodSource("montoList")//Nombre de metodo que devuelve una lista de montos
        void testDebitoCuentaMethodSource(String monto) {
            cuenta.debito(new BigDecimal(monto));
            assertNotNull(cuenta.getSaldo());
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }

        @ParameterizedTest(name = "Repeticion {index} ejecutando con valor {0} - {argumentsWithNames}")
        @CsvSource({"200, 100, John, Andres", "250, 200, Pepe, Pepe", "310, 300, Maria, maria", "510, 500, Lucas, Luca", "750, 700, Pepa, Pepe", "1000.12345, 1000.12345, Sharon, Rubi"})
        void testDebitoCuentaCsvSource2(String saldo, String monto, String esperado, String actual) {
            System.out.println(saldo + " -> " + monto);
            cuenta.setSaldo(new BigDecimal(saldo));
            cuenta.debito(new BigDecimal(monto));
            cuenta.setPersona(actual);
            assertNotNull(cuenta.getSaldo());
            assertNotNull(cuenta.getPersona());
            assertEquals(esperado, actual);
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }

        @ParameterizedTest(name = "Repeticion {index} ejecutando con valor {0} - {argumentsWithNames}")
        @CsvFileSource(resources = "/data2.csv")
        void testDebitoCuentaCsvFile2(String saldo, String monto, String esperado, String actual) {
            cuenta.setSaldo(new BigDecimal(saldo));
            cuenta.debito(new BigDecimal(monto));
            cuenta.setPersona(actual);
            assertNotNull(cuenta.getSaldo());
            assertNotNull(cuenta.getPersona());
            assertEquals(esperado, actual);
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }
    }

    private static List<String> montoList() {
        return Arrays.asList("100", "200", "300", "500", "700", "1000.12345");
    }
}