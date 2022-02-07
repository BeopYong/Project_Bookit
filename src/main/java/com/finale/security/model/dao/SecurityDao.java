package com.finale.security.model.dao;

import org.springframework.security.core.userdetails.UserDetails;

public interface SecurityDao {

	UserDetails loadUserByUsername(String username);

}