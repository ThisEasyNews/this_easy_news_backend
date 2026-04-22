package com.thiseasynews.server.repository;

import com.thiseasynews.server.entity.UserAuthProvider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserAuthProviderRepository extends JpaRepository<UserAuthProvider, Long> {
    Optional<UserAuthProvider> findByProviderTypeAndProviderUserId(String providerType, String providerUserId);
}