package persistencia;

import static org.junit.Assert.fail;

/**
 * Verificación de excepciones esperadas con mensaje propio.
 *
 * <p>Se usa la Opción 2 de la cátedra (<code>try-catch</code> + <code>fail</code>,
 * <code>Teoria/Semana 3/Apuntes teoricos/Casos de Prueba JUnit -apuntes.md</code>
 * §3.2) en lugar de <code>@Test(expected = …)</code>, porque esa anotación no
 * admite texto propio y la corrida queda sin la regla que se estaba verificando.</p>
 *
 * <p>Además intercepta la excepción inesperada: si el método bajo prueba lanza
 * algo distinto de lo esperado, el fallo lo nombra en lugar de propagarlo sin
 * contexto.</p>
 */
final class VerificacionDeExcepcion {

    /** Llamada bajo prueba, que puede lanzar excepciones verificadas. */
    @FunctionalInterface
    interface Llamada {
        void ejecutar() throws Exception;
    }

    private VerificacionDeExcepcion() {
    }

    /**
     * Exige que la llamada lance una excepción del tipo indicado.
     *
     * @param caso    descripción del caso, para el mensaje de fallo
     * @param esperada tipo de excepción que el contrato exige
     * @param llamada método bajo prueba
     * @return la excepción lanzada, para poder verificar algo más sobre ella
     */
    static Throwable rechazar(String caso, Class<? extends Throwable> esperada, Llamada llamada) {
        Throwable lanzada = null;
        try {
            llamada.ejecutar();
        } catch (Throwable capturada) {
            lanzada = capturada;
        }
        if (lanzada == null) {
            fail("Se esperaba " + esperada.getSimpleName() + " para " + caso
                    + ", pero no se lanzo ninguna excepcion");
        }
        if (!esperada.isInstance(lanzada)) {
            fail("Se esperaba " + esperada.getSimpleName() + " para " + caso
                    + ", pero se lanzo " + lanzada);
        }
        return lanzada;
    }

    /**
     * Exige que la llamada se complete sin lanzar nada.
     *
     * @param caso    descripción del caso, para el mensaje de fallo
     * @param llamada método bajo prueba
     */
    static void aceptar(String caso, Llamada llamada) {
        try {
            llamada.ejecutar();
        } catch (Throwable lanzada) {
            fail("Se esperaba que " + caso + " fuera aceptado, pero se lanzo " + lanzada);
        }
    }
}
