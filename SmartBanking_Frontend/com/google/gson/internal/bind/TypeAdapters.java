package com.google.gson.internal.bind;

import android.support.v4.app.NotificationCompat.WearableExtender;
import android.support.v4.media.TransportMediator;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.annotations.SerializedName;
import com.google.gson.internal.LazilyParsedNumber;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.URI;
import java.net.URL;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;

public final class TypeAdapters {
    public static final TypeAdapter<AtomicBoolean> ATOMIC_BOOLEAN;
    public static final TypeAdapterFactory ATOMIC_BOOLEAN_FACTORY;
    public static final TypeAdapter<AtomicInteger> ATOMIC_INTEGER;
    public static final TypeAdapter<AtomicIntegerArray> ATOMIC_INTEGER_ARRAY;
    public static final TypeAdapterFactory ATOMIC_INTEGER_ARRAY_FACTORY;
    public static final TypeAdapterFactory ATOMIC_INTEGER_FACTORY;
    public static final TypeAdapter<BigDecimal> BIG_DECIMAL;
    public static final TypeAdapter<BigInteger> BIG_INTEGER;
    public static final TypeAdapter<BitSet> BIT_SET;
    public static final TypeAdapterFactory BIT_SET_FACTORY;
    public static final TypeAdapter<Boolean> BOOLEAN;
    public static final TypeAdapter<Boolean> BOOLEAN_AS_STRING;
    public static final TypeAdapterFactory BOOLEAN_FACTORY;
    public static final TypeAdapter<Number> BYTE;
    public static final TypeAdapterFactory BYTE_FACTORY;
    public static final TypeAdapter<Calendar> CALENDAR;
    public static final TypeAdapterFactory CALENDAR_FACTORY;
    public static final TypeAdapter<Character> CHARACTER;
    public static final TypeAdapterFactory CHARACTER_FACTORY;
    public static final TypeAdapter<Class> CLASS;
    public static final TypeAdapterFactory CLASS_FACTORY;
    public static final TypeAdapter<Currency> CURRENCY;
    public static final TypeAdapterFactory CURRENCY_FACTORY;
    public static final TypeAdapter<Number> DOUBLE;
    public static final TypeAdapterFactory ENUM_FACTORY;
    public static final TypeAdapter<Number> FLOAT;
    public static final TypeAdapter<InetAddress> INET_ADDRESS;
    public static final TypeAdapterFactory INET_ADDRESS_FACTORY;
    public static final TypeAdapter<Number> INTEGER;
    public static final TypeAdapterFactory INTEGER_FACTORY;
    public static final TypeAdapter<JsonElement> JSON_ELEMENT;
    public static final TypeAdapterFactory JSON_ELEMENT_FACTORY;
    public static final TypeAdapter<Locale> LOCALE;
    public static final TypeAdapterFactory LOCALE_FACTORY;
    public static final TypeAdapter<Number> LONG;
    public static final TypeAdapter<Number> NUMBER;
    public static final TypeAdapterFactory NUMBER_FACTORY;
    public static final TypeAdapter<Number> SHORT;
    public static final TypeAdapterFactory SHORT_FACTORY;
    public static final TypeAdapter<String> STRING;
    public static final TypeAdapter<StringBuffer> STRING_BUFFER;
    public static final TypeAdapterFactory STRING_BUFFER_FACTORY;
    public static final TypeAdapter<StringBuilder> STRING_BUILDER;
    public static final TypeAdapterFactory STRING_BUILDER_FACTORY;
    public static final TypeAdapterFactory STRING_FACTORY;
    public static final TypeAdapterFactory TIMESTAMP_FACTORY;
    public static final TypeAdapter<URI> URI;
    public static final TypeAdapterFactory URI_FACTORY;
    public static final TypeAdapter<URL> URL;
    public static final TypeAdapterFactory URL_FACTORY;
    public static final TypeAdapter<UUID> UUID;
    public static final TypeAdapterFactory UUID_FACTORY;

