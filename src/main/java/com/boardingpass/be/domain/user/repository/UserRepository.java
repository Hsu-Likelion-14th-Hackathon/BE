package com.boardingpass.be.domain.user.repository;

import com.boardingpass.be.domain.user.Provider;
import com.boardingpass.be.domain.user.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

  Optional<User> findByProviderAndProviderUid(Provider provider, String providerUid);
}
