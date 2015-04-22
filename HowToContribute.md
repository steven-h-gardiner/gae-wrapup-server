## Version Control ##

The project is hosted on google code and has a Mercurial repository.

To clone the project from the command line, use

`hg clone https://your.googleid.here@code.google.com/p/gae-wrapup-server/`

## Building ##

The server is implemented in Java using the Google App Engine SDK.  Once you have a local clone you can download the current version of the SDK using

`ant sdk`

You can compile the application code using

`ant compile`

## The default instance ##

A compiled version of the project is hosted on Google App Engine at [http://gae-wrapup-server.appspot.com/](http://gae-wrapup-server.appspot.com/)

You can deploy updated code to the default instance using

`ant deploy`

Note that this command assumes that the correct google credentials are cached.  If they've expired, the command will spit out the proper `bash` command to let you re-enter your google credentials.  You can get just that `bash` command using the `ant predeploy` target.

## Development instances ##

To test experimental features, you can either deploy to a local server on port 8080 by using `ant runserver`, or you can spin up a new Google App Engine instance and set your `war/WEB-INF/appengine-web.xml` to point at it.