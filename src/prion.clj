(ns prion
    (:gen-class)
    (:use [clj-native.core :only [defclib]]))

(System/setProperty "jna.library.path" "/usr/lib/libc.so")

(defclib
  c
  (:functions
    (sleep [ int ] void)))

(loadlib-c)

(defn -main [& args]
      (println "prion args" args)
      (time (sleep (Integer. (nth args 0)))))
