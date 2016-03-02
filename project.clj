(defproject auth "0.1.0-SNAPSHOT"
  :description "Testbed for combined Auth0 and cookie based, MongoDB backed, Node.js/Express session store"
  :url "https://github.com/exicon/auth"
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies
  [    ; Common dependencies
   [org.clojure/clojure "1.8.0"]
   [hoplon/castra "3.0.0-alpha3"]

   ; Backend dependencies
   [com.novemberain/monger "3.0.1"]

   [camel-snake-kebab "0.3.2"]
   [org.clojure/data.json "0.2.6"]
   [org.clojure/data.csv "0.1.3"]
   [org.clojure/data.xml "0.0.8"]

   [http-kit "2.1.18"]

   [jumblerg/ring.middleware.cors "1.0.1"]
   [com.stuartsierra/component "0.3.1"]
   [org.danielsz/system "0.3.0-SNAPSHOT"]
   [environ "1.0.1"]
   [danielsz/boot-environ "0.0.5"]
   [ring "1.4.0"]
   [ring/ring-defaults "0.1.5"]
   [ring.middleware.conditional "0.2.0"]

   [buddy/buddy-sign "0.9.0"]

   ; Frontend dependencies
   [org.clojure/clojurescript "1.7.228"]
   [adzerk/boot-cljs "1.7.228-1"]
   [binaryage/devtools "0.5.2"]
   [pandeiro/boot-http "0.7.3"]
   [adzerk/boot-reload "0.4.5"]
   [cljsjs/boot-cljsjs "0.5.1"]
   [hoplon/boot-hoplon "0.1.13"]
   [hoplon "6.0.0-alpha13"]
   [cljsjs/auth0-lock "8.1.5-0"]]
  :source-paths ["src/fe" "src/be"])
