package model;

import java.io.Serializable;

/**
 * Handles operations related to the Tag object
 * @author Jason Mooney (jcm370)
 * @author Crystal Zhang (cz298)
 * 
 */

public class Tag implements Serializable {
	/** key for the current Tag */
	public String tname;
	/** value for the current Tag */
	public String tvalue;
	
	/**
	 * Constructor for a new Tag
	 * @param name the key of the Tag
	 * @param value the value of the Tag
	 */
	public Tag (String name, String value) {
		tname = name;
		tvalue = value;
	}
	
	/**
	 * Checks if two Tags are equivalent
	 * @param compare the Tag being compared to the current Tag
	 * @return whether the Tags are equivalent
	 */
	public boolean equals(Tag compare) {
		if (this.tname.equals(compare.tname) && this.tvalue.equals(compare.tvalue)) {
			return true;
		}
		return false;
	}
	
	/**
	 * toString for the Photo's Tag list
	 */
	@Override
	public String toString() {
		return tname + ": " + tvalue;
	}
}