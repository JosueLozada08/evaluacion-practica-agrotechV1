package com.agrotech.model;

// Java 17: usamos record para un POJO inmutable
public record Lectura(String id_sensor, String fecha, double humedad, double temperatura) {}
