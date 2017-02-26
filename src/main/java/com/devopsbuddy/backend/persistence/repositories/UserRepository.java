package com.devopsbuddy.backend.persistence.repositories;

import com.devopsbuddy.backend.persistence.domain.backend.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by madgergely on 2017.02.26..
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
