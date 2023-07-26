package com.dnd.repositories;

import com.dnd.models.Hero;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HeroRepository extends JpaRepository<Hero, Long> {

    Hero findByName(String name);
}
