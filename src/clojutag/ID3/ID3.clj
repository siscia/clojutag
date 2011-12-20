(ns clojuremp3.ID3
  (:use clojuremp3.core))

(defprotocol TAG
  (set-version [TAG stream])
  (set-dimension [TAG stream])
  (set-flags [TAG stream])
  (set-frames [TAG stream])
  (build [TAG stream])
  (look [TAG for-what]))

(extend-type clojure.lang.IPersistentMap
  TAG
  (look [this for-what]
    (:content (for-what this))))

(defprotocol FRAME
  (set-id [FRAME stream])
  (set-size [FRAME stream])
  (set-flags [FRAME stream])
  (set-content [FRAME stream])
  (build [FRAME stream]))

(defprotocol SONG-MP3
  (get-stream [SONG-MP3])
  (close-stream [SONG-MP3])
  (build [SONG-MP3])
  (get-kind-tag [SONG-MP3])
  (get-tag [SONG-MP3]))

(defrecord Tag [minor-version dimension flags frames]
  TAG
  (set-version [this stream]
    (let [ver (apply str (take 2 (drop 3 stream)))]
      (Tag. ver dimension flags frames)))
  
  (set-dimension [this stream]
    (let [dm (dimension-synchsafe (take 4 (drop 6 stream)))]
      (Tag. minor-version dm flags frames)))

  (set-flags [this stream]
    (let [fl  (take 1 (drop 5 stream))]
      (Tag. minor-version dimension (map #(bit-test fl) (range 8)) frames)))

  (set-frames [this stream]
    (Tag. minor-version dimension flags (hash-map)))

  (build [this stream]
    (-> this set-version set-dimension set-flags set-frames)))

(defrecord Song-mp3 [filename file stream kind-tag tag]
  SONG-MP3
  (get-stream [this]
    (let [st (new java.io.FileInputStream (:filename this)),
          rd (lazy-reader st)]
      (Song. filename st (take 100 rd) kind-tag tag)))
  
  (get-kind-tag [this]
    (let [major-version (apply str (map char (take 3 stream)))
          minor-version (apply str "v" (take 2 (drop 3 stream)))
          kndtag (apply str major-version minor-version)]
     (Song. filename file stream  (keyword kndtag) tag)))

  (close-stream [this]
    (.close file))

  (build [this]
    (-> this get-stream get-kind-tag))

  (get-tag [this]
    (let [newtag (Tag. kind-tag nil nil nil)]
      (Song. filename file stream kind-tag newtag))))

(defrecord Frame [id content size flags])

(defn read-id3-tags [frame-array header-size]
  (loop [frame-no 0 frame-start frame-array total-bytes-so-far 0 acc []]
    (if (< total-bytes-so-far header-size)      
      (let [size-of-frame  (read-dimension-frame (take 10 frame-start))]
	(let [frame-array (take size-of-frame (drop 11 frame-start))
	      frame-id (read-kind-of-frames (take 10 frame-start))
	      frame-content (apply str (map char frame-array))
              frame-flags (read-flag-frames (take 10 frame-start))]
	  (when *debug*
	    (printf "Frame %d: %s, Size: %d, Content: %s\n" frame-no frame-id
		    size-of-frame (apply str (map char frame-content))))
	  (recur (+ 1 frame-no)
		 (drop (+ 10 size-of-frame) frame-start)
		 (+ total-bytes-so-far 10 size-of-frame)
		 (assoc acc (dec frame-no) [frame-id frame-content size-of-frame frame-flags]))))
      acc)))
