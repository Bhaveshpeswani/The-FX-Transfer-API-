package com.bhavesh.fxtransfer.repository;

import com.bhavesh.fxtransfer.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long>{
}
