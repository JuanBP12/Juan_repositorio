package com.example.BatchProcessor.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

/**
 * Entidad JPA que representa una persona en la base de datos.
 * Esta clase almacena información sobre una persona y está asociada a un empleo mediante una relación ManyToOne.
 */
@Entity
@Table(name = "persona")  // Prefijo 'tbl_' añadido al nombre de la tabla
public class Persona {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("personaId")
    private Long personaId;

    @JsonProperty("nombreCompleto")
    private String nombreCompleto;

    @JsonProperty("empleo")
    private String empleo;

    public Persona() {}

    public Persona(String nombreCompleto, String empleo) {
        this.nombreCompleto = nombreCompleto;
        this.empleo = empleo;
    }


    public Long getPersonaId() {
        return personaId;
    }

    public void setPersonaId(Long personaId) {
        this.personaId = personaId;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getEmpleo() {
        return empleo;
    }

    public void setEmpleo(String empleo) {
        this.empleo = empleo;
    }


}
