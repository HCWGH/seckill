create table t_user(
`id` BIGINT(20) NOT NULL COMMENT '用户ID,手机号码',
`nickname` VARCHAR(255) NOT NULL,
`password` VARCHAR(32) DEFAULT NULL COMMENT 'MD5加密后的密文',
`salt` VARCHAR(10) DEFAULT NULL,
`register_date` DATETIME DEFAULT NULL COMMENT '注册时间',
`last_login_date` DATETIME DEFAULT NULL COMMENT '最后一次登录的时间',
`login_count` int(11) DEFAULT '0' COMMENT '登录次数',
PRIMARY KEY(`id`))

create table `t_goods`(
`id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '商品ID',
`goods_name` VARCHAR(16) DEFAULT NULL COMMENT '商品名称',
`goods_title` VARCHAR(64) DEFAULT NULL COMMENT '商品标题',
`goods_img` VARCHAR(64) DEFAULT NULL COMMENT '图片地址',
`goods_detail` LONGTEXT COMMENT '商品详情',
`goods_price` DECIMAL(10,2) DEFAULT '0.00' COMMENT '商品价格',
`goods_stock` int(11) DEFAULT '0' COMMENT '商品库存',
PRIMARY KEY(`id`))ENGINE=INNODB AUTO_INCREMENT=3 DEFAULT CHARSET=UTF8MB4

CREATE TABLE `t_order`(
`id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '订单ID',
`user_id` BIGINT(20) DEFAULT NULL COMMENT '用户ID',
`goods_id` BIGINT(20) DEFAULT NULL COMMENT '商品ID',
`delivery_add_id1` BIGINT(20) DEFAULT NULL COMMENT '收货地址ID',
`goods_name` VARCHAR(16) DEFAULT NULL COMMENT '冗余过来的商品名称',
`goods_count` int(11) DEFAULT '0' COMMENT '订单商品数量',
`goods_price` DECIMAL(10,2) DEFAULT '0.00' COMMENT '商品单价',
`order_channel` TINYINT(4) DEFAULT '0' COMMENT '1PC,2安卓,3ios',
`order_status` TINYINT(4) DEFAULT'0' COMMENT '订单状态：0 未支付,1已支付...',
`creaate_date` datetime DEFAULT NULL COMMENT '订单创建时间',
`pay_date` DATETIME DEFAULT NULL COMMENT '支付时间',
PRIMARY KEY(`id`))ENGINE=INNODB AUTO_INCREMENT=12 DEFAULT CHARSET=UTF8MB4

CREATE TABLE `t_seckill_goods`(
`id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT'秒杀商品ID',
`goods_id` BIGINT(20) DEFAULT NULL COMMENT '商品ID',
`seckill_price` DECIMAL(10,2) DEFAULT '0.00' COMMENT '商品的秒杀价格',
`stock_count` int(10) DEFAULT '0' COMMENT '秒杀商品库存数量',
`start_date` DATETIME DEFAULT NULL COMMENT '开始秒杀的时间',
`end_date` DATETIME DEFAULT NULL COMMENT '秒杀结束时间',
PRIMARY KEY(`id`))ENGINE=INNODB AUTO_INCREMENT=3 DEFAULT CHARSET=UTF8MB4

CREATE TABLE `t_seckill_order`(
`id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '秒杀订单的ID',
`user_id` BIGINT(20) DEFAULT NULL COMMENT '用户ID',
`order_id` BIGINT(20) DEFAULT NULL COMMENT '订单ID',
`goods_id` BIGINT(20)DEFAULT NULL COMMENT '商品ID',
PRIMARY KEY(`id`))ENGINE=INNODB AUTO_INCREMENT=3 DEFAULT CHARSET=UTF8MB4

INSERT INTO `t_goods` value(1,'HuaweiMate40Pro','HUAWEI MATE 40PRO 256GB','/img/huaweimate40pro','HUAWEI MATE 40 PRO 256GB',6999,100),(2,'iPhone12ProMax','iPhone 12 Pro Max 256GB','/img/iphone12promax','iPhone 12 Pro Max 256GB',10999,100);
INSERT INTO `t_seckill_goods` value(1,1,6000,88,'2021-3-10 17:31:00','2021-3-25 23:00:00'),(2,2,8888,88,'2021-3-10 17:31:00','2021-3-25 23:00:00');

show INDEX FROM `t_order`;
create UNIQUE INDEX index_userid_goodsid ON `t_order`(`user_id`,`goods_id`);
drop index `index_userid_goodsid` on `t_order`;