package com.example.BatchProcessor.writer;

import com.example.BatchProcessor.model.Empleo;
import com.example.BatchProcessor.model.Persona;
import com.example.BatchProcessor.repository.EmpleoRepository;
import com.example.BatchProcessor.repository.PersonaRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;


/**
 * Escritor de base de datos para almacenar objetos Persona y Empleo.
 * Esta clase guarda las personas y los empleos relacionados en la base de datos,
 * asegurando que los empleos no se guarden repetidos.
 */
@Component
public class DatabaseWriter implements ItemWriter<Persona> {  // Renombrado a DatabaseWriter

    private final PersonaRepository personaRepository;
    private final EmpleoRepository empleoRepository;
    private EntityManager entityManager;

    public DatabaseWriter(PersonaRepository personaRepository, EmpleoRepository empleoRepository, EntityManager entityManager) {
        this.personaRepository = personaRepository;
        this.empleoRepository = empleoRepository;
        this.entityManager = entityManager;
    }


    @Override
    public void write(Chunk<? extends Persona> chunk) throws Exception {
        try {
            // Obtener las personas que vienen en el chunk
            List<? extends Persona> personas = chunk.getItems();
            // Extraer los empleos de las personas, asegurándonos de no tener duplicados
            List<Empleo> empleos = personas.stream()
                    .map(Persona::getEmpleo)
                    .distinct() // Evita los duplicados de empleos
                    .collect(Collectors.toList());

            List<Empleo> empleosPersistidos = empleoRepository.saveAll(empleos);
            empleoRepository.flush();// Asegurarse de que los cambios se persistan en la base de datos

            // Asociar las instancias persistidas de Empleo a las personas
            personas.forEach(persona -> {
                Empleo empleoOriginal = persona.getEmpleo();
                Empleo empleoPersistido = empleosPersistidos.stream()
                        .filter(e -> e.equals(empleoOriginal)) // Usamos equals para encontrar el match
                        .findFirst()
                        .orElseThrow(() -> new IllegalStateException("No se encontró el empleo persistido"));
                persona.setEmpleo(empleoPersistido); // Actualizamos el empleo en la persona
            });

            // Filtrar las personas para agregar solo las nuevas (evitar duplicados)
            List<Persona> personasNuevas = personas.stream()
                    // Evita guardar personas repetidas
                    .filter(persona -> !personaRepository.existsByNombreCompleto(persona.getNombreCompleto()))
                    .distinct()
                    .collect(Collectors.toList());
            // Guardar todas las personas en una sola operación (solo las únicas)
            personaRepository.saveAll(personasNuevas);// Guardar los empleos primero, para evitar duplicados
            // Asegurarse de que los cambios se persistan en la base de datos
            personaRepository.flush();
            // Limpiar el contexto de persistencia para evitar que JPA mantenga todas las entidades en memoria
            entityManager.clear();  // Elimina las entidades del contexto de persistencia

        } catch (Exception e) {
            // Loguear el error para obtener más detalles
            System.err.println("Error durante el commit del chunk: " + e.getMessage());
            throw e;  // Re-lanzar la excepción para que Spring Batch maneje el rollback
        }
    }
}
