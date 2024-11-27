package com.example.BatchProcessor.job;

import com.example.BatchProcessor.listener.JobCompletionListener;
import com.example.BatchProcessor.model.FileRecord;
import com.example.BatchProcessor.model.Persona;
import com.example.BatchProcessor.reader.CsvFileReader;
import com.example.BatchProcessor.service.RecordProcessor;
import com.example.BatchProcessor.writer.DatabaseWriter;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;


/**
 * Configuración del trabajo por lotes que procesa un archivo CSV y escribe los resultados en la base de datos.
 * Esta clase define un trabajo por lotes que consiste en un solo paso, que incluye un lector de archivos CSV,
 * un procesador de registros y un escritor de base de datos.
 */
@Configuration
@EnableBatchProcessing
public class FileProcessingJob {

    private final JobCompletionListener jobCompletionListener;  // Inyectamos el listener

    private final TaskExecutor stepTaskExecutor;  // Usamos TaskExecutor de los pasos

    public FileProcessingJob(JobCompletionListener jobCompletionListener, @Qualifier("stepTaskExecutor") TaskExecutor stepTaskExecutor) {
        this.jobCompletionListener = jobCompletionListener;
        this.stepTaskExecutor = stepTaskExecutor;
    }

    @Bean(name = "job")
    public Job job(JobRepository jobRepository, Step step) {
        // Aquí pasamos el bean 'step' como parámetro
        return new JobBuilder("fileProcessingJob", jobRepository)
                .start(step)  // Referencia al metodo Step, que ya tiene los parámetros
                .listener(jobCompletionListener)  // Registramos el listener
                .build();
    }

    @Bean(name = "step")
    public Step step(JobRepository jobRepository, JpaTransactionManager transactionManager,
                     CsvFileReader reader, RecordProcessor processor, DatabaseWriter writer) {
        // Aquí definimos el paso utilizando los beans y JobRepository
        return new StepBuilder("fileProcessingStep", jobRepository)
                .<FileRecord, Persona>chunk(10, transactionManager)  // Aquí defines el tamaño del chunk
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .taskExecutor(stepTaskExecutor)  // Usar TaskExecutor para paralelización en el procesamiento y escritura
                .build();
    }
}