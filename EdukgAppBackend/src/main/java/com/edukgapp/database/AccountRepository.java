package com.edukgapp.database;

import org.springframework.data.repository.CrudRepository;

public interface AccountRepository extends CrudRepository<Account, Integer> {

	Account findByName(String name);
}
