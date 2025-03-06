/opt/bitnami/spark/bin/spark-submit \
  --class it.unisa.diem.hpc.spark.SparkDriver \
  --master spark://spark-master:7077 \
  --deploy-mode client \
  --supervise \
  --executor-memory 1G \
  ./Project_big_data.jar \
  ./Input/airbnb2.csv ./results/cluster
