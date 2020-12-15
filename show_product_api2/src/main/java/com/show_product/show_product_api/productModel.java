package com.show_product.show_product_api;

import javax.persistence.*;

@Entity
@Table(name="articles")
public class productModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "num_produit")
    private int num_produit;

    @Column
    private String Nom;

    @Column
    private String Prix;

    @Column
    private String Quantite;

    @Column
    private String id_categorie;


    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    @Column
    private String Description;

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    @Column
    private String Image;

    public int getNum_produit() {
        return num_produit;
    }

    public void setNum_produit(int num_produit) {
        this.num_produit = num_produit;
    }

    public String getNom() {
        return Nom;
    }

    public void setNom(String nom) {
        Nom = nom;
    }

    public String getPrix() {
        return Prix;
    }

    public void setPrix(String prix) {
        Prix = prix;
    }

    public String getQuantite() {
        return Quantite;
    }

    public void setQuantite(String quantite) {
        Quantite = quantite;
    }

    public String getId_categorie() {
        return id_categorie;
    }

    public void setId_categorie(String id_categorie) {
        this.id_categorie = id_categorie;
    }

}