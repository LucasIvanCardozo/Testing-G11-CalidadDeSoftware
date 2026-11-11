package modelo.modeloDominio.ambulancia;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import modelo.modeloDominio.personas.asociado.Asociado;
import modelo.modeloDominio.personas.operario.Operario;

import org.junit.Test;

/**
 * Pruebas de concurrencia e integración de la máquina de estados de
 * {@link Ambulancia}.
 *
 * <p>Los hilos se coordinan con {@link CountDownLatch} y se ejercitan
 * directamente los métodos <code>synchronized</code> de la ambulancia: no se usa
 * {@code TiempoMuerto} ni {@code run()} de {@link Asociado} u {@link Operario},
 * porque duermen un valor aleatorio de hasta 8 segundos y volverían la suite
 * lenta y no determinista.</p>
 */
public class AmbulanciaConcurrenciaTest {

    @Test
    public void testLosMetodosSincronizadosSeExcluyenEntreHilos_C1() throws Exception {
        final Ambulancia ambulancia = new Ambulancia();
        State estado = mock(State.class);
        ambulancia.setState(estado);

        final CountDownLatch estadoAlcanzado = new CountDownLatch(1);
        final CountDownLatch liberarEstado = new CountDownLatch(1);
        doAnswer(invocacion -> {
            estadoAlcanzado.countDown();
            liberarEstado.await();
            return null;
        }).when(estado).SolicitudDeAtencionDomicilio();

        Thread atencion = new Thread(() -> ambulancia.atenderDomicilio(asociado()));
        atencion.start();
        assertTrue("El hilo deberia haber alcanzado el estado",
                estadoAlcanzado.await(5, TimeUnit.SECONDS));

        final AtomicBoolean elSegundoHiloEntro = new AtomicBoolean(false);
        Thread segundo = new Thread(() -> {
            ambulancia.isOcupado();
            elSegundoHiloEntro.set(true);
        });
        segundo.start();
        segundo.join(500);
        assertFalse("Un metodo synchronized no debe ejecutarse mientras otro hilo retiene el monitor",
                elSegundoHiloEntro.get());

        liberarEstado.countDown();
        atencion.join(5000);
        segundo.join(5000);
        assertTrue("Al liberarse el monitor el segundo hilo debe poder entrar",
                elSegundoHiloEntro.get());
    }

    @Test
    public void testUnHiloBloqueadoSeLiberaCuandoTerminaLaSimulacion_C2() throws Exception {
        final Ambulancia ambulancia = new Ambulancia();
        ambulancia.setOcupado(true);

        final CountDownLatch intento = new CountDownLatch(1);
        Thread hilo = new Thread(() -> {
            intento.countDown();
            ambulancia.atenderDomicilio(asociado());
        });
        hilo.start();
        assertTrue(intento.await(5, TimeUnit.SECONDS));

        Thread.sleep(200);
        ambulancia.setSimulacionActiva(false);

        hilo.join(5000);
        assertFalse("El hilo debe terminar cuando la simulacion finaliza y se notifica a los que esperan",
                hilo.isAlive());
    }

    @Test
    public void testAgregarAsociadosMientrasSeVerificaLaSimulacion_C3() throws Exception {
        final Ambulancia ambulancia = new Ambulancia();
        final Asociado asociado = asociadoTerminado();
        for (int i = 0; i < 500; i++) {
            ambulancia.agregarAsociado(asociado);
        }

        final long fin = System.currentTimeMillis() + 2000;
        final AtomicReference<Throwable> falla = new AtomicReference<>();

        Thread agregador = new Thread(() -> {
            while (System.currentTimeMillis() < fin) {
                ambulancia.agregarAsociado(asociado);
            }
        });
        Thread verificador = new Thread(() -> {
            try {
                while (System.currentTimeMillis() < fin) {
                    ambulancia.setSimulacionActiva(true);
                    ambulancia.verificarYFinalizarSimulacion();
                }
            } catch (Throwable capturada) {
                falla.compareAndSet(null, capturada);
            }
        });

        verificador.start();
        agregador.start();
        agregador.join(10000);
        verificador.join(10000);

        Throwable capturada = falla.get();
        if (capturada != null) {
            fail("Recorrer la lista de asociados mientras se la modifica no deberia fallar: "
                    + capturada);
        }
    }

    @Test
    public void testRetornoAutomaticoInvocaElRetornoConLaSimulacionActiva_C4() {
        Ambulancia ambulancia = mock(Ambulancia.class);
        when(ambulancia.isSimulacionActiva()).thenReturn(true);

        Thread hilo = new Thread(new RetornoAutomatico(ambulancia));
        hilo.setDaemon(true);
        hilo.start();

        verify(ambulancia, timeout(12000)).retornoAutomatico(any(RetornoAutomatico.class));
    }

    @Test
    public void testElRetornoDeTrasladarPacienteVuelveSinPaciente_C5() {
        Ambulancia ambulancia = new Ambulancia();
        ambulancia.setState(new TrasladandoPacienteClinicaState(ambulancia));

        ambulancia.retornoAutomatico(new RetornoAutomatico(ambulancia));

        assertTrue("Debe volver a la clinica sin paciente, pero quedo en "
                        + ambulancia.getEstadoActual().getClass().getSimpleName(),
                ambulancia.getEstadoActual() instanceof RegresandoClinicaSinPacienteState);
    }

    @Test
    public void testTrasladoDesdeDisponibleCambiaElEstado_C6() {
        Ambulancia ambulancia = new Ambulancia();
        ambulancia.setState(new DisponibleState(ambulancia));

        ambulancia.trasladarALaClinica(asociado());

        assertTrue("Debe quedar trasladando al paciente, pero quedo en "
                        + ambulancia.getEstadoActual().getClass().getSimpleName(),
                ambulancia.getEstadoActual() instanceof TrasladandoPacienteClinicaState);
    }

    @Test
    public void testMantenimientoDesdeDisponibleOcupaLaAmbulancia_C7() {
        Ambulancia ambulancia = new Ambulancia();
        ambulancia.setState(new DisponibleState(ambulancia));

        ambulancia.solicitarMantenimiento(operario());

        assertTrue("Debe quedar en el taller, pero quedo en "
                        + ambulancia.getEstadoActual().getClass().getSimpleName(),
                ambulancia.getEstadoActual() instanceof EnTallerState);
        assertTrue("La ambulancia debe quedar ocupada", ambulancia.isOcupado());
    }

    private Asociado asociado() {
        Asociado asociado = mock(Asociado.class);
        when(asociado.getNombre()).thenReturn("Asociado");
        return asociado;
    }

    private Asociado asociadoTerminado() {
        Asociado asociado = mock(Asociado.class);
        when(asociado.getCantSolicitudesAtendidas()).thenReturn(1);
        when(asociado.getMaxCantSolicitudes()).thenReturn(1);
        return asociado;
    }

    private Operario operario() {
        Operario operario = mock(Operario.class);
        when(operario.getNombre()).thenReturn("Operario");
        return operario;
    }
}
