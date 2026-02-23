package com.jungle.learning.repository;

import com.jungle.learning.model.Collection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CollectionRepository extends JpaRepository<Collection, Long> {
    
    List<Collection> findByOwnerId(Long ownerId);
    
    List<Collection> findByIsPublicTrue();
    
    List<Collection> findByOwnerIdOrIsPublicTrue(Long ownerId);
}
