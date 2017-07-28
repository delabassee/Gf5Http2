## Simple JDK9 Client to demo HTTP/2 ServerPush

* Issue a request and will display all the correspsonding repsonse (inc. the Pushed ones).

* Only works on JDK 9, make sure to allow 'incubator module' via (--add-modules)[http://openjdk.java.net/jeps/11] to enable the use of the new (JEP110 HttpCient API([[http://openjdk.java.net/jeps/11]].

* For demo purposes only, certificate validation is skipped!

