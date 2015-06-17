create table TBL_KEYWORD_CATEGORY(FLD__ID int primary key auto_increment,
                                  FLD_NAME varchar(50) not null unique);
create table TBL_KEYWORD(FLD__ID int primary key auto_increment,
                         FLD_NAME varchar(50) not null unique,
                         FKF_CATEGORY int not null references TBL_KEYWORD_CATEGORY(FLD__ID) on delete cascade);
create table TBL_TWITTER_USER(FLD__ID int primary key auto_increment,
                              FLD_NAME varchar(50),
                              FLD_MONITORED int not null);
create table TBL_USER_ACIVITY(FLD__ID int primary key auto_increment,
                              FLD_DATE timestamp not null,
                              FLD_COUNT bigint not null,
                              FKF_USER int references TBL_TWITER_USER on delete cascade);
create table TBL_USER_CATEGORY_USAGE(FLD__ID int primary key auto_increment,
                                     FLD_DATE timestamp not null,
                                     FLD_SHARE real not null,
                                     FKF_CATEGORY int references TBL_KEYWORD_CATEGORY on delete cascade,
                                     FKF_USER int references TBL_MONITORED_USER on delete cascade);
create table TBL_USER_KEYWORD_USAGE(FLD__ID int primary key auto_increment,
                                    FLD_COUNT bigint not null,
                                    FKF_KEYWORD int references TBL_KEYWORD on delete cascade,
                                    FKF_CATEGORY int references TBL_HASHTAG_CATEGORY_USAGE on delete cascade);
create table TBL_HASHTAG(FLD__ID int primary key auto_increment,
                         FLD_NAME varchar(50) not null unique,
                         FLD_MONITORED int not null);
create table TBL_HASHTAG_ACTIVITY(FLD__ID int primary key auto_increment,
                                 FLD_DATE timestamp not null,
                                 FLD_COUNT bigint not null,
                                 FKF_TAG int references TBL_HASHTAG on delete cascade);
create table TBL_HASHTAG_CATEGORY_USAGE(FLD__ID int primary key auto_increment,
                                        FLD_DATE timestamp not null,
                                        FLD_SHARE real not null,
                                        FKF_CATEGORY int references TBL_KEYWORD_CATEGORY on delete cascade,
                                        FKF_TAG int references TBL_MONITORED_HASHTAG on delete cascade);
create table TBL_KEYWORD_USAGE(FLD__ID int primary key auto_increment,
                                       FLD_COUNT bigint not null,
                                       FKF_KEYWORD int references TBL_KEYWORD on delete cascade);
create table TBL_HASHTAG_KEYWORD_USAGE(FLD__ID int primary key auto_increment,
                                       FLD_COUNT bigint not null,
                                       FKF_KEYWORD int references TBL_KEYWORD on delete cascade,
                                       FKF_CATEGORY int references TBL_HASHTAG_CATEGORY_USAGE on delete cascade );

