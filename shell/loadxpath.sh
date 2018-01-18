#!/bin/bash
# 加载解析的xpath数据到crawler_content_xpath表中
mysql="/usr/local/mysql/bin/mysql"
xpath="/Users/xiaofengfu/Documents/ideaworkspace/webcontent-extract/contentout/part-m-00000"
cmd="$mysql -h192.168.88.195 -P5506 -umysql -p12345678 -Dhainiureport -e \
\"load data local infile '$xpath' into table crawler_content_xpath \
(id,webhosts,content_xpath,md5_xpath,url,md5_url,create_time)\""
echo $cmd
eval $cmd
