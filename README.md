### simple healthz handler

Tested with gradle v4.5.1, see https://gradle.org/install/

To test:
```sh
sudo mkdir -p /run/jhealthz && echo '<status>OK</status>' | sudo tee /run/jhealthz/status.xml
git clone https://github.com/metno/jhealthz.git && \
cd jhealthz && \
gradle appRun
# wait to see that it is running, and then in another terminal
curl -i http://localhost:8080/jhealthz/
```

To build:
```sh
gradle war && \
ls -l build/libs/jhealthz.war
```
