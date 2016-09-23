package com.starterkit.javafx.model;

import java.util.ArrayList;
import java.util.List;

import com.starterkit.javafx.dataprovider.data.PlayerVO;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;

public class UserProfileSearch {
	
	private final StringProperty userId = new SimpleStringProperty();
	private final StringProperty firstName = new SimpleStringProperty();
	private final StringProperty lastName = new SimpleStringProperty();
	
	private final ListProperty<PlayerVO> result = new SimpleListProperty<>(
			FXCollections.observableList(new ArrayList<>()));
	
	public final String getUserId() {
		return userId.get();
	}

	public final void setUserId(String value) {
		userId.set(value);
	}

	public StringProperty userIdProperty() {
		return userId;
	}
	
	public final String getFirstName() {
		return firstName.get();
	}

	public final void setFirstName(String value) {
		firstName.set(value);
	}

	public StringProperty firstNameProperty() {
		return firstName;
	}
	
	public final String getLastName() {
		return lastName.get();
	}

	public final void setLastName(String value) {
		lastName.set(value);
	}

	public StringProperty lastNameProperty() {
		return lastName;
	}
	
	public final List<PlayerVO> getResult() {
		return result.get();
	}

	public final void setResult(List<PlayerVO> value) {
		result.setAll(value);
	}

	public ListProperty<PlayerVO> resultProperty() {
		return result;
	}

	@Override
	public String toString() {
		return "PersonSearch [name=" + firstName + ", surname=" + lastName + ", login=" + userId + "]";
	}
}
