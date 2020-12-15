package com.service_users.service_users;

import javax.persistence.*;

@Entity
@Table(name="users")
public class userModel {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "num_client")
    private int num_client;

    @Column
    private String Nom;

    @Column
    private String Prenom;

    @Column
    private String Mail;

    @Column
    private String Mdp;

    @Column
    private float Montant_dispo;

    @Column
    private String Adresse;

    public userModel() {
    }

    public int getnum_client() {
        return num_client;
    }

    public void setnum_client(int numClient) {
        num_client = numClient;
    }

    public String getNom() {
        return Nom;
    }

    public void setNom(String nom) {
        Nom = nom;
    }

    public String getPrenom() {
        return Prenom;
    }

    public void setPrenom(String prenom) {
        Prenom = prenom;
    }

    public String getMail() {
        return Mail;
    }

    public void setMail(String mail) {
        Mail = mail;
    }

    public String getMdp() {
        return Mdp;
    }

    public void setMdp(String mdp) {
        Mdp = mdp;
    }

    public float getMontant_dispo() {
        return Montant_dispo;
    }

    public void setMontant_dispo(float montant_dispo) {
        Montant_dispo = montant_dispo;
    }

    public String getAdresse() {
        return Adresse;
    }

    public void setAdresse(String adresse) {
        Adresse = adresse;
    }
}
