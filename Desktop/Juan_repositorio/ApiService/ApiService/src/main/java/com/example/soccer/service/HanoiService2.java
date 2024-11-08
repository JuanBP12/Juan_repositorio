package com.example.soccer.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class HanoiService2 {

    public List<String> torresHanoi(int discos, int origen, int auxiliar, int destino) {
        List<String> movimientos = new ArrayList<>();

        // Caso base: si solo hay un disco, moverlo directamente al destino.
        if (discos == 1) {
            movimientos.add("Mover disco 1 de Torre " + origen + " a Torre " + destino);
            return movimientos;
        }

        // Paso 1: Mover todos los discos menos el último (discos-1) desde la torre origen a la torre auxiliar.
        movimientos.addAll(torresHanoi(discos - 1, origen, destino, auxiliar));

        // Paso 2: Mover el disco más grande (el último) directamente de la torre origen a la torre destino.
        movimientos.add("Mover disco " + discos + " de Torre " + origen + " a Torre " + destino);

        // Paso 3: Mover los discos-1 discos de la torre auxiliar a la torre destino.
        movimientos.addAll(torresHanoi(discos - 1, auxiliar, origen, destino));

        return movimientos;
    }
}

