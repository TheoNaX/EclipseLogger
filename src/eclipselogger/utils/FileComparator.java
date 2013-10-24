package eclipselogger.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;

import difflib.Delta;
import difflib.DiffUtils;
import difflib.Patch;

public class FileComparator {
	
	private static List<String> fileToLines(String fileContent) {
		List<String> lines = new LinkedList<String>();
		String line = "";
		try {
			BufferedReader in = new BufferedReader(
					new StringReader(fileContent));
			while ((line = in.readLine()) != null) {
				lines.add(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return lines;
	}
	
	public static FileChanges getFileChanges(String oldFileContent, String newFileContent) {
    	List<String> original = fileToLines(oldFileContent);
        List<String> revised  = fileToLines(newFileContent);
        
        int changedLines = 0;
        int deletedLines = 0;
        int addedLines = 0;
        
        // Compute diff. Get the Patch object. Patch is the container for computed deltas.
        Patch patch = DiffUtils.diff(original, revised);

        List<Delta> deltas = patch.getDeltas();
        if (deltas == null || deltas.isEmpty()) {
        	System.out.println(">>>>>>>>> No changes in file");
        	return null;
        }
        
        for (Delta delta : patch.getDeltas()) {
            System.out.println("############### DELTA: " + delta);
        	switch (delta.getType()) {
            case INSERT:
            	addedLines += delta.getRevised().getLines().size();
            	break;
            case DELETE:
            	deletedLines += delta.getOriginal().getLines().size();
            	break;
            case CHANGE:
            	changedLines += delta.getRevised().getLines().size();
            	break;
            }
        }
        
        FileChanges changes = new FileChanges(changedLines, deletedLines, addedLines);
        return changes;
    }


}
