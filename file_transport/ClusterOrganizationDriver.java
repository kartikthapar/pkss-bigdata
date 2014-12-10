import java.io.File;
import java.io.IOException;

public class ClusterOrganizationDriver {

	public static void main(String[] args) throws IOException{
		ClusterOrganization co = new ClusterOrganization();
		
		//test input map
		co.getKeysValuePairingsFromFiles("/paul_input/random",':');
		co.printMap();
		
		//test result -> new input 
		//co.createInputFileFromResultsFiles(new File("/Users/slpearlman/Documents/workspace/HadoopClusterer/results0"), ':');
	}

}

