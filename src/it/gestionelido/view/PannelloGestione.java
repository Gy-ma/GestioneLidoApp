package it.gestionelido.view;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;

import org.jdatepicker.JDatePicker;

import it.gestionelido.business.GestioneLidoBusiness;
import it.gestionelido.model.Prenotazione;
import java.awt.Toolkit;
import java.awt.Font;

public class PannelloGestione {

	private static JFrame frmGestioneOmbrelloni;
	private int widthButton = 50;
	private int heightButton = 50;
	private int x = 10;
	private int y = 11;
	public static int numeroOmbrelloni = getConfigNumeroOmbrelloni();
	private JDatePicker datePicker_1;
	private static JSpinner dateSpinner;
	private static JPanel panel;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					PannelloGestione window = new PannelloGestione();
					window.frmGestioneOmbrelloni.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}


	public PannelloGestione() {
		try {
			initialize();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void initialize() throws SQLException {
		frmGestioneOmbrelloni = new JFrame();
		frmGestioneOmbrelloni.getContentPane().setBackground(new Color(229, 236, 244));
		frmGestioneOmbrelloni.setIconImage(Toolkit.getDefaultToolkit().getImage("icons\\umbrella.png"));
		frmGestioneOmbrelloni.setTitle("Gestione Ombrelloni");
		frmGestioneOmbrelloni.setBounds(100, 100, 950, 710);
		frmGestioneOmbrelloni.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmGestioneOmbrelloni.getContentPane().setLayout(null);
		
		/*
		 * panel principale
		 */
        panel = new JPanel();
        panel.setBackground(new Color(229, 236, 244));
		panel.setBounds(10, 43, 914, 617);
		frmGestioneOmbrelloni.getContentPane().add(panel);
		
		/*
		 * spinner per la data
		 */
		SpinnerDateModel dateModel = new SpinnerDateModel();
		
		dateSpinner = new JSpinner(dateModel);
		dateSpinner.setFont(new Font("Arial", Font.BOLD, 14));
		dateSpinner.setBounds(10, 11, 100, 20);
		
		JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "dd-MM-yyyy");
		
		dateSpinner.setEditor(dateEditor);
		
		Calendar c = Calendar.getInstance();
		c.set(2024, Calendar.JANUARY, 1);
		
		dateModel.setValue(c.getTime());
		
		frmGestioneOmbrelloni.getContentPane().add(dateSpinner);
		
		
		/*
		 * creazione iniziale pulsanti ombrelloni
		 */
		Date dateSpinnerValue = (Date) dateSpinner.getValue();
		List<Prenotazione> listaPrenotazioni = GestioneLidoBusiness.getInstance().getPrenotazioni(dateSpinnerValue);
		creaButtonOmbrelloni(numeroOmbrelloni, panel, listaPrenotazioni, (Date) dateSpinner.getValue());
		panel.setLayout(new GridLayout(0, 10, 5, 5));
		
		/*
		 * pulsante 'vai' che permette di visualizzare gli ombrelloni per una cera data
		 */
		JButton buttonVai = new JButton("Vai");
		buttonVai.setFont(new Font("Arial", Font.BOLD, 12));
		buttonVai.setBackground(new Color(255, 255, 255));
		buttonVai.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Date dateSpinnerValue = (Date) dateSpinner.getValue();
				
				try {
					List<Prenotazione> listaPrenotazioni = GestioneLidoBusiness.getInstance().getPrenotazioni(dateSpinnerValue);
					/*
					 * ripulisco il pannello degli ombrelloni
					 */
					panel.removeAll();
					panel.revalidate();
					frmGestioneOmbrelloni.repaint();
					/*
					 * ripulisco il pannello degli ombrelloni
					 */
					creaButtonOmbrelloni(numeroOmbrelloni, panel, listaPrenotazioni, (Date) dateSpinner.getValue());
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
				
			}
		});
		buttonVai.setIcon(null);
		buttonVai.setBounds(134, 9, 53, 23);
		frmGestioneOmbrelloni.getContentPane().add(buttonVai);
		
		/*
		 * pulsante 'impostazioni' 
		 */
		JButton btnImpostazioni = new JButton("Impostazioni");
		btnImpostazioni.setFont(new Font("Arial", Font.BOLD, 12));
		btnImpostazioni.setBackground(new Color(255, 255, 255));
		btnImpostazioni.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				PannelloImpostazioni impPan = new PannelloImpostazioni();
				impPan.setBounds(100, 100, 324, 185);
				impPan.setVisible(true);
			}
		});
		btnImpostazioni.setBounds(796, 10, 128, 23);
		frmGestioneOmbrelloni.getContentPane().add(btnImpostazioni);
	}
	
	/**
	 * Metodo per la creazione dei pulsanti ombrelloni
	 * @param n Numero degli ombrelloni
	 * @param panel Pannello in cui inserire i pulsanti per gli ombrelloni
	 * @param listaPrenotazioni Lista delle prenotazioni per la data x
	 * @param data Data per cui cercare le prenotazioni
	 */
	public static void creaButtonOmbrelloni(int n, JPanel panel, List<Prenotazione> listaPrenotazioni, Date data) {
		List<JButton> listaButtons = new ArrayList<JButton>();
		for(int i = 1; i <= n ; i++) {
			JButton button = new JButton("" + i);
			button.setName("button" + i);
			button.setFont(new Font("Reem Kufi", Font.BOLD, 16));
			button.setBackground(Color.white);
			for (Prenotazione prenotazione : listaPrenotazioni) {
				if(prenotazione.getNumeroOmbrellone() == i) {
					if(prenotazione.getStatoPagamento() == 1) {
						button.setBackground(new Color(80, 206, 80));
					}else {
						button.setBackground(new Color(255, 179, 179));
					}
					
					
				}
			}
			listaButtons.add(button);
		}
		
		/*
		 * ciclo per l'identificazione degli ombrelloni prenotati per una certa data
		 */
		for (JButton jButton : listaButtons) {
			jButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					PannelloPrenotazione pp = new PannelloPrenotazione();
					pp.setBounds(100, 100, 456, 320);
					pp.setVisible(true);
					pp.updateOmbrelloneText("Ombrellone " + jButton.getText());
					pp.setData(data);
					pp.setNumeroOmbrellone(Integer.parseInt(jButton.getText()));
					for (Prenotazione prenotazione : listaPrenotazioni) {
						if(prenotazione.getNumeroOmbrellone() == Integer.parseInt(jButton.getText())) {
							pp.setCodiceFiscale(prenotazione.getCfCliente());
							pp.setTextNomeCliente(prenotazione.getCliente().getNome());
							pp.setTextCognomeCliente(prenotazione.getCliente().getCognome());
							pp.setSpinnerNumeroSdraio(prenotazione.getNumeroSdraio());
							pp.setSpinnerNumeroSedie(prenotazione.getNumeroSedie());
							pp.setChcPagamento(prenotazione.getStatoPagamento());
						}
					}
				}
			});
			panel.add(jButton);
		}

	}
	
	/**
	 * Metodo per l'aggiornamento della lista dei pulsanti ombrellone
	 */
	public static void aggiornaLista() throws SQLException {
		Date dateSpinnerValue = (Date) dateSpinner.getValue();
		
		List<Prenotazione> listaPrenotazioni = GestioneLidoBusiness.getInstance().getPrenotazioni(dateSpinnerValue);

		panel.removeAll();
		panel.revalidate();
		frmGestioneOmbrelloni.repaint();
			
		creaButtonOmbrelloni(getConfigNumeroOmbrelloni(), panel, listaPrenotazioni, (Date) dateSpinner.getValue());
	}
	
	/**
	 * Metodo per prendere il numero degli ombrelloni dal file config.properties
	 * @return Ritorna il numero degli ombrelloni impostato  
	 */
	public static int getConfigNumeroOmbrelloni() {
		Properties p = new Properties();
		int numeroOmbrelloni = 0;
		
		InputStream is;
		try {
			is = new FileInputStream(new File("properties\\config.properties"));
			
			p.load(is);
			
			numeroOmbrelloni = Integer.valueOf(p.getProperty("numeroOmbrelloni"));

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return numeroOmbrelloni;
	}
	
	
	/**
	 * Metodo per settare il numero degli ombrelloni nel file config.properties
	 * @param nuovoNumeroOmbrelloni Nuovo numero di ombrelloni da settare nel file config.properties
	 */
	public static void setConfigNumeroOmbrelloni(int nuovoNumeroOmbrelloni) {
		Properties p = new Properties();
		
		InputStream is;
		OutputStream os;
		
		try {
			is = new FileInputStream(new File("properties\\config.properties"));
			
			p.load(is);
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		p.setProperty("numeroOmbrelloni", String.valueOf(nuovoNumeroOmbrelloni));
		
		try {
			os = new FileOutputStream(new File("properties\\config.properties"));
			
			p.store(os, null);
			
			os.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}
}
