begin;

create extension postgis schema public;

create sequence public.action_tokens_id_seq;

create sequence public.refresh_token_id_seq;

create sequence public.categories_id_seq increment by 50;

create sequence public.comment_likes_id_seq increment by 50;

create sequence public.comment_likes_seq increment by 50;

create sequence public.comments_id_seq increment by 50;

create sequence public.issues_id_seq increment by 50;

create sequence public.locations_id_seq increment by 50;

create sequence public.roles_id_seq increment by 50;

create sequence public.users_id_seq increment by 50;

create sequence public.votes_id_seq increment by 50;

CREATE TABLE persistent_logins
(
    series    VARCHAR(64) PRIMARY KEY,
    username  VARCHAR(64) NOT NULL,
    token     VARCHAR(64) NOT NULL,
    last_used TIMESTAMP   NOT NULL
);


create table public.action_tokens
(
    id         bigint                      not null
        primary key,
    created_at timestamp(6) with time zone not null,
    expires_at timestamp(6) with time zone not null,
    token      varchar(255)                not null,
    type       varchar(255)                not null
        constraint action_tokens_type_check
            check ((type)::text = ANY
                   (ARRAY [('EMAIL_VERIFY'::character varying)::text, ('PASSWORD_RESET'::character varying)::text])),
    used_at    timestamp(6) with time zone,
    user_id    bigint                      not null
);

create table public.categories
(
    category_id bigint       not null
        primary key,
    name        varchar(255) not null
        constraint ukt8o6pivur7nn124jehx7cygw5
            unique
);

create table public.comment_likes
(
    id         bigint                      not null
        primary key,
    comment_id bigint                      not null,
    created_at timestamp(6) with time zone not null,
    user_id    bigint                      not null
);

create table public.comments
(
    comment_id bigint                      not null
        primary key,
    created_at timestamp(6) with time zone not null,
    issue_id   bigint                      not null,
    parent_id  bigint,
    text       text                        not null,
    user_id    bigint                      not null
);

create table public.issues
(
    issue_id          bigint                      not null
        primary key,
    issue_category_id bigint                      not null,
    created_at        timestamp(6) with time zone not null,
    delete_flag       boolean                     not null,
    description       text                        not null,
    location_id       bigint                      not null,
    media_urls        text[],
    severity_score    double precision            not null,
    issue_status      varchar(255)                not null
        constraint issues_issue_status_check
            check ((issue_status)::text = ANY
                   (ARRAY [('OPEN'::character varying)::text, ('PENDING'::character varying)::text, ('RESOLVED'::character varying)::text, ('CLOSED'::character varying)::text])),
    title             varchar(150)                not null,
    updated_at        timestamp(6) with time zone,
    urgency_flag      boolean                     not null,
    user_id           bigint                      not null
);

create table public.locations
(
    location_id  bigint                      not null
        primary key,
    address_line varchar(255)                not null,
    city         varchar(255)                not null,
    coordinate   geometry(Point, 4326)       not null,
    created_at   timestamp(6) with time zone not null,
    pin_code     varchar(255)                not null,
    state        varchar(255)                not null
);

create table public.refresh_tokens
(
    id         bigint                      not null
        primary key,
    created_at timestamp(6) with time zone not null,
    expires_at timestamp(6) with time zone not null,
    revoked    boolean                     not null,
    token      varchar(64)                 not null,
    user_id    bigint                      not null
);

create index idx_refresh_token_token
    on public.refresh_tokens (token);

create index idx_refresh_token_user_id
    on public.refresh_tokens (user_id);

create table public.roles
(
    role_id    bigint                      not null
        primary key,
    created_at timestamp(6) with time zone not null,
    name       varchar(255)                not null
        constraint ukofx66keruapi6vyqpv6f2or37
            unique,
    updated_at timestamp(6) with time zone
);

create table public.users
(
    user_id               bigint                      not null
        primary key,
    created_at            timestamp(6) with time zone not null,
    date_of_birth         date                        not null,
    email                 varchar(255)                not null
        constraint uk6dotkott2kjsp8vw4d0m25fb7
            unique,
    enabled               boolean                     not null,
    failed_login_attempts integer                     not null,
    hashed_password       varchar(255)                not null,
    lock_until            timestamp(6) with time zone,
    name                  varchar(255)                not null,
    phone_number          varchar(255)                not null
        constraint uk9q63snka3mdh91as4io72espi
            unique,
    profile_image_url     varchar(255),
    role                  varchar(255)                not null
        constraint users_role_check
            check ((role)::text = ANY
                   (ARRAY [('CITIZEN'::character varying)::text, ('GOVERNMENT'::character varying)::text, ('CREATOR'::character varying)::text])),
    updated_at            timestamp(6) with time zone,
    username              varchar(255)                not null
        constraint ukr43af9ap4edm43mmtq01oddj6
            unique,
    fcm_token             text
);

create table public.votes
(
    vote_id    bigint                      not null
        primary key,
    created_at timestamp(6) with time zone not null,
    issue_id   bigint                      not null,
    user_id    bigint                      not null
);

commit;