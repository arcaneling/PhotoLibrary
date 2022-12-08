package model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Handles operations related to the Album object
 * @author Jason Mooney (jcm370)
 * @author Crystal Zhang (cz298)
 * 
 */

public class Album implements Serializable {
	/** name of current Album */
	public String name;
	/** list of current Album's Photos */
	public LinkedList<Photo> photos;
	
	/**
	 * Constructor for new Album, generates an empty list of Photos
	 * @param n the name of the new Album
	 */
	public Album (String n) {
		name = n;
		photos = new LinkedList<Photo>();
	}
	
	/**
	 * Returns the current Album's name
	 * @return the current Album's name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Returns a Photo matching the given file address
	 * @param adr a file address
	 * @return an instance of a Photo containing the address, null otherwise
	 */
	public Photo getPhoto(String adr) {
		for (Photo ph : photos) {
			if (ph.address.equals(adr)) return ph;
		}
		return null;
	}
	
	/**
	 * Returns all Photos inside of the Album
	 * @return a list of Photos contained in the Album
	 */
	public LinkedList<Photo> getPhotos() {
		return photos;
	}
	
	/**
	 * Adds the given Photo to the current Album
	 * @param x the Photo being added
	 */
	public void addPhoto(Photo x) {
		if (!photos.contains(x)) {
		photos.add(x);
		}
	}
	
	/**
	 * Checks if the Album contains a Photo matching the given file address
	 * @param adr a file address
	 * @return whether the Album contains a Photo matching the file address
	 */
	public boolean contains(String adr) {
		for (Photo ph : photos) {
			if (ph.address.equals(adr)) return true;
		}
		return false;
	}
	
	/**
	 * Removes the given Photo from the Album
	 * @param x the Photo being removed
	 */
	public void removePhoto(Photo x) {
		photos.remove(x);
	}
	
	/**
	 * Copies a Photo from the current Album to another
	 * @param x the Photo being copied
	 * @param y the destination Album
	 */
	public void copyPhoto(Photo x, Album y) {
		y.addPhoto(x);
	}
	
	/**
	 * Moves a Photo from the current Album to another
	 * @param x the Photo being moved
	 * @param y the destination Album
	 */
	public void movePhoto(Photo x, Album y) {
		photos.remove(x);
		y.addPhoto(x);
	}
	
	/**
	 * Returns the oldest created Photo inside the current Album
	 * @return an instance of the oldest Photo
	 */
	public Photo getEarliest() {
		if (photos.size() == 0) return null;
		Photo min = photos.get(0);
		
		for (Photo ph: photos) {
			if (min.time.compareTo(ph.time) > 0) min = ph;
		}
		
		return min;
	}
	
	/**
	 * Returns the newest created Photo inside the current Album
	 * @return an instance of the newest Photo
	 */
	public Photo getLatest() {
		if (photos.size() == 0) return null;
		Photo max = photos.get(0);
		
		for (Photo ph: photos) {
			if (max.time.compareTo(ph.time) < 0) max = ph;
		}
		
		return max;
	}
	
	/**
	 * Finds all Photos in between two given dates
	 * @param start the lower bound date
	 * @param end the upper bound date
	 * @return an Album containing all Photos within bounds
	 */
	public Album searchDate(Calendar start, Calendar end) {
		Album temp = new Album("temp");
		for (int i = 0; i< photos.size(); i++) {
			if (photos.get(i).getTime().after(start) && photos.get(i).getTime().before(end)) {
				temp.addPhoto(photos.get(i));
			}
		}
		return temp;
	}
	
	/**
	 * Finds all Photos that have a given Tag
	 * @param x the Tag being searched on
	 * @return an Album containing all Photos with the Tag
	 */
	public Album searchTag(Tag x) {
		Album temp = new Album("temp");
		for (int i = 0; i< photos.size(); i++) {
			if (photos.get(i).hasTag(x)) {
			temp.addPhoto(photos.get(i));
			}
		}
		return temp;
	}
	
	/**
	 * Finds all Photos that have both Tag x AND Tag y
	 * @param x the first Tag being searched on
	 * @param y the second Tag being searched on
	 * @return an Album containing all Photos with both Tags
	 */
	public Album searchConjunctive(Tag x, Tag y) {
		Album temp = new Album("temp");
		for (int i = 0; i< photos.size(); i++) {
			if (photos.get(i).hasTag(x) && photos.get(i).hasTag(y)) {
			temp.addPhoto(photos.get(i));
			}
		}
		return temp;
	}
	
	/**
	 * Finds all Photos that have either Tag x OR Tag y
	 * @param x the first Tag being searched on
	 * @param y the second Tag being searched on
	 * @return an Album containing all Photos with one of the given Tags
	 */
	public Album searchDisjunctive (Tag x, Tag y) {
		Album temp = new Album("temp");
		for (int i = 0; i< photos.size(); i++) {
			if (photos.get(i).hasTag(x) || photos.get(i).hasTag(y)) {
			temp.addPhoto(photos.get(i));
			}
		}
		return temp;
	}
	
	/**
	 * toString for User's Album list
	 */
	@Override
	public String toString() {
		String str = name;
		int size = photos.size();
		Photo min = getEarliest();
		Photo max = getLatest();
		
		SimpleDateFormat format1 = new SimpleDateFormat("MM/dd/yyyy");
		String earliest = (min == null) ? "N/A" : format1.format(min.time.getTime());
		String latest = (max == null) ? "N/A" : format1.format(max.time.getTime());
		
		return str + " | " + size + " | " + earliest + " - " + latest;
		
	}
	
}