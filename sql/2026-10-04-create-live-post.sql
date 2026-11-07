create table tb_live_post (
live_post_idx bigint not null auto_increment,
board_group_idx int not null,
user_id varchar(50) not null,
content varchar(500) not null,
created_at datetime not null default current_timestamp,
primary key(live_post_idx),
key idx_live_post_group_post (board_group_idx, live_post_idx),
foreign key(board_group_idx) references tb_board_group(board_group_idx),
foreign key(user_id) references members(USER_ID))
ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
