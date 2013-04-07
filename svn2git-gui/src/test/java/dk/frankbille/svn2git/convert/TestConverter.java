package dk.frankbille.svn2git.convert;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import dk.frankbille.svn2git.model.AuthorMapping;
import dk.frankbille.svn2git.model.MappingEntry;
import dk.frankbille.svn2git.model.Project;

public class TestConverter {
	
	private Project project;

	@Before
	public void createProject() throws IOException {
		project = new Project();
		project.addAuthor(new AuthorMapping("fb", "Frank Bille <github@frankbille.dk>"));
		project.addMappingEntry(new MappingEntry("/", "", "refs/heads/master"));
		project.setStartRevision(1);
		project.setEndHeadRevision(true);
		File svnRepoFile = new File("src/test/resources/repo1");
		String svnRepo = svnRepoFile.getAbsolutePath();
		svnRepo = svnRepo.replace("\\", "/");
		project.setSvnUrl("file://"+svnRepo);
		File gitFastImportFile = new File("target/repo1.gitfastimport");
		gitFastImportFile.delete();
		project.setGitFastImportFile(gitFastImportFile.getAbsolutePath());
		File workspaceFolder = new File("target/repo1.workspace");
		FileUtils.deleteDirectory(workspaceFolder);
		project.setWorkspaceFolder(workspaceFolder.getAbsolutePath());
	}
	
	@Test
	public void testConvert() throws Exception {
		Converter converter = new Converter(project);
		
		converter.convert(); 
		
		// TODO test that the result is actually ok.
	}

}
