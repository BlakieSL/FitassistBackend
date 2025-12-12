create table activity_category
(
    id   int auto_increment
        primary key,
    name varchar(50) not null
);

create table activity
(
    id                   int auto_increment
        primary key,
    met                  decimal(38, 2) not null,
    name                 varchar(50)    not null,
    activity_category_id int            not null,
    constraint FKdp1iuiugdyvggej0rb7srlbqr
        foreign key (activity_category_id) references activity_category (id)
);

create table equipment
(
    id   int auto_increment
        primary key,
    name varchar(255) not null
);

create table expertise_level
(
    id   int auto_increment
        primary key,
    name varchar(255) not null
);

create table food_category
(
    id   int auto_increment
        primary key,
    name varchar(255) not null
);

create table food
(
    id               int auto_increment
        primary key,
    calories         decimal(38, 2) not null,
    carbohydrates    decimal(38, 2) not null,
    fat              decimal(38, 2) not null,
    name             varchar(50)    not null,
    protein          decimal(38, 2) not null,
    food_category_id int            not null,
    constraint FKpnfa6f8ubf600psx2mhefa1a1
        foreign key (food_category_id) references food_category (id)
);

create table force_type
(
    id   int auto_increment
        primary key,
    name varchar(255) not null
);

create table mechanics_type
(
    id   int auto_increment
        primary key,
    name varchar(255) not null
);

create table exercise
(
    id                 int auto_increment
        primary key,
    description        varchar(255) not null,
    name               varchar(100) not null,
    equipment_id       int          not null,
    expertise_level_id int          not null,
    force_type_id      int          not null,
    mechanics_type_id  int          not null,
    constraint FK1pnc72jktj32lju0xs13417e8
        foreign key (expertise_level_id) references expertise_level (id),
    constraint FKdaiw8tnyg1v77vh9aocupho2n
        foreign key (mechanics_type_id) references mechanics_type (id),
    constraint FKmfhgf7qfmbgn21id9ppq5yxy6
        foreign key (force_type_id) references force_type (id),
    constraint FKrchhr3djb6bfyrq3gy1m9q0om
        foreign key (equipment_id) references equipment (id)
);

create table media
(
    id         int auto_increment
        primary key,
    image_name  varchar(255)                                                                                                                not null,
    parent_id  int                                                                                                                         not null,
    parentType enum ('USER', 'ACTIVITY', 'COMMENT', 'COMMENT_COMPLAINT', 'EXERCISE', 'FOOD', 'FORUM_THREAD', 'PLAN', 'RECIPE', 'THREAD_COMPLAINT') not null
);

create table plan_category
(
    id   int auto_increment
        primary key,
    name varchar(50) not null
);

create table recipe_category
(
    id   int auto_increment
        primary key,
    name varchar(50) not null
);

create table role
(
    id   int auto_increment
        primary key,
    name varchar(5) not null
);

create table target_muscle
(
    id   int auto_increment
        primary key,
    name varchar(50) not null
);

create table exercise_target_muscle
(
    id               int auto_increment
        primary key,
    priority         decimal(38, 2) not null,
    exercise_id      int            not null,
    target_muscle_id int            not null,
    constraint FK96dsvb4of0glllrnwyra2msdk
        foreign key (target_muscle_id) references target_muscle (id),
    constraint FK9g95qn3tsf53f8labrp8svjoq
        foreign key (exercise_id) references exercise (id)
);

create table thread_category
(
    id   int auto_increment
        primary key,
    name varchar(255) not null
);

create table user
(
    id             int auto_increment
        primary key,
    activity_level enum ('LIGHTLY_ACTIVE', 'MODERATELY_ACTIVE', 'SEDENTARY', 'SUPER_ACTIVE', 'VERY_ACTIVE') null,
    birthday       date                                                                                     not null,
    email          varchar(50)                                                                              not null,
    gender         enum ('FEMALE', 'MALE')                                                                  not null,
    goal           enum ('BUILD_MUSCLE', 'LOSE_WEIGHT', 'MAINTAIN_WEIGHT')                                  null,
    height         decimal(38, 2)                                                                           null,
    password       varchar(60)                                                                              not null,
    username       varchar(40)                                                                              not null,
    weight         decimal(38, 2)                                                                           null
);

create table daily_cart
(
    id      int auto_increment
        primary key,
    date    date not null,
    user_id int  not null,
    constraint FKlp039xb3s16oatvn8js5ywr4f
        foreign key (user_id) references user (id)
);

create table daily_cart_activity
(
    id            int auto_increment
        primary key,
    time          smallint       not null,
    weight        decimal(38, 2) null,
    activity_id   int            not null,
    daily_cart_id int            not null,
    constraint FKdgesrmrhq8h50f65y57nan844
        foreign key (activity_id) references activity (id),
    constraint FKsk898dh43l6p4csbx0b656qe8
        foreign key (daily_cart_id) references daily_cart (id)
);

