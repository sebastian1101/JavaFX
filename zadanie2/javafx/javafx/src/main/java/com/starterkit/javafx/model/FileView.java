package com.starterkit.javafx.model;

import java.util.ArrayList;
import java.util.List;

import com.starterkit.javafx.dataprovider.data.FileVO;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;

public class FileView {

	private final StringProperty name = new SimpleStringProperty();
	private final StringProperty directory = new SimpleStringProperty();
	private final ListProperty<FileVO> result = new SimpleListProperty<>(
			FXCollections.observableList(new ArrayList<>()));
	
	public final String getName() {
		return name.get();
	}

	public final void setName(String value) {
		name.set(value);
	}

	public StringProperty nameProperty() {
		return name;
	}
	
	public final String getDirectory() {
		return directory.get();
	}

	public final void setDirectory(String value) {
		directory.set(value);
	}

	public StringProperty directoryProperty() {
		return directory;
	}
	
	public final List<FileVO> getResult() {
		return result.get();
	}

	public final void setResult(List<FileVO> value) {
		result.setAll(value);
	}

	public ListProperty<FileVO> resultProperty() {
		return result;
	}
	
}
