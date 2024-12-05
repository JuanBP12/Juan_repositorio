package com.example.BatchProcessor.config;

import com.example.BatchProcessor.listener.JobCompletionListener;
import com.example.BatchProcessor.model.Material;
import com.example.BatchProcessor.reader.CsvItemReader;
import com.example.BatchProcessor.service.MaterialProcessor;
import com.example.BatchProcessor.writer.CsvItemWriter;
import com.example.BatchProcessor.job.ProcessingJob;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.Step;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class MaterialBatchConfig {

    // Inyectamos la propiedad csv.file.path desde application.properties
    @Value("${csv.file.path}")
    private String filePath;

    private final JobCompletionListener jobCompletionListener;

    public MaterialBatchConfig(JobCompletionListener jobCompletionListener) {
        this.jobCompletionListener = jobCompletionListener;
    }

    // Definimos el Job, que utiliza el Step correspondiente
    @Bean(name = "materialJob")
    public Job materialJob(JobRepository jobRepository,
                           Step materialStep) {
        return new ProcessingJob(jobCompletionListener).genericJob(jobRepository, materialStep);  // Elimina el JobCompletionListener
    }

    // Definimos el Step, que utiliza el lector, procesador y escritor de Material
    @Bean(name = "materialStep")
    public Step materialStep(JobRepository jobRepository,
                             CsvItemReader<Material> reader,
                             MaterialProcessor processor,
                             CsvItemWriter<Material> writer) {
        return new ProcessingJob(jobCompletionListener).genericStep(jobRepository, reader, processor, writer);
    }

    // Definimos el lector de archivos CSV para el tipo Material
    @Bean
    public CsvItemReader<Material> materialItemReader() throws IOException {
        return new CsvItemReader<>(filePath, Material.class);  // Asegúrate de que CsvItemReader acepte Material.class correctamente
    }

    // Definimos el procesador para Material
    @Bean
    public MaterialProcessor materialProcessor() {
        return new MaterialProcessor();
    }

    // Definimos el escritor para escribir Material en un archivo CSV
    @Bean
    public CsvItemWriter<Material> materialItemWriter() {
        return new CsvItemWriter<>(Material.class);  // Asegúrate de que CsvItemWriter acepte Material.class correctamente
    }
}
