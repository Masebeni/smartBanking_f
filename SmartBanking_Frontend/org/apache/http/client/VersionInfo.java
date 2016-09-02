package org.apache.http.client;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class VersionInfo {
    private static Properties RELEASE_PROPERTIES = null;
    private static String RELEASE_VERSION = null;
    private static final String RESOURCE = "org/apache/http/client/version.properties";
    static Class class$org$apache$http$client$VersionInfo;

    private static Properties getReleaseProperties() {
        InputStream instream;
        if (RELEASE_PROPERTIES == null) {
            try {
                Class class$;
                if (class$org$apache$http$client$VersionInfo == null) {
                    class$ = class$("org.apache.http.client.VersionInfo");
                    class$org$apache$http$client$VersionInfo = class$;
                } else {
                    class$ = class$org$apache$http$client$VersionInfo;
                }
                instream = class$.getClassLoader().getResourceAsStream(RESOURCE);
                Properties props = new Properties();
                props.load(instream);
                RELEASE_PROPERTIES = props;
                instream.close();
            } catch (IOException e) {
            } catch (Throwable th) {
                instream.close();
            }
            if (RELEASE_PROPERTIES == null) {
                RELEASE_PROPERTIES = new Properties();
            }
        }
        return RELEASE_PROPERTIES;
    }

    static Class class$(String x0) {
        try {
            return Class.forName(x0);
        } catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError().initCause(x1);
        }
    }

    public static String getReleaseVersion() {
        if (RELEASE_VERSION == null) {
            RELEASE_VERSION = (String) getReleaseProperties().get("httpclient.release");
            if (RELEASE_VERSION == null || RELEASE_VERSION.length() == 0 || RELEASE_VERSION.equals("${pom.version}")) {
                RELEASE_VERSION = "UNKNOWN_SNAPSHOT";
            }
        }
        return RELEASE_VERSION;
    }
}