create table daily_cart_food
(
    id            int auto_increment
        primary key,
    quantity      decimal(38, 2) not null,
    daily_cart_id int            not null,
    food_id       int            not null,
    constraint FKbknvob606ghq2w8lopi49t094
        foreign key (daily_cart_id) references daily_cart (id),
    constraint FKfh3vkst24iavc3i6btr26f2md
        foreign key (food_id) references food (id)
);

create table plan
(
    id             int auto_increment
        primary key,
    description    varchar(255) not null,
    name           varchar(100) not null,
    views          bigint       not null default 0,
    is_public      BOOLEAN      NOT NULL,
    created_at     datetime(6)  not null,
    structure_type enum ('WEEKLY_SPLIT', 'FIXED_PROGRAM') not null,
    user_id        int          not null,
    constraint FK271ok4ss5pcte25w6o3hvv60x
        foreign key (user_id) references user (id)
);

create table plan_category_association
(
    id               int auto_increment
        primary key,
    plan_id          int not null,
    plan_category_id int not null,
    constraint FKixtep7wfnx8ue29sxsxupsg10
        foreign key (plan_id) references plan (id),
    constraint FKkwmufk924k0s0pspiq9ib5a5o
        foreign key (plan_category_id) references plan_category (id)
);

create table recipe
(
    id                 int auto_increment
        primary key,
    description        varchar(255) not null,
    name               varchar(100) not null,
    views              bigint       not null default 0,
    minutes_to_prepare smallint     not null default 0,
    is_public          BOOLEAN      not null,
    created_at         datetime(6)  not null,
    user_id            int          not null,
    constraint FKc8o8io8s0f7nqcd3429u6cxjs
        foreign key (user_id) references user (id)
);

create table recipe_category_association
(
    id                 int auto_increment
        primary key,
    recipe_id          int not null,
    recipe_category_id int not null,
    constraint FK6xwmj5n0uvwbko8wsc2exjm6s
        foreign key (recipe_category_id) references recipe_category (id),
    constraint FKchpnhktvdy9xwv8ndmahb8r9r
        foreign key (recipe_id) references recipe (id)
);

create table recipe_food
(
    id        int auto_increment
        primary key,
    quantity  decimal(38, 2) not null,
    food_id   int            not null,
    recipe_id int            not null,
    constraint FKptig88aycf6b7voflw0r3arb3
        foreign key (recipe_id) references recipe (id),
    constraint FKsuti5sfesb4gae6xwabtx5ius
        foreign key (food_id) references food (id)
);

create table text
(
    type        varchar(31)  not null,
    id          int auto_increment
        primary key,
    order_index  smallint     not null,
    text        varchar(255) not null,
    title       varchar(255) null,
    exercise_id int          null,
    plan_id     int          null,
    recipe_id   int          null,
    constraint FK3vpv1nlpyjyeu70pxsveqncs9
        foreign key (exercise_id) references exercise (id),
    constraint FKt7iblsq2n1x1npjd03i5s6pqh
        foreign key (recipe_id) references recipe (id),
    constraint FKtea0vpvpu2hreq0o07fhblntk
        foreign key (plan_id) references plan (id)
);

create table thread
(
    id                 int auto_increment
        primary key,
    created_at         datetime(6)  not null,
    text               varchar(255) not null,
    title              varchar(50)  not null,
    views              bigint       not null default 0,
    thread_category_id int          not null,
    user_id            int          not null,
    constraint FK2rmkr0vlimyhly7n8460ysfl9
        foreign key (user_id) references user (id),
    constraint FKl7kvoao6mpqr79cii1b6uq8q2
        foreign key (thread_category_id) references thread_category (id)
);

create table comment
(
    id                int auto_increment
        primary key,
    text              varchar(255) not null,
    created_at        datetime(6)  not null,
    parent_comment_id int          null,
    thread_id         int          not null,
    user_id           int          not null,
    constraint FK8kcum44fvpupyw6f5baccx25c
        foreign key (user_id) references user (id),
    constraint FKehf7mvstlwwl8fy9ahfo515rm
        foreign key (thread_id) references thread (id),
    constraint FKhvh0e2ybgg16bpu229a5teje7
        foreign key (parent_comment_id) references comment (id) ON DELETE CASCADE
);

create table complaint
(
    type       varchar(31)                                     not null,
    id         int auto_increment
        primary key,
    reason     enum ('INAPPROPRIATE_CONTENT', 'OTHER', 'SPAM') not null,
    status     enum ('PENDING', 'RESOLVED')                    not null,
    user_id    int                                             not null,
    comment_id int                                             null,
    thread_id  int                                             null,
    constraint FK1um0lnkkt6ddjylek8kppofnr
        foreign key (thread_id) references thread (id),
    constraint FKh8dg5n7ibjyack6pn6e71djj6
        foreign key (user_id) references user (id),
    constraint FKmbv2o7p31vgo1y6ni7h36tvj6
        foreign key (comment_id) references comment (id)
);

