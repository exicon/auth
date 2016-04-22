(defproject auth "0.1.0-SNAPSHOT"
  :description "Testbed for combined Auth0 and cookie based, MongoDB backed, Node.js/Express session store"
  :url "https://github.com/exicon/auth"
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies
  [; Common dependencies
   [org.clojure/clojure "1.8.0"]
   [hoplon/castra "3.0.0-alpha3"]
   [philoskim/debux "0.2.0"]

   ; Backend dependencies
   [com.novemberain/monger "3.0.2"]

   [camel-snake-kebab "0.4.0"]
   [org.clojure/data.json "0.2.6"]
   [org.clojure/data.csv "0.1.3"]
   [org.clojure/data.xml "0.0.8"]

   [http-kit "2.1.18"]

   [jumblerg/ring.middleware.cors "1.0.1"]
   [com.stuartsierra/component "0.3.1"]
   [org.danielsz/system "0.3.0-SNAPSHOT"]
   [environ "1.0.2"]
   [danielsz/boot-environ "0.0.5"]
   [ring "1.4.0"]
   [ring/ring-defaults "0.2.0"]
   [ring.middleware.conditional "0.2.0"]

   [buddy/buddy-auth "0.12.0"]
   [buddy/buddy-sign "0.12.0"]

   [midje "1.8.2" :scope "test"]
   [zilti/boot-midje "0.2.1-SNAPSHOT" :scope "test"]
   [ring/ring-mock "0.3.0" :scope "test"]

   ; Frontend dependencies
   [org.clojure/clojurescript "1.8.40" :scope "test"]
   [adzerk/boot-cljs "1.7.228-1" :scope "test"]
   [binaryage/devtools "0.6.1"]
   [pandeiro/boot-http "0.7.3"]
   [adzerk/boot-reload "0.4.7" :scope "test"]
   [cljsjs/boot-cljsjs "0.5.1" :scope "test"]
   [deraen/boot-less "0.5.0" :scope "test"]
   [hoplon/boot-hoplon "0.1.13" :scope "test"]
   [hoplon "6.0.0-alpha13"]
   [cljsjs/auth0-lock "8.1.5-1"]
   [org.slf4j/slf4j-nop "1.7.13" :scope "test"]

   ; IntelliJ dependencies
   [boot/core "2.6.0-SNAPSHOT"]
   ]
  :source-paths ["src/fe" "src/be"])
