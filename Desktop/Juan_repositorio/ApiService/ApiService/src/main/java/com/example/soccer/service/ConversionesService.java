package com.example.soccer.service;

public class ConversionesService {

    // Metodo para convertir una dirección IP en formato decimal a un número entero de 32 bits
    public long intStringToIpAddress32(String stringIpv4) { // Conversión implícita de IP decimal a un entero de 32 bits
        // Dividir la dirección IP en sus cuatro octetos.
        String[] octetos = stringIpv4.split("\\.");


        // Convertir cada octeto a long y multiplicarlo por la potencia correspondiente de 256.
        long parte1 = Long.parseLong(octetos[0]) * (long) Math.pow(256, 3); // 256^3
        long parte2 = Long.parseLong(octetos[1]) * (long) Math.pow(256, 2); // 256^2
        long parte3 = Long.parseLong(octetos[2]) * (long) Math.pow(256, 1); // 256^1
        long parte4 = Long.parseLong(octetos[3]) * (long) Math.pow(256, 0); // 256^0

        // Sumar todas las partes para obtener el número como un long.
        return parte1 + parte2 + parte3 + parte4;
    }

    // Metodo para convertir un número entero de 32 bits a una dirección IP en formato decimal
    public String int32ToIpAddressString(long ipEn32Bits) {
        // Inicializar un StringBuilder para construir la dirección IP
        StringBuilder ip = new StringBuilder();

        // Obtener los octetos usando división y módulo
        for (int i = 0; i < 4; i++) {
            // Obtener el octeto actual(ultimo octeto)
            long octeto = ipEn32Bits % 256;
            // Insertar al principio del StringBuilder
            ip.insert(0, octeto);

            // Si no es el último octeto, añadir un punto
            if (i < 3) {
                ip.insert(0, ".");
            }

            // Actualizar ipAddress para obtener el siguiente octeto (eliminar el ultimo octeto procesado)
            ipEn32Bits /= 256;
        }

        return ip.toString();
    }
}
