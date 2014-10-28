import javax.naming.*;
import javax.naming.directory.*;

public class LDAPClient {

	private static String BASE_NAME =  "ou=students, dc=security, dc=ch";
	private DirContext context;
	
	
	public LDAPClient(DirContext context){
		this.context = context;
	}
	
	/**
	 * This method can be used to add a person.
	 * @param cn String: used to identify the entry within students.security.ch
	 * @param description String: content of the attribute "description"
	 */
	public void add(String cn, String description){
		   Attributes attributes = new BasicAttributes(true);
		   attributes.put("objectClass","person");
		   attributes.put("sn","Freddy");
		   attributes.put("cn",cn);
		   attributes.put("description",description);
		
			try {
				context.bind("cn="+cn+", " + BASE_NAME, null, attributes);
			} catch (NamingException e) {
				System.out.println("Could not add entry.");
				e.printStackTrace();
			}
	}
	
	/**
	 * This method can be used to change the values of existing entries.
	 * @param cn String: used to identify the entry within students.security.ch
	 * @param attributeName String: name of the attribute that wants to be modified
	 * @param newValue String: new value of the attribute
	 */
	public void modify(String cn, String attributeName, String newValue){
		ModificationItem[] modification = {new ModificationItem(DirContext.REPLACE_ATTRIBUTE, new BasicAttribute(attributeName,newValue))};   
		
		try {
			context.modifyAttributes("cn="+cn+", " + BASE_NAME, modification);
		} catch (NamingException e) {
			System.out.println("Could not add entry.");
			e.printStackTrace();
		}
	}
	
	/**
	 * This method can be used to remove an entry.
	 * @param cn String: used to identify the entry within students.security.ch
	 */
	public void remove(String cn){
		try {
			context.unbind("cn="+cn+", " + BASE_NAME);
		} catch (NamingException e) {
			System.out.println("Could not remove entry.");
			e.printStackTrace();
		}
	}
	
	/**
	 * This method performs a simple username search.
	 * @param cn - a string (can also contain some regex-stuff)
	 * @return string with the found values
	 */
	public String search(String cn){
		String searchResult = "";
		
		try {
			// Set search scope
			SearchControls ctls = new SearchControls();
			ctls.setSearchScope(SearchControls.SUBTREE_SCOPE);
			
			// Set the filter 
			String filter = "cn="+cn;
			
			// Perform search
			NamingEnumeration<SearchResult> answer = context.search(BASE_NAME, filter, ctls);
			
			// Put all the answers into a string.
			while(answer.hasMore()){
				   searchResult += answer.next().toString()+"\n";
			}
		} catch (NamingException e) {
			e.printStackTrace();
		}
		
		if (searchResult.isEmpty()){
			searchResult = "Sorry. Entity " + cn + " not found.";
		}
		return searchResult;
	}
}
