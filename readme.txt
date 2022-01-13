to run the client:
	1. open terminal in client folder
	2. enter the command make
	3. run the client with ./bin/BGSclient host port
where host is the ip of the server, and port is the port of the server
use 127.0.0.1 if the server is on the same computer (localhost)

to run the server:
	1. open terminal is server folder
	2. run "mvn clean compile"
	3. run the reactor server with:
mvn exec:java -Dexec.mainClass="bgu.spl.net.impl.BGSServer.ReactorMain" -Dexec.args="<port> <Num of threads>"
	4. run the thread per client server with :
mvn exec:java -Dexec.mainClass="bgu.spl.net.impl.BGSServer.TPCMain" -Dexec.args="<port>"

Birthday is formmated as DD-MM-YYYY, for example 02-10-1999

We store filtered words inside package bgu.spl.net.impl.BGSServer.DataBase, at one of the private fields named filtered_words

