; to make Cursive able to resolve symbols
(require '[boot.core :refer :all])

(set-env!
  :project 'auth
  :version "0.1.0-SNAPSHOT"
  :dependencies
  '[
    ; Common dependencies
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

    [buddy/buddy-sign "0.12.0"]

    [midje "1.8.2"]
    [zilti/boot-midje "0.2.1-SNAPSHOT"]

    ; Frontend dependencies
    [org.clojure/clojurescript "1.8.40" :scope "test"]
    [adzerk/boot-cljs "1.7.228-1"]
    [binaryage/devtools "0.6.1"]
    [pandeiro/boot-http "0.7.3"]
    [adzerk/boot-reload "0.4.7"]
    [cljsjs/boot-cljsjs "0.5.1"]
    [hoplon/boot-hoplon "0.1.13"]
    [hoplon "6.0.0-alpha13"]
    [cljsjs/auth0-lock "8.1.5-1"]
    [deraen/boot-less "0.5.0"]
    [org.slf4j/slf4j-nop "1.7.13" :scope "test"]]
  :source-paths #{"src/be"}
  :resource-paths #{"resources"})

; =============== Backend ===============

(require
  '[reloaded.repl :as repl :refer [system start stop go reset]]
  '[danielsz.boot-environ :refer [environ]]
  '[sys]
  '[system.boot]
  '[zilti.boot-midje])

(task-options!
  speak {:theme "ordinance"}

  ; lein-generate gets the project name and version from here
  pom {:project (get-env :project)
       :version (get-env :version)})

;;; Copied from boot-test:
;;; This prevents a name collision WARNING between the test task and
;;; clojure.core/test, a function that nobody really uses or cares
;;; about.
(if ((loaded-libs) 'boot.user)
  (ns-unmap 'boot.user 'test))

(alter-var-root #'midje.sweet/include-midje-checks (constantly false))

(deftask test []
  (alter-var-root #'midje.sweet/include-midje-checks (constantly true))
  (merge-env! :source-paths #{"test"})
  (zilti.boot-midje/midje))

(deftask be "Back End" []
  (comp
    (environ :env {:http-port 9001})
    (watch)
    (system.boot/system
      :sys #'sys/dev
      :auto true
      :files ["sys.clj" "core.clj"])
    (repl :server true)))

; =============== Frontend ===============

(require
  '[hoplon.boot-hoplon :refer [hoplon prerender html2cljs]]
  '[adzerk.boot-reload :refer [reload]]
  '[pandeiro.boot-http :refer [serve]]
  '[adzerk.boot-cljs :refer [cljs]]
  '[cljsjs.boot-cljsjs :refer [from-cljsjs]]
  '[deraen.boot-less :refer [less]])

(task-options!
  cljs {:compiler-options {:pseudo-names   true
                           :parallel-build true}}
  from-cljsjs {:profile :production}
  cljs {:optimizations :advanced})

(deftask fe-build []
  (set-env! :source-paths #{"src/fe/"})
  (comp
    (from-cljsjs)
    (environ :env {:backend-url "http://localhost:9001"})
    (less)
    (hoplon :pretty-print true)
    (reload)
    (cljs)))

(deftask fe
  []
  (task-options!
    from-cljsjs {:profile :production}
    cljs {:optimizations :none
          :source-map    true})
  (comp
    (watch)
    (speak)
    (fe-build)
    (serve :port 9000)))
