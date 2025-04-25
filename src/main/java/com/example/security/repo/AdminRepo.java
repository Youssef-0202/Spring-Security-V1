package com.example.security.repo;

import com.example.security.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author HP
 **/
public interface AdminRepo extends JpaRepository<Admin,Long> {

}
