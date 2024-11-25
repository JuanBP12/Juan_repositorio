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

@Entity
@Table(name = "attribute")
@Data
@JsonSerialize
@JsonDeserialize(builder = Attribute.Builder.class)

// Entidad para Attribute
public class Attribute  extends AbstractEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(name="name", length=255, nullable=false)
    private String name;

    //Relacion Many to one con AttributeType (bidireccional)
    @OneToMany(mappedBy = "attribute")//cascade = CascadeType.ALL: todas las operaciones de persistencia realizadas en la entidad padre se aplicaran automaticamentete sobre la entidad hija
    @JsonManagedReference
    private List<Config> config;



    public Attribute( String name, AttributeType attributeType) {
        super();
        this.name = name;
        this.attributeType = attributeType;
    }


    public Attribute(){
        super();
    }

    @JsonPOJOBuilder
    static class Builder {
        @JsonProperty("name")
        String name;
        @JsonProperty("attributeType")
        AttributeType attributeType;


        public Attribute build() {
            return new Attribute(name,attributeType);
        }
    }
}