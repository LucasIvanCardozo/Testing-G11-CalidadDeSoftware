package persistencia;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.Before;
import org.junit.Test;

import modelo.modeloDominio.personas.asociado.Asociado;
import modelo.modeloDominio.util.Domicilio;

/**
 * Pruebas de integración de {@link DataAccessObject} contra un motor
 * MySQL/MariaDB real. Cada caso parte de la semilla de 20 registros que
 * reconstruye <code>iniciarBD()</code>.
 *
 * <p>El escenario de actualización usa la fila del DNI <code>11223344</code>
 * (id 3) y no la del id 1: es lo que permite detectar que la operación ignora
 * el criterio único indicado.</p>
 */
public class DataAccessObjectTest {

    private static final int CANTIDAD_SEMILLA = 20;
    private static final String DNI_FILA_UNO = "12345678";
    private static final String DNI_FILA_TRES = "11223344";

    private DataAccessObject dao;

    @Before
    public void setUp() throws SQLException {
        BaseDeDatos.getInstancia().iniciarBD();
        dao = new DataAccessObject();
    }

    @Test
    public void testGuardarIncorporaElAsociado() throws SQLException {
        dao.guardar(crearAsociado("Nuevo", "Registro", "90000001", "Rivadavia", 100,
                "Mar del Plata", "2235000001"));

        assertEquals("La tabla debe pasar a 21 registros",
                CANTIDAD_SEMILLA + 1, contarAsociados());
        assertEquals("Nuevo", leerNombrePorDni("90000001"));
    }

    @Test
    public void testGuardarConDniDuplicadoLanzaSQLException() throws SQLException {
        VerificacionDeExcepcion.rechazar("un DNI ya existente", SQLException.class,
                () -> dao.guardar(crearAsociado("Duplicado", "Registro", DNI_FILA_UNO, "Rivadavia", 100,
                        "Mar del Plata", "2235000002")));

        assertEquals("La tabla no debe cambiar", CANTIDAD_SEMILLA, contarAsociados());
    }

    @Test
    public void testCargarBDevuelveTodaLaSemilla() throws SQLException {
        assertEquals(CANTIDAD_SEMILLA, dao.cargarBD().size());
    }

    @Test
    public void testCargarConLimiteDevuelveLaCantidadPedida() throws SQLException {
        assertEquals(5, dao.cargarConLimite(5).size());
    }

    @Test
    public void testCargarDevuelveElAsociadoIndicado() throws SQLException {
        AsociadoDTO aDTO = dao.cargar(3);
        assertNotNull("Deberia devolver el asociado del id 3", aDTO);
        assertEquals(DNI_FILA_TRES, aDTO.getDni());
        assertEquals("Carlos", aDTO.getNombre());
    }

    @Test
    public void testActualizarModificaLaFilaDelDniIndicado() throws SQLException {
        dao.actualizar(crearAsociado("NombreActualizado", "Observer", DNI_FILA_TRES, "Colon", 456,
                "Mar del Plata", "2235999999"));

        assertEquals("Debe cambiar la fila del DNI indicado",
                "NombreActualizado", leerNombrePorDni(DNI_FILA_TRES));
        assertEquals("No debe tocar otras filas",
                "Juan", leerNombrePorDni(DNI_FILA_UNO));
    }

    @Test
    public void testEliminarQuitaElAsociadoIndicado() throws SQLException {
        dao.eliminar(DNI_FILA_UNO);
        assertEquals(CANTIDAD_SEMILLA - 1, contarAsociados());
    }

    @Test
    public void testEliminarConDniNuloEsRechazado() {
        VerificacionDeExcepcion.rechazar("un DNI nulo", IllegalArgumentException.class,
                () -> dao.eliminar(null));
    }

    private Asociado crearAsociado(String nombre, String apellido, String dni, String calle,
                                   int numero, String ciudad, String telefono) {
        return new Asociado(nombre, apellido, dni, telefono, new Domicilio(calle, numero, ciudad));
    }

    private int contarAsociados() throws SQLException {
        Statement sentencia = BaseDeDatos.getInstancia().getConexion().createStatement();
        ResultSet resultado = sentencia.executeQuery("SELECT COUNT(*) FROM Asociados;");
        resultado.next();
        int total = resultado.getInt(1);
        resultado.close();
        sentencia.close();
        return total;
    }

    private String leerNombrePorDni(String dni) throws SQLException {
        PreparedStatement sentencia = BaseDeDatos.getInstancia().getConexion()
                .prepareStatement("SELECT nombre FROM Asociados WHERE dni = ? ;");
        sentencia.setString(1, dni);
        ResultSet resultado = sentencia.executeQuery();
        String nombre = null;
        if (resultado.next()) {
            nombre = resultado.getString(1);
        }
        resultado.close();
        sentencia.close();
        return nombre;
    }
}