    /* renamed from: com.google.gson.internal.bind.TypeAdapters.36 */
    static /* synthetic */ class AnonymousClass36 {
        static final /* synthetic */ int[] $SwitchMap$com$google$gson$stream$JsonToken;

        static {
            $SwitchMap$com$google$gson$stream$JsonToken = new int[JsonToken.values().length];
            try {
                $SwitchMap$com$google$gson$stream$JsonToken[JsonToken.NUMBER.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$google$gson$stream$JsonToken[JsonToken.BOOLEAN.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$com$google$gson$stream$JsonToken[JsonToken.STRING.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$com$google$gson$stream$JsonToken[JsonToken.NULL.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$com$google$gson$stream$JsonToken[JsonToken.BEGIN_ARRAY.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$com$google$gson$stream$JsonToken[JsonToken.BEGIN_OBJECT.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$com$google$gson$stream$JsonToken[JsonToken.END_DOCUMENT.ordinal()] = 7;
            } catch (NoSuchFieldError e7) {
            }
            try {
                $SwitchMap$com$google$gson$stream$JsonToken[JsonToken.NAME.ordinal()] = 8;
            } catch (NoSuchFieldError e8) {
            }
            try {
                $SwitchMap$com$google$gson$stream$JsonToken[JsonToken.END_OBJECT.ordinal()] = 9;
            } catch (NoSuchFieldError e9) {
            }
            try {
                $SwitchMap$com$google$gson$stream$JsonToken[JsonToken.END_ARRAY.ordinal()] = 10;
            } catch (NoSuchFieldError e10) {
            }
        }
    }

    /* renamed from: com.google.gson.internal.bind.TypeAdapters.1 */
    static class C02331 extends TypeAdapter<Class> {
        C02331() {
        }

        public void write(JsonWriter out, Class value) throws IOException {
            if (value == null) {
                out.nullValue();
                return;
            }
            throw new UnsupportedOperationException("Attempted to serialize java.lang.Class: " + value.getName() + ". Forgot to register a type adapter?");
        }

        public Class read(JsonReader in) throws IOException {
            if (in.peek() == JsonToken.NULL) {
                in.nextNull();
                return null;
            }
            throw new UnsupportedOperationException("Attempted to deserialize a java.lang.Class. Forgot to register a type adapter?");
        }
    }

    /* renamed from: com.google.gson.internal.bind.TypeAdapters.2 */
    static class C02352 extends TypeAdapter<BitSet> {
        C02352() {
        }

        public BitSet read(JsonReader in) throws IOException {
            if (in.peek() == JsonToken.NULL) {
                in.nextNull();
                return null;
            }
            BitSet bitset = new BitSet();
            in.beginArray();
            int i = 0;
            JsonToken tokenType = in.peek();
            while (tokenType != JsonToken.END_ARRAY) {
                boolean set;
                switch (AnonymousClass36.$SwitchMap$com$google$gson$stream$JsonToken[tokenType.ordinal()]) {
                    case SwipeRefreshLayout.DEFAULT /*1*/:
                        if (in.nextInt() == 0) {
                            set = false;
                            break;
                        }
                        set = true;
                        break;
                    case DrawerLayout.STATE_SETTLING /*2*/:
                        set = in.nextBoolean();
                        break;
                    case WearableExtender.SIZE_MEDIUM /*3*/:
                        String stringValue = in.nextString();
                        try {
                            set = Integer.parseInt(stringValue) != 0;
                            break;
                        } catch (NumberFormatException e) {
                            throw new JsonSyntaxException("Error: Expecting: bitset number value (1, 0), Found: " + stringValue);
                        }
                    default:
                        throw new JsonSyntaxException("Invalid bitset value type: " + tokenType);
                }
                if (set) {
                    bitset.set(i);
                }
                i++;
                tokenType = in.peek();
            }
            in.endArray();
            return bitset;
        }

        public void write(JsonWriter out, BitSet src) throws IOException {
            if (src == null) {
                out.nullValue();
                return;
            }
            out.beginArray();
            for (int i = 0; i < src.length(); i++) {
                out.value((long) (src.get(i) ? 1 : 0));
            }
            out.endArray();
        }
    }

    /* renamed from: com.google.gson.internal.bind.TypeAdapters.31 */
    static class AnonymousClass31 implements TypeAdapterFactory {
        final /* synthetic */ TypeToken val$type;
        final /* synthetic */ TypeAdapter val$typeAdapter;

        AnonymousClass31(TypeToken typeToken, TypeAdapter typeAdapter) {
            this.val$type = typeToken;
            this.val$typeAdapter = typeAdapter;
        }

        public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
            return typeToken.equals(this.val$type) ? this.val$typeAdapter : null;
        }
    }

    /* renamed from: com.google.gson.internal.bind.TypeAdapters.32 */
    static class AnonymousClass32 implements TypeAdapterFactory {
        final /* synthetic */ Class val$type;
        final /* synthetic */ TypeAdapter val$typeAdapter;

        AnonymousClass32(Class cls, TypeAdapter typeAdapter) {
            this.val$type = cls;
            this.val$typeAdapter = typeAdapter;
        }

        public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
            return typeToken.getRawType() == this.val$type ? this.val$typeAdapter : null;
        }

        public String toString() {
            return "Factory[type=" + this.val$type.getName() + ",adapter=" + this.val$typeAdapter + "]";
        }
    }

    /* renamed from: com.google.gson.internal.bind.TypeAdapters.33 */
    static class AnonymousClass33 implements TypeAdapterFactory {
        final /* synthetic */ Class val$boxed;
        final /* synthetic */ TypeAdapter val$typeAdapter;
        final /* synthetic */ Class val$unboxed;

        AnonymousClass33(Class cls, Class cls2, TypeAdapter typeAdapter) {
            this.val$unboxed = cls;
            this.val$boxed = cls2;
            this.val$typeAdapter = typeAdapter;
        }

        public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
            Class<? super T> rawType = typeToken.getRawType();
            return (rawType == this.val$unboxed || rawType == this.val$boxed) ? this.val$typeAdapter : null;
        }

        public String toString() {
            return "Factory[type=" + this.val$boxed.getName() + "+" + this.val$unboxed.getName() + ",adapter=" + this.val$typeAdapter + "]";
        }
    }

    /* renamed from: com.google.gson.internal.bind.TypeAdapters.34 */
    static class AnonymousClass34 implements TypeAdapterFactory {
        final /* synthetic */ Class val$base;
        final /* synthetic */ Class val$sub;
        final /* synthetic */ TypeAdapter val$typeAdapter;

        AnonymousClass34(Class cls, Class cls2, TypeAdapter typeAdapter) {
            this.val$base = cls;
            this.val$sub = cls2;
            this.val$typeAdapter = typeAdapter;
        }

        public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
            Class<? super T> rawType = typeToken.getRawType();
            return (rawType == this.val$base || rawType == this.val$sub) ? this.val$typeAdapter : null;
        }

        public String toString() {
            return "Factory[type=" + this.val$base.getName() + "+" + this.val$sub.getName() + ",adapter=" + this.val$typeAdapter + "]";
        }
    }

    /* renamed from: com.google.gson.internal.bind.TypeAdapters.35 */
    static class AnonymousClass35 implements TypeAdapterFactory {
        final /* synthetic */ Class val$clazz;
        final /* synthetic */ TypeAdapter val$typeAdapter;

        /* renamed from: com.google.gson.internal.bind.TypeAdapters.35.1 */
        class C02361 extends TypeAdapter<T1> {
            final /* synthetic */ Class val$requestedType;

            C02361(Class cls) {
                this.val$requestedType = cls;
            }

            public void write(JsonWriter out, T1 value) throws IOException {
                AnonymousClass35.this.val$typeAdapter.write(out, value);
            }

            public T1 read(JsonReader in) throws IOException {
                T1 result = AnonymousClass35.this.val$typeAdapter.read(in);
                if (result == null || this.val$requestedType.isInstance(result)) {
                    return result;
                }
                throw new JsonSyntaxException("Expected a " + this.val$requestedType.getName() + " but was " + result.getClass().getName());
            }
        }

        AnonymousClass35(Class cls, TypeAdapter typeAdapter) {
            this.val$clazz = cls;
            this.val$typeAdapter = typeAdapter;
        }

        public <T2> TypeAdapter<T2> create(Gson gson, TypeToken<T2> typeToken) {
            Class<? super T2> requestedType = typeToken.getRawType();
            if (this.val$clazz.isAssignableFrom(requestedType)) {
                return new C02361(requestedType);
            }
            return null;
        }

        public String toString() {
            return "Factory[typeHierarchy=" + this.val$clazz.getName() + ",adapter=" + this.val$typeAdapter + "]";
        }
    }

    /* renamed from: com.google.gson.internal.bind.TypeAdapters.3 */
    static class C02373 extends TypeAdapter<Boolean> {
        C02373() {
        }

        public Boolean read(JsonReader in) throws IOException {
            if (in.peek() == JsonToken.NULL) {
                in.nextNull();
                return null;
            } else if (in.peek() == JsonToken.STRING) {
                return Boolean.valueOf(Boolean.parseBoolean(in.nextString()));
            } else {
                return Boolean.valueOf(in.nextBoolean());
            }
        }

        public void write(JsonWriter out, Boolean value) throws IOException {
            out.value(value);
        }
    }

    /* renamed from: com.google.gson.internal.bind.TypeAdapters.4 */
    static class C02384 extends TypeAdapter<Boolean> {
        C02384() {
        }

        public Boolean read(JsonReader in) throws IOException {
            if (in.peek() != JsonToken.NULL) {
                return Boolean.valueOf(in.nextString());
            }
            in.nextNull();
            return null;
        }

        public void write(JsonWriter out, Boolean value) throws IOException {
            out.value(value == null ? "null" : value.toString());
        }
    }

    /* renamed from: com.google.gson.internal.bind.TypeAdapters.5 */
    static class C02395 extends TypeAdapter<Number> {
        C02395() {
        }

        public Number read(JsonReader in) throws IOException {
            if (in.peek() == JsonToken.NULL) {
                in.nextNull();
                return null;
            }
            try {
                return Byte.valueOf((byte) in.nextInt());
            } catch (Throwable e) {
                throw new JsonSyntaxException(e);
            }
        }

        public void write(JsonWriter out, Number value) throws IOException {
            out.value(value);
        }
    }

    /* renamed from: com.google.gson.internal.bind.TypeAdapters.6 */
    static class C02406 extends TypeAdapter<Number> {
        C02406() {
        }

        public Number read(JsonReader in) throws IOException {
            if (in.peek() == JsonToken.NULL) {
                in.nextNull();
                return null;
            }
            try {
                return Short.valueOf((short) in.nextInt());
            } catch (Throwable e) {
                throw new JsonSyntaxException(e);
            }
        }

        public void write(JsonWriter out, Number value) throws IOException {
            out.value(value);
        }
    }

    /* renamed from: com.google.gson.internal.bind.TypeAdapters.7 */
    static class C02417 extends TypeAdapter<Number> {
        C02417() {
        }

        public Number read(JsonReader in) throws IOException {
            if (in.peek() == JsonToken.NULL) {
                in.nextNull();
                return null;
            }
            try {
                return Integer.valueOf(in.nextInt());
            } catch (Throwable e) {
                throw new JsonSyntaxException(e);
            }
        }

        public void write(JsonWriter out, Number value) throws IOException {
            out.value(value);
        }
    }

    /* renamed from: com.google.gson.internal.bind.TypeAdapters.8 */
    static class C02428 extends TypeAdapter<AtomicInteger> {
        C02428() {
        }

        public AtomicInteger read(JsonReader in) throws IOException {
            try {
                return new AtomicInteger(in.nextInt());
            } catch (Throwable e) {
                throw new JsonSyntaxException(e);
            }
        }

        public void write(JsonWriter out, AtomicInteger value) throws IOException {
            out.value((long) value.get());
        }
    }

    /* renamed from: com.google.gson.internal.bind.TypeAdapters.9 */
    static class C02439 extends TypeAdapter<AtomicBoolean> {
        C02439() {
        }

        public AtomicBoolean read(JsonReader in) throws IOException {
            return new AtomicBoolean(in.nextBoolean());
        }

        public void write(JsonWriter out, AtomicBoolean value) throws IOException {
            out.value(value.get());
        }
    }

    private static final class EnumTypeAdapter<T extends Enum<T>> extends TypeAdapter<T> {
        private final Map<T, String> constantToName;
        private final Map<String, T> nameToConstant;

        public EnumTypeAdapter(Class<T> classOfT) {
            this.nameToConstant = new HashMap();
            this.constantToName = new HashMap();
            try {
                for (T constant : (Enum[]) classOfT.getEnumConstants()) {
                    String name = constant.name();
                    SerializedName annotation = (SerializedName) classOfT.getField(name).getAnnotation(SerializedName.class);
                    if (annotation != null) {
                        name = annotation.value();
                        for (String alternate : annotation.alternate()) {
                            this.nameToConstant.put(alternate, constant);
                        }
                    }
                    this.nameToConstant.put(name, constant);
                    this.constantToName.put(constant, name);
                }
            } catch (NoSuchFieldException e) {
                throw new AssertionError(e);
            }
        }

        public T read(JsonReader in) throws IOException {
            if (in.peek() != JsonToken.NULL) {
                return (Enum) this.nameToConstant.get(in.nextString());
            }
            in.nextNull();
            return null;
        }

        public void write(JsonWriter out, T value) throws IOException {
            out.value(value == null ? null : (String) this.constantToName.get(value));
        }
    }

    private TypeAdapters() {
        throw new UnsupportedOperationException();
    }

    static {
        CLASS = new C02331();
        CLASS_FACTORY = newFactory(Class.class, CLASS);
        BIT_SET = new C02352();
        BIT_SET_FACTORY = newFactory(BitSet.class, BIT_SET);
        BOOLEAN = new C02373();
        BOOLEAN_AS_STRING = new C02384();
        BOOLEAN_FACTORY = newFactory(Boolean.TYPE, Boolean.class, BOOLEAN);
        BYTE = new C02395();
        BYTE_FACTORY = newFactory(Byte.TYPE, Byte.class, BYTE);
        SHORT = new C02406();
        SHORT_FACTORY = newFactory(Short.TYPE, Short.class, SHORT);
        INTEGER = new C02417();
        INTEGER_FACTORY = newFactory(Integer.TYPE, Integer.class, INTEGER);
        ATOMIC_INTEGER = new C02428().nullSafe();
        ATOMIC_INTEGER_FACTORY = newFactory(AtomicInteger.class, ATOMIC_INTEGER);
        ATOMIC_BOOLEAN = new C02439().nullSafe();
        ATOMIC_BOOLEAN_FACTORY = newFactory(AtomicBoolean.class, ATOMIC_BOOLEAN);
        ATOMIC_INTEGER_ARRAY = new TypeAdapter<AtomicIntegerArray>() {
            public AtomicIntegerArray read(JsonReader in) throws IOException {
                List<Integer> list = new ArrayList();
                in.beginArray();
                while (in.hasNext()) {
                    try {
                        list.add(Integer.valueOf(in.nextInt()));
                    } catch (Throwable e) {
                        throw new JsonSyntaxException(e);
                    }
                }
                in.endArray();
                int length = list.size();
                AtomicIntegerArray array = new AtomicIntegerArray(length);
                for (int i = 0; i < length; i++) {
                    array.set(i, ((Integer) list.get(i)).intValue());
                }
                return array;
            }

            public void write(JsonWriter out, AtomicIntegerArray value) throws IOException {
                out.beginArray();
                int length = value.length();
                for (int i = 0; i < length; i++) {
                    out.value((long) value.get(i));
                }
                out.endArray();
            }
        }.nullSafe();
        ATOMIC_INTEGER_ARRAY_FACTORY = newFactory(AtomicIntegerArray.class, ATOMIC_INTEGER_ARRAY);
        LONG = new TypeAdapter<Number>() {
            public Number read(JsonReader in) throws IOException {
                if (in.peek() == JsonToken.NULL) {
                    in.nextNull();
                    return null;
                }
                try {
                    return Long.valueOf(in.nextLong());
                } catch (Throwable e) {
                    throw new JsonSyntaxException(e);
                }
            }

            public void write(JsonWriter out, Number value) throws IOException {
                out.value(value);
            }
        };
        FLOAT = new TypeAdapter<Number>() {
            public Number read(JsonReader in) throws IOException {
                if (in.peek() != JsonToken.NULL) {
                    return Float.valueOf((float) in.nextDouble());
                }
                in.nextNull();
                return null;
            }

            public void write(JsonWriter out, Number value) throws IOException {
                out.value(value);
            }
        };
        DOUBLE = new TypeAdapter<Number>() {
            public Number read(JsonReader in) throws IOException {
                if (in.peek() != JsonToken.NULL) {
                    return Double.valueOf(in.nextDouble());
                }
                in.nextNull();
                return null;
            }

            public void write(JsonWriter out, Number value) throws IOException {
                out.value(value);
            }
        };
        NUMBER = new TypeAdapter<Number>() {
            public Number read(JsonReader in) throws IOException {
                JsonToken jsonToken = in.peek();
                switch (AnonymousClass36.$SwitchMap$com$google$gson$stream$JsonToken[jsonToken.ordinal()]) {
                    case SwipeRefreshLayout.DEFAULT /*1*/:
                        return new LazilyParsedNumber(in.nextString());
                    case TransportMediator.FLAG_KEY_MEDIA_PLAY /*4*/:
                        in.nextNull();
                        return null;
                    default:
                        throw new JsonSyntaxException("Expecting number, got: " + jsonToken);
                }
            }

            public void write(JsonWriter out, Number value) throws IOException {
                out.value(value);
            }
        };
        NUMBER_FACTORY = newFactory(Number.class, NUMBER);
        CHARACTER = new TypeAdapter<Character>() {
            public Character read(JsonReader in) throws IOException {
                if (in.peek() == JsonToken.NULL) {
                    in.nextNull();
                    return null;
                }
                String str = in.nextString();
                if (str.length() == 1) {
                    return Character.valueOf(str.charAt(0));
                }
                throw new JsonSyntaxException("Expecting character, got: " + str);
            }

            public void write(JsonWriter out, Character value) throws IOException {
                out.value(value == null ? null : String.valueOf(value));
            }
        };
        CHARACTER_FACTORY = newFactory(Character.TYPE, Character.class, CHARACTER);
        STRING = new TypeAdapter<String>() {
            public String read(JsonReader in) throws IOException {
                JsonToken peek = in.peek();
                if (peek == JsonToken.NULL) {
                    in.nextNull();
                    return null;
                } else if (peek == JsonToken.BOOLEAN) {
                    return Boolean.toString(in.nextBoolean());
                } else {
                    return in.nextString();
                }
            }

            public void write(JsonWriter out, String value) throws IOException {
                out.value(value);
            }
        };
        BIG_DECIMAL = new TypeAdapter<BigDecimal>() {
            public BigDecimal read(JsonReader in) throws IOException {
                if (in.peek() == JsonToken.NULL) {
                    in.nextNull();
                    return null;
                }
                try {
                    return new BigDecimal(in.nextString());
                } catch (Throwable e) {
                    throw new JsonSyntaxException(e);
                }
            }

            public void write(JsonWriter out, BigDecimal value) throws IOException {
                out.value((Number) value);
            }
        };
        BIG_INTEGER = new TypeAdapter<BigInteger>() {
            public BigInteger read(JsonReader in) throws IOException {
                if (in.peek() == JsonToken.NULL) {
                    in.nextNull();
                    return null;
                }
                try {
                    return new BigInteger(in.nextString());
                } catch (Throwable e) {
                    throw new JsonSyntaxException(e);
                }
            }

            public void write(JsonWriter out, BigInteger value) throws IOException {
                out.value((Number) value);
            }
        };
        STRING_FACTORY = newFactory(String.class, STRING);
        STRING_BUILDER = new TypeAdapter<StringBuilder>() {
            public StringBuilder read(JsonReader in) throws IOException {
                if (in.peek() != JsonToken.NULL) {
                    return new StringBuilder(in.nextString());
                }
                in.nextNull();
                return null;
            }

            public void write(JsonWriter out, StringBuilder value) throws IOException {
                out.value(value == null ? null : value.toString());
            }
        };
        STRING_BUILDER_FACTORY = newFactory(StringBuilder.class, STRING_BUILDER);
        STRING_BUFFER = new TypeAdapter<StringBuffer>() {
            public StringBuffer read(JsonReader in) throws IOException {
                if (in.peek() != JsonToken.NULL) {
                    return new StringBuffer(in.nextString());
                }
                in.nextNull();
                return null;
            }

            public void write(JsonWriter out, StringBuffer value) throws IOException {
                out.value(value == null ? null : value.toString());
            }
        };
        STRING_BUFFER_FACTORY = newFactory(StringBuffer.class, STRING_BUFFER);
        URL = new TypeAdapter<URL>() {
            public URL read(JsonReader in) throws IOException {
                if (in.peek() == JsonToken.NULL) {
                    in.nextNull();
                    return null;
                }
                String nextString = in.nextString();
                if ("null".equals(nextString)) {
                    return null;
                }
                return new URL(nextString);
            }

            public void write(JsonWriter out, URL value) throws IOException {
                out.value(value == null ? null : value.toExternalForm());
            }
        };
        URL_FACTORY = newFactory(URL.class, URL);
        URI = new TypeAdapter<URI>() {
            public URI read(JsonReader in) throws IOException {
                URI uri = null;
                if (in.peek() == JsonToken.NULL) {
                    in.nextNull();
                } else {
                    try {
                        String nextString = in.nextString();
                        if (!"null".equals(nextString)) {
                            uri = new URI(nextString);
                        }
                    } catch (Throwable e) {
                        throw new JsonIOException(e);
                    }
                }
                return uri;
            }

            public void write(JsonWriter out, URI value) throws IOException {
                out.value(value == null ? null : value.toASCIIString());
            }
        };
        URI_FACTORY = newFactory(URI.class, URI);
        INET_ADDRESS = new TypeAdapter<InetAddress>() {
            public InetAddress read(JsonReader in) throws IOException {
                if (in.peek() != JsonToken.NULL) {
                    return InetAddress.getByName(in.nextString());
                }
                in.nextNull();
                return null;
            }

            public void write(JsonWriter out, InetAddress value) throws IOException {
                out.value(value == null ? null : value.getHostAddress());
            }
        };
        INET_ADDRESS_FACTORY = newTypeHierarchyFactory(InetAddress.class, INET_ADDRESS);
        UUID = new TypeAdapter<UUID>() {
            public UUID read(JsonReader in) throws IOException {
                if (in.peek() != JsonToken.NULL) {
                    return UUID.fromString(in.nextString());
                }
                in.nextNull();
                return null;
            }

            public void write(JsonWriter out, UUID value) throws IOException {
                out.value(value == null ? null : value.toString());
            }
        };
        UUID_FACTORY = newFactory(UUID.class, UUID);
        CURRENCY = new TypeAdapter<Currency>() {
            public Currency read(JsonReader in) throws IOException {
                return Currency.getInstance(in.nextString());
            }

            public void write(JsonWriter out, Currency value) throws IOException {
                out.value(value.getCurrencyCode());
            }
        }.nullSafe();
        CURRENCY_FACTORY = newFactory(Currency.class, CURRENCY);
        TIMESTAMP_FACTORY = new TypeAdapterFactory() {

            /* renamed from: com.google.gson.internal.bind.TypeAdapters.26.1 */
            class C02341 extends TypeAdapter<Timestamp> {
                final /* synthetic */ TypeAdapter val$dateTypeAdapter;

                C02341(TypeAdapter typeAdapter) {
                    this.val$dateTypeAdapter = typeAdapter;
                }

                public Timestamp read(JsonReader in) throws IOException {
                    Date date = (Date) this.val$dateTypeAdapter.read(in);
                    return date != null ? new Timestamp(date.getTime()) : null;
                }

                public void write(JsonWriter out, Timestamp value) throws IOException {
                    this.val$dateTypeAdapter.write(out, value);
                }
            }

            public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
                if (typeToken.getRawType() != Timestamp.class) {
                    return null;
                }
                return new C02341(gson.getAdapter(Date.class));
            }
        };
        CALENDAR = new TypeAdapter<Calendar>() {
            private static final String DAY_OF_MONTH = "dayOfMonth";
            private static final String HOUR_OF_DAY = "hourOfDay";
            private static final String MINUTE = "minute";
            private static final String MONTH = "month";
            private static final String SECOND = "second";
            private static final String YEAR = "year";

            public Calendar read(JsonReader in) throws IOException {
                if (in.peek() == JsonToken.NULL) {
                    in.nextNull();
                    return null;
                }
                in.beginObject();
                int year = 0;
                int month = 0;
                int dayOfMonth = 0;
                int hourOfDay = 0;
                int minute = 0;
                int second = 0;
                while (in.peek() != JsonToken.END_OBJECT) {
                    String name = in.nextName();
                    int value = in.nextInt();
                    if (YEAR.equals(name)) {
                        year = value;
                    } else if (MONTH.equals(name)) {
                        month = value;
                    } else if (DAY_OF_MONTH.equals(name)) {
                        dayOfMonth = value;
                    } else if (HOUR_OF_DAY.equals(name)) {
                        hourOfDay = value;
                    } else if (MINUTE.equals(name)) {
                        minute = value;
                    } else if (SECOND.equals(name)) {
                        second = value;
                    }
                }
                in.endObject();
                return new GregorianCalendar(year, month, dayOfMonth, hourOfDay, minute, second);
            }

            public void write(JsonWriter out, Calendar value) throws IOException {
                if (value == null) {
                    out.nullValue();
                    return;
                }
                out.beginObject();
                out.name(YEAR);
                out.value((long) value.get(1));
                out.name(MONTH);
                out.value((long) value.get(2));
                out.name(DAY_OF_MONTH);
                out.value((long) value.get(5));
                out.name(HOUR_OF_DAY);
                out.value((long) value.get(11));
                out.name(MINUTE);
                out.value((long) value.get(12));
                out.name(SECOND);
                out.value((long) value.get(13));
                out.endObject();
            }
        };
        CALENDAR_FACTORY = newFactoryForMultipleTypes(Calendar.class, GregorianCalendar.class, CALENDAR);
        LOCALE = new TypeAdapter<Locale>() {
            public Locale read(JsonReader in) throws IOException {
                if (in.peek() == JsonToken.NULL) {
                    in.nextNull();
                    return null;
                }
                StringTokenizer tokenizer = new StringTokenizer(in.nextString(), "_");
                String language = null;
                String country = null;
                String variant = null;
                if (tokenizer.hasMoreElements()) {
                    language = tokenizer.nextToken();
                }
                if (tokenizer.hasMoreElements()) {
                    country = tokenizer.nextToken();
                }
                if (tokenizer.hasMoreElements()) {
                    variant = tokenizer.nextToken();
                }
                if (country == null && variant == null) {
                    return new Locale(language);
                }
                if (variant == null) {
                    return new Locale(language, country);
                }
                return new Locale(language, country, variant);
            }

            public void write(JsonWriter out, Locale value) throws IOException {
                out.value(value == null ? null : value.toString());
            }
        };
        LOCALE_FACTORY = newFactory(Locale.class, LOCALE);
        JSON_ELEMENT = new TypeAdapter<JsonElement>() {
            public JsonElement read(JsonReader in) throws IOException {
                switch (AnonymousClass36.$SwitchMap$com$google$gson$stream$JsonToken[in.peek().ordinal()]) {
                    case SwipeRefreshLayout.DEFAULT /*1*/:
                        return new JsonPrimitive(new LazilyParsedNumber(in.nextString()));
                    case DrawerLayout.STATE_SETTLING /*2*/:
                        return new JsonPrimitive(Boolean.valueOf(in.nextBoolean()));
                    case WearableExtender.SIZE_MEDIUM /*3*/:
                        return new JsonPrimitive(in.nextString());
                    case TransportMediator.FLAG_KEY_MEDIA_PLAY /*4*/:
                        in.nextNull();
                        return JsonNull.INSTANCE;
                    case WearableExtender.SIZE_FULL_SCREEN /*5*/:
                        JsonElement array = new JsonArray();
                        in.beginArray();
                        while (in.hasNext()) {
                            array.add(read(in));
                        }
                        in.endArray();
                        return array;
                    case FragmentManagerImpl.ANIM_STYLE_FADE_EXIT /*6*/:
                        JsonElement object = new JsonObject();
                        in.beginObject();
                        while (in.hasNext()) {
                            object.add(in.nextName(), read(in));
                        }
                        in.endObject();
                        return object;
                    default:
                        throw new IllegalArgumentException();
                }
            }

            public void write(JsonWriter out, JsonElement value) throws IOException {
                if (value == null || value.isJsonNull()) {
                    out.nullValue();
                } else if (value.isJsonPrimitive()) {
                    JsonPrimitive primitive = value.getAsJsonPrimitive();
                    if (primitive.isNumber()) {
                        out.value(primitive.getAsNumber());
                    } else if (primitive.isBoolean()) {
                        out.value(primitive.getAsBoolean());
                    } else {
                        out.value(primitive.getAsString());
                    }
                } else if (value.isJsonArray()) {
                    out.beginArray();
                    Iterator it = value.getAsJsonArray().iterator();
                    while (it.hasNext()) {
                        write(out, (JsonElement) it.next());
                    }
                    out.endArray();
                } else if (value.isJsonObject()) {
                    out.beginObject();
                    for (Entry<String, JsonElement> e : value.getAsJsonObject().entrySet()) {
                        out.name((String) e.getKey());
                        write(out, (JsonElement) e.getValue());
                    }
                    out.endObject();
                } else {
                    throw new IllegalArgumentException("Couldn't write " + value.getClass());
                }
            }
        };
        JSON_ELEMENT_FACTORY = newTypeHierarchyFactory(JsonElement.class, JSON_ELEMENT);
        ENUM_FACTORY = new TypeAdapterFactory() {
            public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
                Class<? super T> rawType = typeToken.getRawType();
                if (!Enum.class.isAssignableFrom(rawType) || rawType == Enum.class) {
                    return null;
                }
                if (!rawType.isEnum()) {
                    rawType = rawType.getSuperclass();
                }
                return new EnumTypeAdapter(rawType);
            }
        };
    }

    public static <TT> TypeAdapterFactory newFactory(TypeToken<TT> type, TypeAdapter<TT> typeAdapter) {
        return new AnonymousClass31(type, typeAdapter);
    }

    public static <TT> TypeAdapterFactory newFactory(Class<TT> type, TypeAdapter<TT> typeAdapter) {
        return new AnonymousClass32(type, typeAdapter);
    }

    public static <TT> TypeAdapterFactory newFactory(Class<TT> unboxed, Class<TT> boxed, TypeAdapter<? super TT> typeAdapter) {
        return new AnonymousClass33(unboxed, boxed, typeAdapter);
    }

    public static <TT> TypeAdapterFactory newFactoryForMultipleTypes(Class<TT> base, Class<? extends TT> sub, TypeAdapter<? super TT> typeAdapter) {
        return new AnonymousClass34(base, sub, typeAdapter);
    }

    public static <T1> TypeAdapterFactory newTypeHierarchyFactory(Class<T1> clazz, TypeAdapter<T1> typeAdapter) {
        return new AnonymousClass35(clazz, typeAdapter);
    }
}
