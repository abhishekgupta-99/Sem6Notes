package com.abhishek.SEM6.models;

import java.util.ArrayList;

/**
 * Created by abhishek on 3/2020.
 */

public class Subject {

    public int id;
    public String subjectName;
    public ArrayList<Book> books;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public ArrayList<Book> getBooks() {
        return books;
    }

    public void setBooks(ArrayList<Book> books) {
        this.books = books;
    }


}
