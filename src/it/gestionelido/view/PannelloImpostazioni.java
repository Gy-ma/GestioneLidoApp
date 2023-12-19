package it.gestionelido.view;

import java.awt.EventQueue;
import java.awt.Rectangle;

import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Color;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import it.gestionelido.business.GestioneLidoBusiness;

import javax.swing.JCheckBox;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.Properties;
import java.awt.event.ActionEvent;
import java.awt.Toolkit;
import java.awt.Font;
import java.awt.Window.Type;

public class PannelloImpostazioni extends JFrame{

	public PannelloImpostazioni() {
		getContentPane().setBackground(new Color(229, 236, 244));
		setIconImage(Toolkit.getDefaultToolkit().getImage("icons\\umbrella.png"));
		setTitle("Impostazioni");
		getContentPane().setLayout(null);
		
		getConfigStatoControlloCf();
		
		/*
		 * spinner numero ombrelloni
		 */
		JSpinner spinnerImpNumeroOmbrelloni = new JSpinner();
		spinnerImpNumeroOmbrelloni.setFont(new Font("Arial", Font.BOLD, 12));
		spinnerImpNumeroOmbrelloni.setBounds(137, 13, 51, 20);
		spinnerImpNumeroOmbrelloni.setModel(new SpinnerNumberModel(10, 10, 500, 1));
		getContentPane().add(spinnerImpNumeroOmbrelloni);
		spinnerImpNumeroOmbrelloni.setValue(PannelloGestione.getConfigNumeroOmbrelloni());
		
		JCheckBox chcImpControlloCF = new JCheckBox("");
		chcImpControlloCF.setBackground(new Color(229, 236, 244));
		chcImpControlloCF.setFont(new Font("Arial", Font.BOLD, 16));
		chcImpControlloCF.setEnabled(true);
		chcImpControlloCF.setSelected(getConfigStatoControlloCf());
		chcImpControlloCF.setBounds(137, 45, 26, 14);
		getContentPane().add(chcImpControlloCF);
		initialize();
		
		/*
		 * pulsante 'applica'
		 */
		JButton btnImpApplica = new JButton("Applica");
		btnImpApplica.setFont(new Font("Arial", Font.BOLD, 12));
		btnImpApplica.setBackground(Color.WHITE);
		btnImpApplica.setBounds(10, 70, 89, 23);
		btnImpApplica.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				PannelloGestione.setConfigNumeroOmbrelloni((int) spinnerImpNumeroOmbrelloni.getValue());
				setConfigStatoControlloCf(chcImpControlloCF.isSelected());
				try {
					PannelloGestione.aggiornaLista();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
				
			}
		});
		getContentPane().add(btnImpApplica);
		
		JLabel lblImpNumeroOmbrelloni = new JLabel("Numero ombrelloni:");
		lblImpNumeroOmbrelloni.setFont(new Font("Arial", Font.BOLD, 12));
		lblImpNumeroOmbrelloni.setBounds(10, 16, 117, 14);
		getContentPane().add(lblImpNumeroOmbrelloni);
		
		JLabel lblImpControlloCF = new JLabel("Controllo CF di default");
		lblImpControlloCF.setFont(new Font("Arial", Font.BOLD, 12));
		lblImpControlloCF.setBounds(10, 45, 131, 14);
		getContentPane().add(lblImpControlloCF);
		
		/*
		 * pulsante 'reset DB'
		 */
		JButton btnImpResetDB = new JButton("Reset Database");
		btnImpResetDB.setFont(new Font("Arial", Font.BOLD, 12));
		btnImpResetDB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					int scelta = JOptionPane.showConfirmDialog(null, "ATTENZIONE: cancellazione dati irreversibile!", "Conferma", JOptionPane.YES_NO_OPTION);
					
					if(scelta == JOptionPane.YES_OPTION) {
						GestioneLidoBusiness.getInstance().resetDB();
						JOptionPane.showMessageDialog(null, "Tutti i dati del DB sono stati elimintati");
						PannelloGestione.aggiornaLista();
					}
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		});
		btnImpResetDB.setBounds(168, 115, 130, 23);
		btnImpResetDB.setBackground(new Color(255, 121, 121));
		getContentPane().add(btnImpResetDB);
	}


	private void initialize() {
		setBounds(100, 100, 324, 185);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}

	public static boolean getConfigStatoControlloCf() {
		Properties p = new Properties();
		boolean statoControlloCf = false;
		
		InputStream is;
		try {
			is = new FileInputStream(new File("properties\\config.properties"));
			
			p.load(is);
			
			statoControlloCf = Boolean.parseBoolean(p.getProperty("statoControlloCf"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return statoControlloCf;
	}
	
	public static void setConfigStatoControlloCf(boolean valoreChcImpControlloCF) {
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
		
		p.setProperty("statoControlloCf", Boolean.toString(valoreChcImpControlloCF));
		
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
