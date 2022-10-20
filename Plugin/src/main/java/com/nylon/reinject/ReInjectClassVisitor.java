package com.nylon.reinject;

import com.nylon.reinject.cfg.Inject;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.List;

public class ReInjectClassVisitor extends ClassVisitor {
    private static final String TAG = "ReInjectClassVisitor";
    private String className;
    private List<Inject> filterList;

    public ReInjectClassVisitor(ClassVisitor classVisitor, List<Inject> filterList) {
        super(Opcodes.ASM6, classVisitor);
        this.filterList = filterList;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        this.className = name;
    }

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
