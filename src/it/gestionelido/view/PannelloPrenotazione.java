package it.gestionelido.view;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;
import javax.swing.JTextField;

import it.gestionelido.business.GestioneLidoBusiness;
import it.gestionelido.model.Cliente;
import it.gestionelido.model.Prenotazione;

import javax.swing.JButton;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.Date;
import java.awt.event.ActionEvent;
import javax.swing.JToggleButton;
import javax.swing.JCheckBox;
import java.awt.Toolkit;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

public class PannelloPrenotazione extends JFrame {
	private JTextField textNomeCliente;
    private JTextField textCognomeCliente;
    private JTextField textCodiceFiscale;
    private int numeroOmbrellone;
    private Date data;
    private JLabel lblOmbrellone;
    private JButton btnAggiungiModifica;
    private JButton btnElimina;
    private JButton btnResetCampi;
    private JSpinner spinnerNumeroSdraio;
    private JSpinner spinnerNumeroSedie;
    private JCheckBox chcPagamento;
   
	public PannelloPrenotazione() {
		getContentPane().setBackground(new Color(229, 236, 244));
		setIconImage(Toolkit.getDefaultToolkit().getImage("icons\\umbrella.png"));
		setTitle("Gestione ombrellone");
		getContentPane().setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Nome");
		lblNewLabel.setFont(new Font("Arial", Font.BOLD, 12));
		lblNewLabel.setBounds(10, 75, 79, 23);
		getContentPane().add(lblNewLabel);
		
		JLabel lblCognome = new JLabel("Cognome");
		lblCognome.setFont(new Font("Arial", Font.BOLD, 12));
		lblCognome.setBounds(10, 109, 79, 23);
		getContentPane().add(lblCognome);
		
		JLabel lblSdraio = new JLabel("Sdraio");
		lblSdraio.setFont(new Font("Arial", Font.BOLD, 12));
		lblSdraio.setBounds(10, 143, 79, 23);
		getContentPane().add(lblSdraio);
		
		JLabel lblSedie = new JLabel("Sedie");
		lblSedie.setFont(new Font("Arial", Font.BOLD, 12));
		lblSedie.setBounds(10, 177, 79, 23);
		getContentPane().add(lblSedie);
		
		lblOmbrellone = new JLabel("Ombrellone");
		lblOmbrellone.setFont(new Font("Arial", Font.BOLD, 16));
		lblOmbrellone.setBounds(10, 11, 132, 23);
		getContentPane().add(lblOmbrellone);
		
		JLabel lblCodiceFiscale = new JLabel("CF");
		lblCodiceFiscale.setFont(new Font("Arial", Font.BOLD, 12));
		lblCodiceFiscale.setBounds(10, 45, 79, 14);
		getContentPane().add(lblCodiceFiscale);
		
		textCodiceFiscale = new JTextField();
		textCodiceFiscale.setFont(new Font("Bahnschrift", Font.PLAIN, 12));
		textCodiceFiscale.setBounds(99, 45, 171, 20);
		getContentPane().add(textCodiceFiscale);
		textCodiceFiscale.setColumns(10);
		
		JCheckBox chcControlloCf = new JCheckBox("");
		chcControlloCf.setBackground(new Color(229, 236, 244));
		chcControlloCf.setBounds(276, 42, 21, 23);
		chcControlloCf.setSelected(PannelloImpostazioni.getConfigStatoControlloCf());
		getContentPane().add(chcControlloCf);
		
		JLabel lblNewLabel_1 = new JLabel("Controllo CF");
		lblNewLabel_1.setFont(new Font("Arial", Font.BOLD, 12));
		lblNewLabel_1.setBounds(303, 45, 89, 14);
		getContentPane().add(lblNewLabel_1);
		
		textNomeCliente = new JTextField();
		textNomeCliente.setFont(new Font("Bahnschrift", Font.PLAIN, 12));
		textNomeCliente.setBounds(99, 76, 171, 20);
		getContentPane().add(textNomeCliente);
		textNomeCliente.setColumns(10);
		
		textCognomeCliente = new JTextField();
		textCognomeCliente.setFont(new Font("Bahnschrift", Font.PLAIN, 12));
		textCognomeCliente.setColumns(10);
		textCognomeCliente.setBounds(99, 110, 171, 20);
		getContentPane().add(textCognomeCliente);
		
		spinnerNumeroSdraio = new JSpinner();
		spinnerNumeroSdraio.setFont(new Font("Arial", Font.BOLD, 12));
		spinnerNumeroSdraio.setModel(new SpinnerNumberModel(0, 0, 99, 1));
		spinnerNumeroSdraio.setBounds(99, 144, 43, 20);
		getContentPane().add(spinnerNumeroSdraio);
		
		spinnerNumeroSedie = new JSpinner();
		spinnerNumeroSedie.setFont(new Font("Arial", Font.BOLD, 12));
		spinnerNumeroSedie.setModel(new SpinnerNumberModel(0, 0, 99, 1));
		spinnerNumeroSedie.setBounds(99, 175, 43, 20);
		getContentPane().add(spinnerNumeroSedie);
		
		chcPagamento = new JCheckBox();
		chcPagamento.setBackground(new Color(229, 236, 244));
		chcPagamento.setBounds(84, 245, 21, 23);
		getContentPane().add(chcPagamento);
		
		/*
		 * pulsante per l'aggiunta o la modifica di una prenotazione
		 */
		btnAggiungiModifica = new JButton("Aggiungi/Conferma Modifica");
		btnAggiungiModifica.setFont(new Font("Arial", Font.BOLD, 12));
		btnAggiungiModifica.setBackground(Color.white);
		btnAggiungiModifica.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int statoPagamento = 0;
				if(chcPagamento.isSelected()) {
					statoPagamento = 1;
				}else {
					statoPagamento = 0;
				}
				
				 /*
				  * controllo che i campi non siano vuoti
				  */
				if(textCodiceFiscale.equals("") || textNomeCliente.getText().equals("") || textCognomeCliente.getText().equals("")) {
					JOptionPane.showMessageDialog(null, "Errore: Inserisci i campi CF, nome e cognome!");
					resettaCampi();
					return;
				}
				
				/*
				 * controllo il CF
				 */
				if(textCodiceFiscale.getText().length() > 16) {
					 JOptionPane.showMessageDialog(null, "ERRORE: inserire massimo 16 caratteri!");
					 textCodiceFiscale.setText("");
					 return;
				 }
				 if(chcControlloCf.isSelected()) {
					 if(validaCf(textCodiceFiscale.getText()) == false) {
						 JOptionPane.showMessageDialog(null, "ERRORE:Codice fiscale non valido!");
						 textCodiceFiscale.setText("");
						 return;
					 }
				 }
				
				 Cliente c = new Cliente();
				 c.setCf(textCodiceFiscale.getText());
				 c.setNome(textNomeCliente.getText());
				 c.setCognome(textCognomeCliente.getText());
				 
				/*
				 * aggiungo o modifico la prenotazione
				 */
				int sdraio = (int) spinnerNumeroSdraio.getValue();
				int sedie = (int) spinnerNumeroSedie.getValue();
				try {
					int result = GestioneLidoBusiness.getInstance().aggiungiPrenotazione(c, numeroOmbrellone, data, sdraio, sedie, statoPagamento);
					PannelloGestione.aggiornaLista();
					if(result > 0) {
						JOptionPane.showMessageDialog(null, "Prenotazione inserita con successo!");
					}else {
						JOptionPane.showMessageDialog(null, "ERRORE: Prenotazione non riuscita!");
					}
				
				}catch (SQLException e1) {
					e1.printStackTrace();
				} 
				
				
			}
		});
		btnAggiungiModifica.setBounds(10, 211, 199, 23);
		getContentPane().add(btnAggiungiModifica);
		
		/*
		 * pulsante per l'eliminazione di una prenotazione
		 */
		btnElimina = new JButton("Elimina");
		btnElimina.setFont(new Font("Arial", Font.BOLD, 12));
		btnElimina.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String cf = textCodiceFiscale.getText();
				int sdraio = (int) spinnerNumeroSdraio.getValue();
				int sedie = (int) spinnerNumeroSedie.getValue();
				/*
				 * provo ad eliminare il contatto
				 */
				try {
					int result = GestioneLidoBusiness.getInstance().eliminaPrenotazione(cf, numeroOmbrellone, data, sdraio, sedie);
					if(result == 1) {
						JOptionPane.showMessageDialog(null, "Prenotazione eliminata!");
						resettaCampi();
						PannelloGestione.aggiornaLista();
					}else {
						JOptionPane.showMessageDialog(null, "ERRORE: impossibile eliminare la prenotazione!");
						Prenotazione p = GestioneLidoBusiness.getInstance().resettaCampi(numeroOmbrellone, data);
						/*
						 * setto nuovamente i campi con i valori della prenotazione esatta
						 */
						textCodiceFiscale.setText(p.getCfCliente());
						textNomeCliente.setText(p.getCliente().getNome());
						textCognomeCliente.setText(p.getCliente().getCognome());
						spinnerNumeroSdraio.setValue(p.getNumeroSdraio());
						spinnerNumeroSedie.setValue(p.getNumeroSedie());
					}
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		});
		btnElimina.setBackground(new Color(255, 125, 125));
		btnElimina.setBounds(341, 245, 89, 23);
		getContentPane().add(btnElimina);
		
		
		/*
		 * pulsante per il reset dei campi
		 */
		btnResetCampi = new JButton("Reset Campi");
		btnResetCampi.setFont(new Font("Arial", Font.BOLD, 12));
		btnResetCampi.setBackground(Color.white);
		btnResetCampi.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				resettaCampi();
			}
		});
		btnResetCampi.setBounds(220, 211, 109, 23);
		getContentPane().add(btnResetCampi);
		
		JLabel lblPagamento = new JLabel("Pagamento");
		lblPagamento.setFont(new Font("Arial", Font.BOLD, 12));
		lblPagamento.setBounds(10, 249, 69, 14);
		getContentPane().add(lblPagamento);
		
	}

	public int getSpinnerNumeroSdraio() {
		return (int) spinnerNumeroSdraio.getValue();
	}

	public void setSpinnerNumeroSdraio(int numeroSdraio) {
		this.spinnerNumeroSdraio.setValue(numeroSdraio);
	}

	public int getSpinnerNumeroSedie() {
		return (int) spinnerNumeroSedie.getValue();
	}

	public void setSpinnerNumeroSedie(int numeroSedie) {
		this.spinnerNumeroSedie.setValue(numeroSedie);
	}

	private void initialize() {
		setBounds(100, 100, 450, 300);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	public void setNumeroOmbrellone(int numeroOmbrellone) {
		this.numeroOmbrellone = numeroOmbrellone;
	}
	
	public void updateOmbrelloneText(String text) {
		lblOmbrellone.setText(text);
	}
	
	public String getLblOmbrellone() {
		return this.lblOmbrellone.getText();
	}
	
	public JTextField getCodiceFiscale() {
		return textCodiceFiscale;
	}

	public JTextField getTextNomeCliente() {
		return textNomeCliente;
	}

	public void setCodiceFiscale(String cf) {
		this.textCodiceFiscale.setText(cf);
	}
	
	public void setTextNomeCliente(String nome) {
		this.textNomeCliente.setText(nome);
	}

	public JTextField getTextCognomeCliente() {
		return textCognomeCliente;
	}

	public void setTextCognomeCliente(String cognome) {
		this.textCognomeCliente.setText(cognome);
	}

	public void setData(Date data) {
		this.data = data;
	}
	
	public void setChcPagamento(int valore) {
		if(valore == 0) {
			this.chcPagamento.setSelected(false);
		}else {
			this.chcPagamento.setSelected(true);
		}
		
	}

	/**
	 * Metodo per svuotare i campi 
	 */
	public void resettaCampi() {
		textCodiceFiscale.setText("");
		textNomeCliente.setText("");
		textCognomeCliente.setText("");
		spinnerNumeroSdraio.setValue(0);
		spinnerNumeroSedie.setValue(0);
	}
	
	/**
	 * Metodo ver validare il codicefiscale
	 * @param cf Codice fiscale da validare
	 */
	public boolean validaCf(String cf) {
		return cf.matches("^[A-Z]{6}\\d{2}[A-Z]\\d{2}[A-Z]\\d{3}[A-Z]$");
		
	}
}
