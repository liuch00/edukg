package com.edukgapp.database;

import org.springframework.data.repository.CrudRepository;

public interface HistoryRepository extends CrudRepository<History, Integer> {
	Iterable<History> findAllByAccount(Account account);

	Iterable<History> findAllByAccountOrderByTime(Account account);
}
