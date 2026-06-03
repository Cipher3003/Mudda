create schema if not exists civic;

create extension if not exists postgis schema civic;

create sequence civic.device_tokens_id_seq increment by 50;

create sequence civic.media_id_seq increment by 50;

alter sequence public.action_tokens_id_seq set schema civic;

alter sequence public.comments_id_seq set schema civic;

alter sequence public.issues_id_seq set schema civic;

alter sequence public.refresh_token_id_seq set schema civic;

alter sequence public.users_id_seq set schema civic;

create table if not exists civic.media
(
    id         bigint                      not null primary key,
    created_at timestamp(6) with time zone not null,
    deleted_at timestamp(6) with time zone,
    media_key  varchar(200)                not null,
    owner_id   bigint,
    owner_type varchar(255)
        constraint check_media_owner_type
            check ((owner_type)::text = ANY ((ARRAY ['ISSUE'::character varying, 'USER'::character varying])::text[])),
    position   integer                     not null,
    public_id  varchar(36)                 not null
        constraint unique_media_public_id
            unique,
    size       bigint,
    status     varchar(255)                not null
        constraint check_media_status
            check ((status)::text = ANY
                   ((ARRAY ['UPLOADING'::character varying, 'SUCCESS'::character varying, 'FAILED'::character varying])::text[]))
);

create index if not exists idx_media_owner_type_and_owner_id
    on civic.media (owner_type, owner_id);

create index if not exists idx_media_public_id
    on civic.media (public_id);

create table if not exists civic.persistent_logins
(
    series    varchar(255)                not null primary key,
    last_used timestamp(6) with time zone not null,
    token     varchar(255)                not null,
    username  varchar(255)                not null
);

create table if not exists civic.users
(
    user_id               bigint                      not null primary key,
    created_at            timestamp(6) with time zone not null,
    date_of_birth         date                        not null,
    deleted_at            timestamp(6) with time zone,
    email                 varchar(254)                not null
        constraint unique_users_email
            unique,
    enabled               boolean                     not null,
    failed_login_attempts integer                     not null,
    hashed_password       varchar(100)                not null,
    lock_until            timestamp(6) with time zone,
    name                  varchar(100)                not null,
    phone_number          varchar(255)                not null
        constraint unique_users_phone_number
            unique,
    profile_image_url     varchar(255),
    role                  varchar(255)                not null
        constraint check_users_role
            check ((role)::text = ANY
                   ((ARRAY ['CITIZEN'::character varying, 'GOVERNMENT'::character varying, 'CREATOR'::character varying])::text[])),
    updated_at            timestamp(6) with time zone,
    username              varchar(20)                 not null
        constraint unique_users_username
            unique
);

create index if not exists idx_users_email
    on civic.users (email);

create table if not exists civic.action_tokens
(
    id         bigint                      not null primary key,
    created_at timestamp(6) with time zone not null,
    expires_at timestamp(6) with time zone not null,
    token      varchar(255)                not null,
    type       varchar(255)                not null
        constraint check_action_tokens_type
            check ((type)::text = ANY
                   ((ARRAY ['EMAIL_VERIFY'::character varying, 'PASSWORD_RESET'::character varying])::text[])),
    used_at    timestamp(6) with time zone,
    user_id    bigint                      not null
        constraint fk_action_tokens_ref_users
            references civic.users
);

create index if not exists idx_action_tokens_token
    on civic.action_tokens (token);

create index if not exists idx_action_tokens_user_id_and_type
    on civic.action_tokens (user_id, type);

create table if not exists civic.issues
(
    issue_id       bigint                      not null primary key,
    issue_category varchar(4)                  not null
        constraint check_issues_issue_category
            check ((issue_category)::text = ANY
                   ((ARRAY ['INF'::character varying, 'SAN'::character varying, 'ELE'::character varying])::text[])),
    city           varchar(255)                not null,
    comment_count  bigint                      not null,
    coordinate     geometry(Point, 4326)       not null,
    created_at     timestamp(6) with time zone not null,
    deleted_at     timestamp(6) with time zone,
    description    text                        not null,
    pin_code       varchar(255)                not null,
    severity_score double precision            not null,
    state          varchar(255)                not null,
    issue_status   varchar(255)                not null
        constraint check_issues_issue_status
            check ((issue_status)::text = ANY
                   ((ARRAY ['OPEN'::character varying, 'PENDING'::character varying, 'RESOLVED'::character varying, 'CLOSED'::character varying])::text[])),
    title          varchar(150)                not null,
    updated_at     timestamp(6) with time zone,
    user_id        bigint                      not null
        constraint fk_issues_ref_users
            references civic.users,
    vote_count     bigint                      not null
);

create index if not exists idx_issues_deleted_at
    on civic.issues (deleted_at);

create table if not exists civic.comments
(
    id          bigint                      not null primary key,
    created_at  timestamp(6) with time zone not null,
    deleted_at  timestamp(6) with time zone,
    issue_id    bigint                      not null
        constraint fk_comments_ref_issues
            references civic.issues,
    like_count  bigint                      not null,
    parent_id   bigint
        constraint fk_comments_ref_comments
            references civic.comments,
    reply_count bigint                      not null,
    text        text                        not null,
    updated_at  timestamp(6) with time zone,
    user_id     bigint                      not null
        constraint fk_comments_ref_users
            references civic.users
);

create table if not exists civic.comment_likes
(
    comment_id bigint                      not null
        constraint fk_comment_likes_ref_comments
            references civic.comments,
    user_id    bigint                      not null
        constraint fk_comment_likes_ref_users
            references civic.users,
    created_at timestamp(6) with time zone not null,
    primary key (comment_id, user_id)
);

create table if not exists civic.refresh_tokens
(
    id         bigint                      not null primary key,
    created_at timestamp(6) with time zone not null,
    expires_at timestamp(6) with time zone not null,
    revoked    boolean                     not null,
    token      varchar(64)                 not null,
    user_id    bigint                      not null
        constraint fk_refresh_tokens_ref_users
            references civic.users
);

create index if not exists idx_refresh_tokens_token
    on civic.refresh_tokens (token);

create index if not exists idx_refresh_tokens_user_id
    on civic.refresh_tokens (user_id);

create table if not exists civic.votes
(
    issue_id   bigint                      not null
        constraint fk_votes_ref_issues
            references civic.issues,
    user_id    bigint                      not null
        constraint fk_votes_ref_users
            references civic.users,
    created_at timestamp(6) with time zone not null,
    primary key (issue_id, user_id)
);

create table if not exists civic.device_tokens
(
    id         bigint                      not null
        primary key,
    active     boolean                     not null,
    created_at timestamp(6) with time zone not null,
    device_id  varchar(255)                not null
        constraint unique_device_tokens_device_id
            unique,
    fcm_token  text                        not null,
    platform   varchar(255)                not null
        constraint check_device_tokens_platform
            check ((platform)::text = ANY
                   ((ARRAY ['ANDROID'::character varying, 'IOS'::character varying, 'WEB'::character varying])::text[])),
    updated_at timestamp(6) with time zone,
    user_id    bigint                      not null
        constraint fk_device_tokens_ref_users
            references civic.users
);

create index if not exists idx_device_tokens_user_id
    on civic.device_tokens (user_id);

create index if not exists idx_device_tokens_device_id
    on civic.device_tokens (device_id);

