(ns clojutag.core
  (:gen-class)
  (:import [org.jaudiotagger.audio AudioFileIO]
	   [org.jaudiotagger.tag Tag FieldKey]
	   [org.jaudiotagger.audio.real RealTag]
	   [com.echonest.api.v4 Song EchoNestAPI])
  (:require [clojure.string :as str]))

(def api-key "SP3VJBGXDTYFD6IBT")

(def echo (new EchoNestAPI api-key))


(defn dizio [a]
  {(. FieldKey ARTIST) (.getArtistName a)
   (. FieldKey ALBUM) (.getReleaseName a)
   (. FieldKey TITLE) (.getTitle a)})

(defn -main [& args]
  (let [api-key "SP3VJBGXDTYFD6IBT"
	echo (new EchoNestAPI api-key)
	f (new java.io.File "C:/Users/mosciatti/Desktop/b.mp3")
	track (.uploadTrack echo f true)]
    (do
       
    (println (dizio track) track ))))

(defn find-tags [path-to-file]
  (let [api-key "SP3VJBGXDTYFD6IBT"
	echo (new EchoNestAPI api-key)
	f (new java.io.File path-to-file)
	track (.uploadTrack echo f true)]
    (dizio track)))



(defn -main2 [& args]
  (let [fl (new java.io.File "C:/Users/mosciatti/Desktop/Rihanna Umbrella PARODY Chris Brown - Copy.mp3")
	inputfile (new AudioFileIO)
	audiofile (.readFile inputfile fl)
	tag (.getTag audiofile)
	artist (.getFirst tag (. FieldKey ARTIST))
	title (.getFirst tag (. FieldKey TITLE))
;	newtag (.createDefaultTag audiofile)
	newtag (new RealTag)]
    (do
      (.deleteTag inputfile audiofile)
      (.setField newtag (. FieldKey ARTIST) (str/upper-case artist))
      (.setField newtag (. FieldKey TITLE) (str/upper-case title))
      ;(.setTag audiofile newtag)
      ;(.commit audiofile)
      (println title "byyyyyyyyyyyyyyyyyyyyy" artist))))

