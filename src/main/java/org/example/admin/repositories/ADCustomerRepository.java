package org.example.admin.repositories;

import org.example.admin.models.ADCustomer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ADCustomerRepository extends JpaRepository<ADCustomer, Long> {
  Optional<ADCustomer> findByEmail(String email);
}

