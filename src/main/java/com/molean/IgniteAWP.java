package com.molean;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class IgniteAWP implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        project.getTasks().create("applyAccessWidener", ApplyAccessWidenerTask.class);
    }
}
