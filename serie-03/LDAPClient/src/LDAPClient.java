
import java.util.Hashtable;
import javax.naming.*;
import javax.naming.directory.*;

public class LDAPClient {

	private static String BASE_NAME =  "ou=students, dc=security, dc=ch";
	
	public static void main (String args[]){
		Hashtable<String, String> env = new Hashtable<String, String>();
		env.put(Context.INITIAL_CONTEXT_FACTORY,"com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.PROVIDER_URL, "ldap://54.68.0.145");
		env.put(Context.SECURITY_AUTHENTICATION,"simple");
		env.put(Context.SECURITY_PRINCIPAL,"cn=admin,dc=security,dc=ch"); // username
		env.put(Context.SECURITY_CREDENTIALS,"security20142014!");           // password

		
		DirContext ctx = null;
		try {
			ctx = new InitialDirContext(env);
		} catch (NamingException e) {
			System.out.println("Could not initialize context.");
		}
		

		LDAPClient ldapClient = new LDAPClient();
		System.out.println(ldapClient.search("*", ctx));
//		ldapClient.remove("freddy", ctx);
//		ldapClient.add("freddy","I am Freddy. I'm not too smart, sadly.",ctx);
//		System.out.println(ldapClient.search("freddy", ctx));
		ldapClient.modify("freddy", "description", "Oh! I am suuuch a smart guy!",ctx);
	}
	
	/**
	 * This method can be used to add a person.
	 * @param name String
	 * @param description String
	 * @param students DirContext
	 */
	public void add(String name, String description, DirContext students){
		   Attributes attributes = new BasicAttributes(true);
		   attributes.put("objectClass","person");
		   attributes.put("sn","Freddy");
		   attributes.put("cn",name);
		   attributes.put("description",description);
		
			try {
				students.bind("cn="+name+", " + BASE_NAME, null, attributes);
			} catch (NamingException e) {
				System.out.println("Could not add entry.");
				e.printStackTrace();
			}
	}
	
	public void modify(String name, String attributeName, String newValue, DirContext ctx){
		ModificationItem[] modification = {new ModificationItem(DirContext.REPLACE_ATTRIBUTE, new BasicAttribute(attributeName,newValue))};   
		
		try {
			ctx.modifyAttributes("cn="+name+", " + BASE_NAME, modification);
		} catch (NamingException e) {
			System.out.println("Could not add entry.");
			e.printStackTrace();
		}
	}
	
	/**
	 * This method can be used to remove a person.
	 * @param name String
	 * @param ctx DirContextit 
	 */
	public void remove(String name, DirContext ctx){
		try {
			ctx.unbind("cn="+name+", " + BASE_NAME);
		} catch (NamingException e) {
			System.out.println("Could not remove entry.");
			e.printStackTrace();
		}
	}
	
	/**
	 * This method performs a simple username search.
	 * @param username - a string (can also contain some regex-stuff)
	 * @param ctx
	 * @return string with the found values
	 */
	public String search(String username, DirContext ctx){
		String searchResult = "";
		
		try {
		    
			// Set search scope
			SearchControls ctls = new SearchControls();
			ctls.setSearchScope(SearchControls.SUBTREE_SCOPE);
			
			// Set the filter 
			String filter = "cn="+username;
			
			// Perform search
			NamingEnumeration<SearchResult> answer = ctx.search(BASE_NAME, filter, ctls);
			
			// Throw all the answers into a string.
			while(answer.hasMore()){
				   searchResult += answer.next().toString()+"\n";
			}
		} catch (NamingException e) {
			e.printStackTrace();
		}
		
		if (searchResult.isEmpty()){
			searchResult = "Sorry. User " + username + " not found.";
		}
		return searchResult;
	}
}
