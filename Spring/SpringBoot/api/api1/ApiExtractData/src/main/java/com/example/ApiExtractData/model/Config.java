package com.example.ApiExtractData.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Entity
@Table(name = "config")
@Data
@JsonSerialize
@JsonDeserialize(builder = Config.Builder.class)
// Entidad para Config
public class Config extends AbstractEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "description", length = 255, nullable = false)
    private String description;
    @Column(name = "is_custom", length = 255, nullable = false)
    private Boolean isCustom;
    @Column(name = "default_value", length = 255, nullable = false)
    private String defaultValue;
    @Column(name = "application_node", length = 255, nullable = false)
    private String applicationNode;

    // Este campo indica el ID del Config padre
    @Column(name = "parent_id")
    private Long parentId;

    // Relación bidireccional con el "Config" padre
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", insertable = false, updatable = false)
    @JsonBackReference // Evita la recursión infinita en la serialización
    private Config parent;

    // Relación OneToMany con los hijos de esta configuración
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference // Esta es la parte que permite la serialización de los hijos
    private List<Config> children;

    //Relacion one to Many con Attribute (bidireccional)
    @OneToMany(mappedBy = "config", orphanRemoval = true, cascade = CascadeType.ALL)//orphanRemoval = true: al desasociar una entidad secundaria de la principal, se elimina automáticamente de la base de datos.
    @JsonManagedReference
    private List<Attribute> attributes;

    public Config(String description, Boolean isCustom, String defaultValue, String applicationNode, Long parentId, List<Attribute> attributes) {
        super();
        this.description = description;
        this.isCustom = isCustom;
        this.defaultValue = defaultValue;
        this.applicationNode = applicationNode;
        this.parentId = parentId;
        this.attributes = attributes;
    }

    public Config() {
        super();
    }

    @JsonPOJOBuilder
    static class Builder {
        @JsonProperty("description")
        String description;
        @JsonProperty("is_custom")
        Boolean isCustom;
        @JsonProperty("default_value")
        String defaultValue;
        @JsonProperty("application_node")
        String applicationNode;
        @JsonProperty("parent")
        Long parentId;
        @JsonProperty("attributes")
        List<Attribute> attributes;


        public Config build() {
            return new Config(description, isCustom, defaultValue, applicationNode, parentId, attributes);
        }
    }
}