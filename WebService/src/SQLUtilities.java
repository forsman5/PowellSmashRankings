import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/*
 * Class to mock the jdbc driver, and to connect to the mysql implementation.
 * 
 * All sql interactions must come through here.
 * 
 * TODO
 * Add logging for every entry added
 */
public class SQLUtilities {
	
	private Connection conn = null;
	
	private String ERROR_ALERT_DESTINATION;
	private String[] ERROR_ALERT_ORIGINATION;
	
	/*
	 * Constructor. Initializes this as the driver for the JDBC of mysql
	 */
	public SQLUtilities() {

		Settings settings = new Settings();
		
		ERROR_ALERT_DESTINATION = settings.getString("ERROR_ALERT_DESTINATION");
		ERROR_ALERT_ORIGINATION = settings.getStringArr("ERROR_ALERT_ORIGINATION");
		
        // This will load the MySQL driver, each DB has its own driver
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			//should not occur...
			e.printStackTrace();
		}
		
        conn = getConnection(settings.getString("DATABASE_NAME"), settings.getString("DATABASE_USERNAME"), settings.getString("DATABASE_PASSWORD"));
	}
	
	/*
	 * Constructor. Initializes this as the driver for the JDBC of mysql
	 * 
	 * providing an already initialized settings object gives a slight performance boost
	 */
	public SQLUtilities(Settings settings) {
		ERROR_ALERT_DESTINATION = settings.getString("ERROR_ALERT_DESTINATION");
		ERROR_ALERT_ORIGINATION = settings.getStringArr("ERROR_ALERT_ORIGINATION");
		
        // This will load the MySQL driver, each DB has its own driver
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			//should not occur...
			e.printStackTrace();
		}
		
        conn = getConnection(settings.getString("DATABASE_NAME"), settings.getString("DATABASE_USERNAME"), settings.getString("DATABASE_PASSWORD"));
	}	
	
	/*
	 * Gets the standard connection object used to connect to the database.
	 */
	private Connection getConnection(String db, String user, String pass) {
		String connectionString = "jdbc:mysql://localhost/" + db + "?"
                + "user=" + user + "&password=" + pass +"&useSSL=false";

		Connection conn = null;
		
		try {
			conn = DriverManager.getConnection(connectionString);
		} catch (SQLException e) {
			String message = Utility.getBody("getConnection", e, "Bad connection string.");
			String subject = "Error occured in Challonge Elo Parser application!";
			
			Utility.sendEmail(ERROR_ALERT_DESTINATION, ERROR_ALERT_ORIGINATION, subject, message);
			
			//remove for production
			e.printStackTrace();
		}
		
		return conn;
	}
	
	/*
	 * Load the date of the latest tournament stored in the database.
	 * 
	 * This should be used such that no searching or processing is done for any tournaments before
	 * this date.
	 * 
	 * Adds one day to the date, so only checks after the latest tournament, not on that day.
	 * 
	 * Return it as a string formatted as YYYY-MM-DD
	 */
	public String getLastCheckedDate() {
		Date out = null;
		
		try {
			CallableStatement cs = conn.prepareCall("{call GetLatestTourneyDate()}");
			
			ResultSet rs = cs.executeQuery();
			
			if (rs.next()) {
				out = rs.getDate(1);
				
				//add a day
				Calendar c = Calendar.getInstance();
				c.setTime(out);
				c.add(Calendar.DATE, 1);
				out = new java.sql.Date(c.getTimeInMillis());
			} else {
				//return the epoch, as no data currently exists in the database
				out = Date.valueOf("1970-01-01");
			}
		} catch (SQLException e) {
			//should be caught by next
			e.printStackTrace();
		}
		
		return out.toString();
	}
	
	/*
	 * Inserts the given tournament into the database.
	 * 
	 * REQUIRES:
	 * 	Tournament ID must be unique in the database.
	 */
	public void insertTournament(Tournament x) {
		try {
			CallableStatement cs = conn.prepareCall("{call InsertTournament(?, ?, ?, ?)}");
			
			SimpleDateFormat textFormat = new SimpleDateFormat("yyyy-MM-dd");
			
			//set parameters
			cs.setInt(1, x.id);
			
			try {
				cs.setDate(2, new java.sql.Date(textFormat.parse(x.dateStarted).getTime()));
			} catch (java.text.ParseException e) {
				String message = Utility.getBody("insertTournament", e, "Tournament contained an invalid date. Tournament: " + x.name + ", date: " + x.dateStarted);
				String subject = "Error occured in Challonge Elo Parser application!";
				
				Utility.sendEmail(ERROR_ALERT_DESTINATION, ERROR_ALERT_ORIGINATION, subject, message);
				
				//remove for production
				e.printStackTrace();
			}
					
			cs.setString(3, x.name);
			cs.setString(4, x.link);
			
			cs.executeQuery();
			
		} catch (SQLException e) {
			String message = Utility.getBody("insertTournament", e);
			String subject = "Error occured in Challonge Elo Parser application!";
			
			Utility.sendEmail(ERROR_ALERT_DESTINATION, ERROR_ALERT_ORIGINATION, subject, message);
			
			//remove for production
			e.printStackTrace();
		}
	}

	/*
	 * Gets the player name associated with this ID
	 */
	public String getPlayerName(int id) {
		String toReturn = "null";
		
		try {
			CallableStatement cs = conn.prepareCall("{call GetPlayerName(?)}");
			cs.setInt(1, id);
			
			ResultSet rs = cs.executeQuery();
			
			if (rs.next()) {
				toReturn = rs.getString(1);
			}
			
			//else, toReturn already initialized to -1
		} catch (SQLException e) {
			//should be caught by next
			e.printStackTrace();
		}
		
		return toReturn;
	}

	/*
	 * Inserts the given player into the database.
	 * 
	 * REQUIRES:
	 * 	player ID must be unique in the database.
	 */
	public void insertPlayer(Player toAdd) {
		try {
			CallableStatement cs = conn.prepareCall("{call InsertPlayer(?, ?, ?)}");
			
			//set parameters
			cs.setInt(1, toAdd.player_id);
			cs.setString(2, toAdd.name);
			cs.setInt(3, toAdd.elo);
			
			cs.executeQuery();
			
		} catch (SQLException e) {
			String message = Utility.getBody("insertPlayer", e);
			String subject = "Error occured in Challonge Elo Parser application!";
			
			Utility.sendEmail(ERROR_ALERT_DESTINATION, ERROR_ALERT_ORIGINATION, subject, message);
			
			//remove for production
			e.printStackTrace();
		}
		
	}

	/*
	 * Inserts the given alias into the database.
	 */
	public void insertAlias(String name, String alias) {
		try {
			CallableStatement cs = conn.prepareCall("{call InsertAlias(?, ?)}");
			
			//set parameters
			cs.setString(1, name);
			cs.setString(2, alias);
			
			cs.executeQuery();
			
		} catch (SQLException e) {
			String message = Utility.getBody("insertAlias", e);
			String subject = "Error occured in Challonge Elo Parser application!";
			
			Utility.sendEmail(ERROR_ALERT_DESTINATION, ERROR_ALERT_ORIGINATION, subject, message);
			
			//remove for production
			e.printStackTrace();
		}
	}
	
	/*
	 * Method to get the id attached to this master name.
	 * 
	 * If no id is attached to this name, returns -1;
	 */
	public int getPlayerID(String name) {
		int toReturn = -1;
		
		try {
			CallableStatement cs = conn.prepareCall("{call GetPlayerID(?)}");
			cs.setString(1, name);
			
			ResultSet rs = cs.executeQuery();
			
			if (rs.next()) {
				toReturn = rs.getInt(1);
			}
			
			//else, toReturn already initialized to -1
		} catch (SQLException e) {
			//should be caught by next
			e.printStackTrace();
		}
		
		return toReturn;
	}

	/*
	 * Create a new player record in the database, using this name.
	 * 
	 * Allow the player id to be autofilled by the database
	 */
	public void insertPlayerByName(String name) {
		//create a player to invoke the constructor
		
		Player toInsert = new Player();
		toInsert.name = name;
		
		try {
			CallableStatement cs = conn.prepareCall("{call InsertPlayerName(?, ?)}");
			
			//set parameters
			cs.setString(1, toInsert.name);
			cs.setInt(2, toInsert.elo);
			
			cs.executeQuery();
			
		} catch (SQLException e) {
			String message = Utility.getBody("insertPlayerByName", e);
			String subject = "Error occured in Challonge Elo Parser application!";
			
			Utility.sendEmail(ERROR_ALERT_DESTINATION, ERROR_ALERT_ORIGINATION, subject, message);
			
			//remove for production
			e.printStackTrace();
		}
	}

	/*
	 * Given a name, check all the names and aliases to find the true player ID for this record.
	 * 
	 * Returns -1 if this ID is never found
	 */
	public int getIDFromAlias(String name) {
		int id = getPlayerID(name);
		
		if (id == -1) {
			String trueName = getNameFromAlias(name); //query the alias database for the name attached to this
			
			if (!Utility.isNull(trueName)) {
				id = getPlayerID(trueName);
			}
			
			//else do nothing, id remains -1
		}
		
		//else do nothing, return the original id
		
		return id;
	}

	/*
	 * Get the first name associated with the given alias in the database.
	 * 
	 * Returns "null" if nothing found.
	 */
	public String getNameFromAlias(String alias) {
		String toReturn = "null";
		
		try {
			CallableStatement cs = conn.prepareCall("{call GetNameFromAlias(?)}");
			cs.setString(1, alias);
			
			ResultSet rs = cs.executeQuery();
			
			if (rs.next()) {
				toReturn = rs.getString(1);
			}
			
			//else, toReturn already initialized to -1
		} catch (SQLException e) {
			//should be caught by next
			e.printStackTrace();
		}
		
		return toReturn;
	}
	
	/*
	 * Wipes and recreates all tables.
	 * 
	 * WARNING - ALL DATA WILL BE LOST
	 */
	public void wipeTables() {		
		try {
			CallableStatement cs = conn.prepareCall("{call CreateTables()}");
			
			cs.executeQuery();
		} catch (SQLException e) {
			String message = Utility.getBody("wipeTables", e, "Perhaps the stored procedure doesn't exist?");
			String subject = "Error occured in Challonge Elo Parser application!";
			
			Utility.sendEmail(ERROR_ALERT_DESTINATION, ERROR_ALERT_ORIGINATION, subject, message);
			
			//remove for production
			e.printStackTrace();
		}
	}
	
	/*
	 * Create a new placing record in the database.
	 */
	public void insertPlacing(int player_id, int t_id, int final_placing) {
		try {
			CallableStatement cs = conn.prepareCall("{call InsertPlacing(?, ?, ?)}");
			
			//set parameters
			cs.setInt(1, player_id);
			cs.setInt(2, t_id);
			cs.setInt(3, final_placing);
			
			cs.executeQuery();
			
		} catch (SQLException e) {
			String message = Utility.getBody("insertPlacing", e);
			String subject = "Error occured in Challonge Elo Parser application!";
			
			Utility.sendEmail(ERROR_ALERT_DESTINATION, ERROR_ALERT_ORIGINATION, subject, message);
			
			//remove for production
			e.printStackTrace();
		}
	}

	/*
	 * Create a new match record in the database.
	 */
	public void insertMatch(Match m) {
		try {
			CallableStatement cs = conn.prepareCall("{call InsertMatch(?, ?, ?, ?, ?, ?)}");
			
			//set parameters
			cs.setInt(1, m.match_id);
			cs.setInt(2, m.winner_id);
			cs.setInt(3, m.loser_id);
			cs.setInt(4, m.winner_score);
			cs.setInt(5, m.loser_score);
			cs.setInt(6, m.tourney_id);
			
			cs.executeQuery();
			
		} catch (SQLException e) {
			String message = Utility.getBody("insertMatch", e);
			String subject = "Error occured in Challonge Elo Parser application!";
			
			Utility.sendEmail(ERROR_ALERT_DESTINATION, ERROR_ALERT_ORIGINATION, subject, message);
			
			//remove for production
			e.printStackTrace();
		}
	}

	/*
	 * Record the elo value associated with this player_id
	 */
	public int getElo(int id) {
		int toReturn = -1;
		
		try {
			CallableStatement cs = conn.prepareCall("{call GetElo(?)}");
			cs.setInt(1, id);
			
			ResultSet rs = cs.executeQuery();
			
			if (rs.next()) {
				toReturn = rs.getInt(1);
			}
			
			//else, toReturn already initialized to -1
		} catch (SQLException e) {
			//should be caught by next
			e.printStackTrace();
		}
		
		return toReturn;
	}
	
	/*
	 * Record the elo value associated with this player_id
	 */
	public void setElo(int id, int newElo) {
		try {
			CallableStatement cs = conn.prepareCall("{call SetElo(?, ?)}");
			cs.setInt(1, id);
			cs.setInt(2, newElo);
			
			cs.executeQuery();
			
		} catch (SQLException e) {
			String message = Utility.getBody("setElo", e);
			String subject = "Error occured in Challonge Elo Parser application!";
			
			Utility.sendEmail(ERROR_ALERT_DESTINATION, ERROR_ALERT_ORIGINATION, subject, message);
			
			//remove for production
			e.printStackTrace();
		}
	}

	/*
	 * Gets an array of Player objects who have no match records associated with
	 * their relevant player_id in the match table of the database.
	 */
	public Player[] getEmptyPlayers() {
		Player[] toReturn = new Player[] {};
		
		try {
			CallableStatement cs = conn.prepareCall("{call GetEmptyPlayers()}");
			
			ResultSet rs = cs.executeQuery();
			
			ArrayList<Player> tempList = new ArrayList<>();
			
			while (rs.next()) {
				Player p = new Player();
				
				p.player_id = rs.getInt(1);
				p.elo = rs.getInt(2);
				p.name = rs.getString(3);
				
				tempList.add(p);
			}
			
			toReturn = tempList.toArray(toReturn);
			
			//else, toReturn already initialized to -1
		} catch (SQLException e) {
			//should be caught by next
			e.printStackTrace();
		}
		
		return toReturn;
	}
	
	/*
	 * Get an array of every alias attached to the given name present in the aliases datatable.
	 */
	public String[] getAliases(String name) {
		String[] toReturn = new String[] {};
		
		try {
			CallableStatement cs = conn.prepareCall("{call GetAliases(?)}");
			cs.setString(1, name);
			
			ResultSet rs = cs.executeQuery();
			
			ArrayList<String> tempList = new ArrayList<>();
			
			while (rs.next()) {
				tempList.add(rs.getString(1));
			}
			
			toReturn = tempList.toArray(toReturn);
			
			//else, toReturn already initialized to -1
		} catch (SQLException e) {
			//should be caught by next
			e.printStackTrace();
		}
		
		return toReturn;
	}

	/*
	 * Removes a player from the player database.
	 * 
	 * This will also remove any associate match records, as well as any placing records.
	 * 
	 * Removes any trace of a player ever playing -- except elo. Elo is not updated, and cannot be updated, short of a total
	 * recalculation.
	 */
	public void deletePlayer(int player_id) {
		try {
			//these are not exposed individually
			//if they were, possible that theres leftover match records with no players attached..
			CallableStatement cs = conn.prepareCall("{call DeletePlayer(?)}");
			CallableStatement cs1 = conn.prepareCall("{call DeletePlacings(?)}");
			CallableStatement cs2 = conn.prepareCall("{call DeleteMatches(?)}");
			
			cs.setInt(1, player_id);
			cs1.setInt(1, player_id);
			cs2.setInt(1, player_id);
			
			cs.executeQuery();
			cs1.executeQuery();
			cs2.executeQuery();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/*
	 * Return every match, in an array, with the given playerId
	 */
	public Match[] getMatches(int playerID) {
		Match[] toReturn = new Match[] {};
		
		try {
			CallableStatement cs = conn.prepareCall("{call getMatches(?)}");
			cs.setInt(1, playerID);
			
			ResultSet rs = cs.executeQuery();
			
			ArrayList<Match> tempList = new ArrayList<>();
			
			while (rs.next()) {
				Match m = new Match();
				
				//creating match from results
				m.match_id = rs.getInt(1);
				m.winner_id = rs.getInt(2);
				m.loser_id = rs.getInt(3);
				m.winner_score = rs.getInt(4);
				m.loser_score = rs.getInt(5);
				m.tourney_id = rs.getInt(6);
				
				tempList.add(m);
			}
			
			toReturn = tempList.toArray(toReturn);
			
			//else, toReturn already initialized to -1
		} catch (SQLException e) {
			//should be caught by next
			
			//if something breaks, empty array is still returned
			e.printStackTrace();
		}
		
		return toReturn;
	}

	/*
	 * Switch every instance of the oldId to the playerId provided.
	 * 
	 * This will also delete the player record of the oldId once everything has been switched.
	 * 
	 * Sort of merges all records from oldId into playerID
	 */
	public void updatePlayerId(int oldId, int playerID) {
		try {
			CallableStatement cs = conn.prepareCall("{call UpdatePlayerId(?, ?)}");
			cs.setInt(1, oldId);
			cs.setInt(2, playerID);
			
			cs.executeQuery();
			
			//once all associated records are fixed, remove the old player
			//player cannot be fixed with rest of updates, will result in duplicate id
			deletePlayer(oldId);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/*
	 * Force every alias record that referred to oldName before, now to refer to newName
	 */
	public void updateAliasReference(String oldName, String newName) {
		try {
			CallableStatement cs = conn.prepareCall("{call UpdateAliasReferences(?, ?)}");
			cs.setString(1, oldName);
			cs.setString(2, newName);
			
			cs.executeQuery();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
