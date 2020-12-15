package com.fournisseur.fournisseur;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ArticleRepository extends JpaRepository<ArticleModel, Integer> {

    @Query(value = "SELECT Quantite FROM ARTICLES WHERE num_produit = ?1", nativeQuery = true)
    String findQuantite(String numProduit);

    @Query(value = "SELECT Prix FROM ARTICLES WHERE num_produit = ?1", nativeQuery = true)
    String findPrix(String numProduit);

    @Transactional
    @Modifying
    @Query(value = "UPDATE ARTICLES SET QUANTITE = QUANTITE - ?2 WHERE NUM_PRODUIT = ?1", nativeQuery = true)
    void updateQuantite(int numProduit, int quantite);

}
