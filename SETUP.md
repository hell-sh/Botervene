# Setup

Setting up Botervene to run on your server is simply.

First, we'll create an extra log file which will give us something like this:

    8.8.8.8 /robots.txt
    8.8.8.8 /index.html

Achieving this should be relatively easy. Here's how it could be done in Apache:

    LogFormat "%{CF-Connecting-IP}i %U" botervene
    CustomLog "logs/botervene.log" botervene

After you've configured and reloaded your web server to work with Botervene, you'll need to start Botervene once in order for the config.json to be generated.


