package com.dnd.repositories;

import com.dnd.models.Enemies;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EnemiesRepository extends JpaRepository<Enemies, Long> {

}

