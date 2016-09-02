package org.apache.http.util;

import java.io.IOException;
import org.apache.http.Header;
import org.apache.http.HttpException;
import org.apache.http.io.HttpDataReceiver;

public final class HeaderUtils {
    private HeaderUtils() {
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static org.apache.http.Header[] parseHeaders(org.apache.http.io.HttpDataReceiver r11, int r12, int r13) throws org.apache.http.HttpException, java.io.IOException {
        /*
        if (r11 != 0) goto L_0x000a;
    L_0x0002:
        r9 = new java.lang.IllegalArgumentException;
        r10 = "HTTP data receiver may not be null";
        r9.<init>(r10);
        throw r9;
    L_0x000a:
        r4 = new java.util.ArrayList;
        r4.<init>();
        r2 = 0;
        r8 = 0;
    L_0x0011:
        if (r2 != 0) goto L_0x0045;
    L_0x0013:
        r2 = new org.apache.http.util.CharArrayBuffer;
        r9 = 64;
        r2.<init>(r9);
    L_0x001a:
        r7 = r11.readLine(r2);
        r9 = -1;
        if (r7 == r9) goto L_0x0028;
    L_0x0021:
        r9 = r2.length();
        r10 = 1;
        if (r9 >= r10) goto L_0x0049;
    L_0x0028:
        r9 = r4.size();
        r5 = new org.apache.http.Header[r9];
        r6 = 0;
    L_0x002f:
        r9 = r4.size();
        if (r6 >= r9) goto L_0x00b9;
    L_0x0035:
        r0 = r4.get(r6);
        r0 = (org.apache.http.util.CharArrayBuffer) r0;
        r9 = new org.apache.http.message.BufferedHeader;	 Catch:{ IllegalArgumentException -> 0x00ae }
        r9.<init>(r0);	 Catch:{ IllegalArgumentException -> 0x00ae }
        r5[r6] = r9;	 Catch:{ IllegalArgumentException -> 0x00ae }
        r6 = r6 + 1;
        goto L_0x002f;
    L_0x0045:
        r2.clear();
        goto L_0x001a;
    L_0x0049:
        r9 = 0;
        r9 = r2.charAt(r9);
        r10 = 32;
        if (r9 == r10) goto L_0x005b;
    L_0x0052:
        r9 = 0;
        r9 = r2.charAt(r9);
        r10 = 9;
        if (r9 != r10) goto L_0x00a8;
    L_0x005b:
        if (r8 == 0) goto L_0x00a8;
    L_0x005d:
        r6 = 0;
    L_0x005e:
        r9 = r2.length();
        if (r6 >= r9) goto L_0x0070;
    L_0x0064:
        r1 = r2.charAt(r6);
        r9 = 32;
        if (r1 == r9) goto L_0x0088;
    L_0x006c:
        r9 = 9;
        if (r1 == r9) goto L_0x0088;
    L_0x0070:
        if (r13 <= 0) goto L_0x008b;
    L_0x0072:
        r9 = r8.length();
        r9 = r9 + 1;
        r10 = r2.length();
        r9 = r9 + r10;
        r9 = r9 - r6;
        if (r9 <= r13) goto L_0x008b;
    L_0x0080:
        r9 = new java.io.IOException;
        r10 = "Maximum line length limit exceeded";
        r9.<init>(r10);
        throw r9;
    L_0x0088:
        r6 = r6 + 1;
        goto L_0x005e;
    L_0x008b:
        r9 = 32;
        r8.append(r9);
        r9 = r2.length();
        r9 = r9 - r6;
        r8.append(r2, r6, r9);
    L_0x0098:
        if (r12 <= 0) goto L_0x0011;
    L_0x009a:
        r9 = r4.size();
        if (r9 < r12) goto L_0x0011;
    L_0x00a0:
        r9 = new java.io.IOException;
        r10 = "Maximum header count exceeded";
        r9.<init>(r10);
        throw r9;
    L_0x00a8:
        r4.add(r2);
        r8 = r2;
        r2 = 0;
        goto L_0x0098;
    L_0x00ae:
        r3 = move-exception;
        r9 = new org.apache.http.ProtocolException;
        r10 = r3.getMessage();
        r9.<init>(r10);
        throw r9;
    L_0x00b9:
        return r5;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.http.util.HeaderUtils.parseHeaders(org.apache.http.io.HttpDataReceiver, int, int):org.apache.http.Header[]");
    }

    public static Header[] parseHeaders(HttpDataReceiver datareceiver) throws HttpException, IOException {
        return parseHeaders(datareceiver, -1, -1);
    }
}
