package com.sismics.util.resource;

import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.google.common.collect.Lists;

public class JarResourceHandler implements ResourceHandler {
    private JarFile jarFile;
    private String path;

    public JarResourceHandler(URL jarUrl, String path) throws URISyntaxException, IOException {
    	// Extract the JAR path
    	String jarPath = jarUrl.getPath().substring(5, jarUrl.getPath().indexOf("!"));
        this.jarFile = new JarFile(URLDecoder.decode(jarPath, "UTF-8"));
        this.path = path;
    }

    @Override
    public List<String> listFiles(FilenameFilter filter) throws IOException {
        Set<String> fileSet = new HashSet<>();
        Enumeration<JarEntry> entries = jarFile.entries();
        try {
        	while (entries.hasMoreElements()) {
                // Filter according to the path
                String entryName = entries.nextElement().getName();
                if (entryName.startsWith(path)) {
                	String name = entryName.substring(path.length());
                    addFileToList(fileSet, name, filter);
                }
            }
        }finally {
			jarFile.close();
		}
        return Lists.newArrayList(fileSet);
    }

    private static void addFileToList(Set<String> fileSet, String name, FilenameFilter filter) {
        if (!name.isEmpty()) {
        	// If it is a subdirectory, just return the directory name
            int checkSubdir = name.indexOf("/");
            if (checkSubdir >= 0) {
            	name = name.substring(0, checkSubdir);
            }
            if (filter == null || filter.accept(null, name)) {
                fileSet.add(name);
            }
        }
    }
}