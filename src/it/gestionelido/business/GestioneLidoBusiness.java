package it.gestionelido.business;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import it.gestionelido.model.Cliente;
import it.gestionelido.model.Prenotazione;

public class GestioneLidoBusiness {
	private Connection con;
	private static GestioneLidoBusiness glb;
	private static final String DATE_FORMAT_PATTERN = "dd-MM-yyyy";
	
	private GestioneLidoBusiness() {
		
	}
	
	/**
	 * Metodo di istanza syncronized per evitare problemi di accesso con SQLite
	 */
	public static synchronized GestioneLidoBusiness getInstance() {
		if(glb == null) {
			glb = new GestioneLidoBusiness();
			glb.initialize();
		}
		
		return glb;
	}
	
	private void initialize() {
        try {
            con = getConnection();

            /*
             * chiamata per creare le tabelle se non esistono
             * */
            createTablesIfNotExist();
        } catch (SQLException e) {
            e.printStackTrace(); 
        }
    }
	
	/**
	 * Metodo per creare la connessione con il database
	 * @return Ritorna la connessione
	 */
	private Connection getConnection() throws SQLException  {
		if(con == null || con.isClosed()) {
			/*
			 * crea la connessione al database (se non esiste, il database, verrà creato)
			 * */
			String url = "jdbc:sqlite:db/miodb.db";
			try {
				con = DriverManager.getConnection(url);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return con;
	}
	
	/**
	 * Metodo per la chiusura della connessione con il database
	 */
	private void closeConnection() {
	    try {
	        if (con != null && !con.isClosed()) {
	            con.close();
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	/**
	 * Metodo per la creazione delle tabelle e del database SQLite se ancora non esistono 
	 */
	private void createTablesIfNotExist() throws SQLException {
        try (Statement stmt = con.createStatement()) {
            /*
             * Verifica se le tabelle esistono già
             */
            ResultSet resultSet = con.getMetaData().getTables(null, null, "clienti", null);
            boolean clientiTableExists = resultSet.next();
            resultSet.close();

            resultSet = con.getMetaData().getTables(null, null, "prenotazioni", null);
            boolean prenotazioniTableExists = resultSet.next();
            resultSet.close();

            /*
             * Se le tabelle non esistono, le crea
             */
            if (!clientiTableExists) {
                String createClientiTable = "CREATE TABLE clienti ("
                        + "cf TEXT PRIMARY KEY,"
                        + "nome TEXT NOT NULL,"
                        + "cognome TEXT NOT NULL)";
                stmt.executeUpdate(createClientiTable);
            }

            if (!prenotazioniTableExists) {
                String createPrenotazioniTable = "CREATE TABLE prenotazioni ("
                        + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + "cf_cliente TEXT NOT NULL,"
                        + "n_ombrellone INTEGER NOT NULL,"
                        + "data TEXT NOT NULL,"
                        + "sdraio INTEGER,"
                        + "sedie INTEGER, "
                        + "pagamento INTEGER, "
                        + "FOREIGN KEY (cf_cliente) REFERENCES clienti (cf))";
                stmt.executeUpdate(createPrenotazioniTable);
            }
        }
    }
	
	/**
	 * Metodo per ottenere la lista delle prenotazioni effettuate in una data
	 * @param data Data per cui cercare le prenotazioni
	 * @return Ritorna la lista delle prenotazioni per la data data in input
	 */
	public List<Prenotazione> getPrenotazioni(Date data) throws SQLException{
		List<Prenotazione> list = new ArrayList<Prenotazione>();
		String sql = "SELECT prenotazioni.id, prenotazioni.cf_cliente, prenotazioni.n_ombrellone, prenotazioni.data, prenotazioni.sdraio, prenotazioni.sedie, "
				+ "clienti.nome, clienti.cognome, prenotazioni.pagamento "
				+ "FROM prenotazioni "
				+ "JOIN clienti ON prenotazioni.cf_cliente = clienti.cf "
				+ "WHERE prenotazioni.data = ?";
		
		try (Connection connection = getConnection();
		         PreparedStatement ps = connection.prepareStatement(sql)) {
		        ps.setString(1, dateToString(data));

		        try (ResultSet rs = ps.executeQuery()) {
		            while (rs.next()) {
		                Prenotazione p = new Prenotazione();
		                Cliente c = new Cliente(rs.getString(2), rs.getString(7), rs.getString(8));
		                p.setCliente(c);
		                p.setId(rs.getInt(1));
		                p.setCfCliente(rs.getString(2));
		                p.setNumeroOmbrellone(rs.getInt(3));
		                p.setData(stringToDate(rs.getString(4)));
		                p.setNumeroSdraio(rs.getInt(5));
		                p.setNumeroSedie(rs.getInt(6));
		                p.setStatoPagamento(rs.getInt(9));
		                
		                list.add(p);
		                
		            }
		        } finally {
		        	closeConnection();
		        }
		    }
		
		return list;
	}
	
	/**
	 * Metodo per l'aggiunta di una prenotazione
	 * @param cliente Cliente per cui fare la prenotazione
	 * @param nOmbrellone Numero dell'ombrellone da prenotare
	 * @param data Data in cui fare la prenotazione
	 * @param sdraio Numero di sdraio da prenotare
	 * @param sedie Numero di sedie da prenotare
	 * @param statoPagamento Stato del pagamento della prenotazione
	 * @return Ritorna 1 se la prenotazione è stata inserita, 0 se la prenotazione non è andata a buon fine
	 */
	public int aggiungiPrenotazione(Cliente cliente, int nOmbrellone, Date data, int sdraio, int sedie, int statoPagamento) throws SQLException {
		/*
		 * Controllo se il cliente esiste altrimenti lo creo o lo modifico
		 * @param cliente Cliente da controllare nel database
		 */
		controlloCliente(cliente);
		
		/*
		 * controllo se la prenotazione esiste altrimenti la creo o la modifico
		 * @param cliente Cliente per cui fare il controllo della prenotazione
		 * @param nOmbrellone Ombrellone per cui fare il controllo della prenotazione
		 * @param data Data per cui fare il controllo della prenotazione
		 * @param sdraio Numero di sdraio da prenotare
	     * @param sedie Numero di sedie da prenotare
		 * @param statoPagamento Stato del pagamento della prenotazione
		 * @return Ritorna 1 se la prenotazione è stata aggiornata altrimenti 0
		 */
		int result2 = controlloPrenotazione(cliente, nOmbrellone, sdraio, sedie, data, statoPagamento);
		if(result2 == 0) {
			String sql = "INSERT INTO prenotazioni(cf_cliente, n_ombrellone, data, sdraio, sedie, pagamento) VALUES (?, ?, ?, ?, ?, ?)";
			
			try (Connection connection = getConnection();
		             PreparedStatement ps = connection.prepareStatement(sql)) {
		            ps.setString(1, cliente.getCf().toUpperCase());
		            ps.setInt(2, nOmbrellone);
		            ps.setString(3, dateToString(data));
		            ps.setInt(4, sdraio);
		            ps.setInt(5, sedie);
		            ps.setInt(6, statoPagamento);

		            return ps.executeUpdate();
		        } catch (SQLException e) {
		            e.printStackTrace(); 
		            return 0; 
		        }
		  } else {
		      return 1;
		  }
		
	}
	
	/**
	 * Metodo per il controllo del cliente
	 * @param cliente Cliente per cui fare il controllo
	 */
	public void controlloCliente(Cliente cliente) throws SQLException {		
		String sql = "SELECT cf FROM clienti WHERE cf = ?";
		String sql2 = "INSERT INTO clienti (cf, nome, cognome) VAlUES (?, ?, ?) ";
		String sql3 = "UPDATE clienti SET nome = ?, cognome = ? WHERE cf = ?";
		
		try (Connection connection = getConnection();
	         PreparedStatement psSelect = connection.prepareStatement(sql);
	         PreparedStatement psInsert = connection.prepareStatement(sql2);
			 PreparedStatement psUpdate = connection.prepareStatement(sql3)) {

	        /*
	         * controllo se il cliente esiste già
	         * */
	        psSelect.setString(1, cliente.getCf().toUpperCase());
	        ResultSet rs = psSelect.executeQuery();

	        if (rs.next() == false) {
	            /*
	             * se il cliente non esiste lo inserisco
	             * */
	            psInsert.setString(1, cliente.getCf().toUpperCase());
	            psInsert.setString(2, cliente.getNome());
	            psInsert.setString(3, cliente.getCognome());
	            psInsert.executeUpdate();
	        }else {
	        	/*
	        	 * se il cliente esiste lo aggiorno
	        	 * */
	        	psUpdate.setString(1, cliente.getNome());
	        	psUpdate.setString(2, cliente.getCognome());
	        	psUpdate.setString(3, cliente.getCf().toUpperCase());
	        	psUpdate.executeUpdate();
	        }
	        
		 } catch (SQLException e) {
		    e.printStackTrace(); 
		 }finally {
			 closeConnection();
		 }
		
	}

	/**
	 * Metodo per il controllo della prenotazione
	 * @param cliente Cliente per cui fare il controllo della prenotazione
	 * @param numeroOmbrellone Numero dell'ombrellone per cui fare il controllo
	 * @param sdraio Numero di sdraio da prenotare
	 * @param sedie Numero di sedie da prenotare
	 * @param data Data per cui fare il controllo
	 * @param statoPagamento Stato del pagamento della prenotazione
	 * @return Ritorna 1 per indicare che la prenotazione è stata aggiornata altrimenti 0
	 */
	public int controlloPrenotazione(Cliente cliente, int numeroOmbrellone, int sdraio, int sedie, Date data, int statoPagamento) throws SQLException {
		String sql = "SELECT * FROM prenotazioni WHERE n_ombrellone = ? and data = ?";
		String sql2 = "UPDATE prenotazioni SET cf_cliente = ?, sdraio = ?, sedie = ?, pagamento = ?  WHERE id = ?";
		try (Connection connection = getConnection();
	         PreparedStatement psSelect = connection.prepareStatement(sql);
	         PreparedStatement psUpdate = connection.prepareStatement(sql2)) {

	        /*
	         * controllo se la prenotazione esiste già
	         * */
	        psSelect.setInt(1, numeroOmbrellone);
	        psSelect.setString(2, dateToString(data));
	        ResultSet rs = psSelect.executeQuery();

	        if (rs.next()) {
	            /*
	             * se la prenotazione esiste la aggiorno
	             * */
	            psUpdate.setString(1, cliente.getCf().toUpperCase());
	            psUpdate.setInt(2, sdraio);
	            psUpdate.setInt(3, sedie);
	            psUpdate.setInt(4, statoPagamento);
	            psUpdate.setInt(5, rs.getInt(1));
	           

	            psUpdate.executeUpdate();
	            return 1;  
	        }

	    } catch (SQLException e) {
	        e.printStackTrace(); 
	    } finally {
	    	closeConnection();
	    }

		return 0; 
	}
	
	
	/**
	 * Metodo per l'eliminazione delle prenotazioni
	 * @param cf Codice fiscale per l'identificazione del cliente per cui eliminare la prenotazione
	 * @param numeroOmbrellone Numero dell'ombrellone per cui fare l'eliminazione
	 * @param data Data della prenotazione da eliminare
	 * @param sdraio Numero di sdraio della prenotazione
	 * @param sedie Numero di sedie della prenotazione
	 * @return Restituisce il numero di righe eliminate
	 */
	public int eliminaPrenotazione(String cf, int numeroOmbrellone, Date data, int sdraio, int sedie) throws SQLException {
		String sql = "DELETE FROM prenotazioni WHERE n_ombrellone = ? and cf_cliente = ? and data = ? and sdraio = ? and sedie = ?";
		
		 try (Connection connection = getConnection();
	         PreparedStatement ps = connection.prepareStatement(sql)) {

	        ps.setInt(1, numeroOmbrellone);
	        ps.setString(2, cf.toUpperCase());
	        ps.setString(3, dateToString(data));
	        ps.setInt(4, sdraio);
	        ps.setInt(5, sedie);

	        return ps.executeUpdate(); 

	    } catch (SQLException e) {
	        e.printStackTrace();
	        return 0; 
	    } finally {
	    	closeConnection();
	    }
		
	}
	
	/**
	 * Metodo per il reset dei campi nel pannello prenotazione
	 * @param numeroOmbrellone Numero ombrellone per cui resettare i campi
	 * @param data Data per cui resettare i campi
	 * @return Ritorna la prenotazione resettata o null se non ci sono corrispondenze
	 */
	public Prenotazione resettaCampi(int numeroOmbrellone, Date data) throws SQLException {
		String sql = "SELECT prenotazioni.cf_cliente, prenotazioni.sdraio, prenotazioni.sedie, clienti.nome, clienti.cognome "
				+ "FROM prenotazioni "
				+ "JOIN clienti ON prenotazioni.cf_cliente = clienti.cf "
				+ "WHERE prenotazioni.n_ombrellone = ? and prenotazioni.data = ?";
		
		 try (Connection connection = getConnection();
		         PreparedStatement ps = connection.prepareStatement(sql)) {

	        ps.setInt(1, numeroOmbrellone);
	        ps.setString(2, dateToString(data));

	        ResultSet rs = ps.executeQuery();

	        if (rs.next()) {
	            Cliente c = new Cliente(rs.getString(1), rs.getString(4), rs.getString(5));
	            Prenotazione p = new Prenotazione();
	            p.setCliente(c);
	            p.setNumeroOmbrellone(numeroOmbrellone);
	            p.setData(data);
	            p.setNumeroSdraio(rs.getInt(2));
	            p.setNumeroSedie(rs.getInt(3));

	            return p;
	        }

	    } catch (SQLException e) {
	        e.printStackTrace(); 
	    } finally {
	    	closeConnection();
	    }

	    return null; 
	}
	
	/**
	 * Metodo per il reset del database
	 * @return Ritorna un arrai di interi che rappresenta le prenotazioni e i clienti eliminati o un array di 0 in caso di errore
	 */
	public int[] resetDB() throws SQLException {
		String sql = "DELETE FROM prenotazioni";
		String sql2 = "DELETE FROM clienti";
		
		try (Connection connection = getConnection();
	         PreparedStatement psPrenotazioni = connection.prepareStatement(sql);
	         PreparedStatement psClienti = connection.prepareStatement(sql2)) {

	        int righeEliminatePrenotazioni = psPrenotazioni.executeUpdate();
	        int righeEliminateClienti = psClienti.executeUpdate();

	        int[] risultati = {righeEliminatePrenotazioni, righeEliminateClienti};
	        return risultati;

	    } catch (SQLException e) {
	        e.printStackTrace(); 
	        return new int[]{0, 0}; 
	    } finally {
	    	closeConnection();
	    }
		
	}
	
	/**
	 * Metodo per la conversione da Date a String dd-mm-yyyy
	 * @param data Oggetto Date da convertire in stringa
	 * @return Ritorna la data convertita in stringa
	 */
	private String dateToString(Date data) {
		Calendar c = Calendar.getInstance();
		c.setTime(data);
		
		String giorno = String.format("%02d", c.get(Calendar.DAY_OF_MONTH));
	    String mese = String.format("%02d", c.get(Calendar.MONTH) + 1);
	    String anno = String.valueOf(c.get(Calendar.YEAR));
	    
		String dataFormattata = giorno + "-" + mese + "-" + anno;;
		
		return dataFormattata;
	}
	
	/**
	 * Metodo per la conversione da String a Date
	 * @param stringaData Stringa che rappresenta una data da convertire in Date
	 * @return Ritorna la stringa convertita in un oggetto Date
	 */
	private Date stringToDate(String stringaData) {
		String pattern = "dd-MM-yyyy";
		
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_PATTERN);
		
		try {
            /*
             * converto la stringa per ottenere un oggetto Date
             * */
            Date data = sdf.parse(stringaData);
            return data;
        } catch (ParseException e) {
            e.printStackTrace();
        }
		
		return null;
	}
}
