package edu.core.services.model;

public class Item {
    private final int id;
    private String name;

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
