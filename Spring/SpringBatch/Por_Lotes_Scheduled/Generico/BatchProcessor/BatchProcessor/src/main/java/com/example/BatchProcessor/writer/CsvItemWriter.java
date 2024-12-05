package com.example.BatchProcessor.writer;

import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.List;


public class CsvItemWriter<T> implements ItemWriter<T> {

    private final CsvMapper csvMapper = new CsvMapper();
    private final CsvSchema schema;

    public CsvItemWriter(Class<T> clazz) {
        this.schema = csvMapper.schemaFor(clazz).withHeader();
    }

    @Override
    public void write(Chunk<? extends T> chunk) throws Exception {
        File file = new File("output.csv");

        // Obtener los items del Chunk
        List<? extends T> items = chunk.getItems();

        try {
            csvMapper.writerFor((Class<T>) items.get(0).getClass())
                    .with(schema)
                    .writeValue(file, items);
        } catch (IOException e) {
            throw new RuntimeException("Error writing CSV", e);
        }
    }
}


