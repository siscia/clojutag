(ns clojutag.core
  (:gen-class)
  (:import [org.jaudiotagger.audio AudioFileIO]
	   [org.jaudiotagger.tag Tag FieldKey]
	   [org.jaudiotagger.audio.real RealTag]
	   [com.echonest.api.v4 Song EchoNestAPI])
  (:require [clojure.string :as str])
  (:use clojure.java.io))

(def api-key "SP3VJBGXDTYFD6IBT")

(def echo (new EchoNestAPI api-key))

(def percorso "/home/simo/Music/a.mp3")
(def dire "/home/simo/music-to-try")

(defn dizio [a]
  {(. FieldKey ARTIST) (.getArtistName a)
   (. FieldKey ALBUM) (.getReleaseName a)
   (. FieldKey TITLE) (.getTitle a)})


(defn write-tag [f info-to-write]
  (let [audiofileIO (new AudioFileIO)
        audiofile (.readFile audiofileIO f)
        tag (.getTag audiofile)
        newtag (.createDefaultTag audiofile)]
    (do
      (doall
       (map (fn [[fieldkey info]] (.setField newtag fieldkey info)) info-to-write))
      (.deleteTag audiofileIO audiofile)
      (.setTag audiofile newtag)
      (.commit audiofile)
      (pr info-to-write))))

(defn write-tag-song [f]
  (let [echo (new EchoNestAPI api-key)
        track (.uploadTrack echo f true)
        d (dizio track)]
    (write-tag f d)))

(defn get-info [path-to-file]
  (let [api-key "SP3VJBGXDTYFD6IBT"
	echo (new EchoNestAPI api-key)
	f (new java.io.File path-to-file)
	track (.uploadTrack echo f true)]
    (dizio track)))

(defn walk-directory [dir]
  (let [dir (clojure.java.io/file dir)]
    (file-seq dir)))

(defn -main [& args]
  (let [s (walk-directory dire)]
    (doall
     (pmap write-tag-song (drop 1 s)))))

