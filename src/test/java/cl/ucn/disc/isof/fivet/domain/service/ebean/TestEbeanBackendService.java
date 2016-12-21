package cl.ucn.disc.isof.fivet.domain.service.ebean;

import cl.ucn.disc.isof.fivet.domain.model.Persona;
import cl.ucn.disc.isof.fivet.domain.model.Paciente;
import cl.ucn.disc.isof.fivet.domain.model.Control;
import cl.ucn.disc.isof.fivet.domain.service.BackendService;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Stopwatch;
import lombok.extern.slf4j.Slf4j;
import org.junit.*;
import org.junit.rules.Timeout;
import org.junit.runners.MethodSorters;
import cl.ucn.disc.isof.fivet.domain.model.Control;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

/**
 * Clase de testing del {@link BackendService}.
 */
@Slf4j
@FixMethodOrder(MethodSorters.DEFAULT)
public class TestEbeanBackendService {

    /**
     * Todos los test deben terminar antes de 60 segundos.
     */
    @Rule
    public Timeout globalTimeout = Timeout.seconds(60);

    /**
     * Configuracion de la base de datos:  h2, hsql, sqlite
     * WARN: hsql no soporta ENCRYPT
     */
    private static final String DB = "h2";

    /**
     * Backend
     */
    private BackendService backendService;

    /**
     * Cronometro
     */
    private Stopwatch stopWatch;

    /**
     * Antes de cada test
     */
    @Before
    public void beforeTest() {

        stopWatch = Stopwatch.createStarted();
        log.debug("Initializing Test Suite with database: {}", DB);

        backendService = new EbeanBackendService(DB);
        backendService.initialize();
    }

    /**
     * Despues del test
     */
    @After
    public void afterTest() {

        log.debug("Test Suite done. Shutting down the database ..");
        backendService.shutdown();

        log.debug("Test finished in {}", stopWatch.toString());
    }

    /**
     * Test de la persona
     */
    @Test
    public void testPersona() {

        final String rut = "1-1";
        final String nombre = "Este es mi nombre";

        // Insert into backend
        {
            final Persona persona = Persona.builder()
                    .nombre(nombre)
                    .rut(rut)
                    .password("durrutia123")
                    .tipo(Persona.Tipo.CLIENTE)
                    .build();

            persona.insert();

            log.debug("Persona to insert: {}", persona);
            Assert.assertNotNull("Objeto sin id", persona.getId());
        }

        // Get from backend v1
        {
            final Persona persona = backendService.getPersona(rut);
            log.debug("Persona founded: {}", persona);
            Assert.assertNotNull("Can't find Persona", persona);
            Assert.assertNotNull("Objeto sin id", persona.getId());
            Assert.assertEquals("Nombre distintos!", rut, persona.getNombre());
            Assert.assertNotNull("Pacientes null", persona.getPacientes());
            Assert.assertTrue("Pacientes != 0", persona.getPacientes().size() == 0);

            // Update nombre
            persona.setNombre(nombre + nombre);
            persona.update();
        }

        // Get from backend v2
        {
            final Persona persona = backendService.getPersona(rut);
            log.debug("Persona founded: {}", persona);
            Assert.assertNotNull("Can't find Persona", persona);
            Assert.assertEquals("Nombres distintos!", nombre, persona.getNombre());
        }

    }

    /**
     * test de un paciente
     */
    @Test
    public void testPaciente(){
        //num paciente
        final Integer numPaciente=1;
        final Date fechaNac= Calendar.getInstance().getTime();

        //insertar al backend
        {
            final Paciente paciente=Paciente.builder()
                    .numero(numPaciente)
                    .nombre("oddie")
                    .fechaNacimiento(fechaNac)
                    .raza("poddle")
                    .sexo(Paciente.Sexo.MACHO)
                    .color("Negro")
                    .build();

            paciente.insert();

            log.debug("paciente to insert: {}", paciente);
            Assert.assertNotNull("objeto sin ID", paciente.getId());
        }
        //metodo
        {
            final Paciente paciente =backendService.getPaciente(numPaciente);
            log.debug("paciente founded: {}", paciente);
            Assert.assertNotNull("can't find paciente", paciente);
            Assert.assertNotNull("objeto sin ID", paciente.getId());
            Assert.assertEquals("numeros distintos!", numPaciente, paciente.getNumero());

            //update pet name
            paciente.setNombre("donGato");
            paciente.update();

        }

    }

    /**
     * test de un control
     */
    @Test
    public void testControl(){
        final Integer numero=1;
        //fecha de hoy
        final Date fecha= Calendar.getInstance().getTime();

        //insertar al backend
        {
            final Control control = Control.builder()
                    //agrego el numero
                    .numeroControl(numero)
                    //agrego la fecha del control
                    .fechaControl(fecha)
                    //agregar descripcion del control
                    .descripcion("control mensual")
                    .build();

            control.insert();

            log.debug("control to insert: {}", control);

            Assert.assertNotNull("objeto sin ID", control.getId());
        }

    }

    /**
     * test para obtener la lista de pacientes ordenado por nombre
     */
    @Test
    public void testGetPacientesPorNombre(){

        final Integer numPaciente=1;
        final Date fechaNac= Calendar.getInstance().getTime();

        //insertar paciente
        {
            final Paciente paciente=Paciente.builder()
                    .numero(numPaciente)
                    .nombre("oddie")
                    .fechaNacimiento(fechaNac)
                    .raza("poddle")
                    .sexo(Paciente.Sexo.MACHO)
                    .color("Negro")
                    .build();

            paciente.insert();

            log.debug("paciente to insert: {}", paciente);
            Assert.assertNotNull("objeto sin ID", paciente.getId());
        }

        final Integer numPacienteSegundo=2;
        final Date fechaNacSegundo= Calendar.getInstance().getTime();

        //insertar segundo paciente
        {
            final Paciente pacienteSegundo=Paciente.builder()
                    .numero(numPacienteSegundo)
                    .nombre("oddieJr")
                    .fechaNacimiento(fechaNacSegundo)
                    .raza("Doberman")
                    .sexo(Paciente.Sexo.MACHO)
                    .color("Negro")
                    .build();

            pacienteSegundo.insert();

            log.debug("paciente to insert: {}", pacienteSegundo);
            Assert.assertNotNull("objeto sin ID", pacienteSegundo.getId());
        }

        List<Paciente> lista =backendService.getPacientesPorNombre("oddie");
        Assert.assertTrue("lista no vacia", lista.size()==0);
    }

}
