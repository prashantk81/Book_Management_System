package com.sismics.books.core.commonlibrary;

import java.util.List;

import com.sismics.books.core.model.jpa.CommonLibrary;

// package SortingStrategy;

//import com.sismics.books.core.model.jpa.Book;

public interface SortingStrategy {
    // public void sort(List<Booknew> books);
    public List<CommonLibrary>  sort(List<CommonLibrary> listAllBooks);
}


