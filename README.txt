This the CS316 Fall 2016 project for Andrew Bihl, Will Rollins, Sean Hudson, Diane Hadley, and Ezra Lieblich. 

Table schemes are described in 316db_Descriptions.rtf. The database is a Postgres relational database serviced through Heroku and hosted on AWS. 

The database is accessed with the following access credentials for TLS connection:
Host
ec2-107-20-195-181.compute-1.amazonaws.com
Database
d5f790u0pfiql
User
xoondtisxkyxdf
Port
5432
Password
4dc7d512c8054abc040dccc3f92f871e0f3cd49c0d5e43b4c7f89bb6eb911e98
URI
postgres://xoondtisxkyxdf:4dc7d512c8054abc040dccc3f92f871e0f3cd49c0d5e43b4c7f89bb6eb911e98@ec2-107-20-195-181.compute-1.amazonaws.com:5432/d5f790u0pfiql

To Deploy our web application download from our github page or fork our repository at https://github.com/ezra-lieblich/CS316Final. Once downloaded initialize the play framework and go into the play-beers folder. Note that you need the play framework downloaded to run. Once in that folder, run sudo ./activator to run on your local host. We also have the capability to hook up our web app to Heroku or Amazon AWS.
