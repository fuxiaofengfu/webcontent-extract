drop table crawler_content_xpath if exists;
CREATE TABLE `crawler_content_xpath` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `webhosts` VARCHAR(200) COMMENT 'xpath的host',
  `content_xpath` VARCHAR(1024) COMMENT '内容xpath',
  `md5_xpath` VARCHAR(1024) COMMENT '内容xpath的md5值',
  `status` TINYINT(20) DEFAULT '0' COMMENT '{0:正常,1:失效}',
  `create_time` datetime DEFAULT now() COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 comment '扫描出的xpath';

drop table `crawler_content_xpath_use` if exists;
CREATE TABLE `crawler_content_xpath_use` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `webhosts` VARCHAR(200)  COMMENT 'xpath的host',
  `content_xpath` VARCHAR(1024)  COMMENT '内容xpath',
  `source` tinyint default 0 comment '{0:program,1:manual}',
  `status` bigint(20) DEFAULT '0' COMMENT '{0:正常,1:失效}',
  `create_time` datetime DEFAULT now() COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 comment '可使用的xpath';


drop table crawler_content_xpath_report if exists;
CREATE TABLE `crawler_content_xpath_report` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `webhosts` VARCHAR(100)  COMMENT 'xpath的url',
  `scan` bigint(20) DEFAULT '0' COMMENT '成功提取xpath数量',
  `total` bigint(20) DEFAULT '0' COMMENT '总数',
  `rate` float default 0 comment '比例',
  `create_time` datetime DEFAULT now() COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 comment '可使用的xpath';
