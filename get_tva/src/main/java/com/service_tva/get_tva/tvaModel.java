package com.service_tva.get_tva;

import javax.persistence.*;

@Entity
@Table(name="categories")
public class tvaModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_categorie")
    private int id_categorie;

    @Column
    private String Nom;

    @Column
    private float TVA;

    public int getId_categorie() {
        return id_categorie;
    }

    public void setId_categorie(int id_categorie) {
        this.id_categorie = id_categorie;
    }

    public String getNom() {
        return Nom;
    }

    public void setNom(String nom) {
        Nom = nom;
    }

    public float getTVA() {
        return TVA;
    }

    public void setTVA(float TVA) {
        this.TVA = TVA;
    }
}
