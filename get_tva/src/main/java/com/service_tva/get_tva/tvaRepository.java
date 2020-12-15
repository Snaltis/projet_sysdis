package com.service_tva.get_tva;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface tvaRepository extends JpaRepository<tvaModel, Integer> {}

