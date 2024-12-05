package com.example.BatchProcessor.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GenericEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String data; // Aquí puedes almacenar el JSON

    public <T> T getData(Class<T> clazz) {
        // Metodo para deserializar data a un objeto del tipo dado
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(data, clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error deserializing data", e);
        }
    }

    public <T> void setData(T obj) {
        // Metodo para serializar un objeto y guardarlo como JSON
        ObjectMapper mapper = new ObjectMapper();
        try {
            this.data = mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error serializing data", e);
        }
    }
}

