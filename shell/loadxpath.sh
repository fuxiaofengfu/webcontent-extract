#!/bin/bash
# 加载解析的xpath数据到crawler_content_xpath表中
mysql=`which mysql`
xpath="/user/fuxiaofeng/webcontent/xpathoutput/part-m-00000"
local_xpath="/home/fuxiaofeng/webcontent/part-m-00000"
hadoop_path=`which hadoop`

# 下载到本地
$hadoop_path fs -get $xpath $local_xpath

# 执行mysql load
cmd="$mysql -h192.168.88.195 -P5506 -umysql -p12345678 -Dhainiureport -e \
\"load data LOW_PRIORITY local infile '$local_xpath' into table crawler_content_xpath \
(id,webhosts,content_xpath,md5_xpath,url,md5_url,create_time)\""
echo $cmd
eval $cmd
