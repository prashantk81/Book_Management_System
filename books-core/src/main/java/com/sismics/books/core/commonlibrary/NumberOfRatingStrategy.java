package com.sismics.books.core.commonlibrary;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.sismics.books.core.model.jpa.CommonLibrary;
import java.util.Comparator;

//import com.sismics.books.rest.resource.SortingStrategy;

public class NumberOfRatingStrategy implements SortingStrategy {

    public List<CommonLibrary>  sort(List<CommonLibrary> books) {
        
        Collections.sort(books, new Comparator<CommonLibrary>() {
            @Override
            public int compare(CommonLibrary book1, CommonLibrary book2) {
                return -Double.compare(book1.getNoOfRatings(), book2.getNoOfRatings());
            }
        });
        return books;
    }
}


