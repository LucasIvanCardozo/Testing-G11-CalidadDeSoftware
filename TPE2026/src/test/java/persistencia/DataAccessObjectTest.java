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
    public void testGuardarIncorporaElAsociado_I12() throws SQLException {
        dao.guardar(crearAsociado("Nuevo", "Registro", "90000001", "Rivadavia", 100,
                "Mar del Plata", "2235000001"));

        assertEquals("La tabla debe pasar a 21 registros",
                CANTIDAD_SEMILLA + 1, contarAsociados());
        assertEquals("Nuevo", leerNombrePorDni("90000001"));
    }

    @Test
    public void testGuardarConDniDuplicadoLanzaSQLException_I13() throws SQLException {
        VerificacionDeExcepcion.rechazar("un DNI ya existente", SQLException.class,
                () -> dao.guardar(crearAsociado("Duplicado", "Registro", DNI_FILA_UNO, "Rivadavia", 100,
                        "Mar del Plata", "2235000002")));

        assertEquals("La tabla no debe cambiar", CANTIDAD_SEMILLA, contarAsociados());
    }

    @Test
    public void testCargarBDevuelveTodaLaSemilla_I14() throws SQLException {
        assertEquals(CANTIDAD_SEMILLA, dao.cargarBD().size());
    }

    @Test
    public void testCargarConLimiteDevuelveLaCantidadPedida_I15() throws SQLException {
        assertEquals(5, dao.cargarConLimite(5).size());
    }

    @Test
    public void testCargarDevuelveElAsociadoIndicado_I16() throws SQLException {
        AsociadoDTO aDTO = dao.cargar(3);
        assertNotNull("Deberia devolver el asociado del id 3", aDTO);
        assertEquals(DNI_FILA_TRES, aDTO.getDni());
        assertEquals("Carlos", aDTO.getNombre());
    }

    @Test
    public void testActualizarModificaLaFilaDelDniIndicado_I17() throws SQLException {
        dao.actualizar(crearAsociado("NombreActualizado", "Observer", DNI_FILA_TRES, "Colon", 456,
                "Mar del Plata", "2235999999"));

        assertEquals("Debe cambiar la fila del DNI indicado",
                "NombreActualizado", leerNombrePorDni(DNI_FILA_TRES));
        assertEquals("No debe tocar otras filas",
                "Juan", leerNombrePorDni(DNI_FILA_UNO));
    }

    @Test
    public void testEliminarQuitaElAsociadoIndicado_I18() throws SQLException {
        dao.eliminar(DNI_FILA_UNO);
        assertEquals(CANTIDAD_SEMILLA - 1, contarAsociados());
    }

    @Test
    public void testEliminarConDniNuloEsRechazado_I19() {
        VerificacionDeExcepcion.rechazar("un DNI nulo", IllegalArgumentException.class,
                () -> dao.eliminar(null));
    }

    @Test
    public void testGuardarConAsociadoNuloEsRechazado_I20() throws SQLException {
        VerificacionDeExcepcion.rechazar("un asociado nulo", IllegalArgumentException.class,
                () -> dao.guardar(null));

        assertEquals("La tabla no debe cambiar", CANTIDAD_SEMILLA, contarAsociados());
    }

    @Test
    public void testActualizarConAsociadoNuloEsRechazado_I21() throws SQLException {
        VerificacionDeExcepcion.rechazar("un asociado nulo", IllegalArgumentException.class,
                () -> dao.actualizar(null));

        assertEquals("La tabla no debe cambiar", CANTIDAD_SEMILLA, contarAsociados());
    }

    @Test
    public void testCargarConLimiteCeroEsRechazado_I22() throws SQLException {
        VerificacionDeExcepcion.rechazar("un limite cero", IllegalArgumentException.class,
                () -> dao.cargarConLimite(0));

        assertEquals("La tabla no debe cambiar", CANTIDAD_SEMILLA, contarAsociados());
    }

    @Test
    public void testCargarConLimiteNegativoEsRechazado_I23() throws SQLException {
        VerificacionDeExcepcion.rechazar("un limite negativo", IllegalArgumentException.class,
                () -> dao.cargarConLimite(-1));

        assertEquals("La tabla no debe cambiar", CANTIDAD_SEMILLA, contarAsociados());
    }

    @Test
    public void testCargarConIdCeroEsRechazado_I24() throws SQLException {
        VerificacionDeExcepcion.rechazar("un id cero", IllegalArgumentException.class,
                () -> dao.cargar(0));

        assertEquals("La tabla no debe cambiar", CANTIDAD_SEMILLA, contarAsociados());
    }

    @Test
    public void testCargarConIdNegativoEsRechazado_I25() throws SQLException {
        VerificacionDeExcepcion.rechazar("un id negativo", IllegalArgumentException.class,
                () -> dao.cargar(-1));

        assertEquals("La tabla no debe cambiar", CANTIDAD_SEMILLA, contarAsociados());
    }

    @Test
    public void testCrearDTOConAsociadoNuloEsRechazado_I26() throws SQLException {
        VerificacionDeExcepcion.rechazar("un asociado nulo", IllegalArgumentException.class,
                () -> dao.CrearDTO((Asociado) null));

        assertEquals("La tabla no debe cambiar", CANTIDAD_SEMILLA, contarAsociados());
    }

    @Test
    public void testGuardarConDniDeOnceCaracteresLanzaSQLException_I32() throws SQLException {
        VerificacionDeExcepcion.rechazar("un DNI de once caracteres", SQLException.class,
                () -> dao.guardar(crearAsociado("Nuevo", "Registro", "12345678901", "Rivadavia", 100,
                        "Mar del Plata", "2235000001")));

        assertEquals("La tabla no debe cambiar", CANTIDAD_SEMILLA, contarAsociados());
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
