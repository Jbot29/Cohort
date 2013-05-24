(ns cohort-noir.views.welcome
  (:require [cohort-noir.views.common :as common]
            [noir.content.getting-started])
  (:use [noir.core :only [defpage]]))

(defpage "/welcome" []
         (common/layout
           [:p "Welcome to cohort-noir"]))
