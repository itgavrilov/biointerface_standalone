package ru.gsa.biointerfaceController_standalone.businessLayer.serialPortConnection.serialPortHost.serverByPuchkov.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by Пучков Константин on 14.11.2017.
 */
public class Uptime {
    public static final int NOIMPL = -1;
    private static final Uptime INSTANCE = new Uptime();
    private Impl impl;

    private Uptime() {
        try {
            impl = new DefaultImpl();
        } catch (UnsupportedOperationException e) {
            System.err.printf("Defaulting Uptime to NOIMPL due to (%s) %s%n", e.getClass().getName(), e.getMessage());
            impl = null;
        }
    }

    public static Uptime getInstance() {
        return INSTANCE;
    }

    public static long getUptime() {
        Uptime u = getInstance();
        if (u == null || u.impl == null) {
            return NOIMPL;
        }
        return u.impl.getUptime();
    }

    public Impl getImpl() {
        return impl;
    }

    public void setImpl(Impl impl) {
        this.impl = impl;
    }

    public interface Impl {
        long getUptime();
    }

    public static class DefaultImpl implements Impl {
        public Object mxBean;
        public Method uptimeMethod;

        public DefaultImpl() {
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            try {
                Class<?> mgmtFactory = Class.forName("java.lang.management.ManagementFactory", true, cl);
                Class<?> runtimeClass = Class.forName("java.lang.management.RuntimeMXBean", true, cl);
                Class<?>[] noparams = new Class<?>[0];
                Method mxBeanMethod = mgmtFactory.getMethod("getRuntimeMXBean", noparams);
                if (mxBeanMethod == null) {
                    throw new UnsupportedOperationException("method getRuntimeMXBean() not found");
                }
                mxBean = mxBeanMethod.invoke(mgmtFactory);
                if (mxBean == null) {
                    throw new UnsupportedOperationException("getRuntimeMXBean() method returned null");
                }
                uptimeMethod = runtimeClass.getMethod("getUptime", noparams);
                if (mxBean == null) {
                    throw new UnsupportedOperationException("method getUptime() not found");
                }
            } catch (ClassNotFoundException |
                    NoClassDefFoundError |
                    NoSuchMethodException |
                    SecurityException |
                    IllegalAccessException |
                    IllegalArgumentException |
                    InvocationTargetException e) {
                throw new UnsupportedOperationException("Implementation not available in this environment", e);
            }
        }

        @Override
        public long getUptime() {
            try {
                return (long) uptimeMethod.invoke(mxBean);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                return NOIMPL;
            }
        }
    }
}