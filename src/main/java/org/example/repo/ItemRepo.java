package org.example.repo;

import jakarta.persistence.LockModeType;
import org.example.domain.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for Item entities.
 * Extends JpaRepository to provide standard CRUD operations out of the box.
 */
@Repository
public interface ItemRepo extends JpaRepository<Item, Integer> {

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("select i from Item i where i.id in :itemIds")
	List<Item> findAllByIdInForUpdate(@Param("itemIds") List<Integer> itemIds);
}
