import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;


public class TryClient {
	public static void main(String args[]){
		DirContext ctx = setupContext();

		LDAPClient ldapClient = new LDAPClient(ctx);
		

		
//		ldapClient.add("freddy","I am Freddy. I'm not too smart, sadly.");
//		System.out.println(ldapClient.search("freddy"));
		
//		ldapClient.addAttribute("freddy", "telephoneNumber", "012 345 67 89");
//		ldapClient.modifyAttribute("freddy", "description", "Oh! I am suuuch a smart guy!");
//		ldapClient.removeAttribute("freddy", "telephoneNumber");
		
//		ldapClient.remove("freddy");
		

		
		// List directory content
		System.out.println(ldapClient.search("*"));

	}
	
	public static DirContext setupContext(){
		Hashtable<String, String> env = new Hashtable<String, String>();
		env.put(Context.INITIAL_CONTEXT_FACTORY,"com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.PROVIDER_URL, "ldap://54.68.0.145");
		env.put(Context.SECURITY_AUTHENTICATION,"simple");
		env.put(Context.SECURITY_PRINCIPAL,"cn=admin,dc=security,dc=ch"); // username
		env.put(Context.SECURITY_CREDENTIALS,"security20142014!");           // password

		
		DirContext context = null;
		try {
			context = new InitialDirContext(env);
		} catch (NamingException e) {
			System.out.println("Could not initialize context.");
		}
		
		return context;
	}
}
