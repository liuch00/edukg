package com.edukgapp.database;

import org.springframework.data.repository.CrudRepository;

public interface FavoriteRepository extends CrudRepository<Favorite, Integer> {
	Favorite findByCourseAndLabelAndAccount(String course, String label, Account account);

	Iterable<Favorite> findAllByAccount(Account account);
}
