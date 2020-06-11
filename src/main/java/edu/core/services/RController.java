package edu.core.services;

import edu.core.services.exception.ProcessNonRetriableException;
import edu.core.services.model.Item;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;

@RestController
@EnableRetry
public class RController {

    @GetMapping("/api/get")
    @Retryable(value = {SQLException.class, NumberFormatException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000) )
    public void businessAPI() throws Exception {
        System.out.println("Before DB API call....");
        throw new SQLException("DB api fails");
    }

    @PostMapping("/api/item")
    @Retryable(value = {ProcessNonRetriableException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000) )
    public Item getItem(@RequestBody Item item) throws Exception {
        System.out.println("Before DB API call....");
        if ( item.getId() % 10 == 0 ) {
            throw new ProcessNonRetriableException("DB api fails");
        }
        item.setName(item.getName().toUpperCase());
        return item;
    }

    @Recover
    public void recoverOption1(ProcessNonRetriableException e) {
        System.out.println("Recovery Code : due to ProcessNonRetriableException : " + e);
    }

    @Recover
    public void recoverOption2(SQLException e) {
        System.out.println("Recovery Code : due to SQLException Exception : " + e);
    }

}
