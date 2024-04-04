package com.sismics.util;

import com.sismics.util.resource.FileSystemResourceHandler;
import com.sismics.util.resource.JarResourceHandler;
import com.sismics.util.resource.ResourceHandler;

import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.*;

/**
 * Resource utilities.
 *
 * @author jtremeaux 
 */
public class ResourceUtil {
	
	private ResourceUtil() {
		
	}

    /**
     * List files inside a directory. The path can be a directory on the filesystem, or inside a JAR.
     * 
     * @param clazz Class
     * @param path Path
     * @param filter Filter
     * @return List of files
     * @throws URISyntaxException
     * @throws IOException
     */
    public static List<String> list(Class<?> clazz, String path, FilenameFilter filter) throws URISyntaxException, IOException {
    	URL dirUrl = getResourceUrl(clazz, path);
    	try {
        	ResourceHandler resourceHandler = null;
        
            if (dirUrl != null && dirUrl.getProtocol().equals("file")) {
                resourceHandler = new FileSystemResourceHandler(dirUrl);
            }else if(dirUrl != null && dirUrl.getProtocol().equals("jar")) {
            	if (path.startsWith("/")) {
                    path = path.substring(1);
                }
                if (!path.endsWith("/")) {
                    path = path + "/";
                }
                resourceHandler = new JarResourceHandler(dirUrl, path);
            }
            return resourceHandler.listFiles(filter);
        } catch (Exception e) {
        	throw new UnsupportedOperationException(MessageFormat.format("Cannot list files for URL {0}", dirUrl));
		}
            
    }

    /**
     * List files inside a directory. The path can be a directory on the filesystem, or inside a JAR.
     * 
     * @param clazz Class
     * @param path Path
     * @return List of files
     * @throws URISyntaxException
     * @throws IOException
     */
    public static List<String> list(Class<?> clazz, String path) throws URISyntaxException, IOException {
        return list(clazz, path, null);
    }
    
    private static URL getResourceUrl(Class<?> clazz, String path) {
    	// Path is a directory on the filesystem
    	URL dirUrl = clazz.getResource(path);
        
        // Path is a directory inside the same JAR as clazz
        if (dirUrl == null) {
            String className = clazz.getName().replace(".", "/") + ".class";
            dirUrl = clazz.getClassLoader().getResource(className);
        }
        return dirUrl;
    }
}
