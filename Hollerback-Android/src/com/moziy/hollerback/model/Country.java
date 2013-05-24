package com.moziy.hollerback.model;

public class Country {
	@Override
	public boolean equals(Object o) {

		if (o != null && o instanceof Country) {
			if (((Country) o).code.equals(code)) {
				return true;
			}
		}

		return false;
	}

	private String iso;

	private String code;

	public String name;

	public Country(String iso, String code, String name) {
		this.iso = iso;
		this.code = code;
		this.name = name;
	}

	public String toString() {
		return iso + " - " + code + " - " + name.toUpperCase();
	}
}