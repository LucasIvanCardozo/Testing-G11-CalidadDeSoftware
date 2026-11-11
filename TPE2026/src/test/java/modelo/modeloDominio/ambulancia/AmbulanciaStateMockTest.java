package modelo.modeloDominio.ambulancia;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import modelo.modeloDominio.personas.asociado.Asociado;
import modelo.modeloDominio.personas.operario.Operario;

import org.junit.Test;

/**
 * Pruebas atómicas de {@link Ambulancia} con un doble de prueba sobre la interfaz
 * {@link State}.
 *
 * <p>El mock reemplaza al estado concreto, de modo que cada caso verifica qué
 * operación delega la ambulancia y cómo queda su recurso compartido, sin
 * depender de las implementaciones reales de {@code State}.</p>
 */
public class AmbulanciaStateMockTest {

    @Test
    public void testAtenderDomicilioDelegaEnElEstado_M1() {
        Ambulancia ambulancia = new Ambulancia();
        State estado = mock(State.class);
        ambulancia.setState(estado);

        ambulancia.atenderDomicilio(asociado());

        verify(estado).SolicitudDeAtencionDomicilio();
    }

    @Test
    public void testTrasladarALaClinicaDelegaEnElEstado_M2() {
        Ambulancia ambulancia = new Ambulancia();
        State estado = mock(State.class);
        ambulancia.setState(estado);

        ambulancia.trasladarALaClinica(asociado());

        verify(estado).SolicitudDeTraslado();
    }

    @Test
    public void testSolicitarMantenimientoDelegaEnElEstado_M3() {
        Ambulancia ambulancia = new Ambulancia();
        State estado = mock(State.class);
        ambulancia.setState(estado);

        ambulancia.solicitarMantenimiento(operario());

        verify(estado).SolicitudMantenimiento();
    }

    @Test
    public void testRegresarSinPacienteDelegaEnElEstado_M4() {
        Ambulancia ambulancia = new Ambulancia();
        State estado = mock(State.class);
        ambulancia.setState(estado);

        ambulancia.regresarSinPaciente();

        verify(estado).RetornoClinica();
    }

    @Test
    public void testAtenderDomicilioMarcaLaAmbulanciaOcupada_M5() {
        Ambulancia ambulancia = new Ambulancia();
        ambulancia.setState(mock(State.class));

        ambulancia.atenderDomicilio(asociado());

        assertTrue("La ambulancia debe quedar ocupada", ambulancia.isOcupado());
    }

    @Test
    public void testAtenderDomicilioNoDelegaSiLaSimulacionNoEstaActiva_M6() {
        Ambulancia ambulancia = new Ambulancia();
        State estado = mock(State.class);
        ambulancia.setState(estado);
        ambulancia.setSimulacionActiva(false);

        ambulancia.atenderDomicilio(asociado());

        verify(estado, never()).SolicitudDeAtencionDomicilio();
        assertFalse("No debe marcarla ocupada", ambulancia.isOcupado());
    }

    @Test
    public void testRetornoAutomaticoNoCambiaElEstadoSiLaAmbulanciaEstaOcupada_M7() {
        Ambulancia ambulancia = new Ambulancia();
        ambulancia.setState(new AtendiendoEnDomicilioState(ambulancia));
        ambulancia.setOcupado(true);
        State estadoAntes = ambulancia.getEstadoActual();

        ambulancia.retornoAutomatico(new RetornoAutomatico(ambulancia));

        assertSame("El retorno automatico no debe actuar sobre una ambulancia ocupada",
                estadoAntes, ambulancia.getEstadoActual());
    }

    @Test
    public void testVerificarYFinalizarSimulacionLaDesactivaSiTodosTerminaron_M8() {
        Ambulancia ambulancia = new Ambulancia();
        ambulancia.agregarAsociado(asociadoConSolicitudes(3, 3));

        ambulancia.verificarYFinalizarSimulacion();

        assertFalse("La simulacion debe quedar inactiva", ambulancia.isSimulacionActiva());
    }

    @Test
    public void testVerificarYFinalizarSimulacionLaMantieneSiQuedaAlgunAsociado_M9() {
        Ambulancia ambulancia = new Ambulancia();
        ambulancia.agregarAsociado(asociadoConSolicitudes(1, 3));

        ambulancia.verificarYFinalizarSimulacion();

        assertTrue("La simulacion debe seguir activa", ambulancia.isSimulacionActiva());
    }

    private Asociado asociado() {
        Asociado asociado = mock(Asociado.class);
        when(asociado.getNombre()).thenReturn("Asociado");
        return asociado;
    }

    private Asociado asociadoConSolicitudes(int atendidas, int maximas) {
        Asociado asociado = mock(Asociado.class);
        when(asociado.getCantSolicitudesAtendidas()).thenReturn(atendidas);
        when(asociado.getMaxCantSolicitudes()).thenReturn(maximas);
        return asociado;
    }

    private Operario operario() {
        Operario operario = mock(Operario.class);
        when(operario.getNombre()).thenReturn("Operario");
        return operario;
    }
}
