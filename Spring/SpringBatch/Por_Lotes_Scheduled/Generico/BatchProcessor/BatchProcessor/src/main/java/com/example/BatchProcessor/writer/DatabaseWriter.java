package com.example.BatchProcessor.writer;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityManager;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.util.Assert;

public class DatabaseWriter {

    @Bean
    @StepScope
    public <T> JpaItemWriter<T> databaseWriter(EntityManagerFactory entityManagerFactory,
                                               @Value("#{jobParameters['entityClass']}") Class<T> clazz) {
        JpaItemWriter<T> writer = new JpaItemWriter<>();

        // Asegúrate de que la clase de la entidad no sea nula
        Assert.notNull(clazz, "Entity class cannot be null");

        // Obtener el EntityManager de la fábrica de entidades
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        // Configurar el writer JPA
        writer.setEntityManagerFactory(entityManagerFactory);

        // Si necesitas usar una clase dinámica, no es necesario setearla explícitamente
        // el writer manejará las entidades directamente.

        return writer;
    }
}
