import java.io.File;

import org.junit.Test;
import org.ssf4j.Serializations;
import org.ssf4j.datafile.DataFile;


public class DataFileTest {
	@Test
	public void testDataFile() throws Exception {
		File f = File.createTempFile("DataFileTest", ".tmp");
		try {
			DataFile df = new DataFile(f, Serializations.get(Serializations.KRYO));
		} finally {
			f.delete();
		}
	}
}
