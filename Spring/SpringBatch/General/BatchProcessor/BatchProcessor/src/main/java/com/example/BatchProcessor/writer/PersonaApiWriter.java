package com.example.BatchProcessor.writer;


import com.example.BatchProcessor.model.Persona;
import org.springframework.batch.item.Chunk;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;

/**
 * Escritor que envía objetos Persona a una API REST.
 * Hereda de ApiItemWriter para reutilizar la lógica común de escritura a la API.
 */
@Component
public class PersonaApiWriter extends ApiItemWriter<Persona> {

    public PersonaApiWriter(RestTemplateBuilder restTemplateBuilder) {
        super(restTemplateBuilder);  // Llama al constructor de ApiItemWriter
    }

    @Override
    public void write(Chunk<? extends Persona> chunk) throws Exception {
        // Se sobrescribe si necesitamos una lógica adicional específica para Persona
        super.write(chunk);
    }
}

