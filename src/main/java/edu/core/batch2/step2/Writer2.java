package edu.core.batch2.step2;

import edu.core.exception.WriteNonRetriableException;
import edu.core.model.Item;
import org.springframework.batch.item.ItemWriter;

import java.util.List;
import java.util.function.Predicate;

public class Writer2 implements ItemWriter<Item> {

    private Predicate<Item> failedCondition = item -> item.getId() % 20 == 0;

    @Override
    public void write(List<? extends Item> itemList) throws Exception {
        System.out.println("Begin Write :" + itemList);
        if (itemList != null) {
            for (Item item : itemList) {
                if (item != null) {
                    if (failedCondition.test(item)) {
                        throw new WriteNonRetriableException("id:" + item.getId());
                    } else {
                        doBussinessLogic(item);
                    }
                }
            }
        }
        System.out.println("End Write :" + itemList);
    }

    private void doBussinessLogic(Item item) {
        System.out.println(item);
    }
}
