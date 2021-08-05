(ns terraflicks.env
  (:require
    [selmer.parser :as parser]
    [clojure.tools.logging :as log]
    [terraflicks.dev-middleware :refer [wrap-dev]]))

(def defaults
  {:init
   (fn []
     (parser/cache-off!)
     (log/info "\n-=[terraflicks started successfully using the development profile]=-"))
   :stop
   (fn []
     (log/info "\n-=[terraflicks has shut down successfully]=-"))
   :middleware wrap-dev})
