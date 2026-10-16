package persistencia;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.Before;
import org.junit.Test;

/**
 * Pruebas de integración de {@link BaseDeDatos} contra un motor MySQL/MariaDB
 * real en <code>localhost:3306</code> con el usuario <code>progra_c</code>.
 *
 * <p>Cada caso parte de la semilla de 20 registros que reconstruye
 * <code>iniciarBD()</code>. Las verificaciones usan JDBC directo sobre
 * <code>getConexion()</code> como oráculo independiente, sin apoyarse en los
 * métodos bajo prueba.</p>
 */
public class BaseDeDatosTest {

    private static final int CANTIDAD_SEMILLA = 20;

    private BaseDeDatos bd;

    @Before
    public void setUp() {
        bd = BaseDeDatos.getInstancia();
        bd.iniciarBD();
    }

    @Test
    public void testGetInstanciaDevuelveLaMismaReferencia() {
        assertSame("El Singleton debe devolver una unica instancia",
                BaseDeDatos.getInstancia(), BaseDeDatos.getInstancia());
    }

    @Test
    public void testConexionNoEsNula() {
        assertNotNull("La conexion debe estar abierta", bd.getConexion());
    }

    @Test
    public void testIniciarBDEstableceLaSemilla() throws SQLException {
        assertEquals("La semilla debe tener 20 registros",
                CANTIDAD_SEMILLA, contarAsociados());
    }

    @Test
    public void testIniciarBDEsIdempotente() throws SQLException {
        bd.iniciarBD();
        assertEquals("Reiniciar no debe acumular registros",
                CANTIDAD_SEMILLA, contarAsociados());
    }

    @Test
    public void testEjecutarConsultaConParametroDevuelveLaFilaIndicada() throws SQLException {
        ResultSet resultado = bd.ejecutarConsulta("SELECT * FROM Asociados WHERE id = ? ;", 1);
        resultado.next();
        assertEquals("Debe devolver la fila del id 1", "12345678", resultado.getString("dni"));
        assertEquals("Juan", resultado.getString("nombre"));
        resultado.close();
    }

    @Test
    public void testEjecutarConsultaSobreTablaInexistenteLanzaSQLException() throws SQLException {
        Throwable lanzada = VerificacionDeExcepcion.rechazar("una tabla inexistente", SQLException.class,
                () -> bd.ejecutarConsulta("SELECT * FROM TablaInexistente ;", -1));

        assertNotNull("La excepcion de SQL deberia traer un mensaje", lanzada.getMessage());
    }

    @Test
    public void testEjecutarInsertIncorporaElRegistro() throws SQLException {
        bd.ejecutarInsert(consultaInsert(),
                crearDTO("Nuevo", "Registro", "90000001", "Rivadavia", 100, "Mar del Plata", "2235000001"));

        assertEquals("La tabla debe pasar a 21 registros",
                CANTIDAD_SEMILLA + 1, contarAsociados());
        assertEquals("Nuevo", leerNombrePorDni("90000001"));
    }

    @Test
    public void testEjecutarInsertConDniDuplicadoLanzaSQLException() throws SQLException {
        VerificacionDeExcepcion.rechazar("un DNI ya existente", SQLException.class,
                () -> bd.ejecutarInsert(consultaInsert(),
                        crearDTO("Duplicado", "Registro", "12345678", "Rivadavia", 100, "Mar del Plata", "2235000002")));

        assertEquals("La tabla no debe cambiar", CANTIDAD_SEMILLA, contarAsociados());
    }

    @Test
    public void testEjecutarDeleteEliminaElRegistroIndicado() throws SQLException {
        bd.ejecutarDelete("DELETE FROM Asociados WHERE dni = ? ;", "12345678");
        assertEquals(CANTIDAD_SEMILLA - 1, contarAsociados());
    }

    @Test
    public void testEjecutarDeleteConDniInexistenteNoEliminaNada() throws SQLException {
        bd.ejecutarDelete("DELETE FROM Asociados WHERE dni = ? ;", "00000000");
        assertEquals(CANTIDAD_SEMILLA, contarAsociados());
    }

    @Test
    public void testEjecutarDeleteTrataElDniComoDatoYNoComoSql() throws SQLException {
        bd.ejecutarDelete("DELETE FROM Asociados WHERE dni = ? ;", "' OR '1'='1");
        assertEquals("El valor debe viajar como parametro, no como SQL",
                CANTIDAD_SEMILLA, contarAsociados());
    }

    private String consultaInsert() {
        return "INSERT INTO Asociados(nombre, apellido, DNI, calle, numero, ciudad, telefono) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?);";
    }

    private AsociadoDTO crearDTO(String nombre, String apellido, String dni, String calle,
                                 int numero, String ciudad, String telefono) {
        AsociadoDTO aDTO = new AsociadoDTO();
        aDTO.setNombre(nombre);
        aDTO.setApellido(apellido);
        aDTO.setDni(dni);
        aDTO.setCalle(calle);
        aDTO.setNumero(numero);
        aDTO.setCiudad(ciudad);
        aDTO.setTelefono(telefono);
        return aDTO;
    }

    private int contarAsociados() throws SQLException {
        Statement sentencia = bd.getConexion().createStatement();
        ResultSet resultado = sentencia.executeQuery("SELECT COUNT(*) FROM Asociados;");
        resultado.next();
        int total = resultado.getInt(1);
        resultado.close();
        sentencia.close();
        return total;
    }

    private String leerNombrePorDni(String dni) throws SQLException {
        PreparedStatement sentencia = bd.getConexion()
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
