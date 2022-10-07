package com.nylon.reinject.cfg;

public class Inject {
    private String className;
    private String methodName;
    private String methodSignature;
    private String injectClassName;
    private String injectBeforeMethodName;
    private String injectAfterMethodName;

    public String getClassName() {
        return className;
    }

    public String getMethodName() {
        return methodName;
    }

    public String getMethodSignature() {
        return methodSignature;
    }

    public String getInjectClassName() {
        return injectClassName;
    }

    public String getInjectBeforeMethodName() {
        return injectBeforeMethodName;
    }

    public String getInjectAfterMethodName() {
        return injectAfterMethodName;
    }

    @Override
    public String toString() {
        return "Inject{" +
                "className='" + className + '\'' +
                ", methodName='" + methodName + '\'' +
                ", methodSignature='" + methodSignature + '\'' +
                ", injectClassName='" + injectClassName + '\'' +
                ", injectBeforeMethodName='" + injectBeforeMethodName + '\'' +
                ", injectAfterMethodName='" + injectAfterMethodName + '\'' +
                '}';
    }
}
