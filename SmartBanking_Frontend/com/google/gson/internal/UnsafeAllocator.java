package com.google.gson.internal;

import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public abstract class UnsafeAllocator {

    /* renamed from: com.google.gson.internal.UnsafeAllocator.1 */
    static class C02231 extends UnsafeAllocator {
        final /* synthetic */ Method val$allocateInstance;
        final /* synthetic */ Object val$unsafe;

        C02231(Method method, Object obj) {
            this.val$allocateInstance = method;
            this.val$unsafe = obj;
        }

        public <T> T newInstance(Class<T> c) throws Exception {
            UnsafeAllocator.assertInstantiable(c);
            return this.val$allocateInstance.invoke(this.val$unsafe, new Object[]{c});
        }
    }

    /* renamed from: com.google.gson.internal.UnsafeAllocator.2 */
    static class C02242 extends UnsafeAllocator {
        final /* synthetic */ int val$constructorId;
        final /* synthetic */ Method val$newInstance;

        C02242(Method method, int i) {
            this.val$newInstance = method;
            this.val$constructorId = i;
        }

        public <T> T newInstance(Class<T> c) throws Exception {
            UnsafeAllocator.assertInstantiable(c);
            return this.val$newInstance.invoke(null, new Object[]{c, Integer.valueOf(this.val$constructorId)});
        }
    }

    /* renamed from: com.google.gson.internal.UnsafeAllocator.3 */
    static class C02253 extends UnsafeAllocator {
        final /* synthetic */ Method val$newInstance;

        C02253(Method method) {
            this.val$newInstance = method;
        }

        public <T> T newInstance(Class<T> c) throws Exception {
            UnsafeAllocator.assertInstantiable(c);
            return this.val$newInstance.invoke(null, new Object[]{c, Object.class});
        }
    }

    /* renamed from: com.google.gson.internal.UnsafeAllocator.4 */
    static class C02264 extends UnsafeAllocator {
        C02264() {
        }

        public <T> T newInstance(Class<T> c) {
            throw new UnsupportedOperationException("Cannot allocate " + c);
        }
    }

    public abstract <T> T newInstance(Class<T> cls) throws Exception;

    public static UnsafeAllocator create() {
        Method newInstance;
        try {
            Class<?> unsafeClass = Class.forName("sun.misc.Unsafe");
            Field f = unsafeClass.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            return new C02231(unsafeClass.getMethod("allocateInstance", new Class[]{Class.class}), f.get(null));
        } catch (Exception e) {
            try {
                Method getConstructorId = ObjectStreamClass.class.getDeclaredMethod("getConstructorId", new Class[]{Class.class});
                getConstructorId.setAccessible(true);
                int constructorId = ((Integer) getConstructorId.invoke(null, new Object[]{Object.class})).intValue();
                newInstance = ObjectStreamClass.class.getDeclaredMethod("newInstance", new Class[]{Class.class, Integer.TYPE});
                newInstance.setAccessible(true);
                return new C02242(newInstance, constructorId);
            } catch (Exception e2) {
                try {
                    newInstance = ObjectInputStream.class.getDeclaredMethod("newInstance", new Class[]{Class.class, Class.class});
                    newInstance.setAccessible(true);
                    return new C02253(newInstance);
                } catch (Exception e3) {
                    return new C02264();
                }
            }
        }
    }

    private static void assertInstantiable(Class<?> c) {
        int modifiers = c.getModifiers();
        if (Modifier.isInterface(modifiers)) {
            throw new UnsupportedOperationException("Interface can't be instantiated! Interface name: " + c.getName());
        } else if (Modifier.isAbstract(modifiers)) {
            throw new UnsupportedOperationException("Abstract class can't be instantiated! Class name: " + c.getName());
        }
    }
}
