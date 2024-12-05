package com.example.scheduledbach.writer;


import com.example.scheduledbach.models.Venta;
import com.example.scheduledbach.repository.VentaRepository;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

@Component
public class VentaItemWriter implements ItemWriter<Venta> {


    private final VentaRepository ventaRepository;

    public VentaItemWriter(VentaRepository ventaRepository) {
        this.ventaRepository = ventaRepository;
    }

    @Override
    public void write(Chunk<? extends Venta> chunk) throws Exception {
        System.out.println("Guardando un chunk de " + chunk.size() + " ventas");
        for (Venta venta : chunk) {
            System.out.println("Guardando venta con id: " + venta.getId());
        }
        ventaRepository.saveAll(chunk);
    }
}
