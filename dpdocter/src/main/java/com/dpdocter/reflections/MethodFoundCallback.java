package com.dpdocter.reflections;

import java.lang.reflect.Method;

public interface MethodFoundCallback {
    void method(Object o, Method method);
}
