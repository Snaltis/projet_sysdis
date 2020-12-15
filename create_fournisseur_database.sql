set foreign_key_checks = 0;

drop table if exists IKEA.ARTICLES;

create table if not exists IKEA.ARTICLES
(
	num_produit int(11) not null,
	Prix float(9, 2) not null,
	Quantite int (11) not null,
	primary key(num_produit)
)
engine = InnoDB default charset = utf8;

insert into IKEA.ARTICLES values (5, 96.00, 12);
insert into IKEA.ARTICLES values (4, 26.00, 4);



drop table if exists VANDENBORRE.ARTICLES;

create table if not exists VANDENBORRE.ARTICLES
(
	num_produit int(11) not null,
	Prix float(9, 2) not null,
	Quantite int(11) not null,
	primary key(num_produit)
)
engine = InnoDB default charset = utf8;

insert into VANDENBORRE.ARTICLES values (5, 96.00, 12);
insert into VANDENBORRE.ARTICLES values (4, 36.00, 15);



drop table if exists DECATHLON.ARTICLES;

create table if not exists DECATHLON.ARTICLES
(
	num_produit int(11) not null,
	Prix float(9, 2) not null,
	Quantite int(11) not null,
	primary key(num_produit)
)
engine = InnoDB default charset = utf8;

insert into DECATHLON.ARTICLES values (5, 96.00, 12);
