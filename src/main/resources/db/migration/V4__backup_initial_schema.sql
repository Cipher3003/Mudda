drop sequence if exists public.categories_id_seq;

drop sequence if exists public.comment_likes_id_seq;

drop sequence if exists public.comment_likes_seq;

drop sequence if exists public.locations_id_seq;

drop sequence if exists public.roles_id_seq;

drop sequence if exists public.votes_id_seq;

alter table if exists public.action_tokens
    rename to action_tokens_backup;

alter table if exists public.categories
    rename to categories_backup;

alter table if exists public.comment_likes
    rename to comment_likes_backup;

alter table if exists public.comments
    rename to comments_backup;

alter table if exists public.issues
    rename to issues_backup;

alter table if exists public.locations
    rename to locations_backup;

alter table if exists public.refresh_tokens
    rename to refresh_tokens_backup;

alter table if exists public.roles
    rename to roles_backup;

alter table if exists public.users
    rename to users_backup;

alter table if exists public.votes
    rename to votes_backup;


