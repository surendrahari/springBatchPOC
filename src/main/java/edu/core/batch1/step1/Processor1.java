package edu.core.batch1.step1;

import edu.core.batch1.BusinessLogic1;
import edu.core.exception.NonRetriableException;
import edu.core.model.Item;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;

public class Processor1 implements ItemProcessor<Item, Item> {

    @Autowired
    private BusinessLogic1 businessLogic1;

    @Override
    public Item process(Item item) throws NonRetriableException {
        System.out.println("Begin Process :" + item);
        Item respItem = businessLogic1.getRemoteResponse(item);
        System.out.println("End Process :" + respItem);
        return respItem;
    }
}
