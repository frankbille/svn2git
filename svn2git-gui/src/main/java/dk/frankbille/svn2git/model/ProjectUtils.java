package dk.frankbille.svn2git.model;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.representer.Representer;

public final class ProjectUtils {

	private static final Yaml yaml;

	static {
		Constructor constructor = new Constructor(Project.class);
		TypeDescription projectAuthorsDescription = new TypeDescription(Project.class);
		projectAuthorsDescription.putMapPropertyType("authors", String.class, String.class);
		constructor.addTypeDescription(projectAuthorsDescription);
		TypeDescription projectMappingEntriesDescription = new TypeDescription(Project.class);
		projectMappingEntriesDescription.putListPropertyType("mappingEntries", MappingEntry.class);
		constructor.addTypeDescription(projectMappingEntriesDescription);
		DumperOptions dumperOptions = new DumperOptions();
		dumperOptions.setPrettyFlow(true);
		yaml = new Yaml(constructor, new Representer(), dumperOptions);
	}

	public static Project load(File projectFile) throws IOException {
		String config = FileUtils.readFileToString(projectFile, "UTF-8");
		return yaml.loadAs(config, Project.class);
	}

	public static void save(Project project, File projectFile) throws IOException {
		String config = yaml.dump(project);
		FileUtils.write(projectFile, config, "UTF-8");
	}

}
