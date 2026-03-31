package com.example.lab06.repository;

import com.example.lab06.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Integer> {

    @Query("select a from Account a where a.loginName = ?1")
    Optional<Account> findByLoginName(String loginName);
}

