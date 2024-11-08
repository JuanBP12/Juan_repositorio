package com.example.soccer.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class JuegoNumPrimosService {
    public String numPrimos(int m){
        List<Integer> numerosPrimos = new ArrayList<>();
        for (int i = 2; i <= m; i++) {
            numerosPrimos.add(i); // Inicialmente, asumimos que todos son primos
        }

        for(int x=2;x*x<=m;x++){
            if (numerosPrimos.contains(x)) {
                for (int y = x * x; y <= m; y += x) {
                    numerosPrimos.remove(Integer.valueOf(y)); // Eliminar múltiplos de x
                }
            }
        }
        System.out.println("Numeros primos hasta "+m+" Lista: "+numerosPrimos);
        return "Lista numeros primos hasta 20: "+numerosPrimos;
    }
}
