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
    private int access;

    public ReInjectMethodVisitor(MethodVisitor methodVisitor, int access, String name, String desc, Inject inject) {
        super(Opcodes.ASM6, methodVisitor, access, name, desc);
        this.methodVisitor = methodVisitor;
        this.inject = inject;
        this.access = access;
    }

    @Override
    protected void onMethodEnter() {
        super.onMethodEnter();
        if (inject == null || inject.getInjectEnterMethodName() == null) {
            return;
        }
        pushArgs();
        String methodSignature = getCallMethodSignature();
        ReLog.d(TAG, "call " + methodSignature);
        methodVisitor.visitMethodInsn(Opcodes.INVOKESTATIC, inject.getInjectClassName4Call(),
                inject.getInjectEnterMethodName(), methodSignature, false);
    }

    @Override
    protected void onMethodExit(int opcode) {
        super.onMethodExit(opcode);
        if (inject == null || inject.getInjectExitMethodName() == null) {
            return;
        }
        pushArgs();
        String methodSignature = getCallMethodSignature();
        ReLog.d(TAG, "call " + methodSignature);
        methodVisitor.visitMethodInsn(Opcodes.INVOKESTATIC, inject.getInjectClassName4Call(),
                inject.getInjectExitMethodName(), methodSignature, false);
    }

    private void pushArgs() {
        boolean isStatic = (access & Opcodes.ACC_STATIC) != 0;
        int instanceFlag = 0;
        if (!isStatic) {
            ReLog.d(TAG, "push this");
            methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);
            instanceFlag = 1;
        }
        Type methodType = Type.getMethodType(inject.getMethodSignature());
        Type[] argTypes = methodType.getArgumentTypes();
        for (int i = 0; i < argTypes.length; i++) {
            int paramOpcode = ASMUtils.type2Opcodes(argTypes[i]);
            ReLog.d(TAG, inject.getMethodName() + " arg" + i + ": " + argTypes[i].getClassName() + ",paramOpcode=" + paramOpcode);
            methodVisitor.visitVarInsn(paramOpcode, i + instanceFlag);
        }
    }

    private String getCallMethodSignature() {
        boolean isStatic = (access & Opcodes.ACC_STATIC) != 0;
        String methodSignature = inject.getMethodSignature();
        if (!isStatic) {
            methodSignature = inject.getMethodSignatureWithThis();
        }
        return methodSignature;
    }


}
