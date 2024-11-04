package net.tylerwade.FinanceTracker.repositories;

import net.tylerwade.FinanceTracker.models.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User, Integer> {

    @Query(value = "SELECT * FROM User WHERE email = ?", nativeQuery = true)
    public Iterable<User> findAllByEmail(String email);
}
