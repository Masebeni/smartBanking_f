package org.apache.http.cookie;

import com.shane.smartbanking.BuildConfig;
import java.util.Comparator;

public class CookieIdentityComparator implements Comparator {
    public int compare(Object o1, Object o2) {
        Cookie c1 = (Cookie) o1;
        Cookie c2 = (Cookie) o2;
        int res = c1.getName().compareTo(c2.getName());
        if (res != 0) {
            return res;
        }
        String d1 = c1.getDomain();
        if (d1 == null) {
            d1 = BuildConfig.FLAVOR;
        }
        String d2 = c2.getDomain();
        if (d2 == null) {
            d2 = BuildConfig.FLAVOR;
        }
        return d1.compareToIgnoreCase(d2);
    }
}