create table user_activity
(
    id          int auto_increment
        primary key,
    activity_id int not null,
    user_id     int not null,
    created_at  datetime(6) not null,
    constraint FKlw9o1xb2ki2hnwq1o3kk5dlja
        foreign key (activity_id) references activity (id),
    constraint FKp78clcyf5okycdv9teohsr2kq
        foreign key (user_id) references user (id)
);

create table user_comment
(
    id         int auto_increment
        primary key,
    type       enum ('DISLIKE', 'LIKE', 'SAVE') not null,
    comment_id int                              not null,
    user_id    int                              not null,
    created_at datetime(6)                      not null,
    constraint FK8run8dgvadrrwcwe5xpdscynm
        foreign key (comment_id) references comment (id) ON DELETE CASCADE,
    constraint FKornrskknlmumgdhlohpbcvrw5
        foreign key (user_id) references user (id)
);

create table user_exercise
(
    id          int auto_increment
        primary key,
    exercise_id int not null,
    user_id     int not null,
    created_at  datetime(6) not null,
    constraint FK4dsfvd3ee924pwq4078equ1tu
        foreign key (exercise_id) references exercise (id),
    constraint FKkq87ibl7n9bls7n474jh3wrfm
        foreign key (user_id) references user (id)
);

create table user_food
(
    id         int auto_increment
        primary key,
    food_id    int not null,
    user_id    int not null,
    created_at datetime(6) not null,
    constraint FK1g8eq16xsqum2d2ojkk3hx4x9
        foreign key (food_id) references food (id),
    constraint FKcljbolfn2gnq75ujw985r4aa7
        foreign key (user_id) references user (id)
);

create table user_plan
(
    id         int auto_increment
        primary key,
    type       enum ('DISLIKE', 'LIKE', 'SAVE') not null,
    plan_id    int                              not null,
    user_id    int                              not null,
    created_at datetime(6)                      not null,
    constraint FKfgwof219hqbrb6am5awwan8r2
        foreign key (plan_id) references plan (id),
    constraint FKr1gojepx9qoalgmd17gurr1dl
        foreign key (user_id) references user (id)
);

create table user_recipe
(
    id         int auto_increment
        primary key,
    type       enum ('DISLIKE', 'LIKE', 'SAVE') not null,
    recipie_id int                              not null,
    user_id    int                              not null,
    created_at datetime(6)                      not null,
    constraint FKn6pgj5qxw9w3cyxfcq1ahiwg2
        foreign key (recipie_id) references recipe (id),
    constraint FKsv2khyshlbtm7vvpk5sq6wjtl
        foreign key (user_id) references user (id)
);

create table user_roles
(
    users_id int not null,
    roles_id int not null,
    primary key (users_id, roles_id),
    constraint FK7ecyobaa59vxkxckg6t355l86
        foreign key (users_id) references user (id),
    constraint FKj9553ass9uctjrmh0gkqsmv0d
        foreign key (roles_id) references role (id)
);

create table user_thread
(
    id         int auto_increment
        primary key,
    thread_id  int not null,
    user_id    int not null,
    created_at datetime(6) not null,
    constraint FK6to3x5x2bh7baqxdvohs3lg0p
        foreign key (user_id) references user (id),
    constraint FKfdaf07carxbssu63xpu8qa7ya
        foreign key (thread_id) references thread (id)
);

create table workout
(
    id              int auto_increment
        primary key,
    duration        smallint       not null,
    name            varchar(50)    not null,
    plan_id         int            not null,
    order_index     smallint       not null,
    rest_days_after tinyint        not null,
    constraint FK2ijomxprmdq73lr3kwu4mhh20
        foreign key (plan_id) references plan (id)
);

create table workout_set
(
    id           int auto_increment
        primary key,
    order_index  smallint not null,
    rest_seconds smallint not null,
    workout_id   int      not null,
    constraint FKdm91qulyw807de6ydwcm8bky4
        foreign key (workout_id) references workout (id)
);

create table workout_set_exercise
(
    id             int auto_increment
        primary key,
    repetitions    smallint       not null,
    weight         decimal(38, 2) not null,
    exercise_id    int            not null,
    workout_set_id int            not null,
    order_index    smallint       not null,
    constraint FKhg1c36fv3o7wlsv0w31m7un18
        foreign key (exercise_id) references exercise (id),
    constraint FKqt2vub0hl6yhxaul5hwpbjwwe
        foreign key (workout_set_id) references workout_set (id)
);

