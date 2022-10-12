package com.nylon.reinject.cfg;

public class Inject {
    private String className;
    private String methodName;
    private String methodSignature;
    private String injectClassName;
    private String injectEnterMethodName;
    private String injectExitMethodName;

    public String getClassName() {
        return className;
    }

    public String getMethodName() {
        return methodName;
    }

    public String getMethodSignature() {
        return methodSignature;
    }

    public String getMethodSignatureWithThis() {
        String paramClassName = "L" + className.replace('.', '/') + ";";
        int leftBracketIndex = methodSignature.indexOf('(') + 1;
        String ret = methodSignature.substring(0, leftBracketIndex) + paramClassName + methodSignature.substring(leftBracketIndex);
        return ret;
    }

    public String getInjectClassName4Call() {
        return injectClassName.replace('.', '/');
    }

    public String getInjectEnterMethodName() {
        return injectEnterMethodName;
    }

    public String getInjectExitMethodName() {
        return injectExitMethodName;
    }

    @Override
    public String toString() {
        return "Inject{" +
                "className='" + className + '\'' +
                ", methodName='" + methodName + '\'' +
                ", methodSignature='" + methodSignature + '\'' +
                ", injectClassName='" + injectClassName + '\'' +
                ", injectEnterMethodName='" + injectEnterMethodName + '\'' +
                ", injectExitMethodName='" + injectExitMethodName + '\'' +
                '}';
    }
}
