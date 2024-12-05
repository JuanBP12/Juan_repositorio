package com.example.BatchProcessor.service;

import com.example.BatchProcessor.repository.GenericRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GenericService<T> {

    private final GenericRepository<T, Long> repository;

    @Autowired
    public GenericService(GenericRepository<T, Long> repository) {
        this.repository = repository;
    }

    public List<T> findAll() {
        return repository.findAll();
    }

    public T save(T entity) {
        return repository.save(entity);
    }
}

