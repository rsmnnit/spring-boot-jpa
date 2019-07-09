# spring-boot-jpa and Kafka 
Spring boot application using JPA and MySql Database
Apache Kafka config

# how to setup zookeeper
referecnes -  https://dzone.com/articles/running-apache-kafka-on-windows-os 

set up ZooKeeper home path ->  ZOOKEEPER_HOME = C:\zookeeper-3.4.7 (Directory where zookeeper binary is downloaded)
Edit the System Variable named “Path” and add ;%ZOOKEEPER_HOME%\bin; 
Rename file “zoo_sample.cfg” to “zoo.cfg”
Open zoo.cfg in any text editor, like Notepad; I prefer Notepad++.
Find and edit dataDir=/tmp/zookeeper to :\zookeeper-3.4.7\data  

# Setting Up Kafka
Go to your Kafka config directory. For me its C:\kafka_2.11-0.9.0.0\config
Edit the file “server.properties”
Find and edit the line log.dirs=/tmp/kafka-logs” to “log.dir= C:\kafka_2.11-0.9.0.0\kafka-logs.
If your ZooKeeper is running on some other machine or cluster you can edit “zookeeper.connect:2181” to your custom IP and port. For this demo, we are using the same machine so there's no need to change. Also the Kafka port and broker.id are configurable in this file. Leave other settings as is.
Your Kafka will run on default port 9092 and connect to ZooKeeper’s default port, 2181.

#  once setup is done 
start zookeeper on cmd by command 
# zkserver

start kafka - go to your Kafka installation directory: C:\kafka_2.11-0.9.0.0 and run below command on cmd
# .\bin\windows\kafka-server-start.bat .\config\server.properties
