-- PROCESS_FUNCTION_MANAGE
create table PROCESS_FUNCTION_MANAGE
(
    id            int auto_increment
        primary key,
    process_key   varchar(255) default '' null comment 'Process key',
    activity_id   varchar(255) default '' null comment 'Activity id',
    function_name varchar(255) default '' null comment 'Function name',
    in_use        tinyint      default 1  null comment '0=disable 1=use',
    create_user   varchar(64)  default '' null,
    create_time   datetime                null,
    update_user   varchar(64)  default '' null,
    update_time   datetime                null
);

create index idx_function_name
    on PROCESS_FUNCTION_MANAGE (function_name);

create index idx_process_key
    on PROCESS_FUNCTION_MANAGE (process_key);

