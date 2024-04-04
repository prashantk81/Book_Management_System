
package com.sismics.books.core.commonlibrary;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Comparator;

import com.sismics.books.core.model.jpa.Book;
import com.sismics.books.core.model.jpa.CommonLibrary;

public class AverageRatingStrategy implements SortingStrategy {
    	public List<CommonLibrary>  sort(List<CommonLibrary> books) {
            
            Collections.sort(books, new Comparator<CommonLibrary>() {
                @Override
                public int compare(CommonLibrary book1, CommonLibrary book2) {
                    return -Double.compare(book1.getRating(), book2.getRating());
                }
            });
            return books;
    }
}


