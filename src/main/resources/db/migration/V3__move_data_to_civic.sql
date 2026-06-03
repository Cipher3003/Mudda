-- remove this not possible re-upload all images and remove these
-- removes fcm_token needs to re-upload them

insert into civic.users(user_id, created_at, date_of_birth, deleted_at, email, enabled, failed_login_attempts,
                        hashed_password, lock_until, name, phone_number, profile_image_url, role, updated_at, username)
select u.user_id,
       u.created_at,
       u.date_of_birth,
       NULL,
       u.email,
       u.enabled,
       u.failed_login_attempts,
       u.hashed_password,
       u.lock_until,
       u.name,
       u.phone_number,
       u.profile_image_url,
       u.role,
       u.updated_at,
       u.username
from public.users u;

insert into civic.action_tokens(id, created_at, expires_at, token, type, used_at, user_id)
SELECT a.id, a.created_at, a.expires_at, a.token, a.type, a.used_at, a.user_id
FROM public.action_tokens a;

insert into civic.issues(issue_id, issue_category, city, comment_count, coordinate, created_at, deleted_at, description,
                         pin_code, severity_score, state, issue_status, title, updated_at, user_id, vote_count)
select i.issue_id,
       c.name,
       l.city,
       (select count(*) from public.comments co where co.issue_id = i.issue_id),
       l.coordinate,
       i.created_at,
       case
           when i.delete_flag then current_timestamp
           end,
       i.description,
       l.pin_code,
       i.severity_score,
       l.state,
       i.issue_status,
       i.title,
       i.updated_at,
       i.user_id,
       (select count(*) from public.votes vo where vo.issue_id = i.issue_id)
from public.issues i
         join public.categories c on i.issue_category_id = c.category_id
         join public.locations l on i.location_id = l.location_id;

insert into civic.comments(id, created_at, deleted_at, issue_id, like_count, parent_id, reply_count, text, updated_at,
                           user_id)
select c.comment_id,
       c.created_at,
       null,
       c.issue_id,
       (select count(*) from public.comment_likes cl where cl.comment_id = c.comment_id),
       c.parent_id,
       (select count(*) from public.comments p where p.parent_id = c.comment_id),
       c.text,
       null,
       c.user_id
from public.comments c;

insert into civic.comment_likes(comment_id, user_id, created_at)
select cl.comment_id, cl.user_id, cl.created_at
from public.comment_likes cl;

insert into civic.refresh_tokens(id, created_at, expires_at, revoked, token, user_id)
select r.id, r.created_at, r.expires_at, r.revoked, r.token, r.user_id
from public.refresh_tokens r;

insert into civic.votes(issue_id, user_id, created_at)
select v.issue_id, v.user_id, v.created_at
from public.votes v;
