package com.shane.smartbanking.utils;

import com.shane.smartbanking.BuildConfig;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class RequestUtils {
    public static String convertInputStreamToString(InputStream inputStream) throws Exception {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String result = BuildConfig.FLAVOR;
        while (true) {
            String line = bufferedReader.readLine();
            if (line != null) {
                result = result + line;
            } else {
                inputStream.close();
                return result;
            }
        }
    }
}
