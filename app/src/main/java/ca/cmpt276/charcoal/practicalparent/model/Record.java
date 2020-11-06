package ca.cmpt276.charcoal.practicalparent.model;


import java.util.ArrayList;
import java.util.List;

import ca.cmpt276.charcoal.practicalparent.R;

public class Record {

    List<String> users = new ArrayList<>();
    List<String> dateTimes = new ArrayList<>();
    List<String> choices = new ArrayList<>();
    List<Boolean> results = new ArrayList<>();
    List<Integer> images = new ArrayList<>();
    private static Record instance;
    private Record() {

    }
    public void addUser(String User) { users.add(User);}
    public void addDateTime(String dateTime) { dateTimes.add(dateTime);}
    public void addChoice(String choice) { choices.add(choice);}
    public void addResult(Boolean result) {
        if (result)
        {
            images.add(R.drawable.ic_won);

        }
        else
        {
            images.add(R.drawable.ic_lost);
        }
        results.add(result);
    }

    public List<String> getUsers() { return users;}
    public List<String> getDateTimes() { return dateTimes;}
    public List<String> getChoices() { return choices;}
    public List<Boolean> getResults() { return results;}

    public static Record getInstance() {
        if (instance == null) {
            instance = new Record();
        }
        return  instance;
    }
    public List<Integer> getImages() {
        return images;
    }

    public void setUsers(List<String> users) {
        this.users = users;
    }
    public void setDateTimes(List<String> users) {
        this.dateTimes = users;
    }
    public void setChoices(List<String> users) {
        this.choices = users;
    }
    public void setImages(List<Integer> image) {
        this.images = image;
    }
    public void setResults(List<Boolean> res) {
        this.results = res;
    }
}
