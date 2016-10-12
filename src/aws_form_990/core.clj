(ns aws-form-990.core
  (:require 
    [clojure.xml :as xml]
    [clojure.java.io :as io]
    [clojure.zip :as zip]
    [clojure.data.zip :as dzip]
    [clojure.data.zip.xml :as dzip-xml] 
    [clojure.java.shell :as shell]
    [clojure.string :as s]
    ))



(def xml990 (-> "201203199349304395_public.xml"
                xml/parse
                zip/xml-zip))
(type xml990)
xml990

(-> (dzip-xml/xml1-> xml990
                    :Return
                    :ReturnData
                    :IRS990
                    :MaterialDiversionOrMisuse
                    "true"
                    )
    zip/node)

(-> xml990
    zip/down
    zip/rightmost
    zip/down
    zip/down
    zip/node
)


(defn input-file-list
  "Returns a vector of strings, where each string is an element in the working directory"
  ([] (-> (shell/sh "ls" )
             :out
             (s/split,,, #"\n")))
  ([dir] (-> (shell/sh "ls" dir)
             :out
             (s/split,,, #"\n"))))

(input-file-list "resources/")

(defn xml2zip [filename]
  (-> 
      (str "resources/" filename)
      xml/parse
      zip/xml-zip))

 (xml2zip (first (input-file-list "resources/")))

(defn diversion? [zipper]
  (dzip-xml/xml1-> zipper
                    :Return
                    :ReturnData
                    :IRS990
                    :MaterialDiversionOrMisuse
                    "true"
                    ))
(-> "resources/" 
    input-file-list
    first
    xml2zip
    diversion?)


(defn diversion-info [zipper]
  (dzip-xml/xml1-> zipper
                    :Return
                    :ReturnData
                    :IRS990
                    :MaterialDiversionOrMisuse
                   zip/node))

(time (->> (map
             xml2zip
             (input-file-list "resources/"))
           (filter diversion?,,,)
           count))

(time (take 5 (input-file-list "resources/")))

(time (def pos-diversion (->> (map
                                xml2zip
                                (input-file-list "resources/"))
                              (filter diversion?,,,))))
(nth pos-diversion 0)
(nth pos-diversion 1)
(nth pos-diversion 2)
(nth pos-diversion 3)

(def diversion-data 
  (->> (map
         xml2zip
         (input-file-list "resources/"))
       (map diversion-info ,,,)))

(first pos-diversion)

(diversion-info (xml2zip (first (input-file-list "resources/"))))

(-> "resources/"
    input-file-list
    first
    xml2zip
    diversion-info)


diversion-data
