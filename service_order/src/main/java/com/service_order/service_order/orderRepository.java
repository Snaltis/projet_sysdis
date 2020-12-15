package com.service_order.service_order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface orderRepository extends JpaRepository<orderModel, Integer> {
    @Query(value = "SELECT * FROM COMMANDES WHERE NUM_CLIENT = (SELECT NUM_CLIENT FROM USERS WHERE NUM_CLIENT = ?1 AND MDP = ?2) AND STATUT = ?3 LIMIT 1", nativeQuery = true)
    orderModel findByUserAndStatut(String userId, String userPassword, String Statut);

    @Query(value = "SELECT NUM_COMMANDE FROM COMMANDES WHERE NUM_CLIENT = (SELECT NUM_CLIENT FROM USERS WHERE NUM_CLIENT = ?1 AND MDP = ?2) AND STATUT = ?3 LIMIT 1", nativeQuery = true)
    Integer findIdByUserAndStatut(String userId, String userPassword, String Statut);

    @Query(value = "SELECT * FROM COMMANDES WHERE NUM_CLIENT = (SELECT NUM_CLIENT FROM USERS WHERE NUM_CLIENT = ?1 AND MDP = ?2)", nativeQuery = true)
    List<orderModel> findAllOrdersFromUser(String userId, String userPassword);

}
