package com.service_users.service_users;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<userModel, Integer> {

    @Query(value = "SELECT * FROM USERS WHERE MAIL = ?1", nativeQuery = true)
    userModel findByEmailAddress(String emailAddress);

}
