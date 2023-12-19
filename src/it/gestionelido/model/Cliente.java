package it.gestionelido.model;

import java.time.LocalDate;

public class Cliente {
	private String cf;
	private String nome;
	private String cognome;
	
	
	public Cliente(String cf, String nome, String cognome) {
		super();
		this.cf = cf;
		this.nome = nome;
		this.cognome = cognome;
	}

	public Cliente() {
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getCognome() {
		return cognome;
	}

	public void setCognome(String cognome) {
		this.cognome = cognome;
	}

	public String getCf() {
		return cf;
	}

	public void setCf(String cf) {
		this.cf = cf;
	}
	
}
