package com.nylon.reinject;

import com.android.build.api.transform.DirectoryInput;
import com.android.build.api.transform.Format;
import com.android.build.api.transform.JarInput;
import com.android.build.api.transform.QualifiedContent;
import com.android.build.api.transform.Transform;
import com.android.build.api.transform.TransformException;
import com.android.build.api.transform.TransformInput;
import com.android.build.api.transform.TransformInvocation;
import com.android.build.gradle.internal.pipeline.TransformManager;
import com.android.utils.FileUtils;
import com.nylon.reinject.cfg.Inject;
import com.nylon.reinject.cfg.ReInjectBean;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.compress.utils.IOUtils;
import org.gradle.api.Project;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.stream.Collectors;

public class ReInjectTransform extends Transform {
    private static final String TAG = "ReInjectTransform";

    private static ReInjectConfig reInjectConfig;

    private Project project;
    private Map<String, File> modifyMap = new HashMap<>();

    public ReInjectTransform(Project project) {
        this.project = project;
    }

    @Override
    public String getName() {
        return "ReInjectTransform";
    }

    @Override
    public Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS;
    }

    @Override
    public Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT;
    }

    @Override
    public boolean isIncremental() {
        return false;
    }

    private void initConfig() {
        reInjectConfig = (ReInjectConfig) this.project.getExtensions().getByName("ReInjectConfig");
        reInjectConfig.load();
    }

    @Override
    public void transform(TransformInvocation transformInvocation)
            throws TransformException, InterruptedException, IOException {
        super.transform(transformInvocation);
        //transform方法中才能获取到注册的对象
        initConfig();
        if (!isIncremental()) {
            transformInvocation.getOutputProvider().deleteAll();
        }
        // 获取输入（消费型输入，需要传递给下一个Transform）
        Collection<TransformInput> inputs = transformInvocation.getInputs();
        for (TransformInput input : inputs) {
            // 遍历输入，分别遍历其中的jar以及directory
            for (JarInput jarInput : input.getJarInputs()) {
                //对jar文件进行处理
                ReLog.d(TAG, "Find jar input: " + jarInput.getName());
                transformJar(transformInvocation, jarInput);
            }
            for (DirectoryInput directoryInput : input.getDirectoryInputs()) {
                // 对directory进行处理
                ReLog.d(TAG, "Find dir input:" + directoryInput.getFile().getName());
                transformDirectory(transformInvocation, directoryInput);
            }
        }
    }


    private void transformJar(TransformInvocation invocation, JarInput input) throws IOException {
        File tempDir = invocation.getContext().getTemporaryDir();
        String destName = input.getFile().getName();
        String hexName = DigestUtils.md5Hex(input.getFile().getAbsolutePath()).substring(0, 8);
        if (destName.endsWith(".jar")) {
            destName = destName.substring(0, destName.length() - 4);
        }
        // 获取输出路径
        File dest = invocation.getOutputProvider()
                //.getContentLocation(input.getFile().getAbsolutePath(), input.getContentTypes(), input.getScopes(), Format.JAR);
                .getContentLocation(destName + "_" + hexName, input.getContentTypes(), input.getScopes(), Format.JAR);
        JarFile originJar = new JarFile(input.getFile());
        //input:/build/intermediates/runtime_library_classes/release/classes.jar
        File outputJar = new File(tempDir, "temp_" + input.getFile().getName());
        //out:/build/tmp/transformClassesWithAmsTransformForRelease/temp_classes.jar
        //dest:/build/intermediates/transforms/AmsTransform/release/26.jar
        JarOutputStream output = new JarOutputStream(new FileOutputStream(outputJar));

        // 遍历原jar文件寻找class文件
        Enumeration<JarEntry> enumeration = originJar.entries();
        while (enumeration.hasMoreElements()) {
            JarEntry originEntry = enumeration.nextElement();
            InputStream inputStream = originJar.getInputStream(originEntry);
            String entryName = originEntry.getName();
            if (entryName.endsWith(".class")) {
                JarEntry destEntry = new JarEntry(entryName);
                output.putNextEntry(destEntry);
                byte[] sourceBytes = IOUtils.toByteArray(inputStream);
                // 修改class文件内容
                byte[] modifiedBytes = null;
                List<Inject> filterList = filterModifyClass(entryName);
                if (!filterList.isEmpty()) {
                    ReLog.d(TAG, "Modifyjar:" + entryName);
                    modifiedBytes = modifyClass(sourceBytes, filterList);
                }
                if (modifiedBytes == null) {
                    modifiedBytes = sourceBytes;
                }
                output.write(modifiedBytes);
            }
            output.closeEntry();
        }
        output.close();
        originJar.close();
        // 复制修改后jar到输出路径
        FileUtils.copyFile(outputJar, dest);
    }


    private void transformDirectory(TransformInvocation invocation, DirectoryInput input) throws IOException {
        File tempDir = invocation.getContext().getTemporaryDir();
        // 获取输出路径
        File dest = invocation.getOutputProvider()
                .getContentLocation(input.getName(), input.getContentTypes(), input.getScopes(), Format.DIRECTORY);
        File dir = input.getFile();
        if (dir != null && dir.exists()) {
            //tempDir=build/tmp/transformClassesWithAmsTransformForDebug
            //dir=build/intermediates/javac/debug/compileDebugJavaWithJavac/classes

            traverseDirectory(dir.getAbsolutePath(), tempDir, dir);
            //Map<String, File> modifiedMap = new HashMap<>();
            //traverseDirectory(tempDir, dir, modifiedMap, dir.getAbsolutePath() + File.separator);

            //input.getFile=build/intermediates/javac/debug/compileDebugJavaWithJavac/classes
            //dest=build/intermediates/transforms/AmsTransform/debug/52

            FileUtils.copyDirectory(input.getFile(), dest);

            for (Map.Entry<String, File> entry : modifyMap.entrySet()) {
                File target = new File(dest.getAbsolutePath() + File.separatorChar + entry.getKey().replace('.', File.separatorChar) + ".class");
                if (target.exists()) {
                    target.delete();
                }
                FileUtils.copyFile(entry.getValue(), target);
                entry.getValue().delete();
            }
        }
    }

    /**
     * 遍历目录下面的class文件
     *
     * @param basedir 基准目录，和dir对比需要找到包路径
     * @param tempDir 需要写入的临时目录
     * @param dir     class文件目录
     * @throws IOException
     */
    private void traverseDirectory(String basedir, File tempDir, File dir) throws IOException {
        for (File file : Objects.requireNonNull(dir.listFiles())) {
            if (file.isDirectory()) {
                traverseDirectory(basedir, tempDir, file);
            } else if (file.getAbsolutePath().endsWith(".class")) {
                String className = path2ClassName(file.getAbsolutePath()
                        .replace(basedir + File.separator, ""));
                byte[] sourceBytes = IOUtils.toByteArray(new FileInputStream(file));
                byte[] modifiedBytes = null;
                List<Inject> filterList = filterModifyClass(className);
                if (!filterList.isEmpty()) {
                    ReLog.d(TAG, "Modifydir:" + className + ".class");
                    modifiedBytes = modifyClass(sourceBytes, filterList);
                }

                if (modifiedBytes == null) {
                    modifiedBytes = sourceBytes;
                }
                File modified = new File(tempDir, className + ".class");
                if (modified.exists()) {
                    modified.delete();
                }
                modified.createNewFile();
                new FileOutputStream(modified).write(modifiedBytes);
                modifyMap.put(className, modified);
            }
        }
    }

    private List<Inject> filterModifyClass(String className) {
        List<Inject> filterList = new ArrayList<>();
        if (className == null || className.length() == 0) {
            return filterList;
        }
        if (className.endsWith(".class")) {
            className = className.substring(0, className.length() - ".class".length());
        }

        String inputClass = className.replace(File.separator, ".");
        ReInjectBean injectBean = reInjectConfig.getInjectBean();
        if (injectBean == null) {
            return filterList;
        }

        filterList = injectBean.getInjects().stream()
                .filter(inject-> inputClass.equals(inject.getClassName()))
                .collect(Collectors.toList());
        return filterList;
    }

    private byte[] modifyClass(byte[] classBytes, List<Inject> filterList) {
        ClassReader classReader = new ClassReader(classBytes);
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        ClassVisitor classVisitor = new ReInjectClassVisitor(classWriter, filterList);
        classReader.accept(classVisitor, ClassReader.EXPAND_FRAMES);
        return classWriter.toByteArray();
    }

    static String path2ClassName(String pathName) {
        return pathName.replace(File.separator, ".").replace(".class", "");
    }
}
