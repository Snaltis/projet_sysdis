package com.get_livraisons_api.get_livraisons;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface livraisonRepository extends JpaRepository<livraisonModel, Integer> {
    @Query(value = "SELECT * FROM FORFAITS WHERE nom_forfait = ?1", nativeQuery = true)
    livraisonModel findByName(String name);
}
