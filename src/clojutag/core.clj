(ns clojutag.core
  (:gen-class)
  (:import [org.jaudiotagger.audio AudioFileIO]
	   [org.jaudiotagger.tag Tag FieldKey]
	   [com.echonest.api.v4 Song EchoNestAPI])
  (:require [clojure.string :as str]))

(def api-key "SP3VJBGXDTYFD6IBT")

(def echo (new EchoNestAPI api-key))

(defn -main [& args]
  (let [api-key "SP3VJBGXDTYFD6IBT"
	echo (new EchoNestAPI api-key)
	f (new java.io.File "C:/Users/mosciatti/Desktop/a - copy.mp3")
	track (.getKnownTrack echo f)]
    (do
    (println track))))

(defn principale [& args]
  (let [fl (new java.io.File "C:/Users/mosciatti/Desktop/Rihanna Umbrella PARODY Chris Brown - Copy.mp3")
	inputfile (new AudioFileIO)
	audiofile (.readFile inputfile fl)
	tag (.getTag audiofile)
	artist (.getFirst tag (. FieldKey ARTIST))
	title (.getFirst tag (. FieldKey TITLE))
	newtag (.createDefaultTag audiofile)]
    (do
      (.deleteTag inputfile audiofile)
      (.setField newtag (. FieldKey ARTIST) (str/upper-case artist))
      (.setField newtag (. FieldKey TITLE) (str/upper-case title))
      (.setTag audiofile newtag)
      (.commit audiofile)
      (println title "byyyyyyyyyyyyyyyyyyyyy" artist))))

