; A port of http://xcb.freedesktop.org/tutorial/
; "Checking basic information about a connection"

(ns prion
    (:gen-class)
    (:use [clj-native.core :only [defclib]])
    (:import [java.nio ByteBuffer ByteOrder]))

(System/setProperty "jna.library.path" "/usr/lib/libxcb.so")
;(System/setProperty "jna.library.path" "/usr/lib/debug/usr/lib/libxcb.so.1.1.0")

(defclib
  xcb
  (:structs
    (xcb_connection_t #__opaque)
    (xcb_screen_t
      :root i32
      :default_colormap i32
      :white_pixel i32
      :black_pixel i32
      :current_input_masks i32
      :width_in_pixels i16
      :height_in_pixels i16
      :width_in_millimeters i16
      :height_in_millimeters i16
      :min_installed_maps i16
      :max_installed_maps i16
      :root_visual i32
      :backing_stores i8
      :save_unders i8
      :root_depth i8
      :allowed_depths_len i8)
    (xcb_screen_iterator_t :data xcb_screen_t* :rem int :index int))
  (:functions
    (xcb_connect [ constchar* int* ] void*)
    (xcb_disconnect [ void* ] void )
    (xcb_connection_has_error [ void* ] int)
    (xcb_generate_id [ void* ] i32)
    (xcb_grab_server [ void* ] void )
    (xcb_ungrab_server [ void* ] void )
    (xcb_get_setup [ void* ] void*)
    (xcb_setup_roots_iterator [ void* ] xcb_screen_iterator_t)
    (xcb_screen_next [ xcb_screen_iterator_t* ] void )))


(def *xconn* (atom nil))
(def *xdflscr* (atom nil))

(defn cInt
  ([]
   (cInt 1))
  ([size]
   (let [byte-buffer (ByteBuffer/allocateDirect (* size 4))]
     (.order byte-buffer (ByteOrder/nativeOrder))
     (.asIntBuffer byte-buffer))))

(defmacro with-grab [ body ]
  `(do
    (xcb_grab_server @*xconn*)
    ~body
    (xcb_ungrab_server @*xconn*)))

(defn x-connection []
  (let [scrn (cInt)
              conn (xcb_connect nil scrn)
              xerr (xcb_connection_has_error conn)]
    (if (not= 0 xerr)
      (throw (Exception. (pr-str "Error opening display:" xerr))))
    (reset! *xconn* conn)
    (reset! *xdflscr* (.get scrn 0))))

(defmacro with-connection [ body ]
  `(do
     (x-connection)
     ~body
     (xcb_disconnect @*xconn*)))

(defn get_setup []
  (xcb_get_setup @*xconn*))

(defn -main [& args]
  (do
    (loadlib-xcb)
    (with-connection
      (with-grab
        (let [iter (xcb_setup_roots_iterator (get_setup))
              screen (do
                        (dotimes [_ 0 #_(@xdflscr)]
                          (xcb_screen_next iter))
                        (.data iter))]
          (println)
          (println "Information of screen" (.root screen))
          (println "  width.........:" (.width_in_pixels screen))
          (println "  height........:" (.height_in_pixels screen))
          (println "  white pixel...:" (.white_pixel screen))
          (println "  black pixel...:" (.black_pixel screen))
          (println))))))

