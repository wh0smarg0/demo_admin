package org.example.admin.repositories;

import org.example.admin.models.ADOrderTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ADOrderTableRepository extends JpaRepository<ADOrderTable, Long> {

}

