package ca.cmpt276.charcoal.practicalparent.model;

import androidx.annotation.NonNull;

/**
 *  Child activity allows for setting and getting information for child class
 */
public class Child {
    private String name;

    public Child(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @NonNull
    @Override
    public String toString() {
        return name;
    }
}
