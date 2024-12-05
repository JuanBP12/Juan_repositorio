package com.example.BatchProcessor.service;

import com.example.BatchProcessor.model.Material;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;
//MODIFICAR
@Component
public class MaterialProcessor implements ItemProcessor<Material, Material> {
    @Override
    public Material process(Material item) {
        // Puedes agregar lógica para modificar el producto antes de escribirlo
        return item;
    }
}

