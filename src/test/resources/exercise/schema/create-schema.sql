create table expertise_level
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

create table force_type
(
    id   int auto_increment
        primary key,
    name varchar(255) not null
);

create table equipment
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

create table user_exercise
(
    id          int auto_increment
        primary key,
    exercise_id int not null,
    user_id     int not null,
    constraint FK4dsfvd3ee924pwq4078equ1tu
        foreign key (exercise_id) references exercise (id),
    constraint FKkq87ibl7n9bls7n474jh3wrfm
        foreign key (user_id) references user (id)
);

create table text
(
    type        varchar(31)  not null,
    id          int auto_increment
        primary key,
    orderIndex  smallint     not null,
    text        varchar(255) not null,
    title       varchar(255) null,
    exercise_id int          null,
    plan_id     int          null,
    recipe_id   int          null,
    constraint FK3vpv1nlpyjyeu70pxsveqncs9
        foreign key (exercise_id) references exercise (id)
);

create table plan_type
(
    id   int auto_increment
        primary key,
    name varchar(255) not null
);

create table plan
(
    id           int auto_increment
        primary key,
    description  varchar(255) not null,
    name         varchar(100) not null,
    is_public BOOLEAN NOT NULL,
    plan_type_id int          not null,
    user_id      int          not null,
    constraint FK271ok4ss5pcte25w6o3hvv60x
        foreign key (user_id) references user (id),
    constraint FKt7sis5umk6kofuhe2lawg6oje
        foreign key (plan_type_id) references plan_type (id)
);


create table workout
(
    id       int auto_increment
        primary key,
    duration decimal(38, 2) not null,
    name     varchar(50)    not null,
    plan_id  int            not null,
    constraint FK2ijomxprmdq73lr3kwu4mhh20
        foreign key (plan_id) references plan (id)
);

create table workout_set_group
(
    id          int auto_increment
        primary key,
    orderIndex  int not null,
    restSeconds int not null,
    workout_id  int not null,
    constraint FKdm91qulyw807de6ydwcm8bky4
        foreign key (workout_id) references workout (id)
);


create table workout_set
(
    id                   int auto_increment
        primary key,
    repetitions          decimal(38, 2) not null,
    weight               decimal(38, 2) not null,
    exercise_id          int            not null,
    workout_set_group_id int            not null,
    constraint FKhg1c36fv3o7wlsv0w31m7un18
        foreign key (exercise_id) references exercise (id),
    constraint FKqt2vub0hl6yhxaul5hwpbjwwe
        foreign key (workout_set_group_id) references workout_set_group (id)
);
