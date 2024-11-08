package com.example.soccer.controller;

import com.example.soccer.service.ConversionesService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")// Define la ruta base para todos los endpoints en este controlador
public class ConversionesController {

    private final ConversionesService conversionesService;// Servicio que maneja la lógica de conversión

    // Constructor que inyecta el servicio de conversiones
    public ConversionesController(ConversionesService conversionesService) {
        this.conversionesService = conversionesService;
    }

    // Endpoint para convertir una dirección IP en formato de cadena a un número de 32 bits
    @GetMapping("/decimalStringBinarioLong32")
    public ResponseEntity<Long> decimalStringABinarioLong32(@RequestParam String n) {

        // Llama al servicio para realizar la conversión y retorna el resultado
        return ResponseEntity.ok(conversionesService.intStringToIpAddress32(n));
    }

    // Endpoint para convertir un número de 32 bits a una dirección IP en formato de cadena
    @GetMapping("/binarioLongDecimalInt32")
    public ResponseEntity<String> binarioLongDecimalInt32(@RequestParam long n) {

        // Llama al servicio para realizar la conversión y retorna el resultado
        return ResponseEntity.ok(conversionesService.int32ToIpAddressString(n));
    }
}
