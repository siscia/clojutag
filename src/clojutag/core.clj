(ns clojutag.core
  (:gen-class)
  (:import [org.jaudiotagger.audio AudioFileIO]
	   [org.jaudiotagger.tag Tag FieldKey])
  (:require [clojure.string :as str]))

(defn -main [& args]
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

