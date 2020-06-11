package edu.core.services.step;

import edu.core.services.exception.ProcessNonRetriableException;
import edu.core.services.model.Item;
import org.springframework.batch.core.annotation.OnProcessError;
import org.springframework.batch.item.ItemProcessor;

import java.util.Optional;
import java.util.function.Predicate;

public class Processor implements ItemProcessor<Item, Item> {

    private Predicate<Item> failedCondition = item -> item.getId() % 10 == 0;

    @Override
    public Item process(Item item) throws Exception {
        System.out.println("Begin Process :" + item);
        if (item != null) {
            if (failedCondition.test(item)) {
                throw new ProcessNonRetriableException("id:" + item.getId());
            } else {
                doBussinessLogic(item);
            }
        }
        System.out.println("End Process :" + item);
        return item;
    }

    private void doBussinessLogic(Item item) {
        item.setName(
                Optional.ofNullable(item.getName())
                        .orElse("")
                        .toUpperCase()
        );
    }

    @OnProcessError
    public void OnProcessError(Item item, Exception e) {
        System.out.println(" ===> Item process error : " + e.getMessage());
    }
}
