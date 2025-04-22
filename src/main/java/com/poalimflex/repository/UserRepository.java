package com.poalimflex.repository;

import com.poalimflex.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    /**
     * Find a user by their unique user ID
     */
    Optional<User> findByUserId(String userId);

    /**
     * Find a user by their email address
     */
    Optional<User> findByEmail(String email);

    /**
     * Check if a user with the given email exists
     */
    boolean existsByEmail(String email);
}