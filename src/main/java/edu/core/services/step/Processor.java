package edu.core.services.step;

import edu.core.services.exception.ProcessNonRetriableException;
import edu.core.services.model.Item;
import org.springframework.batch.core.annotation.OnProcessError;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class Processor implements ItemProcessor<Item, Item> {

    @Override
    public Item process(Item item) throws Exception {
        Item respItem = item;
        System.out.println("Begin Process :" + item);


        System.out.println("End Process :" + respItem);
        return respItem;
    }

    @OnProcessError
    public void OnProcessError(Item item, Exception e) {
        System.out.println(" ===> Item process error : " + e.getMessage());
    }
}
