set foreign_key_checks = 0;

drop table if exists SNAMAZON.USERS;

create table if not exists SNAMAZON.USERS
(
	num_client int(11) not null auto_increment,
	Nom varchar(40) not null,
	Prenom varchar(40) not null,
    Mdp varchar(40) not null,
    Mail varchar(100) not null,
    Montant_dispo float(11, 2) not null,
    Adresse varchar(150) not null,
	primary key(num_client)
)
engine = InnoDB default charset = utf8;

insert into USERS values (0, 'anc', 'loic', 'oliech', 'loic@anc.com', 1523645, '2 allée des nains, Tourcoing, Espagne');
insert into USERS values (0, 'dul', 'cedric', 'gentil', 'cedric@oliech.fr', 9, '16 rue de la loi, Louisville, Irlande');


drop table if exists SNAMAZON.FOURNISSEURS;

create table if not exists SNAMAZON.FOURNISSEURS
(
	id_fournisseur int(11) not null auto_increment,
	Nom varchar(40) not null,
	Telephone varchar(20) not null,
	primary key(id_fournisseur)
)
engine = InnoDB default charset = utf8;

insert into FOURNISSEURS values (0, 'Decathlon', '+32478555555');
insert into FOURNISSEURS values (0, 'Vandenborre', '+32471000000');
insert into FOURNISSEURS values (0, 'Ikea', '+3271986532');


drop table if exists SNAMAZON.CATEGORIES;

create table if not exists SNAMAZON.CATEGORIES
(
	id_categorie int(11) not null auto_increment,
	Nom varchar(40) not null,
	TVA float(5, 2) not null,
    primary key(id_categorie)
)
engine = InnoDB default charset = utf8;

insert into CATEGORIES values (0, 'Electronique', 21);
insert into CATEGORIES values (0, 'Meubles', 21);
insert into CATEGORIES values (0, 'Vetements', 12);
insert into CATEGORIES values (0, 'Sports', 6);


drop table if exists SNAMAZON.ARTICLES;

create table if not exists SNAMAZON.ARTICLES
(
	num_produit int(11) not null auto_increment,
	Nom varchar(40) not null,
	Prix float(9, 2) not null,
	Quantite int(11) not null,
    id_categorie int(11) not null,
    id_fournisseur int(11) not null,
    Image varchar(800) not null,
    description varchar(800),
	primary key(num_produit),
	CONSTRAINT id_fournisseur
    	FOREIGN KEY (id_fournisseur)
    	REFERENCES SNAMAZON.FOURNISSEURS (id_fournisseur),
    CONSTRAINT id_categorie
    	FOREIGN KEY (id_categorie)
    	REFERENCES SNAMAZON.CATEGORIES (id_categorie)
)
engine = InnoDB default charset = utf8;

insert into ARTICLES values (0, 'xBox 360', 97.00, 48, 1, 2, 'https://images-na.ssl-images-amazon.com/images/I/81%2Blz2g6bJL._AC_SY741_.jpg', 'Console de jeu');
insert into ARTICLES values (0, 'iPhone 4S', 55.00, 25, 1, 2, 'https://www.imore.com/sites/imore.com/files/field/image/2014/03/topic_iphone_4s.png', 'Smartphone');
insert into ARTICLES values (0, 'Commodore 64', 78.46, 18, 1, 2, 'https://65.media.tumblr.com/419d0e679bc6d0f1d5510da7c3adcf2e/tumblr_nymd0eaEoz1skoxlpo1_1280.jpg', 'Ordinateur un peu vieux');
insert into ARTICLES values (0, 'Gueridon', 36.99, 2, 2, 3, 'https://upload.wikimedia.org/wikipedia/commons/f/f6/RedRoomGueridon.jpg', 'Sorte de petite table');
insert into ARTICLES values (0, 'Etagere', 120.00, 5, 2, 3, 'https://medias.maisonsdumonde.com/image/upload/q_auto,f_auto/w_500/img/etagere-en-metal-noir-1000-12-33-185312_1.jpg', 'Meuble formé de montants qui supportent des tablettes horizontales');
insert into ARTICLES values (0, 'Table de salon', 546.66, 1, 2, 3, 'https://www.weba.be/media/catalog/product/cache/8cb41b483db8d8ce476b3ab77aa34a2c/2/2/2277136855.jpg', 'Table de salon');
insert into ARTICLES values (0, 'T-shirt (normal)', 25.00, 12, 3, 1, 'https://i5.walmartimages.com/asr/733942b0-71eb-43ca-904f-f597351f02dc_1.c82b6046422f4c407d314a1a2891817b.jpeg');
insert into ARTICLES values (0, 'T-shirt (beau)', 45.00, 23, 3, 1, 'https://img.secure.cdn-2.warnerartists.net/media/catalog/product/cache/181/image/600x/9df78eab33525d08d6e5fb8d27136e95/m/u/muse-simulationtheoryt-shirt.jpg');
insert into ARTICLES values (0, 'T-shirt (moche)', 5.00, 53, 3, 1, 'https://www.fed-corp.com/media/catalog/product/cache/1/image/500x/9df78eab33525d08d6e5fb8d27136e95/1/3/13-3075.jpg');
insert into ARTICLES values (0, 'Raquette de tennis', 115.00, 5, 4, 1, 'https://contents.mediadecathlon.com/p1709193/k$841e7fa2095eaecc524e421afd95ba25/sq/Raquette+de+Tennis+Adulte+Blade+100L+V7+0+noir+vert.jpg', 'Raquette de tennis pour adulte');
insert into ARTICLES values (0, 'Kayak', 325.00, 3, 4, 1, 'https://cdn.shopify.com/s/files/1/0311/1580/4805/products/ds-kayak-pro-1_800x.jpg', 'Canot de pêche groenlandais');
insert into ARTICLES values (0, 'Fleuret', 72.90, 136, 4, 1, 'https://contents.mediadecathlon.com/p1431692/640x0/14cr5/arme-fleuret.jpg', 'Arme de type estoc');


drop table if exists SNAMAZON.FORFAITS;

create table if not exists SNAMAZON.FORFAITS
(
	nom_forfait varchar(40) not null,
	prix float(4, 2) not null,
	primary key(nom_forfait)
)
engine = InnoDB default charset = utf8;

insert into FORFAITS values ('EXPRESS', 5);
insert into FORFAITS values ('NORMAL', 10);


drop table if exists SNAMAZON.COMMANDES;

create table if not exists SNAMAZON.COMMANDES
(
	num_commande int(11) not null,
	num_client int(11) not null,
	num_produit int(11) not null,
	quantite int(5) not null,
	statut varchar(40) not null default 'NON PAYE' check (statut in('NON PAYE', 'EN PREPARATION', 'EXPEDIEE', 'RECEPTIONEE')),
	date_commande datetime not null default '0000-00-00 00:00:00',
	CONSTRAINT num_client
    	FOREIGN KEY (num_client)
    	REFERENCES SNAMAZON.USERS (num_client),
    CONSTRAINT num_produit
    	FOREIGN KEY (num_produit)
    	REFERENCES SNAMAZON.PRODUITS (num_produit)
)
engine = InnoDB default charset = utf8;