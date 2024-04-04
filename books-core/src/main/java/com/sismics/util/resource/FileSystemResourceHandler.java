package com.sismics.util.resource;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

public class FileSystemResourceHandler implements ResourceHandler {
        private File directory;

        public FileSystemResourceHandler(URL dirUrl) throws URISyntaxException {
            this.directory = new File(dirUrl.toURI());
        }

        @Override
        public List<String> listFiles(FilenameFilter filter) throws IOException {
            return Arrays.asList(this.directory.list(filter));
        }
    }