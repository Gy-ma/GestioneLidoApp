package it.gestionelido.model;

import java.time.LocalDate;
import java.util.Date;

public class Prenotazione {
	private int id;
	private Cliente cliente;
	private int numeroOmbrellone;
	private Date data;
	private int numeroSdraio;
	private int numeroSedie;
	private int statoPagamento;
	
	public Prenotazione(Cliente cliente, int numeroOmbrellone, Date data, int numeroSdraio, int numeroSedie) {
		super();
		this.cliente = cliente;
		this.numeroOmbrellone = numeroOmbrellone;
		this.data = data;
		this.numeroSdraio = numeroSdraio;
		this.numeroSedie = numeroSedie;
	}

	public Prenotazione() {
	}

	public int getId() {
		return id;
	}
	
	public Cliente getCliente() {
		return cliente;
	}
	
	public void setCliente(Cliente c) {
		this.cliente = c;
	}
	
	public String getCfCliente() {
		return this.cliente.getCf();
	}

	public void setCfCliente(String cfCliente) {
		this.cliente.setCf(cfCliente);
	}

	public int getNumeroOmbrellone() {
		return numeroOmbrellone;
	}

	public void setNumeroOmbrellone(int numeroOmbrellone) {
		this.numeroOmbrellone = numeroOmbrellone;
	}

	public Date getData() {
		return data;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public int getNumeroSdraio() {
		return numeroSdraio;
	}

	public void setNumeroSdraio(int numeroSdraio) {
		this.numeroSdraio = numeroSdraio;
	}

	public int getNumeroSedie() {
		return numeroSedie;
	}

	public void setNumeroSedie(int numeroSedie) {
		this.numeroSedie = numeroSedie;
	}

	public int getStatoPagamento() {
		return statoPagamento;
	}

	public void setStatoPagamento(int statoPagamento) {
		this.statoPagamento = statoPagamento;
	}
	
	
}
