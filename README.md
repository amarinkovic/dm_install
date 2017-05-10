# dm_install
Maven util project for importing DFC &amp; DFS dependencies into your local maven repository.

To import DFC dependencies, from dfc_install folder execute `mvn clean install`. Make sure you have `DOCUMENTUM_SHARED` environment variable properly set.

To import DFS dependencies, from dfs_install folder execute `mvn clean install`. Make sure you have `DFS_SDK` environment variable properly set.

After the import has been successfully complete, you can simply include DFC as a dependency in your `pom.xml`:
```
<dependency>
	<groupId>com.emc.documentum.dfc</groupId>
	<artifactId>dfc</artifactId>
	<version>${dfc.version}</version>
	<scope>provided</scope>
</dependency>
```
