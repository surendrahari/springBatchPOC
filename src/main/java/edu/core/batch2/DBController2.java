package edu.core.batch2;

import edu.core.exception.ProcessNonRetriableException;
import edu.core.model.Item;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.function.Predicate;

@RestController
public class DBController2 {

    private Predicate<Item> testCondition = item -> item.getId() % 2 == 0;

    @PostMapping("/api2/item")
    public Item getItem(@RequestBody Item item) throws Exception {
        System.out.println("Begin retry Item ....");
        if (testCondition.test(item)) {
            throw new ProcessNonRetriableException("DB call fails" + item.getId());
        }
        item.setName(item.getName().toUpperCase());
        System.out.println("Begin retry Item ....");
        return item;
    }

    @PostMapping("/api2/retry/item")
    @Retryable(value = {ProcessNonRetriableException.class},
            maxAttempts = 5,
            backoff = @Backoff(delay = 1000))
    public Item retryItem(@RequestBody Item item) throws Exception {
        System.out.println("Begin retry Item ....");
        if (testCondition.test(item)) {
            throw new ProcessNonRetriableException("DB call fails" + item.getId());
        }
        item.setName(item.getName().toUpperCase());
        System.out.println("End retry Item ....");
        return item;
    }

    @Recover
    public void recoverOption1(ProcessNonRetriableException e) {
        System.out.println("Recovery Code : due to ProcessNonRetriableException : " + e);
    }
}
