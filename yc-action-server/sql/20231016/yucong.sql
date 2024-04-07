-- DDL
create schema yucong collate utf8mb4_general_ci;
-- yc_function_groovy
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

-- yc_function_task
create table yc_function_task
(
    id            int auto_increment
        primary key,
    process_id    varchar(64) default '' null comment 'Process',
    function_name varchar(64) default '' null comment 'function name',
    task_name     varchar(64) default '' null comment 'Process task name',
    script        text                   null comment 'task json',
    del_flag      tinyint     default 0  null comment '=0 action >0 deleted',
    create_user   varchar(64)            null,
    create_time   datetime               null,
    update_user   varchar(64)            null,
    update_time   datetime               null
);

create index idx_function_name
    on yc_function_task (function_name);

create index idx_process_id
    on yc_function_task (process_id);

create index idx_task_name
    on yc_function_task (task_name);

-- yc_function_execute_record
create table yc_function_execute_record
(
    id                int auto_increment
        primary key,
    function_name     varchar(128) null comment 'function name',
    execute_arguments text         null comment 'arguments',
    execute_user      varchar(32)  null comment 'user',
    execute_time      datetime     null,
    result_time       datetime     null,
    execute_duration  int          null comment 'execute duration',
    execute_result    text         null comment 'execute result'
)
    comment 'function execute record';

create index idx_execute_user
    on yc_function_execute_record (execute_user);

create index idx_function_name
    on yc_function_execute_record (function_name);

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
