#!/bin/bash
input_path="/user/fuxiaofeng/webcontent/input"
xpathout_path="/user/fuxiaofeng/webcontent/xpathoutput"
contentout_path="/user/fuxiaofeng/webcontent/contentout"
log_path="/home/fuxiaofeng/extract.log"
hadoop_path=`which hadoop`
jar_path="/home/fuxiaofeng/webcontent-extract-1.0-SNAPSHOT.jar"

echo "$hadoop_path"

# 提取网页正文的xpath
nohup $hadoop_path jar $jar_path extractxpath -Dinput.path=$input_path \
-Doutput.path=$xpathout_path >> $log_path 2>&1 &

# 统计满足可用的content_xpath
nohup $hadoop_path jar $jar_path countxpath >> $log_path 2>&1 &

# 根据可用的xpath提取爬取网页的内容
nohup $hadoop_path jar $jar_path extractcontent -Dinput.path=$input_path \
-Doutput.path=$contentout_path >> $log_path 2>&1 &
