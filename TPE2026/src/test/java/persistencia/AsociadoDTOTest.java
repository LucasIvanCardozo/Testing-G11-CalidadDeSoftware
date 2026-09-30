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

    @Test(expected = IllegalArgumentException.class)
    public void testSetNombre_Nulo_Clase2() {
        dto.setNombre(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetApellido_Nulo_Clase2() {
        dto.setApellido(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetDni_Nulo_Clase2() {
        dto.setDni(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetCiudad_Nulo_Clase2() {
        dto.setCiudad(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetCalle_Nulo_Clase2() {
        dto.setCalle(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetTelefono_Nulo_Clase2() {
        dto.setTelefono(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetNombre_Vacio_Clase3() {
        dto.setNombre("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetApellido_Vacio_Clase3() {
        dto.setApellido("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetDni_Vacio_Clase3() {
        dto.setDni("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetCiudad_Vacio_Clase3() {
        dto.setCiudad("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetCalle_Vacio_Clase3() {
        dto.setCalle("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetTelefono_Vacio_Clase3() {
        dto.setTelefono("");
    }

    /** Valor límite: 0 está dentro del dominio del SRS (numero >= 0). */
    @Test
    public void testSetNumero_LimiteCero_Clase4() {
        dto.setNumero(0);
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
    @Test(expected = IllegalArgumentException.class)
    public void testSetNumero_NegativoUno_Clase5() {
        dto.setNumero(-1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetNumero_Minimo_Clase5() {
        dto.setNumero(Integer.MIN_VALUE);
    }
}
