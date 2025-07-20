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

create table daily_cart_activity
(
    id            int auto_increment
        primary key,
    time          int not null,
    activity_id   int not null,
    daily_cart_id int not null,
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

create table user_food
(
    id      int auto_increment
        primary key,
    food_id int not null,
    user_id int not null,
    constraint FK1g8eq16xsqum2d2ojkk3hx4x9
        foreign key (food_id) references food (id),
    constraint FKcljbolfn2gnq75ujw985r4aa7
        foreign key (user_id) references user (id)
);