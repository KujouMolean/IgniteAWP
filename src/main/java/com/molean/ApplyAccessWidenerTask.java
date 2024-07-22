package com.molean;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.gradle.api.DefaultTask;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ResolvedArtifact;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class ApplyAccessWidenerTask extends DefaultTask {

    @TaskAction
    public void remapAccessWidenerTask() throws IOException {
        File asFile = getProject().getLayout().getProjectDirectory().file("src/main/resources/ignite.mod.json").getAsFile();
        JsonObject jsonObject = new Gson().fromJson(Files.readString(asFile.toPath()), JsonObject.class);
        if (!jsonObject.has("wideners") || jsonObject.get("wideners").getAsJsonArray().isEmpty()) {
            return;
        }
        String awPath = jsonObject.get("wideners").getAsJsonArray().get(0).getAsString();
        ApplyAccessWidener applyAccessWidener = new ApplyAccessWidener(new File(asFile.getParent(), awPath));

        getProject().getDependencies();
        for (Configuration configuration : getProject().getConfigurations()) {
            if (!configuration.getName().equals("compileClasspath")) {
                continue;
            }
            try {
                for (ResolvedArtifact resolvedArtifact : configuration.getResolvedConfiguration().getResolvedArtifacts()) {
                    if (resolvedArtifact.getName().endsWith("-server")) {
                        applyAccessWidener.apply(resolvedArtifact.getFile());
                    }
                }
            } catch (Exception ignored) {
            }
        }

    }
}
