## Simple JDK9 Client to demo HTTP/2 ServerPush

* Issue a simple HTTP/2 GET request and display all the correspsonding repsonse(s) (inc. the Pushed ones).

* Only works on JDK 9, make sure to allow 'incubator module' via the [`--add-modules`](http://openjdk.java.net/jeps/11) VM option to enable the use of the new [JEP110 HttpCient API](http://openjdk.java.net/jeps/110).

* For demo purposes only, certificate validation is skipped!

