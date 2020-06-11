package edu.core.services.model;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ItemList {
    private final List<Item> itemList;

    public ItemList(int max) {
        itemList = IntStream.rangeClosed(1, max)
                .mapToObj(i -> new Item(i, "name"))
                .collect(Collectors.toList());
    }

    public List<Item> getItemList() {
        return itemList;
    }
}
