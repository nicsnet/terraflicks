(ns terraflicks.env
  (:require [clojure.tools.logging :as log]))

(def defaults
  {:init
   (fn []
     (log/info "\n-=[terraflicks started successfully]=-"))
   :stop
   (fn []
     (log/info "\n-=[terraflicks has shut down successfully]=-"))
   :middleware identity})
