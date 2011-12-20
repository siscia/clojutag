(ns clojutag.ID3.utility
  (:use clojutag.core))

(defn dimension-synchsafe [size]
  #{:doc "Return the lenght of a frame by an array of bit of the size frame"}
  (assert (= (count size) 4) "Need of 4 elements")
  (+ (bit-shift-left (nth size 0) 21)
     (bit-shift-left (nth size 1) 14)
     (bit-shift-left (nth size 2) 7)
     (nth size 3)))

(defn read-kind-of-frames [header]
  #^{:doc "take the 10 byte of the header frame and return the kind of the frame"}
  (apply str (map char (take 4 header))))

(defn read-dimension-frame [header]
  #^{:doc "take the 10 byte header and return the dimension of the frames"}
  (dimension-synchsafe (take 4 (drop 4 header))))

(defn read-flag-frames [header]
  #^{:doc "return the flags of the frames"}
  (take 2 (drop 8 header)))

