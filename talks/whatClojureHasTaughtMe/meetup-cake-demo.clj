;; An annoying problem I found with cake was that it is not very smart with namespace loading. I always have problems when. Swank is better in that sense

;; Install Datomic into Local ~/.m2 repository
'[$ cd ~/Downloads/datomic-0.1.3164]
'[$ mvn install:install-file -DgroupId=com.datomic -DartifactId=datomic -Dfile=datomic-0.1.3164.jar -DpomFile=pom.xml]

;; Create Project
'[$ cake new cake-demo]
'[$ cd cake-demo]
'[$ e .]

;; Add Dependencies to Project
[com.datomic/datomic "0.1.3164"]

;; Fetch dependencies
'[$ cake deps]


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Show that cake only has access to datomic
;; when in the cake-demo directory
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
'[$ cd ~]
'[$ cake repl]
(use 'datomic.api)
;=> No Class Found

;;;;;;;;;;;;;;;;;;;;;;;
;; Explore in cake repl
;;;;;;;;;;;;;;;;;;;;;;;
'[$ j cake-demo]
'[$ cake repl]
(use 'datomic.api)
(dir datomic.api)
(def conn (connect "datomic:dev://localhost:4334/cake-demo"))
;=> Error, No Connection

;; Startup Datomic Server
'[$ cd ~/Downloads/datomic-0.1.3164]
'[$ bin/transactor config/samples/dev-transactor-template.properties]

(def conn (connect "datomic:dev://localhost:4334/cake-demo"))
; => Error, No Database

(create-database "datomic:dev://localhost:4334/cake-demo")
(def conn (connect "datomic:dev://localhost:4334/cake-demo"))
; => Successful

(delete-database "datomic:dev://localhost:4334/cake-demo")
(def conn (connect "datomic:dev://localhost:4334/cake-demo"))
; => Error, No Database

(create-database "datomic:dev://localhost:4334/cake-demo")
(def conn (connect "datomic:dev://localhost:4334/cake-demo"))
; => Successful

(rename-database "datomic:dev://localhost:4334/cake-demo" "cake-demo1")
(def conn (connect "datomic:dev://localhost:4334/cake-demo"))
; => Error, No Database

(def conn (connect "datomic:dev://localhost:4334/cake-demo1"))
; => Successful

(rename-database "datomic:dev://localhost:4334/cake-demo1" "cake-demo")
(def conn (connect "datomic:dev://localhost:4334/cake-demo"))
; => Successful


;; Populate schema and data files
(import 'datomic.Util)
(require '[clojure.java.io :as io])
(def schema-reader (io/reader "/Users/Chris/Downloads/datomic-0.1.3164/samples/seattle/seattle-schema.dtm"))
(def schema (Util/readAll schema-reader))

(def data0-reader (io/reader "/Users/Chris/Downloads/datomic-0.1.3164/samples/seattle/seattle-data0.dtm"))
(def data0 (Util/readAll data0-reader))

(pprint schema)
(pprint data0)
(transact conn (first schema))
(transact conn (first data0))

;; Start Querying
(q '[:find ?e :where [?e :db/doc ?n]] (db conn))
(q '[:find ?e ?n :where [?e :db/doc ?n]] (db conn))
(q '[:find ?e ?n :where [?e :db/doc ?n] [(> ?e 61)]] (db conn))




;;;;;;
;; Textmate Time!
;;;;;

;; Create new file cake-demo.play 
(ns cake-demo.play
  (:use clojure.pprint
        datomic.api))
(def conn (connect "datomic:dev://localhost:4334/cake-demo"))
(defn q' [query] (q query (db conn)))

;; Ctrl-L to Load File

;; Copy Queries
(q' '[:find ?e ?n :where [?e :db/doc ?n] [(> ?e 61)]])
(q' '[:find ?e ?n :where [?e :db/doc ?n] ])
(q' '[:find ?n ?u
      :where
        [?c :community/name ?n]
        [?c :community/url ?u]])

(q' '[:find ?c ?n
      :where
        [?c :community/name ?n]
        [?c :community/type :community.type/twitter]])
        
(transact conn '[[:db/add 17592186046762 :community/name "MyWallingford1"]] )
        
(q' '[:find ?c_name
      :where
        [?c :community/name ?c_name]
        [?c :community/neighborhood ?n]
        [?n :neighborhood/district ?d]
        [?d :district/region :region/ne]])

(q '[:find ?n
     :in $ ?t
     :where
      [?c :community/name ?n]
      [?c :community/type ?t]]
    (db conn)
    :community.type/twitter)

;; Time Queries
(q' '[:find ?when
      :where
        [?tx :db/txInstant ?when]])
  
;;Doesn't Work      
#_(q '[:find ?e ?aname ?v
     :in $ [[?e ?a ?v]]
     :where
     [?e ?a ?v]
     [?a :db/ident ?aname]] 
     (db conn) 
     [17592186045790 :community/name "Weston"])

;; Ctrl-X to Explore Each Query

