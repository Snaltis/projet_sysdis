package com.service_order.service_order;

import javax.persistence.*;
import java.sql.Date;

@Entity
@javax.persistence.Table(name="commandes")
public class orderModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "num_commande")
    private int num_commande;

    @Column
    private int num_client;

    @Column
    private String forfait;

    @Column
    private String statut;

    @Column
    private Double prix_total;

    public int getNum_commande() {
        return num_commande;
    }

    public void setNum_commande(int num_commande) {
        this.num_commande = num_commande;
    }

    public int getNum_client() {
        return num_client;
    }

    public void setNum_client(int num_client) {
        this.num_client = num_client;
    }

    public String getForfait() {
        return forfait;
    }

    public void setForfait(String forfait) {
        this.forfait = forfait;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public Double getPrix_total() {
        return prix_total;
    }

    public void setPrix_total(Double prix_total) {
        this.prix_total = prix_total;
    }
}
