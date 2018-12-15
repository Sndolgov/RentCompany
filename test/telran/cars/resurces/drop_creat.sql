
drop table car_models if exists;
drop table cars if exists;
drop table drivers if exists;
drop table records if exists;
drop sequence hibernate_sequence if exists;


create sequence hibernate_sequence start with 1 increment by 1;
create table car_models (
  model_name varchar(255) not null,
  company varchar(255),
  country varchar(255),
  gas_tank integer not null check (gas_tank>=0),
  price_day integer check (price_day>=0),
  primary key (model_name));

create table cars (
  reg_number varchar(255) not null,
  color varchar(255),
  fl_removed boolean,
  state varchar(255) not null,
  model_name varchar(255) not null,
  primary key (reg_number),
);
create table drivers (license_id bigint not null check (license_id>=0), birth_year integer, name varchar(255), phone varchar(255), primary key (license_id));
create table records (id integer not null, cost float not null, damages integer not null, gas_tank_percent integer not null, rent_date date, rent_days integer not null, return_date date, reg_number varchar(255), license_id bigint, primary key (id));
create index IDXhnu683roweu34kwwicji206ir on cars (model_name);
create index license_id_idx on records (license_id);
create index IDX90hkm18uy4gn10nrvowrdodfc on records (reg_number);
create index rent_date_idx on records (rent_date);
create index return_date_idx on records (return_date);

alter table cars add constraint FKd9un5cysc3c42ryrs3arg3rgv foreign key (model_name) references car_models(model_name);
alter table records add constraint FKj1f19yt7mmhr4tsl8ja0xpvft foreign key (reg_number) references cars(reg_number);
alter table records add constraint FKnck9o4sf42ni687g5u2jqa6uc foreign key (license_id) references drivers(license_id);
