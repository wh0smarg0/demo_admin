package org.example.admin.repositories;

import org.example.admin.models.ADDelivery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ADDeliveryRepository extends JpaRepository<ADDelivery, Long> {

  boolean existsByOrderId(Long orderId);

}

