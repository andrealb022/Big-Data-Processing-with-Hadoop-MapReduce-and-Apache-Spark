put MapReduce folder into data folder in the cluster.

(ON MASTER BASH)
hdfs namenode -format
$HADOOP_HOME/sbin/start-dfs.sh  
$HADOOP_HOME/sbin/start-yarn.sh  

START APPLICATION
cd /data/MapReduce
hdfs dfs -put Input/airbnb2.csv hdfs:///input 
hadoop jar Project_mapreduce.jar /input /output 

CHECK OUTPUT
hdfs dfs -cat /output/part-r-00000  (print the output)
hdfs dfs -get /output output (load the output file locally)

CLEAN FILES
hdfs dfs -rm -r hdfs:///input
hdfs dfs -rm -r hdfs:///output

STOP CLUSTER
$HADOOP_HOME/sbin/stop-dfs.sh
$HADOOP_HOME/sbin/stop-yarn.sh