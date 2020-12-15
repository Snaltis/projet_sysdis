package com.service_cart.service_cart;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface cartItemRepository extends JpaRepository<cartItemModel, Integer> {

    @Query(value = "SELECT * FROM COMMANDES_PRODUITS WHERE num_commande = ?1", nativeQuery = true)
    List<cartItemModel> findByNumCommande(int numCommande);

    @Query(value = "SELECT NUM_COMMANDE_PRODUIT FROM COMMANDES_PRODUITS WHERE num_commande = ?1 AND num_produit = ?2", nativeQuery = true)
    Integer findIdByCommandeProduit(int numCommande, int numProduit);

    @Query(value = "SELECT QUANTITE FROM COMMANDES_PRODUITS WHERE NUM_COMMANDE_PRODUIT = ?1", nativeQuery = true)
    Integer findQuantiteById(int numCommandeProduit);

}
