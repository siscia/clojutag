(ns clojutag.core
  (:gen-class)
  (:import [org.jaudiotagger.audio AudioFileIO AudioFile]
           [org.jaudiotagger.tag Tag FieldKey]))

(defn -main [& args]
  (let [fl (new java.io.File "/home/simo/b.mp3"),
	IO (new AudioFileIO)
        audiofile (.readFile IO fl)
        tag (.getTag audiofile)]
    
    (do
      (println "AudioFile:" audiofile)
    (println "tag:" (.getFirst tag (. FieldKey ALBUM))))))
