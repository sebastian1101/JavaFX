package com.starterkit.javafx.model;

import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class UserProfileEdit {

	private final LongProperty id = new SimpleLongProperty();
	private final StringProperty login = new SimpleStringProperty();
	private final StringProperty firstName = new SimpleStringProperty();
	private final StringProperty lastName = new SimpleStringProperty();
	private final StringProperty email = new SimpleStringProperty();
	private final StringProperty password = new SimpleStringProperty();
	private final StringProperty aboutMe = new SimpleStringProperty();
	private final StringProperty lifeMotto = new SimpleStringProperty();
	
	public final Long getId() {
		return id.get();
	}

	public final void setId(long value) {
		id.set(value);
	}

	public final LongProperty idProperty() {
		return id;
	}
	
	public final String getLogin() {
		return login.get();
	}

	public final void setLogin(String value) {
		login.set(value);
	}

	public StringProperty loginProperty() {
		return login;
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
	
	public final String getEmail() {
		return email.get();
	}

	public final void setEmail(String value) {
		email.set(value);
	}

	public StringProperty emailProperty() {
		return email;
	}
	
	public final String getPassword() {
		return password.get();
	}

	public final void setPassword(String value) {
		password.set(value);
	}

	public StringProperty passwordProperty() {
		return password;
	}
	
	public final String getAboutMe() {
		return aboutMe.get();
	}

	public final void setAboutMe(String value) {
		aboutMe.set(value);
	}

	public StringProperty aboutMeProperty() {
		return aboutMe;
	}
	
	public final String getLifeMotto() {
		return lifeMotto.get();
	}

	public final void setLifeMotto(String value) {
		lifeMotto.set(value);
	}

	public StringProperty LifeMottoProperty() {
		return lifeMotto;
	}
	
}
