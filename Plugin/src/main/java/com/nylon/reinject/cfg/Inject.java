package com.nylon.reinject.cfg;

public class Inject {
    private String className;
    private String methodName;
    private String methodSignature;
    private String injectClassName;
    private String injectEnterMethodName;
    private String injectExitMethodName;

    /**
     * the class to be injected
     * @return calssName
     */
    public String getClassName() {
        return className;
    }

    /**
     * the method to be injected
     * @return method name
     */
    public String getMethodName() {
        return methodName;
    }

    /**
     * the method signature
     * @return method signature
     */
    public String getMethodSignature() {
        return methodSignature;
    }

    /**
     * replace method signature, add this as first parameter
     * @return method add first parameter
     */
    public String getMethodSignatureWithThis() {
        String paramClassName = "L" + className.replace('.', '/') + ";";
        int leftBracketIndex = methodSignature.indexOf('(') + 1;
        String ret = methodSignature.substring(0, leftBracketIndex) + paramClassName + methodSignature.substring(leftBracketIndex);
        return ret;
    }

    /**
     * replace dot with slash
     * @return revised class name to be invoked
     */
    public String getInjectClassName4Call() {
        return injectClassName.replace('.', '/');
    }

    /**
     * inject method when enter the method
     * @return injectEnterMethodName
     */
    public String getInjectEnterMethodName() {
        return injectEnterMethodName;
    }

    /**
     * inject method when exit the method
     * @return injectExitMethodName
     */
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
