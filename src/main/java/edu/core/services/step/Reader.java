package edu.core.services.step;

import edu.core.services.model.Item;
import edu.core.services.model.ItemList;
import org.springframework.batch.item.ItemReader;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public class Reader implements ItemReader<Item> {

    private final Iterator<Item> iterator;

    public Reader(ItemList itemList) {
        this.iterator =
                Optional.ofNullable(itemList)
                        .map(ItemList::getItemList)
                        .map(List::iterator)
                        .orElse(Collections.emptyIterator());
    }

    @Override
    public Item read() throws Exception {
        Item item = null;
        System.out.println("=====================================");
        System.out.println("Begin Reader...");
        if (iterator.hasNext()) {
            item = iterator.next();
        }
        System.out.println("End Reader..." + item);
        return item;
    }
}
