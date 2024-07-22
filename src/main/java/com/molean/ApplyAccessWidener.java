package com.molean;

import net.fabricmc.accesswidener.AccessWidener;
import net.fabricmc.accesswidener.AccessWidenerClassVisitor;
import net.fabricmc.accesswidener.AccessWidenerReader;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.zeroturnaround.zip.ZipUtil;
import org.zeroturnaround.zip.transform.ByteArrayZipEntryTransformer;
import org.zeroturnaround.zip.transform.ZipEntryTransformer;
import org.zeroturnaround.zip.transform.ZipEntryTransformerEntry;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Set;
import java.util.zip.ZipEntry;

public class ApplyAccessWidener {
    private AccessWidener accessWidener = new AccessWidener();
    private AccessWidenerReader accessWidenerReader;

    public ApplyAccessWidener(File accessWidener) {
        this.accessWidenerReader = new AccessWidenerReader(this.accessWidener);
        try {
            BufferedReader reader = new BufferedReader(new FileReader(accessWidener));
            Throwable var3 = null;

            try {
                this.accessWidenerReader.read(reader);
            } catch (Throwable var13) {
                var3 = var13;
                throw var13;
            } finally {
                if (reader != null) {
                    if (var3 != null) {
                        try {
                            reader.close();
                        } catch (Throwable var12) {
                            var3.addSuppressed(var12);
                        }
                    } else {
                        reader.close();
                    }
                }

            }

        } catch (IOException var15) {
            throw new RuntimeException("Failed to read project access widener file");
        }
    }

    public void apply(File file) {
        ZipUtil.transformEntries(file, this.getTransformers(this.accessWidener.getTargets()));
    }

    private ZipEntryTransformerEntry[] getTransformers(Set<String> classes) {
        return classes.stream().map((string) -> new ZipEntryTransformerEntry(string.replaceAll("\\.", "/") + ".class", this.getTransformer(string))).toArray(ZipEntryTransformerEntry[]::new);
    }

    private ZipEntryTransformer getTransformer(String className) {
        return new ByteArrayZipEntryTransformer() {
            protected byte[] transform(ZipEntry zipEntry, byte[] input) {
                ClassReader reader = new ClassReader(input);
                ClassWriter writer = new ClassWriter(0);
                ClassVisitor classVisitor = AccessWidenerClassVisitor.createClassVisitor(589824, writer, ApplyAccessWidener.this.accessWidener);
                reader.accept(classVisitor, 0);
                return writer.toByteArray();
            }
        };
    }
}
