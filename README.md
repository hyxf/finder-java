### finder-java

This is the repository for finder-java.

[![Build Status](https://travis-ci.org/hyxf/finder-java.svg?branch=master)](https://travis-ci.org/hyxf/finder-java)
[![Software License](https://img.shields.io/badge/license-Apache%202.0-brightgreen.svg)](https://github.com/hyxf/finder-java/blob/master/LICENSE.txt)

It is a tool for 'LAN file Share', based on web server

### Download

Download [the latest JAR](https://github.com/hyxf/finder-java/releases/latest)

### Use

Help doc

~~~bash
# java -jar finder.jar -h
usage: finder -d <arg> [-h <arg>] [-i <arg>] [-p <arg>] [-s <arg>] [-u <arg>]

LAN file Sharing tool

 -d,--dir <arg>    Shared directory path
 -h,--help <arg>   help
 -i,--ip <arg>     Local IP
 -p,--port <arg>   port
 -s,--pass <arg>   password
 -u,--user <arg>   username

make it easy!
~~~

Run

~~~bash
# java -jar finder.jar -d /Users/hyxf/Public/MVN_REPO
Running at http://192.168.43.36:8087/

~~~

Also can use 'Basic Auth'

~~~bash
# java -jar finder.jar -d /Users/hyxf/Public/MVN_REPO -u hyxf -s admin
Running at http://192.168.43.36:8087/
Username:hyxf , Password:admin

~~~

Open your browser

![alt tag](https://github.com/hyxf/finder-java/blob/master/screenshots/running.png)

### How to build

normal

~~~bash
~ gradle clean build
~~~

proguard

~~~bash
~ gradle clean proguard
~~~

----------------------

### Other methods

> If you use python, it will be simple
>
> Enter the directory you want to share and execute the following command

python2.7+

~~~bash
~ cd /path/to/share
~ python -m SimpleHTTPServer
Serving HTTP on 0.0.0.0 port 8000 ...

~~~

python3.6+

~~~bash
~ cd /path/to/share
~ python3 -m http.server
Serving HTTP on 0.0.0.0 port 8000 (http://0.0.0.0:8000/) ...

~~~

Open your browser [ http://127.0.0.1:8000 ]

![alt tag](https://github.com/hyxf/finder-java/blob/master/screenshots/python.png)

----------------------

### License


    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
