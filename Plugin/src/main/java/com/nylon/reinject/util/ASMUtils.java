package com.nylon.reinject.util;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public class ASMUtils {
    public static int type2Opcodes(Type type) {
        switch (type.getSort()) {
            case Type.BOOLEAN:
            case Type.CHAR:
            case Type.BYTE:
            case Type.SHORT:
            case Type.INT:
                return Opcodes.ILOAD;
            case Type.FLOAT:
                return Opcodes.FLOAD;
            case Type.LONG:
                return Opcodes.LLOAD;
            case Type.DOUBLE:
                return Opcodes.DLOAD;
            case Type.ARRAY:
            case Type.OBJECT:
                return Opcodes.ALOAD;
            default:
                throw new AssertionError("Unknown type: " + type.getClassName());
        }
    }
}
