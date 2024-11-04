package net.tylerwade.FinanceTracker.controllers;

import net.tylerwade.FinanceTracker.models.Transaction;
import net.tylerwade.FinanceTracker.models.User;
import net.tylerwade.FinanceTracker.repositories.TransactionRepository;
import net.tylerwade.FinanceTracker.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.swing.text.html.Option;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    // Create Transaction
    @PostMapping("/add")
    private @ResponseBody Transaction addTransaction(@RequestBody Transaction transaction, @CookieValue("user_id") String user_idCookie) {

        if (user_idCookie.isEmpty()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        Integer user_id = Integer.parseInt(user_idCookie);

        if (transaction.getType() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        Optional<User> userOptional = userRepository.findById(user_id);
        if (userOptional.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        User user = userOptional.get();

        // Deposit
        if (transaction.getType().equals("deposit")) {
            user.setBalance(user.getBalance() + transaction.getAmount());
        }
        // Withdraw
        else if (transaction.getType().equals("withdraw")) {
            user.setBalance(user.getBalance() - transaction.getAmount());
        }
        // Update new balance on transaction
        transaction.setNew_balance(user.getBalance());

        transaction.setUser_id(user_id);

        transactionRepository.save(transaction);
        return transaction;
    }


    // Get User Transactions
    @GetMapping("/user")
    private @ResponseBody List<Transaction> getUserTransactions(@CookieValue(name="user_id") String user_idCookie) {
        if (user_idCookie == null ) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        Integer user_id = Integer.parseInt(user_idCookie);

        List<Transaction> transactions = transactionRepository.findByUserId(user_id);

        return transactions;
    }


}
