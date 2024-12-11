package com.example.BatchProcessor.service;

import com.example.BatchProcessor.model.Persona;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


//Esta clase solo hace falta para la escritura en la api

// motivo:
// diferencias entre la tabla personas de la en de la que escribimos y la que tenemos definida aqui
// la tabla persona de la apli a la que leemos tiene un campo asociado de clase empleo, en nuestro proyecto el campo empleo es solo un string

@Component
public class PersonaItemProcessor implements ItemProcessor<Persona, Persona> {


    @Override
    public Persona process(Persona persona) throws Exception {
        System.out.println(persona.toString());
        // Si no se encuentra un empleoId válido, dejamos empleo como null
        if (persona.getEmpleo() != null) {
            // Si deseas asignar un Empleo a la persona, puedes hacerlo aquí, sino asignamos null
            persona.setEmpleo(null);  // Deja empleo como null
        }

        // Retornamos la persona procesada
        return persona;
    }
}


