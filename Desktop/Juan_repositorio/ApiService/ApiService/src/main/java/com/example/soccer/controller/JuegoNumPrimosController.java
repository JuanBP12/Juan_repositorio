package com.example.soccer.controller;

import com.example.soccer.service.JuegoNumPrimosService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@RestController
@RequestMapping("/api")// Define la ruta base para todos los endpoints en este controlador
public class JuegoNumPrimosController {
    private final JuegoNumPrimosService juegoNumPrimosService;

    public JuegoNumPrimosController(JuegoNumPrimosService juegoNumPrimosService) {this.juegoNumPrimosService=juegoNumPrimosService;}

    //Endpoint
    @GetMapping("/numPrimos")
    public ResponseEntity<String> numPrimos(@RequestParam int m){
        try{
            return ResponseEntity.ok(juegoNumPrimosService.numPrimos(m));
        } catch(Exception e){
            return ResponseEntity.notFound().build();
        }

    }

}
