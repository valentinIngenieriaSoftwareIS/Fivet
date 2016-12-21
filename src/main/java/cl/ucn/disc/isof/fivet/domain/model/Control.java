package cl.ucn.disc.isof.fivet.domain.model;

import com.avaje.ebean.annotation.EnumValue;
import com.durrutia.ebean.BaseModel;
import lombok.*;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import java.util.Date;

/**
 * clase que representa un control de un paciente que se realiza en una veterinaria
 *
 * Created by valentin on 16-11-2016.
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table
public class Control extends BaseModel{

    /**
     * correlativo del numero de atencion
     */
    @Getter
    @NotEmpty
    @Column(nullable = false)
    private Integer numeroControl;

    /**
     * fecha en que se realizo el control al paciente
     */
    @Getter
    @Setter
    @Column(nullable = false)
    private Date fechaControl;

    /**
     * breve descripcion del control realizado
     */
    @Getter
    @Setter
    @Column
    private String descripcion;

}
