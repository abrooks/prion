; A port of http://xcb.freedesktop.org/tutorial/
; "Checking basic information about a connection"

(ns prion
    (:gen-class)
    (:use [clj-native.core :only [defclib]])
    (:import [java.nio ByteBuffer ByteOrder]))

(System/setProperty "jna.library.path" "/usr/lib/libxcb.so")

(defclib
  xcb
  (:structs
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
    (xcb_get_setup [ void* ] void*)
    (xcb_setup_roots_iterator [ void* ] xcb_screen_iterator_t)
    (xcb_screen_next [ xcb_screen_iterator_t* ] void )))

(loadlib-xcb)

(defn direct-int-buffer [size]
      (let [byte-buffer (ByteBuffer/allocateDirect (* size 4))]
        (.order byte-buffer (ByteOrder/nativeOrder))
        (.asIntBuffer byte-buffer)))

(defn -main [& args]
      (let [scr-num (direct-int-buffer 1)
            conn (xcb_connect nil scr-num)
            setup (xcb_get_setup conn)
            iter (xcb_setup_roots_iterator setup)
            screen (do
                    (dotimes [_ (.get scr-num)]
                        (xcb_screen_next iter))
                    (.data iter))]
        (println)
        (println "Information of screen" (.root screen))
        (println "  width.........:" (.width_in_pixels screen))
        (println "  height........:" (.height_in_pixels screen))
        (println "  white pixel...:" (.white_pixel screen))
        (println "  black pixel...:" (.black_pixel screen))
        (println)))
