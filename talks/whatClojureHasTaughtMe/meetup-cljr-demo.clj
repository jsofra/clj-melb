;; Show cljr
cljr help
cljr search
cljr remove
cljr install
cljr clean
cljr reload

cljr install korma 0.3.0-beta10

;; (use 'korma.core)
;; (use 'korma.db)
;; (use 'ring.adapter.jetty)
;; (use 'compojure.core)

;; You can easily work the same way with a project and emacs... but sometimes its just alot easier to type and type there is also jline history, in which I can go back and pull specific commands that I have written 

;; The Simplest Server

(use 'ring.adapter.jetty)

;; Look inside - one method
(dir ring.adapter.jetty)

(defn naked-handler [request]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body "Hello World"})

(def app #'naked-handler)
(defonce server (run-jetty #'app {:port 8080 :join? false}))

;; Visit
;; http://localhost:8070/
;; http://localhost:8070/oeuo


;; What is in that request?
(defn naked-handler [request]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body (str request)})

;; Visit
;; http://localhost:8070/
;; Look, its just a map!

;; The Simplest Server with Routing
(use 'compojure.core)

;; Explore
'=> (dir compojure.core)
'=> (doc defroutes)
'=> (source defroutes)
'=> (source GET)

;; Require more libraries
(require '[compojure.route :as route])
(require '[compojure.handler :as handler])
(defroutes main-routes
         (GET "/" [] "<h1>Hello World Wide Web!</h1>")
         (route/resources "/")
         (route/not-found "Page not found"))

(def app (handler/site main-routes))

;; Explore compojure functions
'=> (source route/not-found)
'=> (source handler/site)

;; Visit
;; http://localhost:8070/
;; http://localhost:8070/oeuo

;; Now we can start using Hiccup
(use 'hiccup.core)

'=> (dir hiccup.core)
'=> '(mess around with html)

;; Put (html into main-routes)
'=> 
(defroutes main-routes
         (GET "/" [] (html [:h1 "Hello World Wide Web With Hiccup!"]))
         (route/resources "/")
         (route/not-found "Page not found"))

(def app (handler/site main-routes))
;; Evaluate main-routes and refresh the browser
;; (def app (handler/site main-routes))


;;Now mess with Korma
(use 'korma.core)
(use 'korma.db)
(defdb db (postgres {:db "test"
                     :user "korma"
                     :password "korma"}))

(defentity books)
(select books)

;; Construct a list of specific keys
(map #(select-keys % [:price :author]) (select books))

;; Now we start building our html structure

;; Start off with
(defn book-map-to-vector [book]
  [:li.book [:span.price (book :price)]
            [:span.title (book :title)]])

(book-map-to-vector (first (select books)))
;; Ooops.. the field is not title

;; Fixed :title to :name
(defn book-map-to-vector [book]
  [:li.book [:span.price (book :price)]
            [:span.title (book :name)]])

(book-map-to-vector (first (select books)))
;; Great, now it works, but we want to add in author information


;; Add in author
(defn book-map-to-vector [book]
  [:li.book [:span.price (book :price)]
            [:span.auther (book :author)]
            [:span.title (book :name)]])

(book-map-to-vector (first (select books)))
;; Bingo, it works

;; Now lets convert everything to a html string
(html (book-map-to-vector (first (select books))))

;;=> "<li class=\"book\"><span class=\"price\">30.5</span><span class=\"author\">Alexander Dumas</span><span class=\"title\">The Count of Monte Cristo</span></li>"


;; Lets do that for the entire list of books, and make it an unsorted list
(defn books-to-ul [books]
  (apply vector :ul#booklist 
    (map book-map-to-vector books)))

(books-to-ul (select books))

(html (books-to-ul (select books)))

;; Put into main-routes
(defroutes main-routes
         (GET "/" [] (html [:h1 "Hello World Wide Web With Hiccup!"]))
         (GET "/books" [] (html (books-to-ul (select books))))
         (route/resources "/")
         (route/not-found "Page not found"))

(def app (handler/site main-routes))

;; Look at the Browser.We have now hooked up our webstack!

