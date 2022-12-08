package model;

import java.io.Serializable;
import java.util.LinkedList;

/**
 * Handles operations related to the User object
 * @author Jason Mooney (jcm370)
 * @author Crystal Zhang (cz298)
 * 
 */

public class User implements Serializable{
	/** name of current User */
	public String name;
	/** list of current User's Albums */
	public LinkedList<Album> albums;
	
	/**
	 * Constructor for new User, generates an empty list of Albums
	 * @param username the name of the new User
	 */
	public User(String username) {
		name = username;
		albums = new LinkedList<Album>();
	}
	
	/**
	 * Checks if the User owns an Album of the given name
	 * @param str the name of the Album being searched for
	 * @return whether User has an Album matching the given name
	 */
	public boolean contains(String str) {
		for (Album ab : albums) {
			if (ab.name.equals(str)) return true;
		}
		return false;
	}
	
	/**
	 * Adds new Album to the User's Album list
	 * @param a new Album to be added
	 */
	public void addAlbum(Album a) {
		albums.add(a);
	}
	
	/**
	 * Removes existing Album from the User's Album list
	 * @param a the Album to be removed
	 */
	public void removeAlbum(Album a) {
		albums.remove(a);
	}
	
	/**
	 * Returns an instance of an Album with the given name
	 * @param str the name of the Album being searched for
	 * @return an instance of the Album from the User's Album list
	 */
	public Album getAlbum(String str) {
		for (Album ab : albums) {
			if (ab.name.equals(str)) return ab;
		}
		return null;
	}
	
	/**
	 * Returns all the Albums that the User currently owns
	 * @return a list of Albums owned by the User
	 */
	public LinkedList<Album> getAlbums() {
		return albums;
	}
	
	/**
	 * Renames an instance of the given Album 
	 * @param curr the Album being renamed
	 * @param newName the new name of the Album
	 */
	public void renameAlbum(Album curr, String newName) {
		for (Album ab : albums) {
			if (ab.name.equals(curr.name)) {
				ab.name = newName;
				return;
			}
		}
	}
	
	/**
	 * toString for the admin's User list
	 */
	@Override
	public String toString() {
		return name;
	}
}
