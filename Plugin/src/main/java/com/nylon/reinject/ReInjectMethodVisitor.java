package com.nylon.reinject;

import com.nylon.reinject.cfg.Inject;
import com.nylon.reinject.util.ASMUtils;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.AdviceAdapter;

public class ReInjectMethodVisitor extends AdviceAdapter {
    private static final String TAG = "ReInjectMethodVisitor";
    private final MethodVisitor methodVisitor;
    private Inject inject;

    private boolean needInject;
    private String tag;

    public ReInjectMethodVisitor(MethodVisitor methodVisitor, int access, String name, String desc, Inject inject) {
        super(Opcodes.ASM6, methodVisitor, access, name, desc);
        this.methodVisitor = methodVisitor;
        this.inject = inject;
    }

    @Override
    protected void onMethodEnter() {
        super.onMethodEnter();
        if (inject == null || inject.getInjectBeforeMethodName() == null) {
            return;
        }
        Type methodType = Type.getMethodType(inject.getMethodSignature());
        Type[] argTypes = methodType.getArgumentTypes();
        for (int i = 0; i < argTypes.length; i++) {
            int paramOpcode = ASMUtils.type2Opcodes(argTypes[i]);
            ReLog.d(TAG, inject.getMethodName() + " enter arg" + i + ": " + argTypes[i].getClassName() + ",paramOpcode=" + paramOpcode);
            methodVisitor.visitVarInsn(paramOpcode, i);
        }
        methodVisitor.visitMethodInsn(Opcodes.INVOKESTATIC, inject.getInjectClassName(),
                inject.getInjectBeforeMethodName(), inject.getMethodSignature(), false);
    }

    @Override
    protected void onMethodExit(int opcode) {
        super.onMethodExit(opcode);
        if (inject == null || inject.getInjectAfterMethodName() == null) {
            return;
        }
        Type methodType = Type.getMethodType(inject.getMethodSignature());
        Type[] argTypes = methodType.getArgumentTypes();
        for (int i = 0; i < argTypes.length; i++) {
            int paramOpcode = ASMUtils.type2Opcodes(argTypes[i]);
            ReLog.d(TAG, inject.getMethodName() + " exit arg" + i + ": " + argTypes[i].getClassName() + ",paramOpcode=" + paramOpcode);
            methodVisitor.visitVarInsn(paramOpcode, i);
        }
        methodVisitor.visitMethodInsn(Opcodes.INVOKESTATIC, inject.getInjectClassName(),
                inject.getInjectAfterMethodName(), inject.getMethodSignature(), false);
    }
}
