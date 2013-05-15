#!/bin/sh
#-----------------------------------------------------------------------------
# Paths needed for processing
##############################
FREQUENT_WORDS_FILE=/media/tejas/Trunk/SkyDrive/277/Kaggle/data/train/reduced/unigrams.4.5/top.words.txt

LOCAL_FS_BASE=/media/tejas/Trunk/SkyDrive/277/Kaggle/data
TRAIN_REDUCED=$LOCAL_FS_BASE/train/reduced
TEST_MERGED=$LOCAL_FS_BASE/test/merged

HADOOP_FS_BASE=/user/tejas/kaggle
HDFS_TRAIN_REDUCED=$HADOOP_FS_BASE/training/reduced
HDFS_TEST_MERGED=$HADOOP_FS_BASE/test/merged

HADOOP_HOME=/home/tejas/Desktop/apache/hadoop-1.1.2
HADOOP_CONF_DIR=$HADOOP_HOME/conf
PATH=$PATH:$HADOOP_HOME/bin
#-----------------------------------------------------------------------------
convertData() {
  CLASSPATH=lib/log4j-1.2.15.jar:lib/json-simple-1.1.1.jar:lib/lukeall-4.0.0-ALPHA.jar:lib/data.processing.jar
  java -cp $CLASSPATH com.tp.review.TrainData -bindex $LOCAL_FS_BASE/train/indexes/business.index -uindex $LOCAL_FS_BASE/train/indexes/user.index -rindex $LOCAL_FS_BASE/train/indexes/review.index -freqwords $FREQUENT_WORDS_FILE -input $LOCAL_FS_BASE/train/original/review.json -output $LOCAL_FS_BASE/train/reduced/review.json
  java -cp $CLASSPATH com.tp.review.TestData  -bindex $LOCAL_FS_BASE/test/indexes/business.index -uindex $LOCAL_FS_BASE/test/indexes/user.index -rindex $LOCAL_FS_BASE/test/indexes/review.index -freqwords $FREQUENT_WORDS_FILE -input  $LOCAL_FS_BASE/test/original/review.json -output $LOCAL_FS_BASE/test/merged/review.json
}
#-----------------------------------------------------------------------------
resetHDFS() {
  hadoop dfs -rmr $HDFS_TRAIN_REDUCED/review.json
  hadoop dfs -rmr $HDFS_TRAIN_REDUCED/joined_data*
  hadoop dfs -rmr $HDFS_TEST_MERGED/review.json
  hadoop dfs -rmr $HDFS_TEST_MERGED/joined_data*

  hadoop dfs -put $TRAIN_REDUCED/review.json $HDFS_TRAIN_REDUCED/
  hadoop dfs -put $TEST_MERGED/review.json $HDFS_TEST_MERGED/
}
#-----------------------------------------------------------------------------
executeJoin() {
  # Generate the pig sciript for the updated tables
  # start off with tables related to training dataset
  rm join_script.pig && touch join_script.pig

  printf "%b" "business = load '$1/business.json' using " >> join_script.pig
  printf "%b" "JsonLoader('business_id:chararray, " >> join_script.pig
  printf "%b" "open:chararray, category:chararray, " >> join_script.pig
  printf "%b\n" "review_count:chararray, stars:float');" >> join_script.pig

  printf "%b" "user = load '$1/user.json' using JsonLoader" >> join_script.pig
  printf "%b" "('user_id:chararray, useful_votes:chararray," >> join_script.pig
  printf "%b\n" " review_count:chararray, stars:float');" >> join_script.pig

  printf "%b" "review = load '$1/review.json' using JsonLoader('" >> join_script.pig
  printf "%b" "review_id:chararray, business_id:chararray, user_id:chararray," >> join_script.pig
  printf "%b" "useful_votes:chararray, stars:chararray, age:chararray, text_wc:chararray " >> join_script.pig
  while read line 
  do 
      printf "%b" ", $line:chararray" >> join_script.pig 
  done < $FREQUENT_WORDS_FILE
  printf "%b\n" "');" >> join_script.pig

  printf "%b\n" "review_user = JOIN review BY user_id, user BY user_id;" >> join_script.pig
  printf "%b\n" "business_review_user = JOIN review_user BY business_id, business BY business_id;" >> join_script.pig

  printf "%b" "temp = foreach business_review_user generate review_user::review::review_id as review_id, " >> join_script.pig
  printf "%b" "review_user::review::business_id as business_id, review_user::review::user_id as user_id, " >> join_script.pig
  printf "%b" "review_user::review::useful_votes as review_useful_votes, " >> join_script.pig
  printf "%b" "review_user::review::stars as review_stars, review_user::review::age as review_age, " >> join_script.pig
  printf "%b" "review_user::review::text_wc as review_text_wc, review_user::user::useful_votes as user_useful_votes, " >> join_script.pig
  printf "%b" "review_user::user::review_count as user_review_count, review_user::user::stars as user_stars, " >> join_script.pig
  printf "%b" "business::open as business_open, business::category as category, business::review_count as review_count, " >> join_script.pig
  printf "%b" "business::stars as business::stars" >> join_script.pig
  while read line 
  do 
      printf "%b" ", review_user::review::$line as $line" >> join_script.pig
  done < $FREQUENT_WORDS_FILE
  printf "%b\n" ";" >> join_script.pig

  printf "%b\n" "STORE temp INTO '$1/joined_data' USING PigStorage (',');" >> join_script.pig

  # Time to run the pig script
  $PIG_HOME/bin/pig join_script.pig

  rm -rf $2.joined.data.tmp
  hadoop dfs -get $1/joined_data/part-r-00000 $2.joined.data.txt
  rm join_script.pig
}
#-----------------------------------------------------------------------------
# Main body of script starts here
##################################
HDFS_STATUS=`jps | grep "DataNode" | wc -l`

if test $HDFS_STATUS -eq 0 
then
    rm -rf $HADOOP_HOME/logs
    sh $HADOOP_HOME/bin/start-all.sh
fi

echo "Converting data..."
convertData

echo "Resetting HDFS..."
resetHDFS

echo "Training set join..."
executeJoin $HDFS_TRAIN_REDUCED "train"

echo "Test set join..."
executeJoin $HDFS_TEST_MERGED "test"
sed -i 's/,,/,0,/g' test.joined.data.txt
sed -i 's/,,/,0,/g' test.joined.data.txt 

HDFS_STATUS=`jps | grep "DataNode" | wc -l`

if test $HDFS_STATUS -eq 1
then
    sh $HADOOP_HOME/bin/stop-all.sh
fi
#-----------------------------------------------------------------------------

