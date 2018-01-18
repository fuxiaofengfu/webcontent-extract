drop table if exists crawler_content_xpath;
CREATE TABLE `crawler_content_xpath` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `webhosts` VARCHAR(200) COMMENT 'xpath的host',
  `content_xpath` VARCHAR(1024) COMMENT '内容xpath',
  `md5_xpath` VARCHAR(100) COMMENT '内容xpath的md5值',
  `url` VARCHAR(1024) UNIQUE NOT NULL COMMENT '获取xpath的url,排重使用',
  `md5_url` VARCHAR(100) UNIQUE NOT NULL COMMENT 'url的md5值,排重使用',
  `status` TINYINT(20) DEFAULT '0' COMMENT '{0:正常,1:失效}',
  `create_time` datetime DEFAULT now() COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 comment '扫描出的xpath';

drop table IF EXISTS `crawler_content_xpath_use`;
-- 允许存在同一个host有多个正文内容的xpath
CREATE TABLE `crawler_content_xpath_use` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `webhosts` VARCHAR(200) NOT NULL COMMENT 'xpath的host',
  `content_xpath` VARCHAR(800) NOT NULL COMMENT '内容xpath',
  `source` tinyint default 0 comment '{0:program,1:manual}',
  `status` bigint DEFAULT '0' COMMENT '{0:正常,1:失效}',
  `create_time` datetime DEFAULT now() COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `hostxpath` (`webhosts`,`content_xpath`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 comment '可使用的xpath';


drop table IF EXISTS crawler_content_xpath_report;
CREATE TABLE `crawler_content_xpath_report` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `webhosts` VARCHAR(100)  COMMENT 'xpath的url',
  `scan` bigint(20) DEFAULT '0' COMMENT '成功提取xpath数量',
  `total` bigint(20) DEFAULT '0' COMMENT '总数',
  `rate` float default 0 comment '比例',
  `create_time` datetime DEFAULT now() COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 comment '可使用的xpath';
