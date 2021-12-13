# terraflicks

[![Deploy](https://www.herokucdn.com/deploy/button.svg)](https://heroku.com/deploy)

A simple API to test the integration of the Terraform Cloud Run tasks feature.
To get started set up a Task event Hook. Go to Settings > Task event hooks > Add Event Hook.

Give an appropriate name that describes your task use case scenario.

Choose the event hook url that fits your use case.
* http://heroku-app-domain.herokuapp.com/api/run-tasks/pass will always respond with a passing task result
* http://heroku-app-domain.herokuapp.com/api/run-tasks/fail will always respond with a failing task result
* http://heroku-app-domain.herokuapp.com/api/run-tasks/error-404 responds with a `404`
* http://heroku-app-domain.herokuapp.com/api/run-tasks/error-500 responds with a `500`
* http://heroku-app-domain.herokuapp.com/api/run-tasks/error-503 responds with a `503` eventually after retrying the task result delivery fails
* http://heroku-app-domain.herokuapp.com/api/run-tasks/timeout never replies
* http://heroku-app-domain.herokuapp.com/api/run-tasks/kinder-surprise if you'd rather be surprised by the result 

Save the event hook. You can also create multiple, one per scenario.

Go to your workspace Settings > Tasks and create tasks for the event hooks you've just created. 
Queue a run and see what happens.

**Note:**

This API is hosted on Heroku, and Heroku will shut down this service after a period of inactivity. To make sure the event hooks will respond in time when testing out the run tasks feature, visit http://heroku-app-domain.herokuapp.com/ first and wait for the Swagger UI to load. Then you are ready for testing.

## Swagger

A swagger UI describing the API can be found here http://floating-caverns-48130.herokuapp.com/

## Prerequisites

You will need [Leiningen][1] 2.0 or above installed.

[1]: https://github.com/technomancy/leiningen

## Running

To start a web server for the application, run:

    lein run 

You can also use docker-compose to run the server locally:
```
docker-compose up
```

lein run exposes a port `3000` on localhost, running docker-compose will have it running on port `3006`

In order to use the local server for event hooks, you need to also create an url with `ngrok`

```
ngrok http 3006
```

This will create an url to your local, e.g. http://e28bfacd28ab.ngrok.io

## Deployment

The app runs on Heroku. You need to have the [heroku cli](https://devcenter.heroku.com/articles/heroku-cli) installed.

Once you've installed the cli and created a Heroku account:

```
heroku login
git push heroku main
```

To view the logs:
```
heroku logs
```
