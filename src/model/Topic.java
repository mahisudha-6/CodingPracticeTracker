package model;

/**
 * Model representing a Coding Topic (e.g. Arrays, Trees, Dynamic Programming).
 */
public class Topic {
    private int id;
    private String name;

    public Topic() {}

    public Topic(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public Topic(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Topic topic = (Topic) o;
        return id == topic.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
