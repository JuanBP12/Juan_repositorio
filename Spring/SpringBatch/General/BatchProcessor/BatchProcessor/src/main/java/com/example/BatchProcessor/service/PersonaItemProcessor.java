package com.example.BatchProcessor.service;

import com.example.BatchProcessor.model.Persona;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class PersonaItemProcessor implements ItemProcessor<Persona, Persona> {
    @Override
    public Persona process(Persona persona) throws Exception {
        // Realiza cualquier procesamiento necesario sobre la persona
        // Por ejemplo, transformar datos, validaciones, etc.
        return persona;  // Retorna la persona (sin modificaciones si no es necesario)
    }
}

