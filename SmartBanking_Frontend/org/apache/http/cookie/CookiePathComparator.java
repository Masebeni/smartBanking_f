package org.apache.http.cookie;

import java.util.Comparator;

public class CookiePathComparator implements Comparator {
    private String normalizePath(Cookie cookie) {
        String path = cookie.getPath();
        if (path == null) {
            path = "/";
        }
        if (path.endsWith("/")) {
            return path;
        }
        return new StringBuffer().append(path).append("/").toString();
    }

    public int compare(Object o1, Object o2) {
        Cookie c2 = (Cookie) o2;
        String path1 = normalizePath((Cookie) o1);
        String path2 = normalizePath(c2);
        if (path1.equals(path2)) {
            return 0;
        }
        if (path1.startsWith(path2)) {
            return -1;
        }
        if (path2.startsWith(path1)) {
            return 1;
        }
        return 0;
    }
}
