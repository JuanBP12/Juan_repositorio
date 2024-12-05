package com.example.scheduledbach.reader;

import com.example.scheduledbach.models.Venta;
import com.example.scheduledbach.repository.VentaRepository;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Iterator;

@Component
public class JpaItemReader implements ItemReader<Venta> {

    private final VentaRepository ventaRepository;  // Inyectamos el repositorio JPA
    private Iterator<Venta> currentIterator;  // Iterador para recorrer los resultados

    @Autowired
    public JpaItemReader(VentaRepository ventaRepository) {
        this.ventaRepository = ventaRepository;
    }

    @Override
    public Venta read() throws Exception {
        // Si el iterador está vacío o no hay más elementos, cargamos los siguientes registros
        if (currentIterator == null || !currentIterator.hasNext()) {
            cargarDatos();
        }

        return currentIterator != null && currentIterator.hasNext() ? currentIterator.next() : null;
    }

    private void cargarDatos() {
        List<Venta> ventas = ventaRepository.findAll();  // Carga todas las ventas desde el repositorio
        currentIterator = ventas != null ? ventas.iterator() : null;  // Crea el iterador si hay datos
    }
}
