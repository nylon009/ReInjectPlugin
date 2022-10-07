package com.nylon.reinject;

import com.android.utils.Pair;
import com.nylon.reinject.cfg.Inject;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ReInjectClassVisitor extends ClassVisitor {
    private static final String TAG = "ReInjectClassVisitor";
    private String className;
    private List<Inject> filterList;

    public ReInjectClassVisitor(ClassVisitor classVisitor, List<Inject> filterList) {
        super(Opcodes.ASM6, classVisitor);
        this.filterList = filterList;
    }

    /**
     * 访问类的头部
     *
     * @param version    version指的是类的版本
     * @param access     指的是类的修饰符
     * @param name       类的名称
     * @param signature  类的签名，如果类不是泛型或者没有继承泛型类，那么signature为空
     * @param superName  类的父类名称
     * @param interfaces 类继承的接口
     */
    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        this.className = name;
    }

    /**
     * 访问类的方法，如果需要修改类方法信息，则可以重写此方法;
     *
     * @param access     表示该域的访问方式，public，private或者static,final等等；
     * @param name       指的是方法的名称；
     * @param desc       表示方法的参数类型和返回值类型；
     * @param signature  指的是域的签名，一般是泛型域才会有签名;
     * @param exceptions
     * @return
     */
    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor methodVisitor = super.visitMethod(access, name, desc, signature, exceptions);
        ReLog.d(TAG, "access=" + access + " name=" + name + " desc=" + desc + " signature=" + signature + " exceptions=" + exceptions);
        for (Inject inject : filterList) {
            ReLog.d(TAG, "inject=" + inject);
            if (isInject(name, desc, inject)) {
                return new ReInjectMethodVisitor(methodVisitor, access, name, desc, inject);
            }
        }
        return methodVisitor;
    }

    private boolean isInject(String name, String desc, Inject inject) {
        if (name.equals(inject.getMethodName()) && desc.equals(inject.getMethodSignature())) {
            return true;
        }
        return false;
    }

}
