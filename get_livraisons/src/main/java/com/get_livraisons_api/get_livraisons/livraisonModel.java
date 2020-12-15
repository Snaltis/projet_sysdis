package com.get_livraisons_api.get_livraisons;

import javax.persistence.*;

@Entity
@Table(name="forfaits")
public class livraisonModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "nom_forfait")
    private String nom_forfait;

    @Column
    private float prix;

    public String getNom_forfait() {
        return nom_forfait;
    }

    public void setNom_forfait(String nom_forfait) {
        this.nom_forfait = nom_forfait;
    }

    public float getPrix() {
        return prix;
    }

    public void setPrix(float prix) {
        this.prix = prix;
    }
}
