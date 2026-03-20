package org.example.repo;

import org.example.domain.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WishlistRepo extends JpaRepository<Wishlist, Integer> {

    List<Wishlist> findByUser_Id(int userId);

    Optional<Wishlist> findByUser_IdAndItem_Id(int userId, int itemId);

    List<Wishlist> findByUser_IdAndItem_IdIn(int userId, List<Integer> itemIds);
}


