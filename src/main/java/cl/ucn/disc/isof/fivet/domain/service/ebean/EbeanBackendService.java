package cl.ucn.disc.isof.fivet.domain.service.ebean;

import cl.ucn.disc.isof.fivet.domain.model.Paciente;
import cl.ucn.disc.isof.fivet.domain.model.Persona;
import cl.ucn.disc.isof.fivet.domain.model.Control;
import cl.ucn.disc.isof.fivet.domain.service.BackendService;
import com.avaje.ebean.EbeanServer;
import com.avaje.ebean.EbeanServerFactory;
import com.avaje.ebean.config.EncryptKey;
import com.avaje.ebean.config.EncryptKeyManager;
import com.avaje.ebean.config.ServerConfig;
import com.durrutia.ebean.BaseModel;
import lombok.extern.slf4j.Slf4j;
import com.avaje.ebean.config.EncryptKey;
import com.avaje.ebean.config.EncryptKeyManager;
import java.util.List;

@Slf4j
public class EbeanBackendService implements BackendService {

    /**
     * EBean server
     */
    private final EbeanServer ebeanServer;

    /**
     *
     */
    public EbeanBackendService(final String database) {

        log.debug("Loading EbeanBackend in database: {}", database);

        /**
         * Configuration
         */
        ServerConfig config = new ServerConfig();
        config.setName(database);
        config.setDefaultServer(true);
        config.loadFromProperties();

        // Don't try this at home
        //config.setAutoCommitMode(false);

        // config.addPackage("package.de.la.clase.a.agregar.en.el.modelo");
        config.addClass(BaseModel.class);

        config.addClass(Persona.class);
        config.addClass(Persona.Tipo.class);

        config.addClass(Paciente.class);
        config.addClass(Paciente.Sexo.class);

        config.addClass(Control.class);

        // http://ebean-orm.github.io/docs/query/autotune
        config.getAutoTuneConfig().setProfiling(false);
        config.getAutoTuneConfig().setQueryTuning(false);

        config.setEncryptKeyManager(new EncryptKeyManager() {

            @Override
            public void initialise() {
                log.debug("Initializing EncryptKey ..");
            }

            @Override
            public EncryptKey getEncryptKey(final String tableName, final String columnName) {

                log.debug("gettingEncryptKey for {} in {}.", columnName, tableName);

                // Return the encrypt key
                //nombre de la tabla mas el nombre del campo y eso lo inserta
                //return () -> tableName + columnName;
                //return (tableName+columnName);
                return () -> tableName + columnName;
                //String password=tableName + columnName;
                //return new EncryptKey(password);
            }
        });

        this.ebeanServer = EbeanServerFactory.create(config);

        log.debug("EBeanServer ready to go.");

    }


    /**
     *  Obtiene una persona a traves del backend dado su rut o email
     *
     * @param rutEmail
     * @return the Persona
     */
    @Override
    public Persona getPersona(String rutEmail) {
        return this.ebeanServer.find(Persona.class)
                //.where()
                //.eq("rut", rut)
                //.findUnique();
                //Expr.eq("rut", rutEmail),
                // Expr.eq("email", rutEmail)
                //version antigua
                //.Where()
                //.or()
                //.eq("rut", rutEmail)
                //.eq("email", rutEmail)
                //.findUnique();
                .where().disjunction()
                    .eq("rut", rutEmail)
                    .eq("email", rutEmail)
                .findUnique();

    }

    /**
     * Obtiene el listado de los pacientes
     *
     * @return list of patients(paciente)
     */
    public List<Paciente> getPacientes(){
        //crear la lista de pacientes
        List<Paciente> listaPacientes=this.ebeanServer.find(Paciente.class).findList();
        //return this.ebeanServer.find(Paciente.class)
        return listaPacientes;
    }

    /**
     * Agrega un control a un paciente identificado por su numero
     *
     * @param control
     * @param numeroPaciente
     */
    public void agregarControl(Control control, Integer numeroPaciente){
        //primero se debe buscar al paciente
        Paciente paciente=this.ebeanServer.find(Paciente.class)
                .where()
                //se busca por el numero
                .eq("numero", numeroPaciente)
                .findUnique();
        //se tiene el paciente, ahora se le tiene que agregar el control
        //paciente
    }

    /**
     * Obtiene todos los controles realizados por un veterinario ordenado por fecha de control
     *
     * @param rutVeterinario del que realizo el control.
     * @return una lista de controles
     */
    public List<Control> getControlesVeterinario(String rutVeterinario){
        //crear una persona
        Persona persona=this.ebeanServer.find(Persona.class)
                .where()
                //se busca por el tipo de persona que sea veterinario
                .eq("tipo", "Veterinario")
                //se busca por el rut del veterinario
                .eq("rut", rutVeterinario)
                //unico
                .findUnique();
        //una vez encontrada esta persona, se obtienen los controles de este
        List<Control> controlesVet =persona.getControlesVeterinario();
        //se retorna la lista ordenada por fecha de control
        //controlesVet.
        return controlesVet;
    }


    /**
     * obtiene una lista de pacientes que posea el determinado nombre
     *
     * @param nombre a buscar, ejemplo: "pep" que puede retornar pepe, pepa, pepilla, etc..
     * @return una lista de pacientes
     */
    public List<Paciente> getPacientesPorNombre(String nombre){
        //crear la lista de pacientes
        List<Paciente> listaPacientesPorNombre=this.ebeanServer.find(Paciente.class)
                .where()
                //se busca los nombres de los paciente que empiecen con ese nombre
                .like("nombre",nombre+"%")
                .findList();
        //se devuelve la lista
        return listaPacientesPorNombre;
    }

    /**
     * Obtiene un paciente a partir de su numero
     *
     * @param numeroPaciente de ficha.
     * @return el paciente
     */
    public Paciente getPaciente (Integer numeroPaciente){
            Paciente paciente =this.ebeanServer.find(Paciente.class)
                    .where()
                    .eq("numero", numeroPaciente)
                    .findUnique();
            //retornar
            return paciente;
    }


    /**
     * Inicializa la base de datos
     */
    @Override
    public void initialize() {
        log.info("Initializing Ebean ..");
    }

    /**
     * Cierra la conexion a la BD
     */
    @Override
    public void shutdown() {
        log.debug("Shutting down Ebean ..");

        // TODO: Verificar si es necesario des-registrar el driver
        this.ebeanServer.shutdown(true, false);
    }
}
