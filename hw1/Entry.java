//
// DICTIONARY ENTRY
// 
// This class encapsulates a word and its definitions
//
// You may add new methods to this class as you like
// You MAY NOT change existing methods
//
// Modified by Spencer Everett, 9/15/2015

package hw1;

public class Entry {
	public final String word;
	public String[] defns;

	public Entry(String word, String[] defns) {
		this.word = word;
		this.defns = defns;
	}

	public boolean sameWord(Entry e) {
		return (word == e.word);
	}

	public int compareWord(Entry e) {
		return (word.compareTo(e.word));
	}

	public String toString () {
		String entryString = word + ":\n";
		for (int i = 0; i < defns.length; i++) {
			entryString += "\t" + (i + 1) + ". " + defns[i] + "\n";
		}
		return entryString;
	}
	
	public void addDef(String[] defs) {
		String[] newDefns = new String[this.defns.length+defs.length];		
		for (int i=0;i<this.defns.length;i++) {
			newDefns[i] = this.defns[i];
		}		
		for (int j=0;j<defs.length;j++) {
			newDefns[j+this.defns.length] = defs[j];
		}			
		this.defns = newDefns;
		return;
	}
	
	public void delDef(String def) {
		String[] newDefns = new String[this.defns.length-1];
		int ind = 0;
		for (int i=0;i<this.defns.length;i++) {
			if (!this.defns[i].equals(def)) {
				newDefns[ind] = this.defns[i];
				ind++;
			}
		}		
		this.defns = newDefns;
		return;
	}
}