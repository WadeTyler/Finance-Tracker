package net.tylerwade.FinanceTracker.repositories;

import net.tylerwade.FinanceTracker.models.Transaction;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TransactionRepository extends CrudRepository<Transaction, Integer> {

    @Query(value = "SELECT * FROM transaction WHERE user_id = ?", nativeQuery = true)
    public List<Transaction> findByUserId(Integer user_id);
}
