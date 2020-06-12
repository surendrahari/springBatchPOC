package edu.core.batch1.step1;

import edu.core.model.Item;
import edu.core.model.ItemList;
import org.springframework.batch.item.ItemReader;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public class Reader1 implements ItemReader<Item> {

    private final Iterator<Item> iterator;

    public Reader1(ItemList itemList) {
        this.iterator =
                Optional.ofNullable(itemList)
                        .map(ItemList::getItemList)
                        .map(List::iterator)
                        .orElse(Collections.emptyIterator());
    }

    @Override
    public Item read() throws Exception {
        System.out.println("=====================================");
        System.out.println("Begin Reader...");

        Item item = null;
        if (iterator.hasNext()) {
            item = iterator.next();
        }

        System.out.println("End Reader..." + item);
        return item;
    }
}
