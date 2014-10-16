(ns ip-service.ip
  "IP service to find geography by raw ip address,only supports IPv4."
  (:use [org.clojars.smee.binary.core :only [compile-codec decode
                                             repeated string]])
  (:require [clojure.java.io :as io]
            [clojure.string :as cs])
  (:import [java.nio ByteBuffer MappedByteBuffer ByteOrder]
           [java.io FileInputStream ByteArrayInputStream InputStream]
           [java.net InetAddress]
           [java.nio.channels FileChannel FileChannel$MapMode]))

(def ip-codec
  "IP structures codec"
  (compile-codec
   (repeated
    {:ip-num :int-be
     :offset-bs (repeated :byte :length 3)
     :size :byte})))

(defn- buffer-stream
  "Create a InputStream form a ByteBuffer instance."
  [^ByteBuffer buf]
  (proxy [InputStream] []
    (read
      ([]
         (if (.hasRemaining buf)
           (bit-and 0xFF (.get buf))
           -1))
      ([bs]
         (read bs 0 (alength bs)))
      ([bs off len]
         (if (.hasRemaining buf)
           (let [len (Math/min len (.remaining buf))]
             (.get buf off len)
             len)
           -1)))))

(defn- ip->long
  "Create long value from ip raw address."
  [ip]
  (->> ip
       (InetAddress/getByName)
       (.getAddress)
       (io/input-stream)
       (decode :int-be)))

(defn- load-data
  "Load IP database as s MappedByteBuffer."
  []
  (let [^FileChannel fc
        (->
         (FileInputStream.
          (io/file (io/resource "17monipdb.dat")))
         (.getChannel))]
    (.map fc FileChannel$MapMode/READ_ONLY 0 (.size fc))))

(defn get-int
  "Get int32 from byte buffer"
  [buf pos endian]
  (-> buf
      (.position pos)
      (.order endian)
      (.getInt)))

(defn- init
  "Initialize ip-service"
  []
  (let [data (load-data)
        length (.getInt data)]
    {:data-len length :buf data}))

;; An atom to keep IP database.
(defonce ip-data (init))

(defn find-geography [ip]
  (when (or (empty? ip) (not= (count (cs/split ip #"\.")) 4))
    (throw (ex-info "Illegal address,only supports IPv4." {:ip ip})))
  (let [ip-head (-> ip (cs/split #"\.") first (Integer/parseInt))
        ipn (ip->long ip)
        {:keys [buf data-len]} ip-data
        ;;make a copy of buffer for thread-safe
        buf (.duplicate buf)
        start (-> buf
                  (get-int (+ 4 (* 4 ip-head)) ByteOrder/LITTLE_ENDIAN)
                  (* 8)
                  (+ 4)
                  (+ 1024))
        max-comp-len (- data-len 1024 4)]
    (some
     (fn [{:keys [ip-num offset-bs size]}]
       (when (>= ip-num ipn)
         (let [offset (-> offset-bs
                          (vec)
                          (conj 0) ;;Padding zero at end.
                          (byte-array)
                          (ByteBuffer/wrap)
                          (.order ByteOrder/LITTLE_ENDIAN)
                          (.getInt))]
           (if (<= offset 0)
             (throw (ex-info "Invalid result offset." {:offset offset}))
             (decode (string "utf-8" :length size)
                     (buffer-stream
                      (.position buf
                                 (-> offset (+ data-len) (- 1024)))))))))
     (decode ip-codec
             (buffer-stream
              (-> buf
                  (.duplicate)
                  (.order ByteOrder/BIG_ENDIAN)
                  (.position start)))))))
