package persistencia;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

/**
 * Pruebas unitarias de caja negra sobre {@link AsociadoDTO}.
 *
 * <p>Un dato inválido se rechaza lanzando {@link IllegalArgumentException}. El
 * código provisto valida con <code>assert</code>, de ahí el rojo de las clases
 * erróneas y del valor límite 0.</p>
 */
public class AsociadoDTOTest {

    private AsociadoDTO dto;

    @Before
    public void setUp() {
        dto = new AsociadoDTO();
    }

    @Test
    public void testAsignacionYRecuperacion_Nombre_Clase1() {
        dto.setNombre("Juan");
        assertEquals("El nombre asignado no se recupera", "Juan", dto.getNombre());
    }

    @Test
    public void testAsignacionYRecuperacion_Apellido_Clase1() {
        dto.setApellido("Perez");
        assertEquals("El apellido asignado no se recupera", "Perez", dto.getApellido());
    }

    @Test
    public void testAsignacionYRecuperacion_Dni_Clase1() {
        dto.setDni("12345678");
        assertEquals("El dni asignado no se recupera", "12345678", dto.getDni());
    }

    @Test
    public void testAsignacionYRecuperacion_Ciudad_Clase1() {
        dto.setCiudad("Mar del Plata");
        assertEquals("La ciudad asignada no se recupera", "Mar del Plata", dto.getCiudad());
    }

    @Test
    public void testAsignacionYRecuperacion_Calle_Clase1() {
        dto.setCalle("Belgrano");
        assertEquals("La calle asignada no se recupera", "Belgrano", dto.getCalle());
    }

    @Test
    public void testAsignacionYRecuperacion_Telefono_Clase1() {
        dto.setTelefono("2235551234");
        assertEquals("El telefono asignado no se recupera", "2235551234", dto.getTelefono());
    }

    @Test
    public void testSetNombre_Nulo_Clase2() {
        VerificacionDeExcepcion.rechazar("un nombre nulo", IllegalArgumentException.class,
                () -> dto.setNombre(null));
    }

    @Test
    public void testSetApellido_Nulo_Clase2() {
        VerificacionDeExcepcion.rechazar("un apellido nulo", IllegalArgumentException.class,
                () -> dto.setApellido(null));
    }

    @Test
    public void testSetDni_Nulo_Clase2() {
        VerificacionDeExcepcion.rechazar("un dni nulo", IllegalArgumentException.class,
                () -> dto.setDni(null));
    }

    @Test
    public void testSetCiudad_Nulo_Clase2() {
        VerificacionDeExcepcion.rechazar("una ciudad nula", IllegalArgumentException.class,
                () -> dto.setCiudad(null));
    }

    @Test
    public void testSetCalle_Nulo_Clase2() {
        VerificacionDeExcepcion.rechazar("una calle nula", IllegalArgumentException.class,
                () -> dto.setCalle(null));
    }

    @Test
    public void testSetTelefono_Nulo_Clase2() {
        VerificacionDeExcepcion.rechazar("un telefono nulo", IllegalArgumentException.class,
                () -> dto.setTelefono(null));
    }

    @Test
    public void testSetNombre_Vacio_Clase3() {
        VerificacionDeExcepcion.rechazar("un nombre vacio", IllegalArgumentException.class,
                () -> dto.setNombre(""));
    }

    @Test
    public void testSetApellido_Vacio_Clase3() {
        VerificacionDeExcepcion.rechazar("un apellido vacio", IllegalArgumentException.class,
                () -> dto.setApellido(""));
    }

    @Test
    public void testSetDni_Vacio_Clase3() {
        VerificacionDeExcepcion.rechazar("un dni vacio", IllegalArgumentException.class,
                () -> dto.setDni(""));
    }

    @Test
    public void testSetCiudad_Vacio_Clase3() {
        VerificacionDeExcepcion.rechazar("una ciudad vacia", IllegalArgumentException.class,
                () -> dto.setCiudad(""));
    }

    @Test
    public void testSetCalle_Vacio_Clase3() {
        VerificacionDeExcepcion.rechazar("una calle vacia", IllegalArgumentException.class,
                () -> dto.setCalle(""));
    }

    @Test
    public void testSetTelefono_Vacio_Clase3() {
        VerificacionDeExcepcion.rechazar("un telefono vacio", IllegalArgumentException.class,
                () -> dto.setTelefono(""));
    }

    /** Valor límite: 0 está dentro del dominio del SRS (numero >= 0). */
    @Test
    public void testSetNumero_LimiteCero_Clase4() {
        VerificacionDeExcepcion.aceptar("el numero 0, limite inferior del dominio del SRS",
                () -> dto.setNumero(0));
        assertEquals("El numero 0 deberia ser aceptado segun el SRS", 0, dto.getNumero());
    }

    @Test
    public void testSetNumero_Positivo_Clase4() {
        dto.setNumero(1);
        assertEquals("El numero 1 no se recupera", 1, dto.getNumero());
    }

    @Test
    public void testSetNumero_Maximo_Clase4() {
        dto.setNumero(Integer.MAX_VALUE);
        assertEquals("El numero maximo no se recupera", Integer.MAX_VALUE, dto.getNumero());
    }

    /** Valor límite: -1 está justo fuera del dominio del SRS (numero >= 0). */
    @Test
    public void testSetNumero_NegativoUno_Clase5() {
        VerificacionDeExcepcion.rechazar("un numero negativo", IllegalArgumentException.class,
                () -> dto.setNumero(-1));
    }

    @Test
    public void testSetNumero_Minimo_Clase5() {
        VerificacionDeExcepcion.rechazar("el minimo entero", IllegalArgumentException.class,
                () -> dto.setNumero(Integer.MIN_VALUE));
    }
}
