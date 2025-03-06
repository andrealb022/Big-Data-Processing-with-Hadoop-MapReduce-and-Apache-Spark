/opt/bitnami/spark/bin/spark-submit \
  --class it.unisa.diem.hpc.spark.SparkDriver \
  --master local \
  ./Project_big_data.jar \
  ./Input/airbnb2.csv ./results/local
