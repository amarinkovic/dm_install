## Description

Allows to remove boilerplate code based on org.gradle.api.tasks.JavaExec when running composer tasks from gradle-based projects.

### Examples

**gradle.properties**:

```
COMPOSER_LOCATION=/Users/apanfilov/work/app/emc/composer/6.7.2070
```

**build.gradle**:

```
buildscript {

    dependencies {
        classpath 'pro.documentum:prodctm-composer-gradle:0.1'
    }

}

apply plugin: 'pro.documentum.composer'

task dars(type: ComposerExec) {
    buildFile('build.xml')
    workspace('tmp/buildworkspace'))
    buildTarget('all')
    defineToFile('output.dir', 'build/dars')
    defineToFile('composer.project.dir', 'ComposerProject'))
    subprojects.each { subproject ->
        def name = subproject.name
        if (!subproject.jar) {
            return
        }
        defineToFile("${name}.jar.path", subproject.jar.outputs.files[0])
    }
}

dars.dependsOn {
    subprojects.build
}
```

**build.xml**:

```
<?xml version="1.0"?>
<project name="myproject" default="all">

    <macrodef name="ignore.all">
        <attribute name="project" />
        <sequential>
            <pro.setUpgradeOption project="@{project}">
                <artifacts>
                    <artifact name="*" category="com.emc.ide.artifact.dclass" upgradeOption="IGNORE" />
                    <artifact name="*" category="com.emc.ide.artifact.daspect" upgradeOption="IGNORE" />
                    <artifact name="*" category="com.emc.ide.artifact.relationtype" upgradeOption="IGNORE" />
                    <artifact name="*" category="com.emc.ide.artifact.moduledef" upgradeOption="IGNORE" />
                    <artifact name="*" category="com.emc.ide.artifact.method" upgradeOption="IGNORE" />
                    <artifact name="*" category="com.emc.ide.artifact.jardef.javalibrary" upgradeOption="IGNORE" />
                    <artifact name="*" category="com.emc.ide.artifact.jardef.jardef" upgradeOption="IGNORE" />
                    <artifact name="*" category="com.emc.ide.artifact.bpm.processContainer" upgradeOption="IGNORE" />
                </artifacts>
            </pro.setUpgradeOption>
        </sequential>
    </macrodef>

    <macrodef name="copy.project">
        <attribute name="project" />
        <sequential>
            <pro.importProject project="@{project}" location="${composer.project.dir}" copy="true" replace="true" />
            <ignore.all project="@{project}" />
        </sequential>
    </macrodef>

    <macrodef name="import.project">
        <attribute name="project" />
        <sequential>
            <pro.importProject project="@{project}" location="${composer.project.dir}" copy="false" />
        </sequential>
    </macrodef>

    <macrodef name="copy.dar">
        <attribute name="project" />
        <sequential>
            <mkdir dir="${output.dir}" />
            <pro.copyDar project="@{project}" todir="${output.dir}" />
        </sequential>
    </macrodef>

    <target name="create-workspace" description="Create local composer workspace">
        <import.project project="MyDocumentumProject" />
    </target>

    <target name="create-build-workspace" description="Create build composer workspace">
        <copy.project project="MyDocumentumProject" />
    </target>

    <target name="importcontent" description="Import content">
        <pro.importContents file="${basedir}/importcontents.txt" />
    </target>

    <target name="build-workspace" description="build eclipse project">
        <eclipse.incrementalBuild kind="full" />
    </target>

    <target name="clean-workspace" description="clean eclipse project">
        <eclipse.incrementalBuild kind="clean" />
    </target>

    <target name="copy">
        <copy.dar project="MyDocumentumProject" />
    </target>

    <target name="setoptions" description="Set upgrade options">
        <pro.setUpgradeOptions file="${basedir}/upgradeoptions.txt" />
    </target>

    <target name="all" depends="create-build-workspace, importcontent, setoptions, build-workspace, copy" />

</project>

```
