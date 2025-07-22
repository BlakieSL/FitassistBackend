create table media
(
    id         int auto_increment
        primary key,
    imageName  varchar(255)                                                                       not null,
    parent_id  int                                                                                not null,
    parentType enum ('ACTIVITY', 'COMMENT', 'EXERCISE', 'FOOD', 'FORUM_THREAD', 'PLAN', 'RECIPE') not null
);

create table user
(
    id                  int auto_increment
        primary key,
    activity_level      enum ('LIGHTLY_ACTIVE', 'MODERATELY_ACTIVE', 'SEDENTARY', 'SUPER_ACTIVE', 'VERY_ACTIVE') not null,
    birthday            date                                                                                     not null,
    calculated_calories double                                                                                   not null,
    email               varchar(50)                                                                              not null,
    gender              enum ('FEMALE', 'MALE')                                                                  not null,
    goal                enum ('BUILD_MUSCLE', 'LOSE_WEIGHT', 'MAINTAIN_WEIGHT')                                  not null,
    height              double                                                                                   not null,
    password            varchar(60)                                                                              not null,
    username            varchar(40)                                                                              not null,
    weight              double                                                                                   not null
);

create table recipe
(
    id          int auto_increment
        primary key,
    description varchar(255)  not null,
    name        varchar(100)  not null,
    text        varchar(2000) not null,
    user_id     int           not null,
    constraint FKc8o8io8s0f7nqcd3429u6cxjs
        foreign key (user_id) references user (id)
);



create table recipe_category
(
    id   int auto_increment
        primary key,
    name varchar(50) not null
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