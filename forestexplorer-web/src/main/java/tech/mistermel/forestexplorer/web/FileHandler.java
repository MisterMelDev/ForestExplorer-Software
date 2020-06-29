package tech.mistermel.forestexplorer.web;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.iki.elonen.NanoHTTPD.Response;

public class FileHandler {
	
	private static Logger logger = LoggerFactory.getLogger(FileHandler.class);
	private static final String INDEX_FILE = "index.html";
	
	private File folder;
	
	public FileHandler(File folder) {
		if(!folder.isDirectory()) {
			throw new IllegalArgumentException("Cannot use file, must be folder");
		}
		
		this.folder = folder;
	}
	
	public Response serve(String uri) {
		if(uri.contains("../")) {
			return this.getForbiddenResponse("../ is not allowed");
		}
		
		if(uri.equals("/")) {
			uri = INDEX_FILE;
		}
		
		File requestedFile = new File(folder, uri);
		if(!requestedFile.exists()) {
			return this.getNotFoundResponse();
		}
		
		try {
			String mimeType = WebHandler.mimeTypes().get(this.getFileExtension(requestedFile));
			return WebHandler.newFixedLengthResponse(Response.Status.OK, mimeType, this.readFile(requestedFile));
		} catch (IOException e) {
			logger.error("An error occurred while reading file", e);
			return this.getInternalErrorResponse("File reading error");
		}
	}
	
	private String getFileExtension(File file) {
		String name = file.getName();
		int index = name.lastIndexOf('.');
		
		if(index == -1) {
			return null;
		}
		
		return name.substring(index + 1);
	}
	
	private Response getForbiddenResponse(String msg) {
		return WebHandler.newFixedLengthResponse(Response.Status.FORBIDDEN, WebHandler.MIME_PLAINTEXT, "Forbidden: " + msg);
	}
	
	private Response getInternalErrorResponse(String msg) {
		return WebHandler.newFixedLengthResponse(Response.Status.INTERNAL_ERROR, WebHandler.MIME_PLAINTEXT, "Internal error: " + msg);
	}
	
	private Response getNotFoundResponse() {
		return WebHandler.newFixedLengthResponse(Response.Status.NOT_FOUND, WebHandler.MIME_PLAINTEXT, "File not found");
	}
	
	private String readFile(File file) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(file));
		StringBuilder builder = new StringBuilder();
		
		String line;
		while((line = reader.readLine()) != null) {
			builder.append(line);
			builder.append("\n");
		}
		
		reader.close();
		return builder.toString();
	}
	
}
