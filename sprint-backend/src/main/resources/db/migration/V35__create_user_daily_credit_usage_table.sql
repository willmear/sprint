create table if not exists user_daily_credit_usage (
    id uuid primary key,
    user_id uuid not null references app_user(id),
    usage_date date not null,
    generation_count integer not null,
    created_at timestamptz not null,
    updated_at timestamptz not null,
    constraint uk_user_daily_credit_usage_user_date unique (user_id, usage_date)
);

create index if not exists idx_user_daily_credit_usage_user_id
    on user_daily_credit_usage (user_id);

create index if not exists idx_user_daily_credit_usage_usage_date
    on user_daily_credit_usage (usage_date);
