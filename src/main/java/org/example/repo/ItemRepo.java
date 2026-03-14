package org.example.repo;

import org.example.domain.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for Item entities.
 * Extends JpaRepository to provide standard CRUD operations out of the box.
 */
@Repository
public interface ItemRepo extends JpaRepository<Item, Integer> {
}
