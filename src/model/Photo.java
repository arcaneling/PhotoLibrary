package model;

import java.util.*;
import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;

/**
 * Handles operations related to the Photo object
 * @author Jason Mooney (jcm370)
 * @author Crystal Zhang (cz298)
 * 
 */

public class Photo implements Serializable {
	/** file address to the current Photo */
	public String address;
	/** time that the current Photo was last modified at */
	public Calendar time = null;
	/** caption of the current Photo */
	public String caption;
	/** all Tags on the current Photo */
	public LinkedList<Tag> tags;
	
	/**
	 * Constructor for a new Photo, generates empty caption and Tags
	 * @param location the file address corresponding to the new Photo
	 */
	public Photo (String location){
		address = location;
		caption = "";
		tags = new LinkedList<Tag>();
		time = getLastModified();
	}
	
	/**
	 * Finds the time the current Photo was last modified on
	 * @return when the current Photo was last modified 
	 */
	public Calendar getLastModified() {
		File dir = new File(address);
		Calendar t = Calendar.getInstance();
		if (dir.exists()) {
			t.setTimeInMillis(dir.lastModified());
		}
		else {
			t.set(Calendar.MILLISECOND,0);
		}
		return t;
	}
	
	/**
	 * Returns the current Photo's file address
	 * @return the current Photo's file address
	 */
	public String getAddress() {
		return address;
	}
	
	/**
	 * Returns the current Photo's caption
	 * @return the current Photo's caption
	 */
	public String getCaption() {
		return caption;
	}
	
	/**
	 * Captions/recaptions the current Photo
	 * @param words the new caption for the Photo
	 */
	public void setCaption(String words) {
		caption = words;
	}
	
	/**
	 * Adds a new Tag to the current Photo
	 * @param x the new Tag being added
	 */
	public void addTag(Tag x) {
		tags.add(x);
	}
	
	/**
	 * Delets an existing Tag from the current Photo
	 * @param x the Tag being deleted
	 */
	public void deleteTag(Tag x) {
		tags.remove(x);
	}
	
	/**
	 * Converts the Calendar object into a MM/dd/yyyy String
	 * @return a String of the current Photo's time
	 */
	public String getDate() {
		SimpleDateFormat format1 = new SimpleDateFormat("MM/dd/yyyy");
		return format1.format(time.getTime());
	}
	
	/**
	 * Returns when the Photo was last modified
	 * @return when the Photo was last modified
	 */
	public Calendar getTime () {
		return time;
	}
	
	/**
	 * Checks if the Photo contains a Tag matching the given key value pair
	 * @param dict a key value pair 
	 * @return whether the Photo contains a Tag matching the key value pair
	 */
	public boolean contains(String[] dict) {
		for (Tag tg: tags) {
			if (tg.tname.equals(dict[0]) && tg.tvalue.equals(dict[1])) return true;
		}
		return false;
	}
	
	/**
	 * Checks if the Photo contains a Tag matching the given Tag
	 * @param x a Tag being searched for
	 * @return whether the Photo contains a Tag matching the given Tag
	 */
	public boolean hasTag(Tag x) {
		for (int i = 0; i < tags.size();i++) {
			if(tags.get(i).equals(x)) {
				return true;
			}
		}
		
		return false;
	}
}
