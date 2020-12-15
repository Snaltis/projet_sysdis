package com.service_checkout.service_checkout;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
//commande produit model model obligé pour trouver la liste (findByNumCommande)
public interface CommandeProduitRepository extends JpaRepository<commandeProduitModel, Integer> {

    @Transactional
    @Modifying
    @Query(value = "UPDATE COMMANDES SET STATUT = 'EN PREPARATION', PRIX_TOTAL = ?2 WHERE NUM_COMMANDE = ?1", nativeQuery = true)
    void updateCommande(int numCommande, double prixTotal);

    @Transactional
    @Modifying
    @Query(value = "UPDATE USERS SET Montant_dispo = Montant_dispo - ?2 WHERE NUM_CLIENT = ?1", nativeQuery = true)
    void updateSoldeUser(String numClient, double somme);

    @Transactional
    @Modifying
    @Query(value = "UPDATE ARTICLES SET QUANTITE = QUANTITE - ?2 WHERE NUM_PRODUIT = ?1", nativeQuery = true)
    void updateQuantiteProduit(int numClient, int quantite);

    @Query(value = "SELECT Montant_dispo FROM USERS WHERE NUM_CLIENT = ?1 AND MDP = ?2", nativeQuery = true)
    Double findSoldeUser(String num_client, String mdp);

    @Query(value = "SELECT \n" +
            "ROUND(((SELECT SUM((prix * quantite) + (prix * quantite * TVA)) from(\n" +
            "select COMMANDES_PRODUITS.num_produit, COMMANDES_PRODUITS.quantite, Articles.prix, CATEGORIES.TVA/100 as \"tva\"\n" +
            "from COMMANDES_PRODUITS, ARTICLES, CATEGORIES\n" +
            "where num_commande = ?1\n" +
            "and COMMANDES_PRODUITS.num_produit = Articles.num_produit\n" +
            "and CATEGORIES.id_categorie = Articles.id_categorie) as T)\n" +
            "+\n" +
            "(select prix from FORFAITS, COMMANDES\n" +
            "where Forfaits.nom_forfait = COMMANDES.forfait\n" +
            "and num_commande = ?1)), 2) as \"prix_total\"",
            nativeQuery = true)
    Double findPrixTotalByNumCommande(int numCommande);

    @Query(value = "SELECT * FROM COMMANDES_PRODUITS WHERE NUM_COMMANDE = ?1", nativeQuery = true)
    List<commandeProduitModel> findByNumCommande(int numCommande);
}

