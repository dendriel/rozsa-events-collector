package mocks;

import java.util.List;

public class ObjectForCustomCollection {
    private final String name;

    private final Integer age;

    private final List<String> hobies;

    public ObjectForCustomCollection(String name, Integer age, List<String> hobies) {
        this.name = name;
        this.age = age;
        this.hobies = hobies;
    }

    public String getName() {
        return name;
    }

    public Integer getAge() {
        return age;
    }

    public List<String> getHobies() {
        return hobies;
    }
}
