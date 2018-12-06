(ns clj-ring.core
  (:require [ring.adapter.jetty :as jetty]))

(defn get-people [request]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body "Hello, world!"})

(defn -main []
  (jetty/run-jetty get-people
                   {:port 3000}))
