package pro.documentum.composer.gradle;

import org.gradle.api.Action;
import org.gradle.api.DefaultTask;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.ExtraPropertiesExtension;
import org.gradle.api.tasks.TaskContainer;

import pro.documentum.composer.gradle.tasks.ComposerExec;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class ComposerPlugin implements Plugin<Project> {

    public ComposerPlugin() {
        super();
    }

    @Override
    public void apply(final Project project) {
        if (project.getPlugins().hasPlugin(getClass())) {
            return;
        }
        createPlugin(project, "execComposer", ComposerExec.class);
    }

    protected void createPlugin(final Project project, final String name,
            final Class<? extends DefaultTask> cls) {
        TaskContainer container = project.getTasks();
        final ExtraPropertiesExtension extension = project.getExtensions()
                .getExtraProperties();
        container.create(name, cls, new Action<DefaultTask>() {
            @Override
            public void execute(final DefaultTask defaultTask) {
                extension.set(cls.getSimpleName(), cls);
            }
        });
    }

}
