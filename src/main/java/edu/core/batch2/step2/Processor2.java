package edu.core.batch2.step2;

import edu.core.batch2.BusinessLogic2;
import edu.core.exception.NonRetriableException;
import edu.core.model.Item;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;

public class Processor2 implements ItemProcessor<Item, Item> {

    @Autowired
    private BusinessLogic2 businessLogic2;

    @Override
    public Item process(Item item) throws NonRetriableException {
        System.out.println("Begin Process :" + item);
        Item respItem = businessLogic2.getRemoteResponse(item);
        System.out.println("End Process :" + respItem);
        return respItem;
    }
}
