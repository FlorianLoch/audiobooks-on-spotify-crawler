# Audiobooks on Spotify - Crawler
Spotify is great and has a lot to offer when it comes to audiobooks - at least content-wise. Function-wise audiobooks are treated as "normal" music and therefore functionality like pause and resume later (see [Cassette](https://github.com/FlorianLoch/Cassette), another project of mine utilizing the Spotify Web API) and browsing them in a seperate view suiting audiobooks better is not provided.

This projects aims at providing users a better place to find audiobooks they want to listen to with their Spotify subscription.

This is the crawler/backend component, for the frontend see the [web service's repository](https://github.com/FlorianLoch/audiobooks-on-spotify-webservice).
It fetches content from the Spotify Web API based on some hand-picked starting points and should be able to find a huge amount of audiobooks in Spotify's catalog. If you find one it did not pick up feel free to contribute. ;)
The information retrieved gets cached into an H2 database that gets accessed by the web service component to make it available to the web app.

## Current state
The project is still under active development and not yet ready to be used. Hopefully it will be soon.

## Running & building with Maven
This is a maven-based project, it can be run with ```mvn clean exec:java``` and build with ```mvn clean package```. Attention: One needs to provide valid Spotify API credentials (see ```.env.sample```) in order to access their platform. Furthermore there needs to be an H2 database instance running.

## Disclaimer
The authors of this project are not related to Spotify in any way beside being happy users of their platform. This service is not related to Spotify except using their API and content.