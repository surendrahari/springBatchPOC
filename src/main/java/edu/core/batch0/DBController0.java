package edu.core.batch0;

import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.websocket.server.PathParam;
import java.sql.SQLException;

@RestController
public class DBController0 {

    @GetMapping("/api0/retry")
    @Retryable(value = {SQLException.class, NumberFormatException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000))
    public void retry(@RequestParam("id") Integer id) throws Exception {
        System.out.println("Begin retry check ...." + id);
        if (id == 1)
            throw new SQLException("DB api fails");
        else if (id == 2)
            throw new NumberFormatException("DB api fails");
        System.out.println("End retry check...");
    }

    @Recover
    public void recoverOption1(NumberFormatException e) {
        System.out.println("Recovery Code : due to NumberFormatException : " + e);
    }

    @Recover
    public void recoverOption2(SQLException e) {
        System.out.println("Recovery Code : due to SQLException Exception : " + e);
    }
}
