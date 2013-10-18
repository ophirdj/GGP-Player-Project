package logging;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import utils.Verbose;



public final class CSVGenerator {

	private FileWriter writer;
	private int numFields;

	public CSVGenerator(String directoryPath, String filename, String headers[]) throws IOException {
		try {
			File directory = new File(directoryPath);
			if(!directory.exists()) {
				directory.mkdirs();
			}
			File file = new File(directory, filename);
			this.writer = new FileWriter(file);
			this.numFields = headers.length;
			for(int i = 0; i < numFields - 1; i++) {
				writer.append(headers[i]);
				writer.append(',');
			}
			writer.append(headers[numFields - 1]);
			writer.append('\n');
		} catch(IOException e) {
			Verbose.printVerboseError("CSV file creation failed at: " + filename + ". IOException: " + e.getMessage(), Verbose.FILE_IO);
			throw e;
		}
	}
	
	public void addRow(Object data[]) throws IOException {
		try {
			if(data == null) {
				Verbose.printVerboseError("CSV file save: null data", Verbose.FILE_IO);
				return;
			}
			if(data.length != numFields) {
				Verbose.printVerboseError("CSV file save: incorrect number of data fields. Expected " + numFields + ", given " + data.length, Verbose.FILE_IO);
				return;
			}
			for(int i = 0; i < numFields - 1; i++) {
				writer.append(data[i].toString());
				writer.append(',');
			}
			writer.append(data[numFields - 1].toString());
			writer.append('\n');
		} catch(IOException e) {
			Verbose.printVerboseError("CSV failed to write data to file", Verbose.FILE_IO);
			throw e;
		}
	}
	
	public void close() throws IOException {
		try {
			writer.close();
		} catch (IOException e) {
			Verbose.printVerboseError("CSV failed to close file", Verbose.FILE_IO);
			throw e;
		}
	}
	
}
