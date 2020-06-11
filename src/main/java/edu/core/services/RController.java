package edu.core.services;

import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;

@RestController
@EnableRetry
public class RController {

    @GetMapping("/api/test") // after 3 attempt it's going to invoke the recover
    @Retryable(value = {SQLException.class, NumberFormatException.class}, maxAttempts = 3)
    public void test1() throws Exception {
        System.out.println("test....");
        throw new NumberFormatException("test");
    }

    @Recover
    public void recover1(NumberFormatException nfe) {
        System.out.println("number format exception : " + nfe);
    }

    @Recover
    public void recover1(SQLException s) {
        System.out.println("SQL exception : " + s);
    }

}
