package com.example.BatchProcessor.reader;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;


public class CsvItemReader<T> implements ItemReader<T> {

    private final Iterator<T> iterator;

    // Este constructor permite pasar la ruta del archivo y la clase que vamos a leer
    public CsvItemReader(@Value("${csv.file.path}") String filePath, Class<T> clazz) throws IOException {
        ObjectMapper mapper = new CsvMapper();
        CsvSchema schema = CsvSchema.emptySchema().withHeader(); // Configura el CSV para que tenga cabecera
        MappingIterator<T> it = mapper.readerFor(clazz).with(schema).readValues(new File(filePath));
        this.iterator = it;
    }

    @Override
    public T read() throws Exception {
        return iterator.hasNext() ? iterator.next() : null;
    }
}



    /*private final Iterator<T> iterator;

    public CsvItemReader(String filePath, Class<T> clazz) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filePath));

        // Usamos CsvMapper de Jackson para leer el CSV
        CsvMapper csvMapper = new CsvMapper();
        CsvSchema schema = csvMapper.schemaFor(clazz).withHeader().withColumnReordering(true);

        // Convertir las líneas del CSV en objetos del tipo T
        List<T> records = (List<T>) csvMapper.readerFor(clazz).with(schema).readValues(reader).readAll();

        this.iterator = records.iterator();
    }

    @Override
    public T read() {
        if (iterator.hasNext()) {
            return iterator.next();  // Leer el siguiente objeto del tipo T
        }
        return null;  // Si no hay más elementos, devolver null
    }
}*/



    /*private final Iterator<String> iterator;

    public CsvItemReader(String filePath) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        this.iterator = reader.lines().iterator();
    }

    @Override
    public T read() {
        if (iterator.hasNext()) {
            String line = iterator.next();
            // Aquí parsea el CSV manualmente según tu clase T
            // Por ejemplo, usando String.split(",")
            return (T) parseLine(line);
        }
        return null;
    }

    private T parseLine(String line) {
        // Implementa la lógica para convertir una línea en un objeto T
        return null;
    }
}*/
