#!/bin/bash
# 加载解析的xpath数据到crawler_content_xpath表中
mysql=`which mysql`
xpath="/user/fuxiaofeng/webcontent/xpathoutput/part*"
local_xpath="/home/fuxiaofeng/webcontent/"
hadoop_path=`which hadoop`

# 下载到本地
$hadoop_path fs -get $xpath $local_xpath

if test 0 -ne $? ;then
   exit 1
fi
mysql_loadfile="$local_xpath/mysqlload"

for file in `ls $local_xpath/part*`
do
    if test -f $file ;then
        cat $file >> $mysql_loadfile
        rm -f $file
    fi
done

# 执行mysql load
cmd="$mysql -h192.168.88.195 -P5506 -umysql -p12345678 -Dhainiureport -e \
\"load data LOW_PRIORITY local infile '$mysql_loadfile' into table crawler_content_xpath \
(id,webhosts,content_xpath,md5_xpath,url,md5_url,create_time)\""

echo $cmd
eval $cmd
rm -f $mysql_loadfile