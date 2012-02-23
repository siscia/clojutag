(ns clojutag.core-old
  (:gen-class)
  (:import [org.jaudiotagger.audio AudioFileIO]
	   [org.jaudiotagger.tag Tag FieldKey]
	   [org.jaudiotagger.audio.real RealTag]
	   [com.echonest.api.v4 Song EchoNestAPI]
	   [com.echonest.api.v4 EchoNestException]
	   [java.io File])
  (:require [clojure.string :as str])
  (:use clojure.java.io))

(def api-key "SP3VJBGXDTYFD6IBT")

(def key-diz
     {:artist (. FieldKey ARTIST)
      :album (. FieldKey ALBUM)
      :title (. FieldKey TITLE)
      :comment (. FieldKey COMMENT)})

(defn rename-file [file new-name]
  (.renameTo file (File. (str (.getParent file) (File/separator) new-name (re-find #"\.[^.]*" (.getPath file))))))

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
  {(key-diz :artist) (if-let [artist (.getArtistName a)] artist "Artist not found")
   (key-diz :album) (if-let [release (.getReleaseName a)] release "Album not found")
   (key-diz :title) (if-let [title (.getTitle a)] title "Title not found")
   (key-diz :comment) "Is so fast"})


(defn write-tag [f info-to-write par]
  (let [audiofileIO (new AudioFileIO)
        audiofile (.readFile audiofileIO f)
        tag (.getTag audiofile)
        newtag (.createDefaultTag audiofile)]
    (do
      (dorun
       (map (fn [[fieldkey info]] (.addField newtag fieldkey info)) info-to-write))
      (println (.getFirst newtag (. FieldKey TITLE)))
      (.deleteTag audiofileIO audiofile)
      (.commit (doto audiofile (.setTag newtag)))
      (letfn [(make-title [par]
			  (str (.getFirst newtag (key-diz (first par))) (make-title rest par)))]
	(rename-file f (make-title par)))))
  nil)

(defn write-tag-song [f par]
  (let [echo (new EchoNestAPI api-key)
        track (.uploadTrack echo f true)
	info-to-write (dizio track)]			
    (if (not (nil? track))
      (doall
       (println f)
       (write-tag f info-to-write (keyword par)))
    (do (println "in if" )))))

    
(defn walk-directory [dir]
  (let [dir (clojure.java.io/file dir)]
    (file-seq dir)))


(defn -main  [& args]
  (without-java-output
   (do
    (time
     (let [s (filter #(.isFile %) (reduce concat (map walk-directory args)))]
    (dorun
      (pmap write-tag-song s (repeat (last args)))))))))
