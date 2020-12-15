package com.service_checkout.service_checkout;


import javax.persistence.*;

@Entity
@javax.persistence.Table(name="COMMANDES_PRODUITS")
public class commandeProduitModel {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "num_commande_produit")
    private int num_commande_produit;

    @Column
    private int num_produit;

    @Column
    private int quantite;

    @Column
    private int num_commande;

    public int getNum_commande_produit() {
        return num_commande_produit;
    }

    public void setNum_commande_produit(int num_commande_produit) {
        this.num_commande_produit = num_commande_produit;
    }

    public int getNum_produit() {
        return num_produit;
    }

    public void setNum_produit(int num_produit) {
        this.num_produit = num_produit;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    public int getNum_commande() {
        return num_commande;
    }

    public void setNum_commande(int num_commande) {
        this.num_commande = num_commande;
    }
}
