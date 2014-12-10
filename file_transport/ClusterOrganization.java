import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;

public class ClusterOrganization {
	private Map<Integer, String> keyValueStore;
	
	private Integer key;
	private String value;

	//Configuration conf = getConf();
        private Configuration conf;
        private FileSystem fs;

	
	public ClusterOrganization() {
		keyValueStore =  new HashMap<>();
	
		try {
			conf = new Configuration();
			fs = FileSystem.get(conf);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
        
	public void getKeysValuePairingsFromFiles(String directory, char delimiter) throws IOException {
	            FileStatus[] fstatus = fs.listStatus(new Path(directory));

	            for (FileStatus input : fstatus) {
			if (!(input.getPath().getName().startsWith("_") || input.getPath().getName().startsWith(".")))
			{
			        try (BufferedReader reader = new BufferedReader(new InputStreamReader(fs.open(input.getPath())))) {
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
	public void createInputFileFromResultsFiles(String directory, char delimiter) throws IOException{
		String directoryName = makeNewDirectoryFromResults(directory);
		FileStatus[] fstatus = fs.listStatus(new Path(directory));

		for (FileStatus input : fstatus) {
		    if (!(input.getPath().getName().startsWith("_") || input.getPath().getName().startsWith("."))) 
			{
			        try (BufferedReader reader = new BufferedReader(new InputStreamReader(fs.open(input.getPath())))) {
				    String line = null;
				    String value;
				    int key;
				    BufferedWriter newInput = createNewInputFileForWriting(directoryName + "/" + input.getPath().getName());
				    
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
		Path newInput = new Path(name + "_input");
		OutputStreamWriter fw = null;
		
		try {
		    fw = new OutputStreamWriter(fs.create(newInput));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		BufferedWriter bw = new BufferedWriter(fw);
		
		return bw;
	}

	private String makeNewDirectoryFromResults(String directory)throws IOException {
	        //Path path = new Path(directory);
		Path newDir = new Path(directory + "_input");
		
		if (fs.exists(newDir)) {
		        fs.delete(newDir);
			fs.mkdirs(newDir);
		}
		else {
		        fs.mkdirs(newDir);
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
