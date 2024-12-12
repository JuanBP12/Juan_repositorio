package com.example.BatchProcessor.config;

import com.example.BatchProcessor.model.GenericEntity;
import com.example.BatchProcessor.model.Persona;
import com.example.BatchProcessor.reader.ApiItemReader;
import com.example.BatchProcessor.reader.CsvItemReader;
import com.example.BatchProcessor.reader.DatabaseReader;
import com.example.BatchProcessor.repository.GenericRepository;
import com.example.BatchProcessor.service.GenericProcessor;
import com.example.BatchProcessor.service.PersonaItemProcessor;
import com.example.BatchProcessor.writer.ApiItemWriter;
import com.example.BatchProcessor.writer.CsvItemWriter;
import com.example.BatchProcessor.writer.DatabaseWriter;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.TaskExecutorJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.web.client.RestTemplate;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Map;


/**
 * Configuración de la ejecución de trabajos por lotes utilizando Spring Batch.
 * Esta clase configura los componentes principales para ejecutar un trabajo por lotes,
 * incluyendo el repositorio de trabajos, el lanzador de trabajos y la gestión de transacciones.
 */

/*{
    "reader": "databaseItemReader",
    "processor": "genericProcessor",
    "writer": "csvItemWriter"
}*/

//@EnableScheduling
@Configuration
@PropertySource("classpath:application.properties")
public class BatchConfig {

    private final DataSource dataSource;

    public BatchConfig(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    // Configuracion de JdbcTemplate
    @Bean(name = "jdbcTemplate")
    public JdbcTemplate jdbcTemplate() {
        return new JdbcTemplate(dataSource);
    }

    // Configuración de JpaTransactionManager si no estuvieramos usando JPA usariamos PlatformTransactionManager
    @Bean(name = "transactionManager")
    public JpaTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }


    // Configuración del JobRepository
    @Bean(name = "jobRepository")
    public JobRepository jobRepository(JpaTransactionManager transactionManager) throws Exception {
        JobRepositoryFactoryBean factory = new JobRepositoryFactoryBean();
        factory.setDataSource(dataSource);
        factory.setTransactionManager(transactionManager);
        factory.setDatabaseType("MYSQL");
        factory.afterPropertiesSet(); // Inicializa el factory
        return factory.getObject();
    }

    // Configuración de JobLauncher usando TaskExecutorJobLauncher
    @Bean(name = "jobLauncher")
    public JobLauncher jobLauncher(JobRepository jobRepository) throws Exception {
        TaskExecutorJobLauncher jobLauncher = new TaskExecutorJobLauncher();
        jobLauncher.setJobRepository(jobRepository);

        // Configura un TaskExecutor (sincrónico o asincrónico)
        TaskExecutor taskExecutor = new SyncTaskExecutor();  // O usa SimpleAsyncTaskExecutor para asincrónico
        jobLauncher.setTaskExecutor(taskExecutor);  // Establecer el TaskExecutor
        return jobLauncher;
    }


    //ARCHIVO
    @Bean(name = "csvItemReader")
    public CsvItemReader<?> csvItemReader() {
        return new CsvItemReader<>();
    }

    @Bean(name = "csvItemWriter")
    public CsvItemWriter<GenericEntity> csvItemWriter() {
        return new CsvItemWriter<>();
    }



    //DB
    @StepScope
    @Bean(name = "databaseItemReader")
    public JpaPagingItemReader<?> databaseReader(EntityManagerFactory entityManagerFactory,
                                                 @Value("#{jobParameters['entityClass']}") String entityClassName) throws ClassNotFoundException {
        return new DatabaseReader().databaseReader(entityManagerFactory, entityClassName);
    }

    @Bean(name = "databaseItemWriter")
    public ItemWriter<GenericEntity> databaseItemWriter(GenericRepository genericRepository) {
        return new DatabaseWriter.ManualItemWriter<>(genericRepository);
    }



    //API
    // Bean para ApiItemReader genérico
    @Bean(name = "apiItemReader")
    public <T> ApiItemReader<T> apiItemReader(RestTemplateBuilder restTemplateBuilder, @Value("${apiToReadUrl}") String apiUrl) {
        RestTemplate restTemplate = restTemplateBuilder.build(); // Construye el RestTemplate
        return new ApiItemReader<>(restTemplate, new ArrayList<>(), 0, null, apiUrl); // Inicializa con valores predeterminados
    }


    // Bean para ApiItemWriter genérico
    @Bean(name = "apiItemWriter")
    public <T> ApiItemWriter<T> apiItemWriter(RestTemplateBuilder restTemplateBuilder, @Value("${apiToWriteUrl}") String apiUrl) {
        RestTemplate restTemplate = restTemplateBuilder.build(); // Construye el RestTemplate
        return new ApiItemWriter<>(restTemplate, apiUrl); // Devuelve una instancia genérica de ApiItemWriter
    }




    //PROCESSOR
    // Bean para GenericProcessor
    @Bean(name = "genericProcessor")
    public <T> GenericProcessor<T> genericProcessor() {
        return new GenericProcessor<>();
    }

    // Bean para PersonaItemProcessor
    @Bean(name = "personaItemProcessor")
    public ItemProcessor<Map<String, Object>, Persona> personaItemProcessor() {
        return new PersonaItemProcessor();
    }

}
