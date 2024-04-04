package com.sismics.util.resource;

import java.io.FilenameFilter;
import java.io.IOException;
import java.util.List;

public interface ResourceHandler {
    List<String> listFiles(FilenameFilter filter) throws IOException;
}