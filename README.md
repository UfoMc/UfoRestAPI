
Hello :D 

Aboud
-----
 - I uploaded my own RestAPI. If you notice any possible bugs please feel free to contact me via discord (ufo.dev).
 - You can also contact me on discord if you have any questions about the usage or anything else.

Usage
-----
 - Use the RequestBuilder.jar to build a Request or get your self one out of the internet.
 - example for request: `http://127.0.0.1/webInteraction/database=<my database>&searchField=<user id or so>`
 - Following http methodes are supported: `POST, PUT, GET, DELETE`
 - after changing anything in the config a restart is necessary.

Config
------

  > web
   - `httpPath` is the path your requests need to go
   - `httpPort` is the port your request needs to be send to
   - `httpAdress` is the address your reqest needs to go to

  > database
   - `dataBaseType` is the type of your database. Valid is (`MongoDB; MariaDB; Config; MySQL`)
      If not valid automaticly `Config` is selected
   - `dataBasePort` is the port your dataBase is binded on
   - `password` is the password of your database (only nessecary for MySQL and MariaDB)
   - `user` is the user the client is connecting with (not nessecary for Config)
   - `dataBaseAddress` is the address your database is located at. If it runs on the same server as the rest application leave it `127.0.0.1`

  > tecnical

   - `isReady` is the boolean that tells the system if the config is ready. This is false at basic. After the first start the config is been created. Set it up, then set `isReady` to true and restart.

  > cache
   - `enableChache` tells if you want to use a cache or not (normaly true but if you cache the data your save in your app just turn it off)
   - `cacheExpiration` tells how long an object stays inside the database before expiering without beeing used in minutes.
   - `maxCacheEntrySize` tells how many entrys the cache is allowed to have
   - `maxWeight` tells how big the cache is allowed to be


 - default config:
   
  `"httpPath": "webInteraction",
  "httpPort": "1234",
  "httpAddress": "127.0.0.1",

  "dataBaseType": "config",
  "dataBasePort": "1234",
  "password": "my password",
  "user": "admin",
  "dataBaseAddress": "127.0.0.1",
  "dataBaseName": "network",

  "isReady": false,

  "enableCache": true,
  "cacheExpiration": 5,
  "maxCacheEntrySize": 1000,
  "maxWeight": 20000`

Copyright
---------
> The Ufo Rest API is free 2 use. If u give me a credit or not is your choise. Please dont use it for commercial purposes! 
