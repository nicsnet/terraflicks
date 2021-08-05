# terraflicks


A simple API to test the integration of the Terraform Cloud Run tasks feature.
To get started set up a Task event Hook. Go to Settings > Task event hooks > Add Event Hook.

Give an appropriate name that describes your task use case scenario.

Choose the event hook url that fits your use case.
* http://floating-caverns-48130.herokuapp.com/api/run-tasks/pass will always respond with a passing task result
* http://floating-caverns-48130.herokuapp.com/api/run-tasks/fail will always respond with a failing task result
* http://floating-caverns-48130.herokuapp.com/api/run-tasks/kinder-surprise if you'd rather be surprised by the result 

Save the event hook. You can also create multiple one per scenario.

Go to your workspace Settings > Tasks and create tasks for the event hooks you've just created. 
Queue a run and see what happens.

## Swagger

A swagger UI describing the API can be found here http://floating-caverns-48130.herokuapp.com/

## Prerequisites

You will need [Leiningen][1] 2.0 or above installed.

[1]: https://github.com/technomancy/leiningen

## Running

To start a web server for the application, run:

    lein run 
