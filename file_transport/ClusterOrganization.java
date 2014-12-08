import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ClusterOrganization {
	private Map<Integer, String> keyValueStore;
	
	private Integer key;
	private String value;
	
	public ClusterOrganization() {
		keyValueStore =  new HashMap<>();
	}
	
	public void getKeysValuePairingsFromFiles(File directory, char delimiter) {
		for (File input : directory.listFiles()) {
			if (!(input.getName().startsWith("_") || input.getName().startsWith(".")))
			{
				try (BufferedReader reader = new BufferedReader(new FileReader(input))) {
				    String line = null;
				    String[] pairings;
				    
				    while ((line = reader.readLine()) != null) {
				    	pairings = line.split(Character.toString(delimiter));
				    	keyValueStore.put(Integer.parseInt(pairings[0].trim()), pairings[1].trim());
				    }
				} catch (IOException e) {
					e.printStackTrace();
				} catch (IndexOutOfBoundsException e) {
					e.printStackTrace();
				}
			}
		}
	}  
	
	//Convention: add "_new_input" to the original filename
	public void createInputFileFromResultsFiles(File directory, char delimiter) {
		String directoryName = makeNewDirectoryFromResults(directory);
		
		for (File input : directory.listFiles()) {
			if (!(input.getName().startsWith("_") || input.getName().startsWith("."))) {
				try (BufferedReader reader = new BufferedReader(new FileReader(input))) {
				    String line = null;
				    String value;
				    int key;
				    BufferedWriter newInput = createNewInputFileForWriting(directoryName + "/" + input.getName());
				    
				    while ((line = reader.readLine()) != null) {
				    	key = Integer.parseInt(line.trim());
				    	value = keyValueStore.get(key);
				    	
				    	if (value != null) {
				    		newInput.write(key + ":" + value + "\n");
				    	}
				    }
				    
				    newInput.flush();
				    newInput.close();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (IndexOutOfBoundsException e) {
					e.printStackTrace();
				}
			}
			
		}
	}
	
	private BufferedWriter createNewInputFileForWriting(String name) {
		File newInput = new File(name + "_input");
		FileWriter fw = null;
		
		try {
			newInput.createNewFile();
			fw = new FileWriter(newInput.getAbsoluteFile());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		BufferedWriter bw = new BufferedWriter(fw);
		
		return bw;
	}

	private String makeNewDirectoryFromResults(File directory) {
		File newDir = new File(directory.getName() + "_input");
		
		if (!newDir.exists()) {
			newDir.mkdir();
		} else {
			System.err.println("Something wicked this way comes (you forgot to delete the previous directory)!");
		}
		
		//not safe
		return newDir.getName();
	}

	//DELETE LATER IF NOT NECESSARY!
	private int getClusterNumber(String name) {
		Matcher m = Pattern.compile(".*?(\\d+)$").matcher(name);
		
		if (m.find()) {
			return Integer.parseInt(m.group(1));
		}
	
		//this shouldn't happen!
		return -1;
	}

	public void printMap() {
		for (Map.Entry<Integer, String> entry : keyValueStore.entrySet()) {
			System.out.printf("Key: %d, Value:%s\n", entry.getKey(), entry.getValue());
		}
	}
}
