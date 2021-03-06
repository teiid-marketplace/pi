# PI
A Teiid translator Osisoft's PI Database using JDBC Driver.

To build use maven > 3.0 version and Java 1.7 later and execute

```
git clone https://github.com/teiid-marketplace/pi.git
cd pi/translator-pi
mvn clean install -s settings.xml
```

- This will produce a file like translator-pi-1.0.0-SNAPSHOT-jboss-as7-dist.zip in the "target" directory under "translator-pi", extract this file in over Teiid or DV install on "modules" directory. 
 
- Then edit "standalone-teiid.xml" file, under "teiid" subsystem add following line
```
<translator name="osisoft-pi" module="org.jboss.teiid.translator.pi"/>
```
- save restart the server.

- Now in your VDB, use "osisoft-pi" as the translator name and issue queries. For example, in Dynamic VDB

pi-vdb.xml
```
<vdb name="pidb" version="1">
    <model visible="true" name="accounts">
        <source name="development" translator-name="osisoft-pi"/> 
    </model>    
</vdb>
```
