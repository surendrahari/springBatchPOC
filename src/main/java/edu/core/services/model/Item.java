package edu.core.services.model;

import java.io.Serializable;

public class Item implements Serializable {
    private int id;
    private String name;

    public Item() {
    }

    public Item(int id, String name) {
        this.id = id;
        this.name = name + ":" + id;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Item{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
