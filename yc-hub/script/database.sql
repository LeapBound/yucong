create table yc_account
(
    id           bigint auto_increment
        primary key,
    account_name varchar(64) null,
    user_id      bigint      null,
    bot_id       bigint      null,
    create_time  datetime    null,
    update_time  datetime    null
);

create table yc_bot
(
    id                bigint auto_increment
        primary key,
    bot_id            varchar(64)  null,
    bot_name          varchar(64)  null,
    init_role_content varchar(256) null comment '角色定义',
    create_time       datetime     null,
    update_time       datetime     null
);

create table yc_channel_config
(
    id             bigint auto_increment
        primary key,
    bot_id         varchar(64)  null,
    corp_id        varchar(64)  null,
    agent_id       varchar(64)  null,
    secret_content varchar(256) null,
    token_content  varchar(256) null,
    aes_key        varchar(256) null,
    create_time    datetime     null,
    update_time    datetime     null
);

create table yc_function_manage
(
    id            int auto_increment
        primary key,
    function_name varchar(128)         null comment 'function name',
    function_json text                 null comment 'openai function json',
    function_use  tinyint(1) default 1 null comment '1=using 0=not',
    function_uid  varchar(32)          null comment 'function uid',
    del_flag      tinyint(1) default 0 null comment '0=action >0=deleted',
    create_user   varchar(32)          null,
    create_time   datetime             null,
    update_user   varchar(32)          null,
    update_time   datetime             null
)
    comment 'yc function management';

create index idx_function_name
    on yc_function_manage (function_name);

create index idx_function_uid
    on yc_function_manage (function_uid);

create table yc_message_history
(
    id              bigint auto_increment
        primary key,
    conversation_id varchar(64) null,
    bot_id          varchar(64) null,
    account_id      varchar(64) null,
    role_str        varchar(16) null,
    content         text        null,
    name            varchar(64) null,
    create_time     datetime    null,
    update_time     datetime    null
);

create table yc_message_summary
(
    id              bigint auto_increment
        primary key,
    conversation_id varchar(32) null,
    content         text        null,
    create_time     datetime    null,
    update_time     datetime    null
);

create table yc_role
(
    id          bigint auto_increment
        primary key,
    role_name   varchar(64)  null,
    role_desc   varchar(256) null,
    create_time datetime     null,
    update_time datetime     null
);

create table yc_role_function
(
    id          bigint auto_increment
        primary key,
    role_id     bigint null,
    function_id bigint null
);

create table yc_role_relation
(
    id            bigint auto_increment
        primary key,
    role_id       bigint not null,
    relate_id     bigint not null,
    relation_type int    not null comment '0 bot, 1 account'
);

create table yc_user
(
    id          bigint auto_increment
        primary key,
    username    varchar(64) null,
    create_time datetime    null,
    update_time datetime    null
);
-- 2023/10/08
alter table yc_channel_config
    change bot_id bot_uuid varchar(64) null;

alter table yc_channel_config
    add channel_uuid varchar(33) null after bot_uuid;

alter table yc_account
    add channel_uuid varchar(33) null after account_uuid;

create table yc_wx_cp_kf
(
    id              bigint auto_increment
        primary key,
    group_tag       varchar(32) null,
    service_user_id varchar(64) null,
    create_time     datetime    null
);

alter table yc_account
    change account_name external_id varchar(64) null;

alter table yc_channel_config
    add open_kf_id varchar(64) null after agent_id;

-- 20240702

alter table yc_function_manage
    add is_extend tinyint default 1 null comment '0 built-in, 1 extend' after del_flag;

alter table yc_function_manage
    add constraint yc_function_manage_pk
        unique (function_name);

-- 20241205

alter table yc_function_manage
    add function_description varchar(512) null after function_name;

alter table yc_function_manage
    add function_params text null after function_description;

-- 20241209