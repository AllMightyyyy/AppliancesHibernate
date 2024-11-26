package org.zakaria.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Appliance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String modelo;
    private String marca;
    private String tipo;
    private double precio;

    public Appliance(String modelo, String marca, String tipo, double precio) {
        this.modelo = modelo;
        this.marca = marca;
        this.tipo = tipo;
        this.precio = precio;
    }

    @Override
    public String toString() {
        return "Appliance [id=" + id + ", modelo=" + modelo + ", marca=" + marca + ", tipo=" + tipo + ", precio=" + precio + "]";
    }
}
