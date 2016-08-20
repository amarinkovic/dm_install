## Description

Extends functionality of composer's ant tasks

### Examples

**Importing projects into composer workspace**

```
<pro.importProject project="${composer project name}" location="${directory containing project}" copy="${true/false - whether to copy project's files into workspaces or not}" replace="${true/false - whether to replace already exising project or not}" />
```

**Setting upgrade option to composer artifacts**

```
<pro.setUpgradeOption project="${composer project name}">
    <artifacts>
        <artifact name="${artifact name or *}" category="${category name or *}" upgradeOption="${IGNORE|VERSIONIFNEWER|VERSION|OVERWRITE}" />
    </artifacts>
</pro.setUpgradeOption>
```


**Copying resulting dar file into directory**

```
<mkdir dir="${output.dir}" />
<pro.copyDar project="${project name}" todir="${output directory}" />
```

**Replacing content of artifacts**

```
<pro.importContents file="${basedir}/importcontents.txt" />

where importcontents.txt looks like:

<project name>:<artifact name>:<artifact category>:<path to file|environment variable or system property pointing to the path to file>

```