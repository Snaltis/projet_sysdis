package com.service_inventory.get_stock;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;



@Repository
public interface StockRepository extends JpaRepository<FournisseurModel, Integer> {

    @Query(value = "SELECT Quantite FROM ARTICLES WHERE num_produit = ?1", nativeQuery = true)
    String findStock(String numProduit);

    @Query(value = "SELECT NOM FROM FOURNISSEURS", nativeQuery = true)
    List<String> getFournisseurs();

    @Transactional
    @Modifying
    @Query(value = "UPDATE ARTICLES SET QUANTITE = QUANTITE + ?2 WHERE NUM_PRODUIT = ?1", nativeQuery = true)
    void updateQuantite(String numProduit, int quantite);

}
