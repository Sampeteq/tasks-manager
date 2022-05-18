create table if not exists users(
    uuid UUID unique,
    username varchar not null primary key,
    password varchar,
    role enum('COMMON', 'ADMIN'),
    creation_date varchar,
    status enum('OPEN', 'CLOSED', 'BANNED')
);

create table if not exists tasks(
    uuid UUID unique,
    id bigint not null primary key auto_increment,
    content varchar,
    priority enum('LOW', 'MEDIUM', 'HIGH'),
    creation_date timestamp,
    status enum('UNDONE', 'DONE'),
    owner_username varchar,
    foreign key (owner_username) references users(username)
)