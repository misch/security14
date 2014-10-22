import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

public class LDAPClient {

	public static void main (String args[]){
		Hashtable env = new Hashtable();
		env.put(Context.INITIAL_CONTEXT_FACTORY,"com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.PROVIDER_URL, "ldap://54.68.0.145");
		env.put(Context.SECURITY_AUTHENTICATION,"simple");
		env.put(Context.SECURITY_PRINCIPAL,"cn=admin,dc=security,dc=ch"); // specify the username
		env.put(Context.SECURITY_CREDENTIALS,"security20142014!");           // specify the password

		DirContext ctx;
		try {
			ctx = new InitialDirContext(env);
			Attributes attrs = ctx.getAttributes("");
			System.out.println(ctx.lookup("ou=tutor").toString());
		} catch (NamingException e) {
			e.printStackTrace();
		}
	}
}
