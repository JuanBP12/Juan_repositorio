package com.example.BatchProcessor.job;

import com.example.BatchProcessor.listener.JobCompletionListener;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración genérica para un trabajo por lotes que procesa cualquier tipo de objeto,
 * leyendo datos desde un archivo CSV o base de datos, y escribiendo los resultados.
 */
@Configuration
public class ProcessingJob {

    private final JobCompletionListener jobCompletionListener;  // Inyectamos el listener

    // Constructor para inyectar el JobCompletionListener
    public ProcessingJob(JobCompletionListener jobCompletionListener) {
        this.jobCompletionListener = jobCompletionListener;
    }

    /**
     * Configuración del trabajo genérico que acepta cualquier tipo de objeto.
     * @param jobRepository El repositorio del trabajo
     * @param genericStep El paso genérico para el trabajo
     * @param <T> El tipo de objeto que se va a procesar
     * @return Un trabajo genérico que procesa cualquier tipo de objeto
     */
    @Bean(name = "genericJob")
    public <T> Job genericJob(JobRepository jobRepository,
                              Step genericStep) {
        return new JobBuilder("genericJob", jobRepository)
                .start(genericStep)
                .listener(jobCompletionListener)  // Registramos el listener
                .build();
    }

    /**
     * Configuración del paso genérico que acepta cualquier tipo de objeto.
     * @param jobRepository El repositorio del trabajo
     * @param reader El lector genérico de datos
     * @param processor El procesador genérico de datos
     * @param writer El escritor genérico de datos
     * @param <T> El tipo de objeto que se va a procesar
     * @return Un paso genérico que procesa cualquier tipo de objeto
     */
    @Bean(name = "genericStep")
    public <T> Step genericStep(JobRepository jobRepository,
                                ItemReader<T> reader,
                                ItemProcessor<T, T> processor,
                                ItemWriter<T> writer) {
        return new StepBuilder("genericStep", jobRepository)
                .<T, T>chunk(10)  // Procesa el objeto del tipo T
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }
}
