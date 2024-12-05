package com.example.BatchProcessor.reader;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityManager;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.util.Assert;

public class DatabaseReader {

    @Bean
    @StepScope
    public <T> JpaPagingItemReader<T> databaseReader(EntityManagerFactory entityManagerFactory,
                                                     @Value("#{jobParameters['query']}") String query,
                                                     @Value("#{jobParameters['entityClass']}") Class<T> clazz) {
        JpaPagingItemReader<T> reader = new JpaPagingItemReader<>();

        // Asegúrate de que la clase y la consulta no sean nulas
        Assert.notNull(clazz, "Entity class cannot be null");
        Assert.notNull(query, "Query cannot be null");

        // Obtener el EntityManager de la fábrica de entidades
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        // Configurar el lector JPA
        reader.setEntityManagerFactory(entityManagerFactory);
        reader.setQueryString(query);
        reader.setPageSize(10);  // Define el tamaño de la página
        reader.setSaveState(false);  // Desactivar guardado de estado si no lo necesitas

        // Configurar el tipo de entidad de manera dinámica utilizando el EntityManager
        reader.setEntityManagerFactory(entityManagerFactory);
        reader.setQueryString(query);

        // Puedes agregar configuraciones adicionales aquí si lo necesitas

        return reader;
    }
}
