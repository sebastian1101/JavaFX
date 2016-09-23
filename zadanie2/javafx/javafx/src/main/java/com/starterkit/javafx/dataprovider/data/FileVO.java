package com.starterkit.javafx.dataprovider.data;

public class FileVO {

	private String directory;
	private String name;

	public FileVO(String directory, String name) {

		this.directory = directory;
		this.name = name;
	}

	public String getDirectory() {
		return directory;
	}

	public void setDirectory(String directory) {
		this.directory = directory;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
