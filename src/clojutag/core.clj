(ns clojutag.core
  (:gen-class)
  (:import [org.jaudiotagger.audio AudioFileIO]
	   [org.jaudiotagger.tag Tag FieldKey]
	   [org.jaudiotagger.audio.real RealTag]
	   [com.echonest.api.v4 Song EchoNestAPI]
	   [com.echonest.api.v4 EchoNestException])
  (:require [clojure.string :as str])
  (:use clojure.java.io))

(def api-key "SP3VJBGXDTYFD6IBT")

(def echo (new EchoNestAPI api-key))

(def percorso "/home/simo/Music/a.mp3")
					;(def dire "/home/simo/music-to-try")

(defmacro without-java-output [& body]
  `(with-open [w# (java.io.PrintStream. "NUL")]
     (let [oo# System/out, oe# System/err]
       (System/setOut w#)
       (System/setErr w#)
       (try
         ~@body
         (finally
          (System/setOut oo#)
          (System/setErr oe#))))))

(defn dizio [a]
  {:pre [(not (nil? a))]}
  {(. FieldKey ARTIST) (if-let [artist (.getArtistName a)] artist "Artist not found")
   (. FieldKey ALBUM) (if-let [release (.getReleaseName a)] release "Album not found")
   (. FieldKey TITLE) (if-let [title (.getTitle a)] title "Title not found")
   (. FieldKey COMMENT) "By Clojutag"})


(defn write-tag [f info-to-write]
  (let [audiofileIO (new AudioFileIO)
        audiofile (.readFile audiofileIO f)
        tag (.getTag audiofile)
        newtag (.createDefaultTag audiofile)]
    (do
      (dorun
	(map (fn [[fieldkey info]] (.addField newtag fieldkey info)) info-to-write))
      (.deleteTag audiofileIO audiofile)
      (.commit (doto audiofile (.setTag newtag))))))

(defn write-tag-song [f]
  (let [echo (new EchoNestAPI api-key)
        track (.uploadTrack echo f true)]
    (if (not (nil? track))
      (doall
       (println track)
       (write-tag f (dizio track)))
    (do (println "in if" )))))

    
(defn walk-directory [dir]
  (let [dir (clojure.java.io/file dir)]
    (file-seq dir)))

(defn -main1  [& args]
  (do
    (time
     (let [s (filter #(.isFile %) (walk-directory "C:\\try"))]
       (println s)
    (doseq [files-to-analize s]
      (write-tag-song files-to-analize))))
    ))

(defn -main  [& args]
  (without-java-output
   (do
    (time
  (let [s (filter #(.isFile %) (walk-directory "C:\\try"))]
    (dorun
      (map write-tag-song s))))
    )))

(defn -main1 [& args]
  (do (pr args))
  (let [f (new java.io.File (nth args 0))]
    (do
     (time
    (write-tag-song f)))))

(defn -main2 [& args]
  (pr (nth args 0)))