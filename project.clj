(defproject prion "0.0.1-SNAPSHOT"
  :description "A window manager based loosely on Ion3 written in Clojure."
  :url "http://github.com/abrooks/prion"
  :repositories [["java.net" "http://download.java.net/maven/2/"]]
  :dependencies [[org.clojure/clojure "1.1.0-master-SNAPSHOT"]
                 [org.clojure/clojure-contrib "1.0-SNAPSHOT"]
                 [net.java.dev.jna/jna "3.2.4"]
                 [clj-native/clj-native "0.6.0-SNAPSHOT"]]
  :dev-dependencies []
  :main prion)
