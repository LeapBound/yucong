-- DDL
create table yc_function_groovy
(
    id            int auto_increment
        primary key,
    function_name varchar(128)         null comment '方法名',
    groovy_name   varchar(64)          null comment 'groovy 脚本名',
    groovy_url    varchar(256)         null comment 'groovy 脚本地址',
    del_flag      tinyint(1) default 0 null comment '0=action >0=deleted',
    create_user   varchar(32)          null,
    create_time   datetime             null,
    update_user   varchar(32)          null,
    update_time   datetime             null
);

create index idx_function_name
    on yc_function_groovy (function_name);

create index idx_groovy_name
    on yc_function_groovy (groovy_name);

-- DML
INSERT INTO yucong.yc_function_method ( function_name, function_class, function_method, del_flag, create_user, create_time, update_user, update_time) VALUES ( 'apply_loan', 'yzggy.yucong.action.func.cashloan.CashloanFunctions', 'applyLoan', 0, 'yao', NOW(), null, null);
INSERT INTO yucong.yc_function_method ( function_name, function_class, function_method, del_flag, create_user, create_time, update_user, update_time) VALUES ( 'apply_audit_status', 'yzggy.yucong.action.func.cashloan.CashloanFunctions', 'applyAuditStatus', 0, 'yao', NOW(), null, null);
INSERT INTO yucong.yc_function_method ( function_name, function_class, function_method, del_flag, create_user, create_time, update_user, update_time) VALUES ( 'bind_card', 'yzggy.yucong.action.func.cashloan.CashloanFunctions', 'bindCard', 0, 'yao', NOW(), null, null);
INSERT INTO yucong.yc_function_method ( function_name, function_class, function_method, del_flag, create_user, create_time, update_user, update_time) VALUES ( 'bind_card_captcha', 'yzggy.yucong.action.func.cashloan.CashloanFunctions', 'bindCardCaptcha', 0, 'yao', NOW(), null, null);
INSERT INTO yucong.yc_function_method ( function_name, function_class, function_method, del_flag, create_user, create_time, update_user, update_time) VALUES ( 'loan_submit', 'yzggy.yucong.action.func.cashloan.CashloanFunctions', 'loanSubmit', 0, 'yao', NOW(), null, null);

INSERT INTO yucong.yc_function_groovy ( function_name, groovy_name, groovy_url, del_flag, create_user, create_time, update_user, update_time) VALUES ('get_user_repayment_by_order', 'Order.groovy', '/home/scripts/order/', 0, 'yao', NOW(), null, null);
INSERT INTO yucong.yc_function_groovy ( function_name, groovy_name, groovy_url, del_flag, create_user, create_time, update_user, update_time) VALUES ('get_user_loan_time_by_order', 'Order.groovy', '/home/scripts/order/', 0, 'yao', NOW(), null, null);
INSERT INTO yucong.yc_function_groovy ( function_name, groovy_name, groovy_url, del_flag, create_user, create_time, update_user, update_time) VALUES ('get_loan_status_by_order', 'Order.groovy', '/home/scripts/order/', 0, 'yao', NOW(), null, null);
INSERT INTO yucong.yc_function_groovy ( function_name, groovy_name, groovy_url, del_flag, create_user, create_time, update_user, update_time) VALUES ('try_order_repay', 'Order.groovy', '/home/scripts/order/', 0, 'yao', NOW(), null, null);
INSERT INTO yucong.yc_function_groovy ( function_name, groovy_name, groovy_url, del_flag, create_user, create_time, update_user, update_time) VALUES ('try_order_refund', 'Order.groovy', '/home/scripts/order/', 0, 'yao', NOW(), null, null);
INSERT INTO yucong.yc_function_groovy ( function_name, groovy_name, groovy_url, del_flag, create_user, create_time, update_user, update_time) VALUES ('get_user_account', 'Account.groovy', '/home/scripts/account/', 0, 'yao', NOW(), null, null);
INSERT INTO yucong.yc_function_groovy ( function_name, groovy_name, groovy_url, del_flag, create_user, create_time, update_user, update_time) VALUES ('close_user_account', 'Account.groovy', '/home/scripts/account/', 0, 'yao', NOW(), null, null);
INSERT INTO yucong.yc_function_groovy ( function_name, groovy_name, groovy_url, del_flag, create_user, create_time, update_user, update_time) VALUES ('enable_user_account', 'Account.groovy', '/home/scripts/account/', 0, 'yao', NOW(), null, null);
INSERT INTO yucong.yc_function_groovy ( function_name, groovy_name, groovy_url, del_flag, create_user, create_time, update_user, update_time) VALUES ( 'get_user_by_name', 'Account.groovy', '/home/scripts/account/', 0, 'yao', NOW(), null, null);
INSERT INTO yucong.yc_function_groovy ( function_name, groovy_name, groovy_url, del_flag, create_user, create_time, update_user, update_time) VALUES ( 'apply_loan', 'CashLoan.groovy', '/home/scripts/cashloan/', 0, 'yao', NOW(), null, null);
INSERT INTO yucong.yc_function_groovy ( function_name, groovy_name, groovy_url, del_flag, create_user, create_time, update_user, update_time) VALUES ( 'apply_audit_status', 'CashLoan.groovy', '/home/scripts/cashloan/', 0, 'yao', NOW(), null, null);
INSERT INTO yucong.yc_function_groovy ( function_name, groovy_name, groovy_url, del_flag, create_user, create_time, update_user, update_time) VALUES ( 'bind_card', 'CashLoan.groovy', '/home/scripts/cashloan/', 0, 'yao', NOW(), null, null);
INSERT INTO yucong.yc_function_groovy ( function_name, groovy_name, groovy_url, del_flag, create_user, create_time, update_user, update_time) VALUES ( 'bind_card_captcha', 'CashLoan.groovy', '/home/scripts/cashloan/', 0, 'yao', NOW(), null, null);
INSERT INTO yucong.yc_function_groovy ( function_name, groovy_name, groovy_url, del_flag, create_user, create_time, update_user, update_time) VALUES ( 'loan_submit', 'CashLoan.groovy', '/home/scripts/cashloan/', 0, 'yao', NOW(), null, null);