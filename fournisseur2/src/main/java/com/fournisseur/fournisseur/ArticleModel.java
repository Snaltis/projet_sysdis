package com.fournisseur.fournisseur;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="Articles")
public class ArticleModel {
    @Id
    @Column(name = "num_produit")
    private String num_produit;

    @Column
    private Double prix;

    @Column
    private Integer quantite;

    public String getNum_produit() {
        return num_produit;
    }

    public void setNum_produit(String num_produit) {
        this.num_produit = num_produit;
    }

    public Double getPrix() {
        return prix;
    }

    public void setPrix(Double prix) {
        this.prix = prix;
    }

    public Integer getQuantite() {
        return quantite;
    }

    public void setQuantite(Integer quantite) {
        this.quantite = quantite;
    }
}