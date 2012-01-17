(ns clojutag.core
  (:gen-class)
  (:import [org.jaudiotagger.audio AudioFileIO]))

(defn -main [& args]
  (let [fl (new java.io.FileInputStream "C:/Users/mosciatti/Desktop/a.mp3")]    
  (println "this is launched with this arg:" args)))

